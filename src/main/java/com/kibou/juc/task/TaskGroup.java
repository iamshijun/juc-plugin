package com.kibou.juc.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.kibou.juc.JUCHelper;
import com.kibou.juc.ex.TaskInvokeException;
import com.kibou.utils.collection.CollectionUtils;

/**
 * Runnable/任务组
 * @author aimysaber@gmail.com
 *
 */
public abstract class TaskGroup<T> {
	
	/**
	 * 任务组名称
	 */
	private final String groupName;
	
	/**
	 * 总任务超时时间
	 */
	private long timeout;
	
	/**
	 * 超时时间的单位
	 */
	private TimeUnit timeunit;
	
	/**
	 * 当前任务组的异常信息,用于跟踪任务的起源
	 */
	final Exception trace; 
	
	private final ExecutorService executorService;
	
	/**
	 * 当任务数为1个时 是否由调用者直接执行 不放到线程池执行 (默认为true)
	 */
	private boolean callerRunOnOneTask = true;
	
	/**
	 * 单个任务组 每次最多只能执行的任务数, -1为无限制(默认)
	 */
	private int maxParallelNum = -1;
	
	/**
	 * 当某个任务出现异常的时候 返回一个默认的值
	 * @return
	 */
	protected T getFallback(){ //TODO(shisj) 应该是List<T> ?
		throw new UnsupportedOperationException("No fallback available.");
	}
	
	//任务组状态信息
//	private volatile int state;
//	
//	private static final int INITIAL = 0;
//	private static final int SUCCESS = 1;
//	private static final int FAIL = 2;
	//interrupted , cancelled , ex
	
	public TaskGroup(String groupName,ExecutorService executorService){
		this(groupName,0,TimeUnit.MILLISECONDS,new Exception(groupName + " trace exception"),executorService);
	}
	
	public TaskGroup(String groupName,long timeout,TimeUnit timeunit,Exception trace,ExecutorService executorService){
		this.groupName = groupName;
		this.timeout = timeout;
		this.timeunit = timeunit;
		this.trace = trace;
		this.executorService = Objects.requireNonNull(executorService,"executorService is not specified");
	}
	
	public String getGroupName(){
		return groupName;
	}
	
	public long getTimeout() {
		return timeout;
	}
	
	public TimeUnit getTimeunit() {
		return timeunit;
	}
	
	public boolean hasTimeout(){
		return timeout > 0;
	}
	
	public void setCallerRunOnOneTask(boolean callerRunOnOneTask) {
		this.callerRunOnOneTask = callerRunOnOneTask;
	}
	
	public boolean isCallerRunOnOneTask() {
		return callerRunOnOneTask;
	}
	
	public void setMaxParallelNum(int maxParallelNum) {
		this.maxParallelNum = maxParallelNum;
	}
	
	public int getMaxParallelNum() {
		return maxParallelNum;
	}
	
	public ExecutorService getExecutorService(){
		//return Executors.unconfigurableExecutorService(executorService);
		return executorService;
	}	
	
	
	class SimpleFuture<V> implements Future<V>{

		private final Callable<? extends V> callable;
		
		private V value;
		private Exception cause;
		
		private boolean finished;
		
		SimpleFuture(Callable<? extends V> callable){
			this.callable = Objects.requireNonNull(callable);
		}
		
		private V invoke() throws Exception{
			if(finished){
				if(cause != null)
					throw cause;
				return value;
			}
			try {
				return value = callable.call();
			} catch (Exception e) {
				throw cause = e;
			}finally{
				finished = true;
			}
		}
		
		@Override
		public boolean cancel(boolean mayInterruptIfRunning) {
			return false;
		}

		@Override
		public boolean isCancelled() {
			return false;
		}

		@Override
		public boolean isDone() {
			return finished;
		}

		@Override
		public V get() throws InterruptedException, ExecutionException {
			try {
				return invoke();
			} catch (Exception e) {
				throw new ExecutionException(e); 
			}
		}

		@Override
		public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
			return get();
		}
	}
	
	public List<Future<T>> invokeAll() throws InterruptedException{
		
		List<? extends Callable<T>> tasks = getTask();
		
		if(CollectionUtils.isEmpty(tasks))
			return Collections.emptyList();
		
		if(tasks.size() == 1 && callerRunOnOneTask){
			Future<T> f = new SimpleFuture<T>(tasks.get(0));
			return Collections.singletonList(f);
		}
		
		if(maxParallelNum == -1 || tasks.size() < maxParallelNum){
			if(hasTimeout()){
				return executorService.invokeAll(tasks, timeout, timeunit);//timeout为所有任务总执行超时时间
			}else{ 
				return executorService.invokeAll(tasks);
			}
		}else{//每个任务组 每次最大的执行任务数, TODO(shisj) 暂不支持超时
			int start = 0 , offset = maxParallelNum;
			
//			List<Future<T>> futures = com.google.common.collect.Lists.newArrayListWithExpectedSize(tasks.size());
			List<Future<T>> futures = new ArrayList<>();
			
			while(start < tasks.size()){
				
				List<? extends Callable<T>> subTasks = tasks.subList(start, Math.min(start + offset, tasks.size()));
				if(subTasks.isEmpty())
					break;
				
				List<Future<T>> invokeAll = executorService.invokeAll(subTasks);
				futures.addAll(invokeAll);
				
				try {
					JUCHelper.waitAll(invokeAll);
				} catch (ExecutionException ignore) {
					ignore.printStackTrace();
				}
				
				start = start + offset;
			}
			
			return futures;
		}
	}
	
	public List<T> invokeAndGetAll(){
		
		Throwable cause = null; 
		
		List<T> results = Collections.emptyList();
		
		List<Future<T>> futures = null;
		
		try {
			futures = invokeAll();
		} catch (InterruptedException ie) {
			cause = ie;
		}
		
		if(cause == null){
			if(CollectionUtils.isEmpty(futures))
				return Collections.emptyList();
			
//			results = com.google.common.collect.Lists.newArrayListWithExpectedSize(futures.size());
			results = new ArrayList<>();
			
			for(Future<T> future : futures){
				try {
					results.add(future.get()); //TimeoutException => CancellationException
				} catch (InterruptedException ie) {
					ie.printStackTrace();//never happen!?
				} catch(ExecutionException | CancellationException e){
					cause = e;
				}
			}
		}
		
		if(cause != null){
			if(trace != null){//FIXME(shisj) combine with cause!;
				StackTraceElement stackTraceElement = trace.getStackTrace()[0];
				System.err.println(stackTraceElement);
			}
			throw new TaskInvokeException("TaskGroup : " + groupName + " got a exception!",cause);
		}
		
		return results;
	}
	
	public abstract List<? extends Callable<T>> getTask();
	
	protected void newTaskFor(Runnable task, T ret){
		//TODO(shisj)
	}
	
	protected void newTaskFor(Callable<T> task){
		//TODO(shisj)
	}
}
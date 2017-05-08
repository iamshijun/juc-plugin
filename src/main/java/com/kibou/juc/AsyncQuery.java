package com.kibou.juc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.kibou.juc.ex.TaskInvokeException;
import com.kibou.juc.task.TaskHandler;


/*TODO(shisj)
   1. 加降级开关? 降级=>使用同步方式代替异步执行- (like CallerRunsPolicy).
               降级时 如果队列
   2. maybe we can try "rxjava"
*/
/**
 * @author aimysaber@gmail.com
 */
public final class AsyncQuery {

	private AsyncQuery(){}
	
	private static ExecutorService executoService;
	
//	private final static Logger logger = LoggerFactory.getLogger(AsyncQuery.class);
	
	static{ 
		//TODO(shisj) imitate rxjava - io/computation Scheduler! 
		int availableProcessors = Runtime.getRuntime().availableProcessors();
		executoService = new ThreadPoolExecutor(
					availableProcessors * 10, availableProcessors * 100, 30, TimeUnit.MINUTES, 
					new SynchronousQueue<Runnable>(), //等待队列为空 确保不会有等待的任务
					new NamedThreadFactory("asycn-query-thread-", true),
					new ThreadPoolExecutor.CallerRunsPolicy() // back pressure
				);
		
		Runtime.getRuntime().addShutdownHook(new Thread(){
			@Override
			public void run() {
				executoService.shutdownNow();
			}
		});
	}
	
	public static ExecutorService getExecutorService(){
		return Executors.unconfigurableExecutorService(executoService);
	}
	
	static ActionHandler defaultActionHandler = new ActionHandler() {
		public void onError(Throwable e) {}
		public void onCompleted() {}
	};
	
	static TaskHandler<?> defaultTaskHandler = new TaskHandler<Object>() {
		public void onCompleted(Object result) {}
		public void onError(Throwable e) {}
	};
	
	
	public static void execute(Runnable task){
		//execute(task, defaultActionHandler);
	}
	
	public static void execute(Runnable task,ActionHandler actionHandler){
		//TODO(shisj)
	}
	
	public static Future<?> submit(Runnable task){
		return executoService.submit(task);
	}
	public static <T> Future<T> submit(Callable<T> task){
		return executoService.submit(task);
	}
	
	
	public static <T> List<T> invokeAllAndWait(List<Callable<T>> tasks){
		List<Future<T>> allFutures = invokeAll(tasks);
		if(allFutures != null && !allFutures.isEmpty()){
			List<T> resultList = new ArrayList<>(tasks.size());
			for(Future<T> future : allFutures){
				try {
					resultList.add(future.get());
				} catch (InterruptedException | ExecutionException e) {
					throw new TaskInvokeException(e.getMessage(),e);
				}
			}
		}
		return Collections.emptyList();
	}
	
	public static <T> List<Future<T>> invokeAll(List<Callable<T>> tasks){
		try {
			return executoService.invokeAll(tasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
	
}

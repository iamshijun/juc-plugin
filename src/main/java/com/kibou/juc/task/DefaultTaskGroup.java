package com.kibou.juc.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.kibou.common.Preconditions;
import com.kibou.juc.AsyncQuery;

/**
 * @author aimysaber@gmail.com
 *
 * @see {@link AsyncQuery}
 * @param <K>
 * @param <T>
 */
public class DefaultTaskGroup<T> extends TaskGroup<T>{
	
	private final List<Callable<T>> tasks;
	
	DefaultTaskGroup(Builder<T> builder){
		super(builder.groupName, 
				builder.timeout,
				builder.timeunit,
				builder.trace, 
				builder.executor == null ? 	
						AsyncQuery.getExecutorService() : builder.executor);
		this.tasks = builder.tasks;
	}
	
	@Override
	public List<Callable<T>> getTask() {
		return Collections.unmodifiableList(tasks);
	}
	
	public static class Builder<T> {
		
		private List<Callable<T>> tasks;
		
		private final String groupName;
		
		private long timeout;
		private TimeUnit timeunit;
		
		private Exception trace;
		
		private ExecutorService executor;
		
		public Builder(String groupName) {
			this(groupName,5);
		}
		
		public Builder(String groupName,int estimatedSize) {
			Preconditions.checkArgument(estimatedSize > 0,"size must greater than 0");
			this.tasks = new ArrayList<>(estimatedSize);//Lists.newArrayListWithExpectedSize(estimatedSize);
			this.groupName = groupName;
		}
		
		public Builder<T> addTask(Runnable task) {
			return addTask(task,null);
		}
		
		public Builder<T> addTask(Runnable task,T result) {
			return addTask(Executors.callable(Objects.requireNonNull(task),result));
		}
		
		public  Builder<T> addTask(Callable<T> task) {
			tasks.add(task);
			return this;
		}
		
		public Builder<T> timeout(long timeout){
			return timeout(timeout,TimeUnit.MILLISECONDS);
		}
		
		public Builder<T> timeout(long timeout,TimeUnit timeunit){
			this.timeout = timeout;
			this.timeunit = timeunit;
			return this;
		}
		
		@SuppressWarnings("unchecked")
		public  Builder<T> addTasks(Callable<T>... tasks) {
			for(Callable<T> task : tasks){
				addTask(task);
			}
			return this;
		}
		
		public Builder<T> trace(Exception trace){
			this.trace = trace;
			return this;
		}
		public Builder<T> runOn(ExecutorService executor){
			this.executor = executor;
			return this;
		}
		
		public DefaultTaskGroup<T> build(){
			return new DefaultTaskGroup<T>(this);
		}
	}
}
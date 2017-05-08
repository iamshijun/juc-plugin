package com.kibou.juc;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

	private String poolThreadNamePrefix;
	private AtomicInteger threadCount = new AtomicInteger(0);
	private boolean daemon = false;
	private ThreadGroup group;

	{
		final SecurityManager s = System.getSecurityManager();
		group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
	}

	public NamedThreadFactory(String poolThreadNamePrefix, boolean daemon) {
		this.poolThreadNamePrefix = poolThreadNamePrefix;
		this.daemon = daemon;
	}

	@Override
	public Thread newThread(Runnable target) {
		Thread thread = new Thread(group, target, poolThreadNamePrefix + threadCount.incrementAndGet());
		thread.setDaemon(daemon);
		return thread;
	}
}
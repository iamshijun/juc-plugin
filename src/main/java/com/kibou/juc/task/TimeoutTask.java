package com.kibou.juc.task;

import java.util.concurrent.Delayed;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class TimeoutTask implements Delayed {

	private final Future<?> futureTask;
	private long expireTime = -1;

	public TimeoutTask(Future<?> futureTask, long timeout, TimeUnit unit) {
		this.futureTask = futureTask;
		expireTime = System.currentTimeMillis() + TimeUnit.MILLISECONDS.convert(timeout, unit);
	}

	public boolean cancel() {
		if (futureTask.isDone() || futureTask.isCancelled()) {
			return false;
		}
		return futureTask.cancel(true);
	}

	@Override
	public int compareTo(Delayed o) {
		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(expireTime, TimeUnit.MILLISECONDS);
	}
}
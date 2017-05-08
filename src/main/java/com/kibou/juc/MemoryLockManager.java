package com.kibou.juc;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.kibou.utils.function.Supplier;

/**
 * 内存锁管理 当前只有 mutex, ReentrantReadWriteLock.
 * 
 * @author aimysaber@gmail.com
 *
 */
public abstract class MemoryLockManager {

	private static ConcurrentMap<Object, Object> mutextMap = new ConcurrentHashMap<>();

	private static ConcurrentMap<Object, ReentrantReadWriteLock> readWriteLockMap = new ConcurrentHashMap<>();

	private static <T> T getLock(Object key, Supplier<T> supplier, ConcurrentMap<Object, T> lockMap) {
		Objects.requireNonNull(key, "key cannot be null");

		T lock = lockMap.get(key);
		if (lock == null) {
			lockMap.putIfAbsent(key, supplier.get());
			lock = lockMap.get(key);
		}
		return lock;
	}

	public static Object mutex(Object key) {
		return getLock(key, new Supplier<Object>() {
			public Object get() {
				return new Object();
			}
		}, mutextMap);
	}

	public static ReentrantReadWriteLock readWriteLock(Object key) {
		return getLock(key, new Supplier<ReentrantReadWriteLock>() {
			public ReentrantReadWriteLock get() {
				return new ReentrantReadWriteLock();
			}
		}, readWriteLockMap);
	}
}

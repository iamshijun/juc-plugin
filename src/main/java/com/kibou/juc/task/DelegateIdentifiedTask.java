package com.kibou.juc.task;

import java.util.concurrent.Callable;

/**
 * @author aimysaber@gmail.com
 *
 * @param <K>
 * @param <V>
 */
class DelegateIdentifiedTask<K,V> implements IdentifiedTask<K, V> {

	private final K key;
	private final Callable<V> delegate;
	
	public DelegateIdentifiedTask(K key,Callable<V> task){
		this.key = key;
		this.delegate = task;
	}
	
	@Override
	public V call() throws Exception {
		return delegate.call();
	}

	@Override
	public K getKey() {
		return key;
	}

}

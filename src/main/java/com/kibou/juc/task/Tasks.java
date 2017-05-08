package com.kibou.juc.task;

import java.util.concurrent.Callable;

public final class Tasks {

	public static <K,R> IdentifiedTask<K, R> identifiedTask(K key,Callable<R> delegate){
		return new DelegateIdentifiedTask<K, R>(key, delegate);
	}
}

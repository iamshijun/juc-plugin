package com.kibou.juc.task;

import java.util.concurrent.Callable;

public interface IdentifiedTask<K,V> extends Callable<V> {

	K getKey();
}

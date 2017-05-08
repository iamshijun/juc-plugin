package com.kibou.juc.task;

import java.util.Map;
import java.util.concurrent.Callable;

interface MapReturnTypeTask<V> extends Callable<Map<String,V>>{

	public Map<String, V> call() throws Exception;

}

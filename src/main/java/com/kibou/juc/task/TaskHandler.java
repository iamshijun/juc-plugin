package com.kibou.juc.task;

public interface TaskHandler<T> {

    public void onCompleted(T result);
  
    public void onError(Throwable e);  
}

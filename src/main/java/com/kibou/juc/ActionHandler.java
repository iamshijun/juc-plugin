package com.kibou.juc;

public interface ActionHandler {

    public void onCompleted();
  
    public void onError(Throwable e);  
}

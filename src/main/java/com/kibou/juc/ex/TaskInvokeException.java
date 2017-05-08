package com.kibou.juc.ex;

public class TaskInvokeException extends RuntimeException {

	public TaskInvokeException(String msg){
		super(msg);
	}
	
	public TaskInvokeException(Throwable cause){
		super(cause);
	}
	public TaskInvokeException(String msg,Throwable cause){
		super(msg, cause);
	}
}

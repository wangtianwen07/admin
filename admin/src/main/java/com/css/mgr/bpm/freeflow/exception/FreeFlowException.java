package com.css.mgr.bpm.freeflow.exception;

public class FreeFlowException extends RuntimeException {
	public FreeFlowException(String msg){
		super(msg);
	}
	public FreeFlowException(Exception e){
		
		super(e);
	}
}

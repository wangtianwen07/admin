package com.css.mgr.bpm.freeflow.handler;

import java.util.Date;

/**
 * @author pc
 * nextAble:是否进行下一环节
 * nextUserProcessName:下一环节处理名称
 *
 */
public class Nextor {
	//是否进行下一环节
	private boolean nextAble=true;
	//下一环节处理名称
	public String nextUserProcessName;
	//下一环节完成策略，允许为null.优先级最高
	private String nextCompletionStrategy=null;
	//下一环节限办时间，允许为null.优先级最高
	private Date nextLimitedTime=null;
	//下一环节是否定时自动完成
	private boolean nextAutoPass=false;
	//当前环节办理结果名称，允许为null
	private String currentResultText=null;
	public Nextor(){
	}
	public boolean isNextAble() {
		return nextAble;
	}
	public void setNextAble(boolean nextAble) {
		this.nextAble = nextAble;
	}
	public String getNextUserProcessName() {
		return nextUserProcessName;
	}
	public void setNextUserProcessName(String nextUserProcessName) {
		this.nextUserProcessName = nextUserProcessName;
	}
	
	public boolean isNextAutoPass() {
		return nextAutoPass;
	}
	public void setNextAutoPass(boolean nextAutoPass) {
		this.nextAutoPass = nextAutoPass;
	}
	public String getNextCompletionStrategy() {
		return nextCompletionStrategy;
	}
	public void setNextCompletionStrategy(String completionStrategy) {
		this.nextCompletionStrategy = completionStrategy;
	}
	public Date getNextLimitedTime() {
		return nextLimitedTime;
	}
	public void setNextLimitedTime(Date nextLimitedTime) {
		this.nextLimitedTime = nextLimitedTime;
	}
	public String getCurrentResultText() {
		return currentResultText;
	}
	public void setCurrentResultText(String currentResultText) {
		this.currentResultText = currentResultText;
	}
	
}

package com.css.mgr.bpm.freeflow.dao.pojo;

import java.util.Date;

public class TaskParam {
	private String opinion;//审批意见,可为null
	private String userProcessName;//不一环节处理名称,可为null
	private Date limitedTime;//限办时间,可为null
	private String emergencyLevel;//紧急程度 ,可为null
	private String resultText;//审批结果,可为null
	private String completionStrategy=CompletionStrategy.COMPLETION_STRATEGY_ALONE;//完成策略
	private String autoPass=AutoPass.AUTO_PASS_N;
	//定时通过参数 ,可为null
	public TaskParam(){
	}
	/**
	 *
	 * @param opinion 审批意见
	 */
	public TaskParam(String opinion){
		this.opinion=opinion;
	}
	public String getOpinion() {
		return opinion;
	}
	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public Date getLimitedTime() {
		return limitedTime;
	}
	public void setLimitedTime(Date limitedTime) {
		this.limitedTime = limitedTime;
	}
	public String getEmergencyLevel() {
		return emergencyLevel;
	}
	public void setEmergencyLevel(String emergencyLevel) {
		this.emergencyLevel = emergencyLevel;
	}
	public String getCompletionStrategy() {
		return completionStrategy;
	}
	public void setCompletionStrategy(String completionStrategy) {
		this.completionStrategy = completionStrategy;
	}
	public String getUserProcessName() {
		return userProcessName;
	}
	public void setUserProcessName(String userProcessName) {
		this.userProcessName = userProcessName;
	}
	public String getAutoPass() {
		return autoPass;
	}
	public void setAutoPass(String autoPass) {
		this.autoPass = autoPass;
	}
	public String getResultText() {
		return resultText;
	}
	public void setResultText(String resultText) {
		this.resultText = resultText;
	}

}

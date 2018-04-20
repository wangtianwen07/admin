package com.css.mgr.bpm.freeflow.dao.pojo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.css.mgr.admin.common.IDUtil;
import com.css.mgr.bpm.freeflow.adapter.FfUserService;
import com.css.mgr.bpm.freeflow.exception.FreeFlowException;

@Entity
@Table(name = "FF_TASK_USER")
public class FfTaskUser implements Serializable {

	/**
	 * 
	 */
	//
	@Transient
	private static Set<String> strategy = new HashSet<String>();
	//
	static {
		strategy.add(CompletionStrategy.COMPLETION_STRATEGY_ALONE);
		strategy.add(CompletionStrategy.COMPLETION_STRATEGY_ANY);
		strategy.add(CompletionStrategy.COMPLETION_STRATEGY_PARALLELL);
		strategy.add(CompletionStrategy.COMPLETION_STRATEGY_SERIAL);
	}

	public static void validateSrategy(String completionStrategy) {
		if (!strategy.contains(completionStrategy)) {
			throw new FreeFlowException("不被支持的完成策略:" + completionStrategy);
		}
	}

	//
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "FF_TASK_USER_UUID")
	private String ffTaskUserUuid = IDUtil.getId();
	@Column(name = "FF_TASK_UUID")
	private String ffTaskUuid;
	@Column(name = "ORG_NAME")
	private String orgName;
	@Column(name = "ORG_UUID")
	private String orgUuid;
	@Column(name = "ENTRUST")
	private String entrust;// 委托人
	@Column(name = "HANDLER_USER_ID")
	private String handlerUserId;
	@Column(name = "HANDLER_USER_NAME")
	private String handlerUserName;
	@Transient
	private FfTask task;
	@Column(name = "HANDLER_STATE")
	private String handlerState = HandlerState.HANDLERSTATE_PROCESS;// 处理状态
	@Column(name = "CREATE_TIME")
	private Date createTime = new Date();
	@Transient
	private String createTimeStr;

	// 完成时间
	@Column(name = "END_TIME")
	private Date endTime;
	@Transient
	private String endTimeStr;
	@Column(name = "AUTO_PASS")
	private String autoPass = AutoPass.AUTO_PASS_N;// 自动通过
	// 限办时间
	@Column(name = "LIMITED_TIME")
	private Date limitedTime;
	// 上一环节
	@Column(name = "PREV_USER_PROCESS")
	private String prevUserProcess;
	@Column(name = "USER_PROCESS")
	private String userProcess;
	@Column(name = "USER_PROCESS_NAME")
	private String userProcessName;
	@Column(name = "RESULT")
	private String result = Result.RESULT_PASS;// 处理结果
	@Column(name = "OPINION")
	private String opinion;
	@Column(name = "COMPLETION_STRATEGY")
	private String completionStrategy = CompletionStrategy.COMPLETION_STRATEGY_ALONE;// 完成策略
	@Column(name = "COMPLETION_ORDER")
	private String completionOrder = CompletionOrder.COMPLETION_ORDER_DISORDER;// 扩展字段
	@Column(name = "RESULT_TEXT")
	private String resultText;
	// 处理顺序
	@Column(name = "USER_ORDER")
	private Integer userOrder = 1;
	// 用户类型OA中只有SUser
	@Column(name = "USER_BUSI_TYPE")
	private String userBusiType = FfUserService.USERDATATYPE_SUSER;
	// 父环节，直接创建此环节的用户环节
	@Column(name = "PARENT_USER_PROCESS")
	private String parentUserProcess;

	public String getFfTaskUserUuid() {
		return ffTaskUserUuid;
	}

	public void setFfTaskUserUuid(String ffTaskUserUuid) {
		this.ffTaskUserUuid = ffTaskUserUuid;
	}

	public String getFfTaskUuid() {
		return ffTaskUuid;
	}

	public void setFfTaskUuid(String ffTaskUuid) {
		this.ffTaskUuid = ffTaskUuid;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getOrgUuid() {
		return orgUuid;
	}

	public void setOrgUuid(String orgUuid) {
		this.orgUuid = orgUuid;
	}

	public String getHandlerUserId() {
		return handlerUserId;
	}

	public void setHandlerUserId(String handlerUserId) {
		this.handlerUserId = handlerUserId;
	}

	public String getHandlerUserName() {
		return handlerUserName;
	}

	public void setHandlerUserName(String handlerUserName) {
		this.handlerUserName = handlerUserName;
	}

	public String getHandlerState() {
		return handlerState;
	}

	public void setHandlerState(String handlerState) {
		this.handlerState = handlerState;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getLimitedTime() {
		return limitedTime;
	}

	public void setLimitedTime(Date limitedTime) {
		this.limitedTime = limitedTime;
	}

	public String getUserProcess() {
		return userProcess;
	}

	public void setUserProcess(String userProcess) {
		this.userProcess = userProcess;
	}

	public String getUserProcessName() {
		return userProcessName;
	}

	public void setUserProcessName(String userProcessName) {
		this.userProcessName = userProcessName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getOpinion() {
		return opinion;
	}

	public void setOpinion(String opinion) {
		this.opinion = opinion;
	}

	public String getCompletionStrategy() {
		return completionStrategy;
	}

	public void setCompletionStrategy(String completionStrategy) {
		this.completionStrategy = completionStrategy;
	}

	public String getCompletionOrder() {
		return completionOrder;
	}

	public void setCompletionOrder(String completionOrder) {
		this.completionOrder = completionOrder;
	}

	public Integer getUserOrder() {
		return userOrder;
	}

	public void setUserOrder(Integer userOrder) {
		this.userOrder = userOrder;
	}

	public String getUserBusiType() {
		return userBusiType;
	}

	public void setUserBusiType(String userBusiType) {
		this.userBusiType = userBusiType;
	}

	public String getPrevUserProcess() {
		return prevUserProcess;
	}

	public void setPrevUserProcess(String prevUserProcess) {
		this.prevUserProcess = prevUserProcess;
	}

	public FfTask getTask() {
		return task;
	}

	public void setTask(FfTask task) {
		this.task = task;
	}

	public String getEntrust() {
		return entrust;
	}

	public void setEntrust(String entrust) {
		this.entrust = entrust;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public String getEndTimeStr() {
		return endTimeStr;
	}

	public void setEndTimeStr(String endTimeStr) {
		this.endTimeStr = endTimeStr;
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

	public String getParentUserProcess() {
		return parentUserProcess;
	}

	public void setParentUserProcess(String parentUserProcess) {
		this.parentUserProcess = parentUserProcess;
	}

}

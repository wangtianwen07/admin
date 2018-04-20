package com.css.mgr.bpm.freeflow.dao.pojo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang.StringUtils;
import com.css.mgr.admin.common.IDUtil;
import com.css.mgr.bpm.freeflow.adapter.FfUserService;

@Entity
@Table(name = "FF_TASK")
public class FfTask implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "FF_TASK_UUID")
	private String ffTaskUuid = IDUtil.getId();
	@Column(name = "FF_DEF_UUID")
	private String ffDefUuid;
	@Column(name = "TASK_NAME")
	private String taskName;
	@Column(name = "BUSI_OBJ")
	private String busiObj;
	@Column(name = "BUSI_OBJ_UUID")
	private String busiObjUuid;
	@Column(name = "TASK_URL")
	private String taskUrl;
	/**
	 * 随机数，子、主流程一致
	 */
	@Column(name = "TASK_PROCESS")
	private String taskProcess = IDUtil.getId();
	@Column(name = "HANDLE_STATE_NAME")
	private String handleStateName;
	@Column(name = "TASK_STATE")
	private String taskState = TaskState.TASK_STATE_ACTIVE;
	@Column(name = "MAIN_TASK_UUID")
	private String mainTaskUuid;
	@Column(name = "EMERGENCY_LEVEL")
	private String emergencyLevel;
	// 扩展字段，用户类型
	@Column(name = "USER_BUSI_TYPE")
	private String userBusiType = FfUserService.USERDATATYPE_SUSER;
	@Column(name = "CREATE_TIME")
	private Date createTime = new Date();
	@Column(name = "USER_ID")
	private String userId;
	@Column(name = "CREATE_ORG_ID")
	private String createOrgId;
	@Column(name = "USER_NAME")
	private String userName;
	@Column(name = "CREATE_ORG_NAME")
	private String createOrgName;
	@Column(name = "LIMITED_TIME")
	private Date limitedTime;
	@Column(name = "END_TIME")
	private Date endTime;
    
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	// 是否主办
	public boolean isMan() {
		return StringUtils.isNotEmpty(mainTaskUuid);
	}

	public String getFfTaskUuid() {
		return ffTaskUuid;
	}

	public void setFfTaskUuid(String ffTaskUuid) {
		this.ffTaskUuid = ffTaskUuid;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getBusiObj() {
		return busiObj;
	}

	public void setBusiObj(String busiObj) {
		this.busiObj = busiObj;
	}

	public String getBusiObjUuid() {
		return busiObjUuid;
	}

	public void setBusiObjUuid(String busiObjUuid) {
		this.busiObjUuid = busiObjUuid;
	}

	public String getTaskUrl() {
		return taskUrl;
	}

	public void setTaskUrl(String taskUrl) {
		this.taskUrl = taskUrl;
	}

	public String getTaskProcess() {
		return taskProcess;
	}

	public void setTaskProcess(String taskProcess) {
		this.taskProcess = taskProcess;
	}

	public String getHandleStateName() {
		return handleStateName;
	}

	public void setHandleStateName(String handleStateName) {
		this.handleStateName = handleStateName;
	}

	public String getTaskState() {
		return taskState;
	}

	public void setTaskState(String taskState) {
		this.taskState = taskState;
	}

	public String getMainTaskUuid() {
		return mainTaskUuid;
	}

	public void setMainTaskUuid(String mainTaskUuid) {
		this.mainTaskUuid = mainTaskUuid;
	}

	public String getEmergencyLevel() {
		return emergencyLevel;
	}

	public void setEmergencyLevel(String emergencyLevel) {
		this.emergencyLevel = emergencyLevel;
	}

	public String getUserBusiType() {
		return userBusiType;
	}

	public void setUserBusiType(String userBusiType) {
		this.userBusiType = userBusiType;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCreateOrgId() {
		return createOrgId;
	}

	public void setCreateOrgId(String createOrgId) {
		this.createOrgId = createOrgId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCreateOrgName() {
		return createOrgName;
	}

	public void setCreateOrgName(String createOrgName) {
		this.createOrgName = createOrgName;
	}

	public Date getLimitedTime() {
		return limitedTime;
	}

	public void setLimitedTime(Date limitedTime) {
		this.limitedTime = limitedTime;
	}

	public String getFfDefUuid() {
		return ffDefUuid;
	}

	public void setFfDefUuid(String ffDefUuid) {
		this.ffDefUuid = ffDefUuid;
	}

}

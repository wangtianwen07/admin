/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.dao.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author wangtianwen
 * 2018年3月7日
 */
@Entity
@Table(name="S_MSG")
public class SMsg implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public static final String READ_STATUS_NO = "1";//未读
	public static final String READ_STATUS_YES = "2";//已读
	
	@Id
	@Column(name="UUID")
	private String uuid;
	@Column(name="TITLE")
	private String title;
	@Column(name="CONTENT")
	private String content;
	@Column(name="CREATE_TIME")
	private Date createTime;
	@Column(name="USER_ID")
	private String userId;
	@Column(name="USER_NAME")
	private String userName;
	@Column(name="READ_STATUS")
	private String readStatus;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getReadStatus() {
		return readStatus;
	}
	public void setReadStatus(String readStatus) {
		this.readStatus = readStatus;
	}
}

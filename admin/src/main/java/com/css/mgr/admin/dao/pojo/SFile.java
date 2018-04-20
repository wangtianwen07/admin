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
 * 2018年3月2日
 */
@Entity
@Table(name = "S_FILE")
public class SFile implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "UUID")
	private String uuid;
	@Column(name = "NAME")
	private String name;
	@Column(name = "BID")
	private String bId;
	@Column(name = "FILE_BUSINESS_TYPE")
	private String fileBusinessType;
	@Column(name = "URL")
	private String url;
	@Column(name = "READABLE_FILE_SIZE")
	private String readableFileSize;
	@Column(name = "FILE_SIZE")
	private String fileSize;
	@Column(name = "EXT_NAME")
	private String extName;
	@Column(name = "UPLOAD_TIME")
	private Date uploadTime;
	@Column(name = "USER_ID")
	private String userId;
	@Column(name = "USER_NAME")
	private String userName;
	@Column(name = "REMARK")
	private String remark;
	@Column(name = "MD5")
	private String md5;
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFileBusinessType() {
		return fileBusinessType;
	}
	public void setFileBusinessType(String fileBusinessType) {
		this.fileBusinessType = fileBusinessType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	public String getExtName() {
		return extName;
	}
	public void setExtName(String extName) {
		this.extName = extName;
	}
	public Date getUploadTime() {
		return uploadTime;
	}
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
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
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getMd5() {
		return md5;
	}
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public String getbId() {
		return bId;
	}
	public void setbId(String bId) {
		this.bId = bId;
	}
	public String getReadableFileSize() {
		return readableFileSize;
	}
	public void setReadableFileSize(String readableFileSize) {
		this.readableFileSize = readableFileSize;
	}
}

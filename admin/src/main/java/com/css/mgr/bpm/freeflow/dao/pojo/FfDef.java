package com.css.mgr.bpm.freeflow.dao.pojo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FF_DEF")
public class FfDef implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "FF_DEF_UUID")
	private String ffDefUuid;
	@Column(name = "CODE")
	private String code;
	@Column(name = "NAME")
	private String name;
	@Column(name = "URL")
	private String url;
	// 自定义用户选择器
	@Column(name = "SUBSCRIBER")
	private String subscriber;
	// 处理事件
	@Column(name = "HANDLER")
	private String handler;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSubscriber() {
		return subscriber;
	}

	public void setSubscriber(String subscriber) {
		this.subscriber = subscriber;
	}

	public String getHandler() {
		return handler;
	}

	public void setHandler(String handler) {
		this.handler = handler;
	}

	public String getFfDefUuid() {
		return ffDefUuid;
	}

	public void setFfDefUuid(String ffDefUuid) {
		this.ffDefUuid = ffDefUuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
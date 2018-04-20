package com.css.mgr.bpm.freeflow.adapter;

import java.util.List;

import com.css.mgr.base.dao.pojo.IUser;

public interface IFfUserService {
	/**
	 * 
	 * 得到用户类型
	 * 
	 */
	String getCurUserType(IUser user);
	/**
	 * 
	 * 根据类型得到用户
	 * 
	 */
	IUser getIUser(String userId,String userType);
	/**
	 *当前用户类型 
	 * 
	 */
	String getCurUserType();
	/**
	 * 
	 * 是管理用户
	 */
	boolean isSUser();
	boolean isSUser(IUser user);
	/**
	 * @return 默认签收部门
	 */
	public String getDefaultPost();
	/**
	 * @param postid 岗位
	 * @param dp 部门
	 * @return
	 */
	public List<IUser> getUserIdByOrg(String postid, String dp);
}

package com.css.mgr.bpm.freeflow.subscriber;

import java.util.List;

import com.css.mgr.base.dao.pojo.IUser;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;

import net.sf.json.JSONObject;

public interface IUserSubscriber {
	/****
	 *json:{
	 *  sele:'afes22423,afes22423,afes22423',//固定选择人
	 *  sele:'afes22423,afes22423,afes22423',//固定部门签收岗
	 *  calc:true //自动计算通过UserSubscriber获取
	 *} 
	 * 
	 */
	//选择用户：用户ID以逗号隔开
	public static final String  SUBSCRIBER_SELE="sele";
	//选择部门签收岗：部门ID以逗号隔开
	public static final String  SUBSCRIBER_ORG="org";
	//通过类进行自动计算：true/false
	public static final String  SUBSCRIBER_CALC="calc";
	/**
	 * @param user 当前用户
	 * @param ftu   当前任务
	 * @param json 选择值
	 * @param id  业务对像ID
	 * @return
	 */
	public List<IUser> selector(IUser user,FfTaskUser ftu,JSONObject json,String id);
}

/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.srv.msg;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.css.mgr.admin.dao.pojo.SMsg;
import com.css.mgr.base.dao.pojo.IUser;

import net.sf.json.JSONObject;

/**
 * @author wangtianwen
 * 2018年3月7日
 */
public interface IMsgService {
	/**
	 * 发送消息
	 * @param title
	 * @param msg
	 * @param user
	 * @return
	 */
	public boolean send(String title,String msg,IUser user);
	/**
	 * 系统消息
	 * @param msg
	 * @param user
	 * @return
	 */
	public boolean sysMsg(String msg,IUser user);
	
	public Page<SMsg> findAll(JSONObject paramJson,Pageable pageable);
	
	//条件、分页查询
	public Page<SMsg> listSerach(JSONObject paramJson,String searchValue,Pageable pageable);
	
	public JSONObject addMsg(SMsg item);
	public JSONObject delMsg(String uuids);
	public JSONObject getMsg(String uuid);
	public JSONObject updMsgStatus(String uuid);
	public List<SMsg> findMsgByUserId(String userId);
}

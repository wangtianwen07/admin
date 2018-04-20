/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.srv.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.css.mgr.base.dao.pojo.SPost;
import com.css.mgr.base.dao.pojo.SRole;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author wangtianwen
 * 2018年1月22日
 */
public interface IPostService {

	public Page<SPost> findAll(JSONObject paramJson,Pageable pageable);
	
	//条件、分页查询
	public Page<SPost> listSerach(JSONObject paramJson,String searchValue,Pageable pageable);
	
	public SPost save(SPost sPost);
	
	public SPost getSPostByUuid(String uuid);
	
	public void delByUuid(String uuid);
	
	//根据岗位uuid得到该岗位下的角色
	public JSONArray findRelatedRole(String postId);
	
	public List<SRole> findAllRole();
	
	public void bindRole(String postUuid,String roleUuid);
	
	public void cancelRole(String sPostRoleUuid);
}

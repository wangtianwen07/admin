/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.srv.menu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.css.mgr.base.dao.pojo.SMenu;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author wangtianwen 2018年2月2日
 */
public interface IMenuService {

	public String getTreeHtml();

	public Page<SMenu> findAll(JSONObject paramJson, Pageable pageable);

	public Page<SMenu> listSerach(JSONObject paramJson, String searchValue, Pageable pageable);

	public SMenu save(SMenu sMenu);
	
	public void del(String uuid);
	
	public SMenu get(String uuid);
	
	public JSONArray getFuncTree();
	
	public String getSysIdByFuncId(String funcId);
}

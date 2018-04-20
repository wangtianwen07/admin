/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.srv.func;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.css.mgr.base.dao.pojo.SFunc;
import com.css.mgr.base.dao.pojo.SResource;

import net.sf.json.JSONObject;

/**
 * @author wangtianwen
 * 2018年1月30日
 */
public interface IFuncService {

	 /**
     * 获取树的HTML
     * @return
     */
    public String getTreeHtml(String sysId);	
    
    public Page<SFunc> findAll(JSONObject paramJson,Pageable pageable);
	
	//条件、分页查询
	public Page<SFunc> listSerach(JSONObject paramJson,String searchValue,Pageable pageable);
	
	public SFunc save(SFunc sFunc);
	
	public void delByUuid(String uuid);
	
	public SFunc getSFuncByUuid(String uuid);
	
	public List<SResource> findResByFuncId(String funcId);
	
	public void delRes(String resUuid);
	
	public SResource getRes(String resUuid);
	
	public SResource saveRes(SResource sRes);
	
	public boolean exists(String uuid);
	
	public boolean ResExists(String resUuid);
}

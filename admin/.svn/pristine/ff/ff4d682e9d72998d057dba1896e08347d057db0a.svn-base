/**
 * Date:2018年1月17日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.srv.unit;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.css.mgr.base.dao.pojo.SUnit;
import com.css.mgr.base.dao.pojo.SUser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Date:2018年1月17日
 * Description:
 * Author:QinMing
 */
public interface IUnitService {
    
    public String getTreeHtml();
    
    public JSONArray getUnitTree();
    
    public Page<SUnit> findAll(JSONObject paramJson, Pageable pageable);

	public Page<SUnit> listSerach(JSONObject paramJson, String searchValue, Pageable pageable);
	
	public SUnit save(SUnit sUnit);
	
	public void del(String uuid);
	
	public SUnit get(String uuid);

	public JSONArray findRelatedMgr(String unitId,String mgrName);
	
	public void cancelMgr(String sUnitUserUuid);
	
	public List<SUser> findAllMgr(String loginName,String realName);
	
	public void bindMgr(String sUserId,String sUnitId);
}

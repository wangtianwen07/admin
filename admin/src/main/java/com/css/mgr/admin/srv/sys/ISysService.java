/**
 * Date:2018年1月17日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.srv.sys;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.css.mgr.base.dao.pojo.SSys;

import net.sf.json.JSONObject;

/**
 * Date:2018年1月23日
 * Description:
 * Author:hjw
 */

public interface ISysService {

    //查询所有  
    public Page<SSys> findAll(JSONObject paramJson,Pageable pageable);
	
	//分页查询
	public Page<SSys> listSerach(SSys sSys,Pageable pageable);
	
	//条件、分页查询
	public Page<SSys> listSerach(JSONObject paramJson,String searchValue,Pageable pageable);
	
	//根据id删除sapp数据
	void delById(String uuid);
	
	//保存
    public SSys save(SSys sSys);
	
    //获取对象信息
	public SSys getSAppById(String uuid);

	public List<SSys> findByOpenFlag(String openFlag);
	
	public List<SSys> findAll();
	
}

/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.css.mgr.base.dao.pojo.SFunc;

/**
 * @author wangtianwen
 * 2018年1月31日
 */
public interface SFuncDao extends JpaRepository<SFunc, String>,JpaSpecificationExecutor<SFunc> {

	public List<SFunc> findByParentId(String parentId);
	public List<SFunc> findByParentIdAndSysId(String parentId,String sysId);
	
	@Query("select f.uuid as id,f.parentId as pId,f.name as name from SFunc f order by f.parentId ASC")
	public List<Object[]> findByOrderByParentIdAsc();
}

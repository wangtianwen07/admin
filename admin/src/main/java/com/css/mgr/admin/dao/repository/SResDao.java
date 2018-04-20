/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.mgr.base.dao.pojo.SResource;

/**
 * @author wangtianwen
 * 2018年1月22日
 */
public interface SResDao extends JpaRepository<SResource, String>,JpaSpecificationExecutor<SResource> {

	public List<SResource> findByFuncId(String funcId);
	
	public void deleteByFuncId(String funcId);
}

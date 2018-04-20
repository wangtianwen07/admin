/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.mgr.base.dao.pojo.SMenu;

/**
 * @author wangtianwen
 * 2018年2月2日
 */
public interface SMenuDao extends JpaRepository<SMenu, String>,JpaSpecificationExecutor<SMenu>{

	public List<SMenu> findByParentId(String parentId);
}

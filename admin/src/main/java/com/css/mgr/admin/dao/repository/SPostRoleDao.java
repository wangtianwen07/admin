/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.mgr.base.dao.pojo.SPostRole;

/**
 * @author wangtianwen
 * 2018年1月29日
 */
public interface SPostRoleDao extends JpaRepository<SPostRole, String>,JpaSpecificationExecutor<SPostRole> {
	public List<SPostRole> findSPostRoleByPostId(String postId);
	public SPostRole findByPostIdAndRoleId(String postId,String roleId);
}

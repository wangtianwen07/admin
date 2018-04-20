package com.css.mgr.admin.dao.repository;

import com.css.mgr.base.dao.pojo.SOrgPost;
import com.css.mgr.base.dao.pojo.SRoleResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * litao
 */
public interface SRoleResDao extends JpaRepository<SRoleResource, String>,JpaSpecificationExecutor<SRoleResource> {

	@Query("select resourceId from SRoleResource where roleId=?1")
	List<String> getResourceIdsByRoleId(String roleId);



	void deleteByRoleIdAndResourceId(String roleId, String resourceId);
}

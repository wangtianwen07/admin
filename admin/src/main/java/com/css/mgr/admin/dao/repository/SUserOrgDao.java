package com.css.mgr.admin.dao.repository;

import com.css.mgr.base.dao.pojo.SUserOrg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * litao
 */
public interface SUserOrgDao extends JpaRepository<SUserOrg, String>,JpaSpecificationExecutor<SUserOrg> {

	@Query("select userId from SUserOrg where orgId=?1")
	List<String> getUserIdsByOrgId(String orgId);

	@Query("from SUserOrg where userId=?1")
	public List<SUserOrg> findSUserOrgByuserId(String userId);

	void deleteByOrgIdAndUserId(String orgId, String userId);
}

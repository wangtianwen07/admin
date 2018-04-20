package com.css.mgr.admin.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.css.mgr.base.dao.pojo.SUnit;

public interface SUnitDao extends JpaRepository<SUnit, String>,JpaSpecificationExecutor<SUnit> {
	public List<SUnit> findByParentId(String parentId);
	
	@Query("select f.uuid as id,f.parentId as pId,f.unitName as name from SUnit f order by f.parentId ASC")
	public List<Object[]> findByOrderByParentIdAsc();
	
	@Query("from SUnit u where u.parentId = ?1")
	public List<SUnit> getChildren(String parentId);
}

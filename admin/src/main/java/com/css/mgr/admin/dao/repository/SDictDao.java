package com.css.mgr.admin.dao.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.css.mgr.base.dao.pojo.SDict;

public interface SDictDao extends JpaRepository<SDict, String>,JpaSpecificationExecutor<SDict> {
	
	List<SDict> findByTableName(String tableName);
    
	@Query(value="select name from SDict u where tableName=?1 and code =?2")
	String findByTableNameAndCode(String tableName,String code);
	
	
	List<SDict> findByParentId(String ParentId);
	
	@Query(value=" from SDict u where u.code=:code")
	public List<SDict> findSDictAllByCode(@Param("code") String code);
	
}

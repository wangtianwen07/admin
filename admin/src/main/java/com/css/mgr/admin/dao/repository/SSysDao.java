package com.css.mgr.admin.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.mgr.base.dao.pojo.SSys;

public interface SSysDao extends JpaRepository<SSys, String>,JpaSpecificationExecutor<SSys>{

	public List<SSys> findByOpenFlag(String openFlag);
}

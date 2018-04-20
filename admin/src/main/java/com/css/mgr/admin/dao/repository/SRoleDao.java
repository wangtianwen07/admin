package com.css.mgr.admin.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.mgr.base.dao.pojo.SRole;

public interface SRoleDao extends JpaRepository<SRole, String>,JpaSpecificationExecutor<SRole> {
}

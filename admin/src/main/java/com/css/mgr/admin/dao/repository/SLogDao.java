package com.css.mgr.admin.dao.repository;

import com.css.mgr.base.dao.pojo.SLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SLogDao extends JpaRepository<SLog, String>,JpaSpecificationExecutor<SLog> {
}

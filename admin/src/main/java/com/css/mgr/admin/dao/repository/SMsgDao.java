/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.mgr.admin.dao.pojo.SMsg;

/**
 * 
 * @author wangtianwen
 * 2018年3月2日
 */
public interface SMsgDao extends JpaRepository<SMsg, String>,JpaSpecificationExecutor<SMsg> {

	public List<SMsg> findByUserId(String userId);
}

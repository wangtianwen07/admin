package com.css.mgr.admin.dao.repository;

import com.css.mgr.base.dao.pojo.SOrg;
import com.css.mgr.base.dao.pojo.SOrgPost;
import com.css.mgr.base.dao.pojo.SUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
/**
 * litao
 */
public interface SOrgDao extends JpaRepository<SOrg, String>,JpaSpecificationExecutor<SOrg> {
	List<SOrg> findByParentIdAndDelFlag(String ParentId,String DelFlag);
}

package com.css.mgr.admin.dao.repository;

import com.css.mgr.base.dao.pojo.SOrg;
import com.css.mgr.base.dao.pojo.SOrgPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * litao
 */
public interface SOrgPostDao extends JpaRepository<SOrgPost, String>,JpaSpecificationExecutor<SOrgPost> {

	@Query("select postId from SOrgPost where orgId=?1")
	List<String> getPostIdsByOrgId(String orgId);



	void deleteByOrgIdAndPostId(String orgId,String postId);
}

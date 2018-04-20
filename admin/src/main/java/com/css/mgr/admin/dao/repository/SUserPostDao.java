package com.css.mgr.admin.dao.repository;


import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import com.css.mgr.base.dao.pojo.SUserPost;

public interface SUserPostDao extends JpaRepository<SUserPost, String>,JpaSpecificationExecutor<SUserPost>{
	@Query("select distinct postId from SUserPost where userId=?1")
	public List<String> findSUserPostByUserId(String userId);
	
	@Query("from SUserPost where postId=?1")
	public List<SUserPost> findSUserPostBypostId(String postId);
	
	public List<SUserPost> findSUserPostsByUserId(String userId);
	
	public SUserPost findByPostIdAndUserId(String postId,String userId);
	
	void deleteByUserIdAndPostId(String userId,String postId);
}

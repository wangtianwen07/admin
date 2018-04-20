package com.css.mgr.admin.dao.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.css.mgr.base.dao.pojo.SUser;

public interface SUserDao extends JpaRepository<SUser, String>,JpaSpecificationExecutor<SUser> {
	public List<SUser> findByLoginName(String loginName);
	public Page<SUser> findByLoginName(String loginName, Pageable pageable);
	@Query(value="select a.uuid,a.phone,a.realName,a.loginName,a.activeStatus,a.sex,a.userType,a.openFlag,a.orderNum,a.secLevel,b.name as orgName,d.name as postName,b.uuid,d.uuid " + 
		     " from SUser a, SOrg b ,SUserPost c ,SPost d ,SUserOrg e"
		     + " where  e.userId=a.uuid and b.uuid=e.orgId and c.userId=a.uuid and d.uuid=c.postId and b.uuid= ?1")
	public Page<Object[]> findAllByOrgId(String orgId,Pageable pageable);
	
	@Query(value="SELECT u.uuid as uuid,u.phone as phone,u.realName as realName,u.loginName as loginName,u.activeStatus as activeStatus,u.sex as sex,u.userType as userType,u.openFlag as openFlag,u.orderNum as orderNum,u.secLevel as secLevel,o.name as orgName "
			+ "FROM SUser u, SOrg o,SUserOrg uo WHERE "
			+ "uo.userId = u.uuid and o.uuid = uo.orgId AND o.uuid= ?1 AND u.realName like ?2 AND u.activeStatus like ?3 AND u.openFlag like ?4"
			+ " group by u.uuid")
	public Page<Map<String,String>> findAllByOrgIdMap(String orgId,String realName,String activeStatus,String openFlag,Pageable pageable);
	
	@Query("select DISTINCT p.name from SPost p,SUserPost up "
			+ " where p.uuid = up.postId and up.userId = ?1 ")
	public List<String> getPostsByUserId(String userId);
	
	public SUser findByUuid(String uuid);

	public List<SUser> findByUserType(String userType);
	
	@Query(value="select u.loginName from SUser u where u.loginName=?1 ")
	public String checkLoginName(String loginName);

	@Query(value="select u.mobile from SUser u where  u.mobile=?1 ")
	public String checkMobile(String mobile);
	
	@Query("from SUser u where exists(select up.uuid from SUserPost up,SUserOrg uo where up.userId=uo.userId and uo.userId=u.uuid and up.postId =?1 and uo.orgId =?2)")
	public List<SUser> getUserByPostAndOrg(String postId,String orgId);
}

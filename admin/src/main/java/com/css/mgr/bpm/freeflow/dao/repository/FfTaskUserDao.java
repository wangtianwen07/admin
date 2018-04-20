/**
 * Date:2018年2月23日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.bpm.freeflow.dao.repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;

/**
 * Date:2018年2月23日 Description: Author:QinMing
 */
public interface FfTaskUserDao extends JpaRepository<FfTaskUser, String>, JpaSpecificationExecutor<FfTaskUser> {
	@Modifying
	@Query("delete from FfTaskUser f where f.userProcess=?1 and f.handlerState=?2 and f.ffTaskUuid=?3 and f.ffTaskUserUuid!=?4")
	public void deleteUserProcess(String userProcess, String handlerState, String ffTaskUuid, String ffTaskUserUuid);

	@Modifying
	@Query("delete from FfTaskUser f where f.parentUserProcess=?1 and f.handlerState=?2 and f.ffTaskUuid=?3 and f.ffTaskUserUuid!=?4")
	public void deleteParentUserProcess(String parentUserProcess, String handlerState, String ffTaskUuid,
			String ffTaskUserUuid);

	@Query("from FfTaskUser f where f.userProcess=?1 and f.handlerState=?2 and f.ffTaskUuid=?3 and f.ffTaskUserUuid!=?4 order by f.userOrder asc")
	public List<FfTaskUser> findWakeUpProcess(String userProcess, String handlerState, String ffTaskUuid,
			String ffTaskUserUuid);

	@Query("select count(f.userProcess) from FfTaskUser f where f.userProcess=?1 and f.handlerState=?2 and f.ffTaskUuid=?3")
	public Integer countByUserProcess(String userProcess, String handlerState, String ffTaskUuid);

	@Query("select count(f.userProcess) from FfTaskUser f where f.prevUserProcess=?1 and f.handlerState=?2 and f.ffTaskUuid=?3")
	public Integer countByPrevUserProcess(String prevUserProcess, String handlerState, String ffTaskUuid);

	@Query("from FfTaskUser p where p.handlerState=?1 and p.ffTaskUuid in ?2 order by p.ffTaskUuid asc,p.createTime desc")
	public List<FfTaskUser> findByHandlerStateAndFfTaskUuidIn(String handlerState, Collection<String> ffTaskUuid);
	
	@Query("from FfTaskUser p where p.handlerState=?1 and p.ffTaskUuid = ?2 order by p.ffTaskUuid asc,p.createTime desc")
	public List<FfTaskUser> findByHandlerStateAndFfTaskUuid(String handlerState,String ffTaskUuid);
	
	@Query("from FfTaskUser where ffTaskUuid =?1 and endTime is not null and handlerState=?2 order by endTime desc")
	public List<FfTaskUser> findByHandlerStateAndFfTaskUuidAndEndTime(String ffTaskUuid,String handlerState);
	
	public List<FfTaskUser> findByUserProcessAndFfTaskUuid(String userProcess, String ffTaskUuid);
	
	@Query("from FfTaskUser p where p.handlerState=?1 and p.autoPass in ?2 and p.limitedTime < ?3")
	public List<FfTaskUser> findByHandlerStateAndAutoPassAndLimitedTime(String handlerState,String autoPass,Date limitedTime);
}

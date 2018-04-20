/**
 * Date:2018年2月23日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.bpm.freeflow.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.mgr.bpm.freeflow.dao.pojo.FfTask;

/**
 * Date:2018年2月23日
 * Description:
 * Author:QinMing
 */
public interface FfTaskDao  extends JpaRepository<FfTask, String>,JpaSpecificationExecutor<FfTask>{
	public List<FfTask> findByBusiObjAndBusiObjUuid(String busiObj,String busiObjUuid);
	public List<FfTask> findByMainTaskUuidAndTaskProcess(String mainTaskUuid,String taskProcess);
}

/**
 * Date:2018年2月23日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.bpm.freeflow.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.css.mgr.bpm.freeflow.dao.pojo.FfDef;

/**
 * Date:2018年2月23日
 * Description:
 * Author:QinMing
 */
public interface FfDefDao extends JpaRepository<FfDef, String>,JpaSpecificationExecutor<FfDef>{
}

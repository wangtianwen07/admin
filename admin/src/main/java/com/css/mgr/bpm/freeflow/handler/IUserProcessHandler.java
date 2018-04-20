package com.css.mgr.bpm.freeflow.handler;

import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;

/**
 * @author pc
 *
 */
public interface IUserProcessHandler {

	/**
	 * @param ftu 待办任务
	 * @param title 标题
	 * @param id  实体ID
	 * @return  true:代表有下一环节，false代表流程结束
	 */
	public Nextor excutor(FfTaskUser ftu,String title,String id)throws Exception;
}

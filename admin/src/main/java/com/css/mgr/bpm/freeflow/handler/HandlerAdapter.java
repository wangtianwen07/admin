package com.css.mgr.bpm.freeflow.handler;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.css.mgr.admin.srv.SpringService;
import com.css.mgr.bpm.freeflow.dao.pojo.FfDef;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTask;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;
import com.css.mgr.bpm.freeflow.dao.repository.FfTaskDao;
import com.css.mgr.bpm.freeflow.dao.repository.FfTaskUserDao;
import com.css.mgr.bpm.freeflow.exception.FreeFlowException;
import com.css.mgr.bpm.freeflow.srv.FlowDefService;

/****
 * { sele:afes22423,afes22423,afes22423 org:afes22423,afes22423,afes22423
 * calc:true }
 * 
 */
@Component
@Transactional
public class HandlerAdapter {
	@Autowired
	private SpringService springService;
	@Autowired
	private FfTaskUserDao ffTaskUserDao;
	@Autowired
	private FfTaskDao ffTaskDao;
	@Autowired
	private FlowDefService flowDefService;
	public Nextor run(String ffTaskUserId) {
		FfTaskUser ftu = ffTaskUserDao.findOne(ffTaskUserId);
		FfTask ft = ffTaskDao.findOne(ftu.getFfTaskUuid());
		String id = ft.getBusiObjUuid();
		String title = ft.getTaskName();
		FfDef df = flowDefService.getFlowDef(ftu.getFfTaskUuid());
		if (StringUtils.isEmpty(df.getHandler())) {
			return new Nextor();
		}
		try {
			IUserProcessHandler handler = (IUserProcessHandler)springService.getBean(df.getHandler());
			return handler.excutor(ftu, title, id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FreeFlowException("错误的用户环节[" + df.getFfDefUuid() + "]处理器[" + df.getHandler() + "]");
		}
	}

}

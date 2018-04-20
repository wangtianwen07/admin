package com.css.mgr.bpm.freeflow;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.css.mgr.base.dao.pojo.IEntity;
import com.css.mgr.base.dao.pojo.IUser;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;
import com.css.mgr.bpm.freeflow.dao.pojo.TaskParam;
import com.css.mgr.bpm.freeflow.srv.TaskService;
import com.css.mgr.bpm.freeflow.srv.TaskUserService;
import com.css.mgr.bpm.freeflow.subscriber.SubscriberAdapter;

import net.sf.json.JSONObject;

@Component
@Transactional
public class FfTaskManager{
	/**
	 *
	 * 一、开启流程但不发送。并持久化
	 * 当前用户生成待办任务。
	 * @param entity  业务实体类
	 * @param param  任务扩展参数
	 * @param user 当前用户
	 * @param tc 事务
	 * @param defid  流程定义编码
	 * @return
	 */
	@Autowired
	private SubscriberAdapter subscriberAdapter;
	@Autowired
	private TaskService taskService;
	@Autowired
	private TaskUserService taskUserService;
	
	public boolean start(IEntity entity,TaskParam param,IUser user,String defid){
		taskService.start(entity, param, user, defid);
		return true;
	}
	/**
	 *
	 * 一、开启流程并发送。并持久化
	 * 当前用户生成同时送出任务。
	 * @param entity  业务实体类
	 * @param opinion  审批意见,可为null
	 * @param handleStateName  环节处理名称,可为null
	 * @param limitedTime 限办时间,可为null
	 * @param emergencyLevel 紧急程度 ,可为null
	 * @param user 当前用户
	 * @param tc 事务
	 * @param touser 下一环节审批人
	 * @param defid  流程定义编码
	 * @return
	 */
	public boolean startAndPass(IEntity entity,TaskParam param,IUser user,List<IUser> touser,String defid){
		FfTaskUser tfu=taskService.start(entity,  param, user, defid);
		//审批通过
		taskUserService.pass(tfu.getFfTaskUserUuid(), user, touser, param);
		return true;
	}
	/**
	 * @param entity  业务实体类
	 * @param opinion  审批意见,可为null
	 * @param handleStateName  环节处理名称,可为null
	 * @param limitedTime 限办时间,可为null
	 * @param emergencyLevel 紧急程度 ,可为null
	 * @param user 当前用户
	 * @param tc 事务
	 * @param touser 下一环节审批人={
	 *  sele:'afes22423,afes22423,afes22423',//固定选择人
	 *  sele:'afes22423,afes22423,afes22423',//固定部门签收岗
	 *  calc:true //自动计算通过UserSubscriber获取
	 * }
	 * @param defid  流程定义编码
	 * @return
	 */
	public boolean startAndPass(IEntity entity,TaskParam param,IUser user,JSONObject touser,String defid){
		FfTaskUser tfu=taskService.start(entity, param, user, defid);
		//
		List<IUser> users=subscriberAdapter.subscriber(user,tfu,touser,entity.getEntityId());
		//审批通过
		taskUserService.pass(tfu.getFfTaskUserUuid(), user, users,param);
		return true;
	}
}

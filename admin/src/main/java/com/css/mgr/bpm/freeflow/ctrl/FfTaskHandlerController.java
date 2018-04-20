package com.css.mgr.bpm.freeflow.ctrl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.css.mgr.admin.common.JsonUtil;
import com.css.mgr.admin.common.StringUtil;
import com.css.mgr.base.CssController;
import com.css.mgr.base.dao.pojo.IUser;
import com.css.mgr.bpm.freeflow.dao.pojo.CompletionStrategy;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTask;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;
import com.css.mgr.bpm.freeflow.dao.pojo.TaskParam;
import com.css.mgr.bpm.freeflow.exception.NoUserException;
import com.css.mgr.bpm.freeflow.srv.TaskService;
import com.css.mgr.bpm.freeflow.srv.TaskUserService;
import com.css.mgr.bpm.freeflow.subscriber.SubscriberAdapter;
import com.css.mgr.bpm.freeflow.utils.DateUtils;

import net.sf.json.JSONObject;
@Controller
@RequestMapping("/taskhandler")   
public class FfTaskHandlerController extends CssController{
	@Autowired
	private TaskUserService taskUserService;
	@Autowired
	private TaskService taskService;
	@Autowired
	private SubscriberAdapter subscriberAdapter;

	/**
	 *流程审批通过 
	 * 
	 * @param opinion 审批意见
	 * @param limitedTime 限时审批
	 * @param ffTaskUuid 流程ID
	 * @param userProcessName 下一节点名称
	 * @param ffTaskUserUuid 用户任务 
	 * @param completionStrategy 完成策略,环节处理方式默认互不干涉
	 * @param touser 下一岗位用户
	 * @return
	 *0:正常 
	 *1:错误
	 *2:需要选人 
	 */
	@RequestMapping(value = "/pass")
	private String pass(String opinion,String limitedTime,String ffTaskUuid,
			String userProcessName,String ffTaskUserUuid,
			String completionStrategy, String touser){
		
		if(StringUtil.isEmpty(completionStrategy)){
			completionStrategy=CompletionStrategy.COMPLETION_STRATEGY_ALONE;
		}
		
		IUser user=getUser();//当前用户
		Date lmt=DateUtils.toDate(limitedTime);
		JSONObject jo=JSONObject.fromObject(touser);
		FfTaskUser tfu=taskUserService.get(ffTaskUserUuid);
		FfTask tf=taskService.get(ffTaskUuid);
		if(tf==null){
			tf=taskService.get(tfu.getFfTaskUuid());
		}
		//
		List<IUser> users=subscriberAdapter.subscriber(user,tfu,jo,tf.getBusiObjUuid());
		//
		TaskParam param=new TaskParam(opinion);
		param.setCompletionStrategy(completionStrategy);
		param.setUserProcessName(userProcessName);
		param.setLimitedTime(lmt);
		//
		try {
			taskUserService.pass(ffTaskUserUuid, user, users, param);
			return JsonUtil.success("审批通过 成功!",null).toString();
		} catch (NoUserException e) {
			e.printStackTrace();
			return JsonUtil.toJson("3","审批通过 成功!",e.getMessage()).toString();
		}catch (Exception e) {
			e.printStackTrace();
			return JsonUtil.error("审批失败!").toString();
		}                                                                                                                                
	}
	/**
	 * 
	 *流程退回 
	 * 
	 */
	@RequestMapping(value = "/reback")
	private String reback(String ffTaskUserUuid,String opinion){
		IUser user=getUser();//当前用户
		taskUserService.reback(ffTaskUserUuid, opinion, user);
		System.out.println(JsonUtil.success("退回 成功!",null).toString());
		return JsonUtil.success("退回 成功!",null).toString();
	}
	/**
	 * 
	 *流程取回 
	 * 
	 */
	@RequestMapping(value = "/retrieve")
	@ResponseBody
	private String retrieve(String ffTaskUserUuid,String opinion){
		IUser user=getUser();//当前用户
		taskUserService.retrieve(ffTaskUserUuid, opinion, user);
		return JsonUtil.success("取回成功!",null).toString();
	}
	/**
	 * 
	 *流程结束 
	 * 
	 */
	@RequestMapping(value = "/end")
	private String end(String ffTaskUserUuid,String opinion){
		IUser user=getUser();//当前用户
		
		taskService.end(ffTaskUserUuid, opinion, user);
		
		return JsonUtil.success("流程结束 !",null).toString();
	}
	/**
	 * 
	 *激活流程 
	 * 
	 */
	@RequestMapping(value = "/active")
	private String active(String ffTaskUserUuid){
	
		IUser user=getUser();//当前用户
		taskService.activation(ffTaskUserUuid, user);
		return JsonUtil.success("激活流程 !",null).toString();
	}
	
	/**
	 * 
	 * 获取请求的url
	 * 
	 */
	@RequestMapping(value = "/geturl")
	private String getByUrl(String ffTaskUserUuid){
		FfTaskUser tfu=taskUserService.get(ffTaskUserUuid);
		FfTask tf=taskService.get(tfu.getFfTaskUuid());
		String url=tf.getTaskUrl();
		return url;
	}
	
	
}

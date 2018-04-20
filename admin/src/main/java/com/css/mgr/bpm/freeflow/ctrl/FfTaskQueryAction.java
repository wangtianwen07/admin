package com.css.mgr.bpm.freeflow.ctrl;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.css.mgr.admin.common.JsonUtil;
import com.css.mgr.base.CssController;
import com.css.mgr.base.dao.pojo.SUser;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTask;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;
import com.css.mgr.bpm.freeflow.srv.TaskService;
import com.css.mgr.bpm.freeflow.srv.TaskUserService;
import com.css.mgr.bpm.freeflow.utils.DateUtils;

import net.sf.json.JSONObject;

/**
 * @author pc
 *
 */
@Controller
@RequestMapping("/taskquery")
public class FfTaskQueryAction extends CssController{
	@Autowired
	private TaskService taskService;
	@Autowired
	private TaskUserService taskUserService;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@RequestMapping(value="/dir",method=RequestMethod.GET)
	public String dir(Model model){
		logger.info(">>>>>>user:"+getUser().getRealName());
		return "freeflow/task/dirTasking";
	}
	
	@RequestMapping(value="/completedir",method=RequestMethod.GET)
	public String completedir(Model model){
		logger.info(">>>>>>user:"+getUser().getRealName());
		return "freeflow/task/dirTaskingcompete";
	}
	
	/**
	 * 查看待办
	 * @param tus
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/process",method=RequestMethod.GET)
	public String getProcessTaskUser(){
//		if(StringUtils.isNotEmpty(ffDefUuid)){
//			qm.setHql(" and t.ffDefUuid=:ffDefUuid ");
//			qm.addParam("ffDefUuid",ffDefUuid);
//		}
		SUser sUser=getUser();
		System.out.println(sUser.getUuid());
		List<FfTaskUser> ls=taskUserService.getProcessTaskUserQc(sUser);
		for(FfTaskUser u:ls){
			u.setTask(taskService.get(u.getFfTaskUuid()));
			u.setCreateTimeStr(DateUtils.toString(u.getCreateTime()));
		}
		JSONObject jo=JsonUtil.success("成功");
		jo.put("data", JsonUtil.toJson(ls));
		return jo.toString();
	}
	/**
	 * 查看已办
	 * @param tus
	 * @return
	 */
	@RequestMapping(value = "/complete",method=RequestMethod.GET)
	@ResponseBody
	public String getCompleteTaskUser(HttpServletRequest request){
		@SuppressWarnings("unused")
		String taskName=request.getParameter("name");
//		if(StringUtils.isNotEmpty(ffDefUuid)){
//			qm.setHql(" and t.ffDefUuid=:ffDefUuid ");
//			qm.addParam("ffDefUuid",ffDefUuid);
//		}
		SUser sUser=getUser();
		List<FfTaskUser> ls=taskUserService.getCompleteTaskUserQc(sUser);
		for(FfTaskUser u:ls){
				FfTask ffTask = taskService.get(u.getFfTaskUuid());
				u.setTask(ffTask);
			u.setEndTimeStr(DateUtils.toString(u.getEndTime()));
		}
		JSONObject jo=JsonUtil.success("成功");
		jo.put("data", JsonUtil.toJson(ls));
		return jo.toString();
	}
	
	
	@RequestMapping(value="/opiniondir",method=RequestMethod.GET)
	public String opiniondir(Model model){
		logger.info(">>>>>>user:"+getUser().getRealName());
		
		return "freeflow/task/dirTaskingcompete";
	}
	
	
	/**
	 * 查看处理意见
	 * @param ts
	 * @param tus
	 * @return
	 */
	@RequestMapping(value = "/opinion")
	@ResponseBody
	public String getOpinionByTaskUser(String ffTaskUserUuid,String ffTaskUuid){
		FfTaskUser tfu=taskUserService.get(ffTaskUserUuid);
		FfTask tf=taskService.get(ffTaskUuid);
		if(tf==null){
			tf=taskService.get(tfu.getFfTaskUuid());
		}
		//主任务意 见
		List<FfTaskUser> manOpinion=taskUserService.getOpinionByTaskUser(tfu);
		for(FfTaskUser u:manOpinion){
			u.setEndTimeStr(DateUtils.toString(u.getEndTime()));
		}
		//子任务意见
		List<FfTaskUser> subOpinion=taskService.getSubOpinionByTaskUser(tf);
		for(FfTaskUser u:subOpinion){
			u.setEndTimeStr(DateUtils.toString(u.getEndTime()));
		}
		JSONObject jo=JsonUtil.success("成功");
		jo.put("manOpinion", JsonUtil.toJson(manOpinion));
		System.out.println(manOpinion.toString());
		jo.put("subOpinion", JsonUtil.toJson(subOpinion));
		return jo.toString();
	}
	/**
	 * 查看当前处理人
	 * @param ts
	 * @param tus
	 * @return
	 */
	@RequestMapping(value = "/currUser")
	@ResponseBody
	public String getCurrUserByTaskUser(String ffTaskUserUuid,String ffTaskUuid,HttpServletRequest request){
		FfTaskUser tfu=taskUserService.get(ffTaskUserUuid);
		FfTask tf=taskService.get(ffTaskUuid);
		if(tf==null){
			tf=taskService.get(tfu.getFfTaskUuid());
		}
		//主任务意 见
		List<FfTaskUser> manOpinion=taskUserService.getCurrUserByTaskUser(tfu);
		for(FfTaskUser u:manOpinion){
			u.setCreateTimeStr(DateUtils.toString(u.getCreateTime()));
		}
		//子任务意见
		List<FfTaskUser> subOpinion=taskService.getSubCurrUserByTaskUser(tf);
		for(FfTaskUser u:subOpinion){
			u.setCreateTimeStr(DateUtils.toString(u.getCreateTime()));
		}
		JSONObject jo=JsonUtil.success("成功");
		jo.put("manOpinion", JsonUtil.toJson(manOpinion));
		jo.put("subOpinion", JsonUtil.toJson(subOpinion));
		System.out.println(jo.toString());
		return jo.toString();
	}

}

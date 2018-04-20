/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.freeflow;

import java.util.Date;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import com.css.App;
import com.css.mgr.admin.common.JsonUtil;
import com.css.mgr.admin.common.StringUtil;
import com.css.mgr.admin.srv.user.IUserService;
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

/**
 * @author hujianwen 2018年2月5日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class) // 指定spring-boot的启动类
public class FreeFlowTest {
	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(FreeFlowTest.class);
	@Autowired
	private TaskUserService taskUserService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private SubscriberAdapter subscriberAdapter;
	
	@Autowired
	private IUserService userService;
	
	@Test
	public void passToTest(){
		String opinion="你好";
	    String limitedTime=null;
	    String ffTaskUuid="111f8c916fc843ff9e53c5d8ddeb6525";
		String userProcessName="李四";
		String ffTaskUserUuid="001543f62e404a5bb1ec9ff5370bb1f7";
		String completionStrategy="10";
		String touser="{calc:\"true\"}";
			if(StringUtil.isEmpty(completionStrategy)){
				completionStrategy=CompletionStrategy.COMPLETION_STRATEGY_ALONE;
			}
			IUser user=userService.getSuserById("css");//当前用户
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
				System.out.println("审批通过 成功!");
			} catch (NoUserException e) {
				e.printStackTrace();
				System.out.println("审批通过 成功!");
			}catch (Exception e) {
				e.printStackTrace();
				System.out.println("审批失败");
			}                                                                                                                                
		}
	@Test
	public void getBackTest(){
		String ffTaskUserUuid="001543f62e404a5bb1ec9ff5370bb1f7";
				String opinion="aaa";
		IUser user=userService.getSuserById("css");//当前用户
			taskUserService.reback(ffTaskUserUuid, opinion, user);
			System.out.println(JsonUtil.success("退回 成功!",null).toString());
			System.out.println("成功");
	}
	@Test
	public void getretrieve(){
		String opinion=null;
		String ffTaskUserUuid="001543f62e404a5bb1ec9ff5370bb1f7";
		IUser user=userService.getSuserById("css");//当前用户
		taskUserService.retrieve(ffTaskUserUuid, opinion, user);
		System.out.println("成功");
	}
	
	@Test
	public void getactive() {
		IUser user=userService.getSuserById("css");//当前用户
		String ffTaskUserUuid="001543f62e404a5bb1ec9ff5370bb1f7";
		taskService.activation(ffTaskUserUuid, user);
		System.out.println("成功");
	   }
	
	
}

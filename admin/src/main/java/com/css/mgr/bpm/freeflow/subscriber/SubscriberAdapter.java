package com.css.mgr.bpm.freeflow.subscriber;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.css.mgr.admin.srv.SpringService;
import com.css.mgr.base.dao.pojo.IUser;
import com.css.mgr.bpm.freeflow.dao.pojo.FfDef;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;
import com.css.mgr.bpm.freeflow.exception.NoUserException;
import com.css.mgr.bpm.freeflow.srv.FlowDefService;

import net.sf.json.JSONObject;

/****
 *{
 *  sele:afes22423,afes22423,afes22423
 *  org:afes22423,afes22423,afes22423
 *  calc:true
 *} 
 * 
 */
@Component
@Transactional(readOnly=true)
public class SubscriberAdapter {
	@Autowired
	private FlowDefService flowDefService;
	@Autowired
	private IUserSubscriber orgUserSubscriber;
	@Autowired
	private IUserSubscriber seleUserSubscriber;
	@Autowired  
	SpringService springService;  
	public List<IUser> subscriber(IUser user, FfTaskUser ftu, JSONObject usersjson, String busiObjUuid){
		List<IUserSubscriber> subscriber=new ArrayList<IUserSubscriber>();
		if(usersjson.containsKey(IUserSubscriber.SUBSCRIBER_ORG)){
			subscriber.add(orgUserSubscriber);
		}
		if(usersjson.containsKey(IUserSubscriber.SUBSCRIBER_SELE)){
			subscriber.add(seleUserSubscriber);
		}
		if(usersjson.containsKey(IUserSubscriber.SUBSCRIBER_CALC)){
			//
			Boolean calc=usersjson.optBoolean(IUserSubscriber.SUBSCRIBER_CALC);
			if(calc){
				//自定义用户选择器
				FfDef df=flowDefService.getFlowDef(ftu.getFfTaskUuid());
				try {
					IUserSubscriber sub=(IUserSubscriber)springService.getBean(df.getSubscriber());
					subscriber.add(sub);
				} catch (Exception e) {
					e.printStackTrace();
					throw new NoUserException("错误的用户选择器");
				} 
			}
		}
		List<IUser> us=new ArrayList<IUser>();
		for(IUserSubscriber fu:subscriber){
			List<IUser> t=fu.selector(user, ftu, usersjson, busiObjUuid);
			if(t==null || t.isEmpty())continue;
			us.addAll(t);
		}
		return us;
	}
}


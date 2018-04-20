package com.css.mgr.bpm.freeflow.subscriber;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.css.mgr.admin.dao.repository.SUserDao;
import com.css.mgr.base.dao.pojo.IUser;
import com.css.mgr.base.dao.pojo.SUser;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;

import net.sf.json.JSONObject;
@Component
@Transactional(readOnly=true)
public class SeleUserSubscriber implements IUserSubscriber {
	@Autowired
	private SUserDao sUserDao;
	@Override
	public List<IUser> selector(IUser user, FfTaskUser ftu, JSONObject json, String id) {
		List<IUser> ls=new ArrayList<IUser>();
		String users=json.optString(SUBSCRIBER_SELE);
		if(StringUtils.isEmpty(users)){
			return null;
		}
		String[] u=users.split(",");
		for(String us:u){
			if(StringUtils.isNotEmpty(us)){
				SUser suser=sUserDao.findOne(us.trim());
				ls.add(suser);
			}
		}
		return ls;
	}

}

package com.css.mgr.bpm.freeflow.subscriber;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.css.mgr.base.dao.pojo.IUser;
import com.css.mgr.bpm.freeflow.adapter.IFfUserService;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTaskUser;

import net.sf.json.JSONObject;

@Component
@Transactional(readOnly=true)
public class OrgUserSubscriber implements IUserSubscriber {
	@Autowired
	private IFfUserService ffUserService;
	@Override
	public List<IUser> selector(IUser user, FfTaskUser ftu, JSONObject json, String id) {
		List<IUser> ls=new ArrayList<IUser>();
		String orgs=json.optString(SUBSCRIBER_SELE);
		if(StringUtils.isEmpty(orgs)){
			return null;
		}
		String[] og=orgs.split(",");
		for(String dp:og){
			if(StringUtils.isNotEmpty(dp)){
				List<IUser> users=ffUserService.getUserIdByOrg(ffUserService.getDefaultPost(), dp);
				ls.addAll(users);
			}
		}
		return null;
	}

}

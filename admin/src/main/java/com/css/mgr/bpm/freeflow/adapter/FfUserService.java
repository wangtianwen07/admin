/**
 * Date:2018年2月24日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.bpm.freeflow.adapter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.css.mgr.admin.dao.repository.SPostDao;
import com.css.mgr.admin.dao.repository.SUserDao;
import com.css.mgr.base.CssController;
import com.css.mgr.base.dao.pojo.IUser;
import com.css.mgr.base.dao.pojo.SPost;
import com.css.mgr.base.dao.pojo.SUser;

/**
 * Date:2018年2月24日
 * Description:
 * Author:QinMing
 */
@Service
@Transactional(readOnly=true)
public class FfUserService implements IFfUserService{
	//管理用户
	public static final String USERDATATYPE_SUSER="SUser";
	
	@Autowired
	private SUserDao sUserDao;
	private SPostDao sPostDao;
	public String getDefaultPost(){
		//默认签收岗位职位为0
		String rank="0";
		List<SPost> p=sPostDao.findByRank(rank);
		if(p==null || p.size()==0)throw new RuntimeException("未找到职位为0的岗位，请联系管理员!");
		return p.get(0).getUuid();
	}

	/**
	 * @param postid 岗位
	 * @param orgId 部门
	 * @return
	 */
	public List<IUser> getUserIdByOrg(String postId, String orgId) {
		List<SUser> us=sUserDao.getUserByPostAndOrg(postId, orgId);
		List<IUser> re=new ArrayList<IUser>(us.size());
		for(SUser s:us){
			re.add(s);
		}
		return re;
	}

	/* (non-Javadoc)
	 * @see com.css.mgr.bpm.freeflow.adapter.IFfUserService#getCurUserType(com.css.mgr.base.dao.pojo.IUser)
	 */
	@Override
	public String getCurUserType(IUser user) {
		if(user instanceof SUser){
			return USERDATATYPE_SUSER;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.css.mgr.bpm.freeflow.adapter.IFfUserService#getIUser(java.lang.String, java.lang.String)
	 */
	@Override
	public IUser getIUser(String userId, String userType) {
		if(userType.equals(USERDATATYPE_SUSER)){
			return sUserDao.findOne(userId);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.css.mgr.bpm.freeflow.adapter.IFfUserService#getCurUserType()
	 */
	@Override
	public String getCurUserType() {
		return getCurUserType(CssController.getUser());
	}

	/* (non-Javadoc)
	 * @see com.css.mgr.bpm.freeflow.adapter.IFfUserService#isSUser()
	 */
	@Override
	public boolean isSUser() {
		String type=getCurUserType();
		if(type==null)return false;
		return type.equals(USERDATATYPE_SUSER);
	}

	/* (non-Javadoc)
	 * @see com.css.mgr.bpm.freeflow.adapter.IFfUserService#isSUser(com.css.mgr.base.dao.pojo.IUser)
	 */
	@Override
	public boolean isSUser(IUser user) {
		String type=getCurUserType(user);
		if(type==null)return false;
		return type.equals(USERDATATYPE_SUSER);
	}
}

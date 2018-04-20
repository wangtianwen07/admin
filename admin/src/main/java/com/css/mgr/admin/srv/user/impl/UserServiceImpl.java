package com.css.mgr.admin.srv.user.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.css.mgr.admin.common.IDUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.css.mgr.admin.common.JsonUtil;
import com.css.mgr.admin.dao.repository.SPostDao;
import com.css.mgr.admin.dao.repository.SUserDao;
import com.css.mgr.admin.dao.repository.SUserOrgDao;
import com.css.mgr.admin.dao.repository.SUserPostDao;
import com.css.mgr.admin.srv.user.IUserService;
import com.css.mgr.base.dao.pojo.SPost;
import com.css.mgr.base.dao.pojo.SUser;
import com.css.mgr.base.dao.pojo.SUserOrg;
import com.css.mgr.base.dao.pojo.SUserPost;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Date:2018年1月31日
 * Description:
 * Author:Hjw
 */
@Service("userService")
@Transactional
public class UserServiceImpl implements IUserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Autowired
    private SUserDao suserDao;
    
    @Autowired
    SUserOrgDao sUserOrgDao;
    
    @Autowired
    SUserPostDao sUserPostDao;
    
    @Autowired
    SPostDao sPostDao;

    @Override
    public Page<SUser> findAll(JSONObject paramJson, Pageable pageable) {

        Page<SUser> page = suserDao.findAll(new Specification<SUser>() {
            @Override
            public Predicate toPredicate(Root<SUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if(StringUtils.isNotEmpty(paramJson.getString("realName"))) {
                    list.add(cb.like(root.get("realName").as(String.class), "%" + paramJson.getString("realName") + "%"));
                }
                if(StringUtils.isNotEmpty(paramJson.getString("orgId"))) {
                    list.add(cb.like(root.get("orgId").as(String.class), "%" + paramJson.getString("orgId") + "%"));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        },pageable);
        if(page!=null) logger.info("findAll size:"+page.getTotalElements());
        return page;
    }

    @Override
    public Page<SUser> listSerach(JSONObject paramJson, String searchValue, Pageable pageable) {
        Page<SUser> page = suserDao.findAll(new Specification<SUser>() {
            @Override
            public Predicate toPredicate(Root<SUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate realName = cb.like(root.get("realName").as(String.class), "%" + paramJson.getString("realName") + "%");
                Predicate orgId = cb.like(root.get("orgId").as(String.class), "%" + paramJson.getString("orgId") + "%");

                List<Predicate> list = new ArrayList<Predicate>();

                list.add(cb.like(root.get("loginName").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("realName").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("activeStatus").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("sex").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("issueName").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("openFlag").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("orderNum").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("secLevel").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("orgId").as(String.class), "%" + searchValue + "%"));
                Predicate[] p = new Predicate[list.size()];

                query.where(cb.and(realName,orgId,cb.or(list.toArray(p))));
                return query.getRestriction();
            }
        },pageable);
        if(page!=null) logger.info("postFilteredPage size:"+page.getTotalElements());
        return page;
    }

	public Page<Object[]> findAllByOrgId(String orgId, Pageable pageable) {
		Page<Object[]> userOrgPost = suserDao.findAllByOrgId(orgId,pageable);
		logger.info(userOrgPost.toString());
		return userOrgPost;
	}

	public void delById(String userId, List<String> postIds){
	            List<SUserOrg> sUserOrg = sUserOrgDao.findSUserOrgByuserId(userId);
	            for (SUserOrg sUserOrg1 : sUserOrg) {
	            	 sUserOrgDao.delete(sUserOrg1.getUuid());
				}   
	            suserDao.delete(userId);
	            List<SUserPost> userPost =null;
	            for (String string : postIds) {
	            	userPost= sUserPostDao.findSUserPostBypostId(string);
				}
	            if(userPost!=null) {
	            for (SUserPost sUserPost : userPost) {
            		sUserPostDao.delete(sUserPost.getUuid());
				   }
	            }
	}

	public List<String> findSUserPostByUserId(String userId) {
		return sUserPostDao.findSUserPostByUserId(userId);
	}

	public Page<Map<String, String>> findAllByOrgIdMap(String orgId,String realName,String activeStatus,String openFlag,Pageable pageable) {
		return suserDao.findAllByOrgIdMap(orgId,"%"+realName+"%","%"+activeStatus+"%","%"+openFlag+"%",pageable);
	}

	public List<String> getPostsByUserId(String userId) {
		return suserDao.getPostsByUserId(userId);
	}

	public SUser getSuserById(String userId) {
		return suserDao.findOne(userId);
	}

	public SUser save(SUser suser) {
		return suserDao.save(suser);
	}

	public SUserOrg saveUserOrg(SUserOrg sUserOrg) {
		return sUserOrgDao.save(sUserOrg);
	}

	public List<SUserPost> saveSUrPost(List<SUserPost> sUserPost) {
	        return  sUserPostDao.save(sUserPost);
	}

	public JSONArray findUserPosts(String userId) {
		//List<SUserPost> userPosts = sUserPostDao.findSUserPostsByUserId(userId);
		List<String> userPostIds = sUserPostDao.findSUserPostByUserId(userId);
		JSONArray datas = new JSONArray();
	for (String postId : userPostIds) {
		 JSONObject object = new JSONObject();
		  SPost  sPost =sPostDao.findOne(postId);
		 object.put("post",sPost);
		  datas.add(object);
	    }
	    logger.info(datas.toString());
	    System.out.println(datas);
		return datas;
	}

	public void delSuserPost(List<SUserPost> suserPost) {
		for (SUserPost sUserPost : suserPost) {
			sUserPostDao.deleteByUserIdAndPostId(sUserPost.getUserId(), sUserPost.getPostId());
		}
	}

	@Override
	public boolean checkLoginName(String loginName) {
		String loginNames = suserDao.checkLoginName(loginName);
		if(loginNames==null){
			return true;
		}else {
			return false;
		}
	}

	@Override
	public JSONObject checkLoginNameAndMobile(SUser sUser) {
		String loginNames = suserDao.checkLoginName(sUser.getLoginName());
		String mobiles = suserDao.checkMobile(sUser.getMobile());
		if(loginNames!=null){
			return JsonUtil.error("用户名已存在！");
		}else if(mobiles!=null) {
			return JsonUtil.error("手机号码已存在！");
		}else{
			sUser.setUuid(IDUtil.getId());
			suserDao.save(sUser);
			return JsonUtil.success("注册成功");
		}
	}

	@Override
	public JSONObject loadSelect() {
		List<SUser> dicts = suserDao.findAll();
		return JsonUtil.success("成功", dicts);
	}
}

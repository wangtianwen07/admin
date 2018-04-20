/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.srv.post.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.css.mgr.admin.common.IDUtil;
import com.css.mgr.admin.dao.repository.SPostDao;
import com.css.mgr.admin.dao.repository.SPostRoleDao;
import com.css.mgr.admin.dao.repository.SRoleDao;
import com.css.mgr.admin.srv.post.IPostService;
import com.css.mgr.base.dao.pojo.SPost;
import com.css.mgr.base.dao.pojo.SPostRole;
import com.css.mgr.base.dao.pojo.SRole;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * @author wangtianwen
 * 2018年1月22日
 */
@Service("postService")
@Transactional
public class PostServiceImpl implements IPostService{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SPostDao sPostDao;
	
	@Autowired
	private SPostRoleDao sPostRoleDao;
	
	@Autowired
	private SRoleDao sRoleDao;
	
	@Override
	public Page<SPost> findAll(JSONObject paramJson,Pageable pageable) {
		Page<SPost> page = sPostDao.findAll(new Specification<SPost>() {
			@Override
			public Predicate toPredicate(Root<SPost> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				if(StringUtils.isNotEmpty(paramJson.getString("code"))) {
					list.add(cb.like(root.get("code").as(String.class), "%" + paramJson.getString("code") + "%"));
				}
				if(StringUtils.isNotEmpty(paramJson.getString("name"))) {
					list.add(cb.like(root.get("name").as(String.class), "%" + paramJson.getString("name") + "%"));
				}
				Predicate[] p = new Predicate[list.size()];
				return cb.and(list.toArray(p));
			}
		},pageable);
		if(page!=null) logger.info("findAll size:"+page.getTotalElements());
		return page;
	}
	
	@Override
	public Page<SPost> listSerach(JSONObject paramJson,String searchValue, Pageable pageable) {
		 Page<SPost> page = sPostDao.findAll(new Specification<SPost>() {
				@Override
				public Predicate toPredicate(Root<SPost> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					List<Predicate> predList = new ArrayList<Predicate>();
					if(StringUtils.isNotEmpty(paramJson.getString("code"))) {
						Predicate pCode = cb.like(root.get("code").as(String.class), "%" + paramJson.getString("code") + "%");
						predList.add(pCode);
					}
					if(StringUtils.isNotEmpty(paramJson.getString("name"))) {
						Predicate pName = cb.like(root.get("name").as(String.class), "%" + paramJson.getString("name") + "%");
						predList.add(pName);
					}
					if(StringUtils.isNotEmpty(searchValue)) {
						List<Predicate> list = new ArrayList<Predicate>();
						list.add(cb.like(root.get("code").as(String.class), "%" + searchValue + "%"));
						list.add(cb.like(root.get("name").as(String.class), "%" + searchValue + "%"));
						list.add(cb.like(root.get("rank").as(String.class), "%" + searchValue + "%"));
						list.add(cb.like(root.get("remark").as(String.class), "%" + searchValue + "%"));
						list.add(cb.like(root.get("unitId").as(String.class), "%" + searchValue + "%"));
						list.add(cb.like(root.get("useType").as(String.class), "%" + searchValue + "%"));
						Predicate[] p = new Predicate[list.size()];
						predList.add(cb.or(list.toArray(p)));
					}
					Predicate[] preds = new Predicate[predList.size()];
					query.where(cb.and(predList.toArray(preds)));
		            return query.getRestriction();
				}
			},pageable);
			if(page!=null) logger.info("postFilteredPage size:"+page.getTotalElements());
			return page;
	}

	@Override
	public SPost save(SPost sPost) {
		return sPostDao.save(sPost);
	}

	@Override
	public SPost getSPostByUuid(String uuid) {
		return sPostDao.findOne(uuid);
	}

	@Override
	public void delByUuid(String uuid) {
		sPostDao.delete(uuid);
	}

	@Override
	public JSONArray findRelatedRole(String postId) {
		List<SPostRole> postRoles = sPostRoleDao.findSPostRoleByPostId(postId);
		JSONArray datas = new JSONArray();
		for(SPostRole sPostRole:postRoles) {
			JSONObject object = new JSONObject();
			object.put("postRoleUuid", sPostRole.getUuid());
			object.put("role", sRoleDao.findOne(sPostRole.getRoleId()));//注意不能用getOne
			datas.add(object);
		}
		logger.info(datas.toString());
		return datas;
	}

	@Override
	public List<SRole> findAllRole() {
		return sRoleDao.findAll();
	}

	@Override
	public void bindRole(String postUuid, String roleUuid) {
		SPostRole sPostRole = sPostRoleDao.findByPostIdAndRoleId(postUuid,roleUuid);
		logger.info("sPostRole:"+sPostRole);
		if(sPostRole==null) {
			sPostRole = new SPostRole();
			sPostRole.setUuid(IDUtil.getId());
			sPostRole.setPostId(postUuid);
			sPostRole.setRoleId(roleUuid);
			SRole sRole = sRoleDao.getOne(roleUuid);
			sPostRole.setSysId(sRole.getSysId());
			sPostRoleDao.save(sPostRole);
		}
	}

	@Override
	public void cancelRole(String sPostRoleUuid) {
		sPostRoleDao.delete(sPostRoleUuid);
	}
}

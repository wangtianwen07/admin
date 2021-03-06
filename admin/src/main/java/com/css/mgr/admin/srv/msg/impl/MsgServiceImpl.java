/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.srv.msg.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.common.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.mgr.admin.common.IDUtil;
import com.css.mgr.admin.common.JsonUtil;
import com.css.mgr.admin.common.StringUtil;
import com.css.mgr.admin.dao.pojo.SMsg;
import com.css.mgr.admin.dao.repository.SMsgDao;
import com.css.mgr.admin.dao.repository.SUserDao;
import com.css.mgr.admin.srv.msg.IMsgService;
import com.css.mgr.base.dao.pojo.IUser;

import net.sf.json.JSONObject;

/**
 * @author wangtianwen
 * 2018年3月7日
 */
@Service("msgService")
@Transactional
public class MsgServiceImpl implements IMsgService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private SMsgDao sMsgDao;
	
	@Autowired
	private SUserDao sUserDao;
	
	@Override
	public boolean send(String title, String msg, IUser user) {
		try {
			SMsg sMsg = new SMsg();
			sMsg.setTitle(title);
			sMsg.setContent(msg);
			sMsg.setCreateTime(new Date());
			sMsg.setReadStatus(SMsg.READ_STATUS_NO);
			sMsg.setUserId(user.getUuid());
			sMsg.setUserName(user.getLoginName());
			sMsg.setUuid(IDUtil.getId());
			sMsgDao.save(sMsg);
			return true;
		} catch (Exception e) {
			logger.info("消息推送失败!");
			return false;
		}
	}

	@Override
	public boolean sysMsg(String msg, IUser user) {
		return false;
	}

	@Override
	public Page<SMsg> findAll(JSONObject paramJson, Pageable pageable) {
		Page<SMsg> page = sMsgDao.findAll(new Specification<SMsg>() {
			@Override
			public Predicate toPredicate(Root<SMsg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				if(StringUtils.isNotEmpty(paramJson.getString("title"))) {
					list.add(cb.like(root.get("title").as(String.class), "%" + paramJson.getString("title") + "%"));
				}
				if(StringUtils.isNotEmpty(paramJson.getString("userName"))) {
					list.add(cb.like(root.get("userName").as(String.class), "%" + paramJson.getString("userName") + "%"));
				}
				Predicate[] p = new Predicate[list.size()];
				return cb.and(list.toArray(p));
			}
		},pageable);
		if(page!=null) logger.info("findAll size:"+page.getTotalElements());
		return page;
	}

	@Override
	public Page<SMsg> listSerach(JSONObject paramJson, String searchValue, Pageable pageable) {
		 Page<SMsg> page = sMsgDao.findAll(new Specification<SMsg>() {
				@Override
				public Predicate toPredicate(Root<SMsg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					List<Predicate> predList = new ArrayList<Predicate>();
					if(StringUtils.isNotEmpty(paramJson.getString("title"))) {
						Predicate pCode = cb.like(root.get("title").as(String.class), "%" + paramJson.getString("title") + "%");
						predList.add(pCode);
					}
					if(StringUtils.isNotEmpty(paramJson.getString("userName"))) {
						Predicate pName = cb.like(root.get("userName").as(String.class), "%" + paramJson.getString("userName") + "%");
						predList.add(pName);
					}
					if(StringUtils.isNotEmpty(searchValue)) {
						List<Predicate> list = new ArrayList<Predicate>();
						list.add(cb.like(root.get("title").as(String.class), "%" + searchValue + "%"));
						list.add(cb.like(root.get("userName").as(String.class), "%" + searchValue + "%"));
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
	public JSONObject addMsg(SMsg item) {
		if(StringUtils.isEmpty(item.getUuid())) {
			item.setUuid(IDUtil.getId());
		}
		if(StringUtils.isEmpty(item.getReadStatus())) {
			item.setReadStatus(SMsg.READ_STATUS_NO);
		}
		if(!StringUtil.isEmpty(item.getUserId())) {
			item.setUserName(sUserDao.findOne(item.getUserId()).getUsername());
		}
		item.setCreateTime(new Date());
		sMsgDao.save(item);
		return JsonUtil.success("成功", null);
	}

	@Override
	public JSONObject delMsg(String uuids) {
		if (StringUtil.isEmpty(uuids)) {
			return JsonUtil.error("删除失败!");
		}
		String[] dictIdList = StringUtil.strToArray(uuids);
		for (String i : dictIdList) {
			sMsgDao.delete(i);
		}
		return JsonUtil.success("成功", null);
	}

	@Override
	public JSONObject getMsg(String uuid) {
		SMsg result = null;
		if (StringHelper.isNotEmpty(uuid)) {
			result = sMsgDao.findOne(uuid);
		}
		return JsonUtil.success("成功", result);
	}

	@Override
	public List<SMsg> findMsgByUserId(String userId) {
		return sMsgDao.findByUserId(userId);
	}

	@Override
	public JSONObject updMsgStatus(String uuid) {
		SMsg msg = sMsgDao.findOne(uuid);
		msg.setReadStatus(SMsg.READ_STATUS_YES);
		sMsgDao.save(msg);
		return JsonUtil.success("成功");
	}

	
}

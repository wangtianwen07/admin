package com.css.mgr.bpm.freeflow.srv;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.common.util.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.css.mgr.admin.common.JsonUtil;
import com.css.mgr.admin.common.StringUtil;
import com.css.mgr.base.dao.pojo.IUser;
import com.css.mgr.bpm.freeflow.dao.pojo.FfDef;
import com.css.mgr.bpm.freeflow.dao.pojo.FfTask;
import com.css.mgr.bpm.freeflow.dao.repository.FfDefDao;
import com.css.mgr.bpm.freeflow.dao.repository.FfTaskDao;

import net.sf.json.JSONObject;

@Service
@Transactional(readOnly = true)
public class FlowDefService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private FfDefDao ffDefDao;
	@Autowired
	private FfTaskDao fftaskDao;

	public FfDef getFlowDef(String ffTaskUuid) {
		FfTask task = fftaskDao.findOne(ffTaskUuid);
		FfDef df = ffDefDao.findOne(task.getFfDefUuid());
		return df;
	}

	public FfDef get(String id) {
		return ffDefDao.findOne(id);
	}

	public JSONObject getDef(String id) {
		FfDef result = null;
		if (StringHelper.isNotEmpty(id)) {
			result = ffDefDao.findOne(id);
		}
		return JsonUtil.success("成功", result);
	}

	/** 检查item为不可添加 */
	public static boolean isNotAdd(FfDef item) {
		return item == null || StringHelper.isEmpty(item.getName());
	}

	@Transactional
	public JSONObject addFfDef(FfDef item) {
		if (isNotAdd(item)) {
			return JsonUtil.error("保存失败!");
		}
		ffDefDao.save(item);
		return JsonUtil.success("成功", null);
	}

	@Transactional
	public JSONObject updFfDef(FfDef item) {
		if (item == null) {
			return JsonUtil.error("保存失败!");
		}
		// user
		FfDef tmp = ffDefDao.findOne(item.getFfDefUuid());
		tmp.setName(item.getName());
		tmp.setHandler(item.getHandler());
		tmp.setSubscriber(item.getSubscriber());
		tmp.setUrl(item.getUrl());
		// group

		ffDefDao.save(tmp);
		return JsonUtil.success("成功", null);
	}

	public static boolean isDelable(String uuid, IUser sUser) {
		return true;
	}

	@Transactional
	public JSONObject delFfDef(String ids) {
		if (StringUtil.isEmpty(ids)) {
			return JsonUtil.error("删除失败!");
		}
		String[] dictIdList = StringUtil.strToArray(ids);
		for (String i : dictIdList) {
			ffDefDao.delete(i);
		}
		return JsonUtil.success("成功", null);
	}
	
	public Page<FfDef> findAll(JSONObject paramJson,Pageable pageable) {
		Page<FfDef> page = ffDefDao.findAll(new Specification<FfDef>() {
			@Override
			public Predicate toPredicate(Root<FfDef> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				/*List<Predicate> list = new ArrayList<Predicate>();
				if(StringUtils.isNotEmpty(paramJson.getString("name"))) {
					list.add(cb.like(root.get("name").as(String.class), "%" + paramJson.getString("name") + "%"));
				}
				Predicate[] p = new Predicate[list.size()];
				return cb.and(list.toArray(p));*/
				return cb.like(root.get("name").as(String.class), "%" + paramJson.getString("name") + "%");
			}
		},pageable);
		if(page!=null) logger.info("findAll size:"+page.getTotalElements());
		return page;
	}
	
	public Page<FfDef> listSerach(JSONObject paramJson,String searchValue, Pageable pageable) {
		 Page<FfDef> page = ffDefDao.findAll(new Specification<FfDef>() {
				@Override
				public Predicate toPredicate(Root<FfDef> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
					List<Predicate> predList = new ArrayList<Predicate>();
					if(StringUtils.isNotEmpty(paramJson.getString("name"))) {
						Predicate pName = cb.like(root.get("name").as(String.class), "%" + paramJson.getString("name") + "%");
						predList.add(pName);
					}
					if(StringUtils.isNotEmpty(searchValue)) {
						List<Predicate> list = new ArrayList<Predicate>();
						list.add(cb.like(root.get("name").as(String.class), "%" + searchValue + "%"));
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
}

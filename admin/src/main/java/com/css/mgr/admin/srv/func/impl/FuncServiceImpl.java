/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.srv.func.impl;

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

import com.css.mgr.admin.dao.repository.SFuncDao;
import com.css.mgr.admin.dao.repository.SResDao;
import com.css.mgr.admin.srv.func.IFuncService;
import com.css.mgr.base.dao.pojo.SFunc;
import com.css.mgr.base.dao.pojo.SResource;

import net.sf.json.JSONObject;

/**
 * @author wangtianwen 2018年1月30日
 */
@Service("funcService")
@Transactional
public class FuncServiceImpl implements IFuncService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SFuncDao sFuncDao;
 
	@Autowired
	private SResDao sResDao;
	
	@Override
	public SFunc save(SFunc sFunc) {
		return sFuncDao.save(sFunc);
	}
	
	@Override
	public void delByUuid(String uuid) {
		sFuncDao.delete(uuid);
		sResDao.deleteByFuncId(uuid);
	}
	
	@Override
	public SFunc getSFuncByUuid(String uuid) {
		return sFuncDao.findOne(uuid);
	}
	@Override
	public Page<SFunc> findAll(JSONObject paramJson, Pageable pageable) {
		Page<SFunc> page = sFuncDao.findAll(new Specification<SFunc>() {
			@Override
			public Predicate toPredicate(Root<SFunc> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(paramJson.getString("name"))) {
					list.add(cb.like(root.get("name").as(String.class), "%" + paramJson.getString("name") + "%"));
				}
				if (StringUtils.isNotEmpty(paramJson.getString("funcType"))) {
					list.add(cb.like(root.get("funcType").as(String.class), "%" + paramJson.getString("funcType") + "%"));
				}
				if (StringUtils.isNotEmpty(paramJson.getString("openflag"))) {
					list.add(cb.like(root.get("openflag").as(String.class), "%" + paramJson.getString("openflag") + "%"));
				}
				if (StringUtils.isNotEmpty(paramJson.getString("parentId"))) {
					list.add(cb.equal(root.get("parentId").as(String.class), paramJson.getString("parentId")));
				}
				Predicate[] p = new Predicate[list.size()];
				return cb.and(list.toArray(p));
			}
		}, pageable);
		if (page != null)
			logger.info("findAll size:" + page.getTotalElements());
		return page;
	}

	@Override
	public Page<SFunc> listSerach(JSONObject paramJson, String searchValue, Pageable pageable) {
		Page<SFunc> page = sFuncDao.findAll(new Specification<SFunc>() {
			@Override
			public Predicate toPredicate(Root<SFunc> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate pName = cb.like(root.get("name").as(String.class), "%" + paramJson.getString("name") + "%");
				Predicate pFuncType = cb.like(root.get("funcType").as(String.class), "%" + paramJson.getString("funcType") + "%");
				Predicate pOpenflag = cb.like(root.get("openflag").as(String.class), "%" + paramJson.getString("openflag") + "%");
				Predicate pParentId = cb.equal(root.get("parentId").as(String.class), paramJson.getString("parentId"));

				List<Predicate> list = new ArrayList<Predicate>();

				list.add(cb.like(root.get("name").as(String.class), "%" + searchValue + "%"));
				list.add(cb.like(root.get("funcType").as(String.class), "%" + searchValue + "%"));
				list.add(cb.like(root.get("openflag").as(String.class), "%" + searchValue + "%"));
				list.add(cb.like(root.get("remark").as(String.class), "%" + searchValue + "%"));
				Predicate[] p = new Predicate[list.size()];

				query.where(cb.and(pName,pFuncType,pOpenflag,pParentId, cb.or(list.toArray(p))));
				return query.getRestriction();
			}
		}, pageable);
		if (page != null)
			logger.info("postFilteredPage size:" + page.getTotalElements());
		return page;
	}

	@Override
	public String getTreeHtml(String sysId) {
		StringBuffer html = new StringBuffer();
		return stitchingHtml(sysId,null, html).toString();
	}

	private StringBuffer stitchingHtml(String sysId,String parentId, StringBuffer html) {
		List<SFunc> sFuncList = sFuncDao.findByParentIdAndSysId(parentId,sysId);
		for (SFunc sFunc : sFuncList) {
			if (StringUtils.isEmpty(sFunc.getParentId()) || sFuncDao.findByParentIdAndSysId(sFunc.getUuid(),sysId).size()>0) {
				html.append("<li data-uuid='" + sFunc.getUuid() + "' data-jstree='{\"opened\":true}'>" + sFunc.getName()
						+ "<ul>");
				stitchingHtml(sysId,sFunc.getUuid(), html);
				html.append("</ul></li>");
			} else {
				html.append("<li  data-uuid='" + sFunc.getUuid() + "' data-jstree='{\"type\":\"file\"}'>"
						+ sFunc.getName() + "</li>");
			}
		}
		return html;
	}

	@Override
	public List<SResource> findResByFuncId(String funcId) {
		return sResDao.findByFuncId(funcId);
	}

	@Override
	public void delRes(String resUuid) {
		sResDao.delete(resUuid);
	}

	@Override
	public SResource getRes(String resUuid) {
		return sResDao.findOne(resUuid);
	}

	@Override
	public SResource saveRes(SResource sRes) {
		return sResDao.save(sRes);
	}

	@Override
	public boolean exists(String uuid) {
		return sFuncDao.exists(uuid);
	}

	@Override
	public boolean ResExists(String resUuid) {
		return sResDao.exists(resUuid);
	}
}

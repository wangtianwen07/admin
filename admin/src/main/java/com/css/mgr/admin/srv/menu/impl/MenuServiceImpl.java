/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.srv.menu.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.css.mgr.admin.dao.repository.SFuncDao;
import com.css.mgr.admin.dao.repository.SMenuDao;
import com.css.mgr.admin.srv.menu.IMenuService;
import com.css.mgr.base.dao.pojo.SMenu;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author wangtianwen
 * 2018年2月2日
 */
@Service("menuService")
@Transactional
public class MenuServiceImpl implements IMenuService{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SMenuDao sMenuDao;
	
	@Autowired
	private SFuncDao sFuncDao;
	
	@Override
	public String getTreeHtml() {
		StringBuffer html = new StringBuffer();
		html.append("<li data-uuid='0' data-jstree='{\"opened\":true}'>系统菜单<ul>");
		stitchingHtml(null, html);
		html.append("</ul></li>");
		return html.toString();
	}

	private StringBuffer stitchingHtml(String parentId, StringBuffer html) {
		List<SMenu> sMenuList = sMenuDao.findByParentId(parentId);
		for (SMenu sMenu : sMenuList) {
			if (StringUtils.isEmpty(sMenu.getParentId()) || sMenuDao.findByParentId(sMenu.getUuid()).size()>0) {
				html.append("<li data-uuid='" + sMenu.getUuid() + "' data-jstree='{\"opened\":true}'>" + sMenu.getName()
						+ "<ul>");
				stitchingHtml(sMenu.getUuid(), html);
				html.append("</ul></li>");
			} else {
				html.append("<li  data-uuid='" + sMenu.getUuid() + "' data-jstree='{\"type\":\"file\"}'>"
						+ sMenu.getName() + "</li>");
			}
		}
		return html;
	}

	@Override
	public Page<SMenu> findAll(JSONObject paramJson, Pageable pageable) {
		String p = paramJson.getString("parentId");
		logger.info("p:"+(p==null));
		logger.info("p2:"+(p.equals("null")));
		Page<SMenu> page = sMenuDao.findAll(new Specification<SMenu>() {
			@Override
			public Predicate toPredicate(Root<SMenu> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(paramJson.getString("name"))) {
					list.add(cb.like(root.get("name").as(String.class), "%" + paramJson.getString("name") + "%"));
				}
				if (StringUtils.isNotEmpty(paramJson.getString("openflag"))) {
					list.add(cb.equal(root.get("openflag").as(String.class), paramJson.getString("openflag")));
				}
				if (StringUtils.isNotEmpty(paramJson.getString("parentId")) &&  !paramJson.getString("parentId").equals("null")) {
					list.add(cb.equal(root.get("parentId").as(String.class), paramJson.getString("parentId")));
				} else {
					list.add(cb.isNull(root.get("parentId").as(String.class)));
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
	public Page<SMenu> listSerach(JSONObject paramJson, String searchValue, Pageable pageable) {
		Page<SMenu> page = sMenuDao.findAll(new Specification<SMenu>() {
			@Override
			public Predicate toPredicate(Root<SMenu> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate pName = cb.like(root.get("name").as(String.class), "%" + paramJson.getString("name") + "%");
				Predicate pOpenflag = cb.equal(root.get("openflag").as(String.class), paramJson.getString("openflag"));
				Predicate pParentId = null;
				if (StringUtils.isNotEmpty(paramJson.getString("parentId")) &&  !paramJson.getString("parentId").equals("null")) {
					pParentId = cb.equal(root.get("parentId").as(String.class), paramJson.getString("parentId"));
				} else {
					pParentId = cb.isNull(root.get("parentId").as(String.class));
				}

				List<Predicate> list = new ArrayList<Predicate>();

				list.add(cb.like(root.get("name").as(String.class), "%" + searchValue + "%"));
				list.add(cb.like(root.get("remark").as(String.class), "%" + searchValue + "%"));
				list.add(cb.like(root.get("url").as(String.class), "%" + searchValue + "%"));
				list.add(cb.like(root.get("orderNum").as(String.class), "%" + searchValue + "%"));
				list.add(cb.like(root.get("openflag").as(String.class), "%" + searchValue + "%"));
				Predicate[] p = new Predicate[list.size()];

				query.where(cb.and(pName,pOpenflag,pParentId, cb.or(list.toArray(p))));
				return query.getRestriction();
			}
		}, pageable);
		if (page != null)
			logger.info("FilteredPage size:" + page.getTotalElements());
		return page;
	}

	@Override
	public SMenu save(SMenu sMenu) {
		return sMenuDao.save(sMenu);
	}

	@Override
	public void del(String uuid) {
		sMenuDao.delete(uuid);
	}

	@Override
	public SMenu get(String uuid) {
		return sMenuDao.findOne(uuid);
	}

	@Override
	public JSONArray getFuncTree() {
		List<Object[]> funcs = sFuncDao.findByOrderByParentIdAsc();
		JSONArray array = new JSONArray();
		for(Object[] func: funcs) {
			JSONObject obj = new JSONObject();
			obj.accumulate("id", func[0]);
			Object pId = func[1];
			if(pId==null) pId = "";//处理null
			obj.accumulate("pId", pId);
			obj.accumulate("name", func[2]);
			array.add(obj);
		}
		logger.info("array:"+array);
		return array;
	}

	@Override
	public String getSysIdByFuncId(String funcId) {
		return sFuncDao.findOne(funcId).getSysId();
	}
}

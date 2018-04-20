/**
 * Date:2018年1月17日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.srv.unit.impl;

import java.util.ArrayList;
import java.util.Iterator;
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
import com.css.mgr.admin.dao.repository.SUnitDao;
import com.css.mgr.admin.dao.repository.SUnitUserDao;
import com.css.mgr.admin.dao.repository.SUserDao;
import com.css.mgr.admin.srv.unit.IUnitService;
import com.css.mgr.base.dao.pojo.SUnit;
import com.css.mgr.base.dao.pojo.SUnitUser;
import com.css.mgr.base.dao.pojo.SUser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Date:2018年1月17日
 * Description:
 * Author:Litao
 */
@Service("unitService")
@Transactional
public class UnitServiceImpl implements IUnitService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
    @Autowired
    private SUnitDao sUnitDao;
    @Autowired
    private SUserDao sUserDao;
    @Autowired 
    private SUnitUserDao sUnitUserDao;
    @Override
    public SUnit save(SUnit sUnit) {
        return sUnitDao.save(sUnit);
    }
    
    @Override
	public JSONArray getUnitTree() {
		List<Object[]> units = sUnitDao.findByOrderByParentIdAsc();
		JSONArray array = new JSONArray();
		for(Object[] func: units) {
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
	public String getTreeHtml() {
		StringBuffer html = new StringBuffer();
		html.append("<li data-uuid='0' data-jstree='{\"opened\":true}'>单位树<ul>");
		stitchingHtml(null, html);
		html.append("</ul></li>");
		return html.toString();
	}

	private StringBuffer stitchingHtml(String parentId, StringBuffer html) {
		List<SUnit> sUnitList = sUnitDao.findByParentId(parentId);
		for (SUnit sUnit : sUnitList) {
			if (StringUtils.isEmpty(sUnit.getParentId()) || sUnitDao.findByParentId(sUnit.getUuid()).size()>0) {
				html.append("<li data-uuid='" + sUnit.getUuid() + "' data-jstree='{\"opened\":true}'>" + sUnit.getUnitName()
						+ "<ul>");
				stitchingHtml(sUnit.getUuid(), html);
				html.append("</ul></li>");
			} else {
				html.append("<li  data-uuid='" + sUnit.getUuid() + "' data-jstree='{\"type\":\"file\"}'>"
						+ sUnit.getUnitName() + "</li>");
			}
		}
		return html;
	}

	@Override
	public Page<SUnit> findAll(JSONObject paramJson, Pageable pageable) {
		String p = paramJson.getString("parentId");
		logger.info("p:"+(p==null));
		logger.info("p2:"+(p.equals("null")));
		Page<SUnit> page = sUnitDao.findAll(new Specification<SUnit>() {
			@Override
			public Predicate toPredicate(Root<SUnit> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(paramJson.getString("unitCode"))) {
					list.add(cb.like(root.get("unitCode").as(String.class), "%" + paramJson.getString("unitCode") + "%"));
				}
				if (StringUtils.isNotEmpty(paramJson.getString("unitName"))) {
					list.add(cb.like(root.get("unitName").as(String.class), "%" + paramJson.getString("unitName") + "%"));
				}
				if (StringUtils.isNotEmpty(paramJson.getString("openFlag"))) {
					list.add(cb.equal(root.get("openFlag").as(String.class), paramJson.getString("openFlag")));
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
	public Page<SUnit> listSerach(JSONObject paramJson, String searchValue, Pageable pageable) {
		Page<SUnit> page = sUnitDao.findAll(new Specification<SUnit>() {
			@Override
			public Predicate toPredicate(Root<SUnit> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate pUnitCode = cb.like(root.get("unitCode").as(String.class), "%" + paramJson.getString("unitCode") + "%");
				Predicate pUnitName = cb.like(root.get("unitName").as(String.class), "%" + paramJson.getString("unitName") + "%");
				Predicate pOpenFlag = cb.equal(root.get("openFlag").as(String.class), paramJson.getString("openFlag"));
				Predicate pParentId = null;
				if (StringUtils.isNotEmpty(paramJson.getString("parentId")) &&  !paramJson.getString("parentId").equals("null")) {
					pParentId = cb.equal(root.get("parentId").as(String.class), paramJson.getString("parentId"));
				} else {
					pParentId = cb.isNull(root.get("parentId").as(String.class));
				}

				List<Predicate> list = new ArrayList<Predicate>();

				list.add(cb.like(root.get("unitCode").as(String.class), "%" + searchValue + "%"));
				list.add(cb.like(root.get("unitName").as(String.class), "%" + searchValue + "%"));
				list.add(cb.like(root.get("openFlag").as(String.class), "%" + searchValue + "%"));
				Predicate[] p = new Predicate[list.size()];

				query.where(cb.and(pUnitCode,pUnitName,pOpenFlag,pParentId, cb.or(list.toArray(p))));
				return query.getRestriction();
			}
		}, pageable);
		if (page != null)
			logger.info("FilteredPage size:" + page.getTotalElements());
		return page;
	}

	@Override
	public void del(String uuid) {
		sUnitDao.delete(uuid);
	}
	
	@Override
	public SUnit get(String uuid) {
		return sUnitDao.findOne(uuid);
	}

	@Override
	public JSONArray findRelatedMgr(String unitId,String mgrName) {
		List<SUnitUser> unitUsers = sUnitUserDao.findByUnitId(unitId);
		if(StringUtils.isNotEmpty(mgrName)) {
			Iterator<SUnitUser> iterator  = unitUsers.iterator();
			while(iterator.hasNext()) {
				SUnitUser sUnitUser = iterator.next();
				SUser user = sUserDao.findOne(sUnitUser.getUserId());//1 getOne与findOne，缓存
				if(user.getRealName().indexOf(mgrName) == -1 
						&& user.getLoginName().indexOf(mgrName) == -1) {
					iterator.remove();
				}
			}
		}
		JSONArray datas = new JSONArray();
		for(SUnitUser sUnitUser : unitUsers) {
			JSONObject object = new JSONObject();
			object.put("unitUserId", sUnitUser.getUuid());
			object.put("user",sUserDao.findOne(sUnitUser.getUserId()));//2
			object.put("unitName", sUnitDao.findOne(unitId).getUnitName());
			datas.add(object);
		}
		logger.info(datas.toString());
		return datas;
	}

	@Override
	public void cancelMgr(String sUnitUserUuid) {
		sUnitUserDao.delete(sUnitUserUuid);
	}

	@Override
	public List<SUser> findAllMgr(String loginName,String realName) {
		List<SUser> users = sUserDao.findAll(new Specification<SUser>() {
			@Override
			public Predicate toPredicate(Root<SUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predList = new ArrayList<Predicate>();
				if(StringUtils.isNotEmpty(loginName)) {
					Predicate pLoginName = cb.like(root.get("loginName").as(String.class), "%"+loginName+"%");
					predList.add(pLoginName);
				}
				if(StringUtils.isNotEmpty(realName)) {
					Predicate pRealName = cb.like(root.get("realName").as(String.class), "%"+realName+"%");
					predList.add(pRealName);
				}
				Predicate[] p = new Predicate[predList.size()];
				return cb.and(predList.toArray(p));
			}
		});
		return users;
	}

	@Override
	public void bindMgr(String sUserId, String sUnitId) {
		SUnitUser sUnitUser = sUnitUserDao.findByUserIdAndUnitId(sUserId,sUnitId);
		logger.info("sUnitUser:"+sUnitUser);
		if(sUnitUser==null) {
			sUnitUser = new SUnitUser();
			sUnitUser.setUuid(IDUtil.getId());
			sUnitUser.setUserId(sUserId);
			sUnitUser.setUnitId(sUnitId);
			sUnitUserDao.save(sUnitUser);
		}
	}
}

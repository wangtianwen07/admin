package com.css.mgr.admin.srv.sys.impl;

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
import com.css.mgr.admin.dao.repository.SSysDao;
import com.css.mgr.admin.srv.sys.ISysService;
import com.css.mgr.base.dao.pojo.SSys;

import net.sf.json.JSONObject;

/**
 * Date:2018年1月24日
 * Author:Hjw
 */
@Service("sysService")
@Transactional
public class SysServiceImpl implements ISysService {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private SSysDao sSysDao;
	
	public Page<SSys> findAll(JSONObject paramJson,Pageable pageable) {
		return sSysDao.findAll(pageable);
	}
	public Page<SSys> listSerach(SSys sSys, Pageable pageable) {
		Page<SSys> page = sSysDao.findAll(new Specification<SSys>() {
			public Predicate toPredicate(Root<SSys> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				if (StringUtils.isNotEmpty(sSys.getUuid())) {
	                list.add(cb.like(root.get("uuid").as(String.class), "%" + sSys.getUuid() + "%"));
	            }
				if (StringUtils.isNotEmpty(sSys.getName())) {
	                list.add(cb.like(root.get("name").as(String.class), "%" + sSys.getName()+ "%"));
	            }
				if (StringUtils.isNotEmpty(sSys.getUrl())) {
	                list.add(cb.like(root.get("url").as(String.class), "%" + sSys.getUrl() + "%"));
	            }
				if (StringUtils.isNotEmpty(sSys.getRemark())) {
	                list.add(cb.like(root.get("remark").as(String.class), "%" + sSys.getRemark() + "%"));
	            }
				if (StringUtils.isNotEmpty(sSys.getOpenFlag())) {
	                list.add(cb.like(root.get("openFlag").as(String.class), "%" + sSys.getOpenFlag() + "%"));
	            }
				Predicate[] p = new Predicate[list.size()];
				if(list.size()==0) {
				   return cb.isNotNull(root);
				}else {
	            return cb.or(list.toArray(p));
				}
				}
		},pageable);
		logger.info("postFilteredPage size:"+page.getTotalElements());
		return page;
	}
	@Override
	public void delById(String uuid) {
		sSysDao.delete(uuid);
	}
	@Override
	public SSys save(SSys sApp) {
		return sSysDao.save(sApp);
	}
	public SSys getSAppById(String uuid) {
		return sSysDao.findOne(uuid);
	}
	public Page<SSys> listSerach(JSONObject paramJson, String searchValue, Pageable pageable) {
			 Page<SSys> page = sSysDao.findAll(new Specification<SSys>() {
					public Predicate toPredicate(Root<SSys> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
							Predicate pCode = cb.like(root.get("uuid").as(String.class), "%" + 							paramJson.getString("uuid") + "%");
							Predicate pName = cb.like(root.get("name").as(String.class), "%" + 							paramJson.getString("name") + "%");
						
						List<Predicate> list = new ArrayList<Predicate>();
						
						list.add(cb.like(root.get("uuid").as(String.class), "%" + searchValue + "%"));
						list.add(cb.like(root.get("name").as(String.class), "%" + searchValue + "%"));
						list.add(cb.like(root.get("url").as(String.class), "%" + searchValue + "%"));
						list.add(cb.like(root.get("remark").as(String.class), "%" + searchValue + "%"));
						list.add(cb.like(root.get("openFlag").as(String.class), "%" + searchValue + "%"));
						Predicate[] p = new Predicate[list.size()];
						
						query.where(cb.and(pCode,pName,cb.or(list.toArray(p))));
			            return query.getRestriction();
					}

				},pageable);
				if(page!=null) logger.info("postFilteredPage size:"+page.getTotalElements());
				return page;
		}

	@Override
	public List<SSys> findByOpenFlag(String openFlag) {
		return sSysDao.findByOpenFlag(openFlag);
	}
	@Override
	public List<SSys> findAll() {
		return sSysDao.findAll();
	}
}

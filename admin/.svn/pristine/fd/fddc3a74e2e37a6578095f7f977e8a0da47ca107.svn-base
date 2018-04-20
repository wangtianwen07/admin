/**
 * Date:2018年1月17日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.srv.log.impl;

import com.css.mgr.admin.common.TimeUtils;
import com.css.mgr.admin.dao.repository.*;
import com.css.mgr.admin.srv.log.SLogService;
import com.css.mgr.base.dao.pojo.*;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Date:2018年3月1日
 * Description:
 * Author:litao
 */
@Service("logService")
@Transactional
public class SLogServiceImpl implements SLogService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SLogDao SLogDao;
    @Override
    public Page<SLog> findAll(JSONObject paramJson, Pageable pageable) {

        Page<SLog> page = SLogDao.findAll(new Specification<SLog>() {
            @Override
            public Predicate toPredicate(Root<SLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if(StringUtils.isNotEmpty(paramJson.getString("userName"))) {
                    list.add(cb.like(root.get("userName").as(String.class), "%" + paramJson.getString("userName") + "%"));
                }
                if(StringUtils.isNotEmpty(paramJson.getString("sTime"))) {
                    list.add(cb.greaterThanOrEqualTo(root.get("requestTime").as(String.class), paramJson.getString("sTime")));
                }
                if(StringUtils.isNotEmpty(paramJson.getString("eTime"))) {
                    list.add(cb.lessThanOrEqualTo(root.get("requestTime").as(String.class), paramJson.getString("eTime")));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        },pageable);
        if(page!=null) logger.info("findAll size:"+page.getTotalElements());
        return page;
    }

    @Override
    public Page<SLog> listSerach(JSONObject paramJson, String searchValue, Pageable pageable) {
        Page<SLog> page = SLogDao.findAll(new Specification<SLog>() {
            @Override
            public Predicate toPredicate(Root<SLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Predicate userName = cb.like(root.get("userName").as(String.class), "%" + paramJson.getString("userName") + "%");
                Predicate sTime = null;
                Predicate eTime = null;
                if(StringUtils.isNotEmpty(paramJson.getString("sTime"))){
                    sTime = cb.greaterThanOrEqualTo(root.get("requestTime").as(String.class),paramJson.getString("sTime"));
                }
                if(StringUtils.isNotEmpty(paramJson.getString("eTime"))) {
                    eTime = cb.lessThanOrEqualTo(root.get("requestTime").as(String.class), paramJson.getString("eTime"));
                }

                List<Predicate> list = new ArrayList<Predicate>();
                list.add(cb.like(root.get("ip").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("requestType").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("requestTime").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("url").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("classMethod").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("response").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("userId").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("userName").as(String.class), "%" + searchValue + "%"));
                Predicate[] p = new Predicate[list.size()];

                if(sTime!=null && eTime!=null){
                    query.where(cb.and(userName,sTime,eTime,cb.or(list.toArray(p))));
                }else if(sTime!=null){
                    query.where(cb.and(userName,sTime,cb.or(list.toArray(p))));
                }else if(eTime!=null){
                    query.where(cb.and(userName,eTime,cb.or(list.toArray(p))));
                }else{
                    query.where(cb.and(userName,cb.or(list.toArray(p))));
                }
                return query.getRestriction();
            }
        },pageable);
        if(page!=null) logger.info("postFilteredPage size:"+page.getTotalElements());
        return page;
    }

    @Override
    public List<SLog> save(List<SLog> sLogList) {
        SLogDao.save(sLogList);
        return sLogList;
    }

    @Override
    public SLog save(SLog sLog) {
        SLogDao.save(sLog);
        return sLog;
    }




}

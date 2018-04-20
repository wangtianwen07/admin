/**
 * Date:2018年1月17日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.srv.role.impl;

import com.css.mgr.admin.dao.repository.*;
import com.css.mgr.admin.srv.role.SRoleService;
import com.css.mgr.base.dao.pojo.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Date:2018年1月17日
 * Description:
 * Author:litao
 */
@Service("roleService")
@Transactional
public class SRoleServiceImpl implements SRoleService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SRoleDao sRoleDao;
    @Autowired
    private SSysDao sSysDao;
    @Autowired
    private SFuncDao sFuncDao;
    @Autowired
    private SRoleResDao sRoleResDao;
    @Autowired
    private SResDao sResDao;

    @Override
    public Page<SRole> findAll(JSONObject paramJson, Pageable pageable) {

        Page<SRole> page = sRoleDao.findAll(new Specification<SRole>() {
            @Override
            public Predicate toPredicate(Root<SRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if(StringUtils.isNotEmpty(paramJson.getString("name"))) {
                    list.add(cb.like(root.get("name").as(String.class), "%" + paramJson.getString("name") + "%"));
                }
                if(StringUtils.isNotEmpty(paramJson.getString("sysId"))) {
                    list.add(cb.like(root.get("sysId").as(String.class), "%" + paramJson.getString("sysId") + "%"));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        },pageable);
        if(page!=null) logger.info("findAll size:"+page.getTotalElements());
        return page;
    }

    @Override
    public Page<SRole> listSerach(JSONObject paramJson, String searchValue, Pageable pageable) {
        Page<SRole> page = sRoleDao.findAll(new Specification<SRole>() {
            @Override
            public Predicate toPredicate(Root<SRole> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate sysId = cb.like(root.get("sysId").as(String.class), "%" + paramJson.getString("sysId") + "%");
                Predicate name = cb.like(root.get("name").as(String.class), "%" + paramJson.getString("name") + "%");

                List<Predicate> list = new ArrayList<Predicate>();

                list.add(cb.like(root.get("name").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("roleType").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("sysId").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("remark").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("openFlag").as(String.class), "%" + searchValue + "%"));
                Predicate[] p = new Predicate[list.size()];

                query.where(cb.and(sysId,name,cb.or(list.toArray(p))));
                return query.getRestriction();
            }
        },pageable);
        if(page!=null) logger.info("postFilteredPage size:"+page.getTotalElements());
        return page;
    }
    @Override
    public List<SSys> getAllSys(){
        return sSysDao.findByOpenFlag(OpenFlag.OPEN);
    }
    @Override
    public SRole save(SRole sRole){
        return sRoleDao.save(sRole);
    }
    @Override
    public void del(List<String> sRole){
        for (String uuid:sRole) {
            sRoleDao.delete(uuid);
        }
    }


    @Override
    public SRole getRoleByUuid(String uuid){
        return sRoleDao.findOne(uuid);
    }

    @Override
    public Page<SResource> findAllResByRole(JSONObject paramJson, Pageable pageable) {
        List <String> resList=sRoleResDao.getResourceIdsByRoleId(paramJson.getString("roleId"));
        Page<SResource> page =  sResDao.findAll(new Specification<SResource>() {
            @Override
            public Predicate toPredicate(Root<SResource> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if(resList.size()>0){
                    list.add(cb.in(root.get("uuid")).value(resList));
                }else{
                    list.add(cb.equal(root.get("uuid"),""));
                }
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        },pageable);
        if(page!=null) logger.info("findAll size:"+page.getTotalElements());
        return page;
    }

    @Override
    public Page<SResource> listSerachResByRole(JSONObject paramJson, String searchValue, Pageable pageable) {
        List <String> resList=sRoleResDao.getResourceIdsByRoleId(paramJson.getString("roleId"));
        Page<SResource> page = sResDao.findAll(new Specification<SResource>() {
            @Override
            public Predicate toPredicate(Root<SResource> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();

                if(resList.size()>0){
                    list.add(cb.in(root.get("uuid")).value(resList));
                }else{
                    list.add(cb.equal(root.get("uuid"),""));
                }
                Predicate[] p = new Predicate[list.size()];

                query.where(cb.and(cb.or(list.toArray(p))));
                return query.getRestriction();
            }
        },pageable);
        if(page!=null) logger.info("resFilteredPage size:"+page.getTotalElements());
        return page;
    }

    @Override
    public List<SRoleResource> saveRoleRes(List<SRoleResource> sRoleResources) {
        return  sRoleResDao.save(sRoleResources);
    }

    @Override
    public void delRoleRes(List<SRoleResource> sRoleResources) {
        for (SRoleResource sRoleResource1:sRoleResources) {
            sRoleResDao.deleteByRoleIdAndResourceId(sRoleResource1.getRoleId(),sRoleResource1.getResourceId());
        }
    }


}

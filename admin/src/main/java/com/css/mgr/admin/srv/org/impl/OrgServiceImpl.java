/**
 * Date:2018年1月17日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.srv.org.impl;

import com.css.mgr.admin.common.IDUtil;
import com.css.mgr.admin.dao.repository.*;
import com.css.mgr.admin.srv.org.ISOrgService;
import com.css.mgr.base.dao.pojo.*;
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
 * Date:2018年1月23日
 * Description:
 * Author:QinMing
 */
@Service("orgService")
@Transactional
public class OrgServiceImpl implements ISOrgService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SOrgDao sOrgDao;

    @Autowired
    private SUnitDao sUnitDao;

    @Autowired
    private SOrgPostDao sOrgPostDao;

    @Autowired
    private SPostDao sPostDao;

    @Autowired
    private SUserOrgDao sUserOrgDao;

    @Autowired
    private SUserDao sUserDao;

    public SOrg find(String orgid){
    	return sOrgDao.getOne(orgid);
    }
    @Override
    public Page<SOrg> findAll(JSONObject paramJson, Pageable pageable) {
        Page<SOrg> page = sOrgDao.findAll(new Specification<SOrg>() {
            @Override
            public Predicate toPredicate(Root<SOrg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if(StringUtils.isNotEmpty(paramJson.getString("code"))) {
                    list.add(cb.like(root.get("code").as(String.class), "%" + paramJson.getString("code") + "%"));
                }
                if(StringUtils.isNotEmpty(paramJson.getString("name"))) {
                    list.add(cb.like(root.get("name").as(String.class), "%" + paramJson.getString("name") + "%"));
                }
                if(StringUtils.isNotEmpty(paramJson.getString("parentId"))) {
                    list.add(cb.equal(root.get("parentId").as(String.class), paramJson.getString("parentId")));
                }
                list.add(cb.equal(root.get("delFlag").as(String.class), DelFlag.UN_DELETE_STATE));
                Predicate[] p = new Predicate[list.size()];
                return cb.and(list.toArray(p));
            }
        },pageable);
        if(page!=null) logger.info("findAll size:"+page.getTotalElements());
        return page;
    }

    @Override
    public Page<SOrg> listSerach(JSONObject paramJson, String searchValue, Pageable pageable) {
        Page<SOrg> page = sOrgDao.findAll(new Specification<SOrg>() {
            @Override
            public Predicate toPredicate(Root<SOrg> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate pCode = cb.like(root.get("code").as(String.class), "%" + paramJson.getString("code") + "%");
                Predicate pName = cb.like(root.get("name").as(String.class), "%" + paramJson.getString("name") + "%");
                Predicate parentId = cb.equal(root.get("parentId").as(String.class),  paramJson.getString("parentId"));
                Predicate delFlag = cb.equal(root.get("delFlag").as(String.class), DelFlag.UN_DELETE_STATE);

                List<Predicate> list = new ArrayList<Predicate>();

                list.add(cb.like(root.get("code").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("name").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("parentId").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("unitName").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("code").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("openFlag").as(String.class), "%" + searchValue + "%"));
                list.add(cb.like(root.get("orgType").as(String.class), "%" + searchValue + "%"));
                Predicate[] p = new Predicate[list.size()];

                query.where(cb.and(pCode,pName,delFlag,parentId,cb.or(list.toArray(p))));
                return query.getRestriction();
            }
        },pageable);
        if(page!=null) logger.info("postFilteredPage size:"+page.getTotalElements());
        return page;
    }

    @Override
    public List<SOrg> findAllByParentId(String parentId) {
        return sOrgDao.findByParentIdAndDelFlag(parentId, DelFlag.UN_DELETE_STATE);
    }

    @Override
    public SOrg findSorgByNearAndUnit(String orgId) {
        SOrg sOrg=null;
        if(StringUtils.isEmpty(orgId)){
            sOrg=sOrgDao.findByParentIdAndDelFlag(null, DelFlag.UN_DELETE_STATE).get(0);
        }else{
            sOrg=sOrgDao.findOne(orgId);
        }
        while (1==1){
            if(sOrg.getOrgType().equals(SOrg.ORG_TYPE_UNIT)){
                    return  sOrg;
            }else{
                if(StringUtils.isEmpty(sOrg.getParentId())){
                    return null;
                }
                sOrg=sOrgDao.findOne(sOrg.getParentId());
            }
        }
    }

    @Override
    public void delByUuid(String uuid) {
        List<SOrg> sOrgList=new ArrayList<SOrg>();
        sOrgList.add(sOrgDao.findOne(uuid));
        getAllSonOrgByOrgId(uuid,sOrgList);
        sOrgDao.delete(sOrgList);
    }

    private List<SOrg> getAllSonOrgByOrgId(String orgId,List<SOrg> returnList){
        for (SOrg sOrg:sOrgDao.findByParentIdAndDelFlag(orgId, DelFlag.UN_DELETE_STATE)) {
            returnList.add(sOrg);
            if(sOrgDao.findByParentIdAndDelFlag(sOrg.getUuid(), DelFlag.UN_DELETE_STATE).size()>0){
                getAllSonOrgByOrgId(sOrg.getUuid(),returnList);
            }
        };
        return returnList;
    }
    @Override
    public SOrg getSOrgByUuid(String uuid) {
        return sOrgDao.findOne(uuid);
    }

    @Override
    public SOrg save(SOrg sOrg) {
        if(StringUtils.isEmpty(sOrg.getUuid())){
            sOrg.setUuid(IDUtil.getId());
            if(sOrg.getOrgType().equals(SOrg.ORG_TYPE_UNIT)){
                SUnit sUnit=new SUnit();
                sUnit.setUuid(IDUtil.getId());
                sUnit.setUnitName(sOrg.getName());
                sUnit.setParentId(sOrg.getUnitId());
                sUnit.setUnitCode(sOrg.getOrgCode());
                sUnitDao.save(sUnit);
                sOrg.setUnitCode(sUnit.getUnitCode());
                sOrg.setUnitName(sUnit.getUnitName());
                sOrg.setUnitId(sUnit.getUuid());
            }
        }
        return sOrgDao.save(sOrg);
    }


    @Override
    public String getTreeHtml() {
        StringBuffer html=new StringBuffer();
        int frequency=0;
        return stitchingHtml(null,html,frequency).toString();
    }

    private StringBuffer stitchingHtml(String ParentId,StringBuffer html,int frequency){
        List<SOrg> sOrgList=sOrgDao.findByParentIdAndDelFlag(ParentId, DelFlag.UN_DELETE_STATE);
        List<SOrg> sOrgPIDList=new ArrayList<SOrg>();
        for (SOrg sOrg:sOrgList) {
            sOrgPIDList=sOrgDao.findByParentIdAndDelFlag(sOrg.getParentId(), DelFlag.UN_DELETE_STATE);
            if(sOrgPIDList.size()>0){
                frequency++;
                html.append(" <li rel='"+sOrg.getUuid()+"' ");
                if(frequency<2){
                    html.append(" data-jstree='{\"opened\":true}' ");
                }
                html.append(" >"+sOrg.getName()+"<ul> ");
                stitchingHtml(sOrg.getUuid(),html,frequency);
                html.append("</ul></li>");
            }else{
                html.append("<li rel='"+sOrg.getUuid()+"'  data-jstree='{\"type\":\"file\"}'>"+sOrg.getName()+"</li>");
            };
        }
        return html;
    }

    @Override
    public Page<SPost> findAllPostByOrg(JSONObject paramJson, Pageable pageable) {
        List <String> postList=sOrgPostDao.getPostIdsByOrgId(paramJson.getString("orgId"));
        Page<SPost> page =  sPostDao.findAll(new Specification<SPost>() {
            @Override
            public Predicate toPredicate(Root<SPost> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if(postList.size()>0){
                    list.add(cb.in(root.get("uuid")).value(postList));
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
    public Page<SPost> listSerachPostByOrg(JSONObject paramJson, String searchValue, Pageable pageable) {
        List <String> postList=sOrgPostDao.getPostIdsByOrgId(paramJson.getString("orgId"));
        Page<SPost> page = sPostDao.findAll(new Specification<SPost>() {
            @Override
            public Predicate toPredicate(Root<SPost> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();

                if(postList.size()>0){
                    list.add(cb.in(root.get("uuid")).value(postList));
                }else{
                    list.add(cb.equal(root.get("uuid"),""));
                }
                Predicate[] p = new Predicate[list.size()];

                query.where(cb.and(cb.or(list.toArray(p))));
                return query.getRestriction();
            }
        },pageable);
        if(page!=null) logger.info("postFilteredPage size:"+page.getTotalElements());
        return page;
    }

    @Override
    public List<SOrgPost> saveSprgPost(List<SOrgPost> sOrgPost) {
        return  sOrgPostDao.save(sOrgPost);
    }

    @Override
    public void delSorgPost(List<SOrgPost> sOrgPost) {
        for (SOrgPost sorgPost:sOrgPost) {
            sOrgPostDao.deleteByOrgIdAndPostId(sorgPost.getOrgId(),sorgPost.getPostId());
        }
    }




    @Override
    public  Page<SUser> findAllUserByOrg(JSONObject paramJson, Pageable pageable){
        List <String> userList=sUserOrgDao.getUserIdsByOrgId(paramJson.getString("orgId"));
        Page<SUser> page =  sUserDao.findAll(new Specification<SUser>() {
            @Override
            public Predicate toPredicate(Root<SUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();
                if(userList.size()>0){
                    list.add(cb.in(root.get("uuid")).value(userList));
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
    public  Page<SUser> listSerachUserByOrg(JSONObject paramJson,String searchValue,Pageable pageable){
        List <String> userList=sUserOrgDao.getUserIdsByOrgId(paramJson.getString("orgId"));
        Page<SUser> page = sUserDao.findAll(new Specification<SUser>() {
            @Override
            public Predicate toPredicate(Root<SUser> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> list = new ArrayList<Predicate>();

                if(userList.size()>0){
                    list.add(cb.in(root.get("uuid")).value(userList));
                }else{
                    list.add(cb.equal(root.get("uuid"),""));
                }
                Predicate[] p = new Predicate[list.size()];

                query.where(cb.and(cb.or(list.toArray(p))));
                return query.getRestriction();
            }
        },pageable);
        if(page!=null) logger.info("userFilteredPage size:"+page.getTotalElements());
        return page;
    }

    @Override
    public List<SUserOrg> saveSprgUser(List<SUserOrg> sUserOrg){
        return  sUserOrgDao.save(sUserOrg);
    }

    @Override
    public void delSorgUser(List<SUserOrg> sUserOrg){
        for (SUserOrg suserOrg:sUserOrg) {
            sUserOrgDao.deleteByOrgIdAndUserId(suserOrg.getOrgId(),suserOrg.getUserId());
        }
    }

}

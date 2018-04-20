/**
 * Date:2018年1月17日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.srv.org;

import com.css.mgr.base.dao.pojo.*;
import net.sf.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Date:2018年1月23日
 * Description:
 * Author:QinMing
 */
public interface ISOrgService {
    public SOrg find(String orgid);
    /**
     * 获取树的HTML
     * @return
     */
    String getTreeHtml();

    Page<SOrg> findAll(JSONObject paramJson, Pageable pageable);

    //条件、分页查询
    Page<SOrg> listSerach(JSONObject paramJson,String searchValue,Pageable pageable);

    //根据父ID查询Sorg集合
    List<SOrg> findAllByParentId(String parentId);

    //根据SOrgId获取最接近的单位信息
    SOrg findSorgByNearAndUnit(String orgId);

    /**
     * 根据ID删除组织
     */
    void delByUuid(String uuid);

    /**
     * 根据UUID获取组织信息
     * @param uuid
     * @return
     */
    SOrg getSOrgByUuid(String uuid);

    /**
     * 添加组织
     * @param sOrg
     * @return
     */
    SOrg save(SOrg sOrg);


    /**
     * 根据组织ID获取相关联的岗位信息
     * @return
     */

    Page<SPost> findAllPostByOrg(JSONObject paramJson, Pageable pageable);

    /**
     * 根据组织ID获取相关联的岗位信息 条件、分页查询
     * @return
     */
    Page<SPost> listSerachPostByOrg(JSONObject paramJson,String searchValue,Pageable pageable);


    /**
     * 添加组织与岗位关联表
     * @return
     */
    List<SOrgPost> saveSprgPost(List<SOrgPost> sOrgPost);


    /**
     * 删除组织与岗位关联表
     * @return
     */
    void delSorgPost(List<SOrgPost> sOrgPost);




    /**
     * 根据组织ID获取相关联的用户
     * @return
     */

    Page<SUser> findAllUserByOrg(JSONObject paramJson, Pageable pageable);

    /**
     * 根据组织ID获取相关联的用户信息 条件、分页查询
     * @return
     */
    Page<SUser> listSerachUserByOrg(JSONObject paramJson,String searchValue,Pageable pageable);


    /**
     * 添加组织与用户关联表
     * @return
     */
    List<SUserOrg> saveSprgUser(List<SUserOrg> sUserOrg);


    /**
     * 删除组织与用户关联表
     * @return
     */
    void delSorgUser(List<SUserOrg> sUserOrg);

//    /**
//     * 查询全部组织信息
//     */
//    List<SOrg> getAllSOrgList();
//    /**
//     * 根据父组织ID获取父组织下全部组织信息
//     */
//    List<SOrg> getSOrgListByParentId(String parentId);
//    /**
//     * 查询组织信息
//     */
//    List<SOrg> getSOrgList();
}

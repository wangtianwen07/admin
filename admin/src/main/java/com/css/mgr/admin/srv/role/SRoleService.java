/**
 * Date:2018年1月17日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.srv.role;

import com.css.mgr.base.dao.pojo.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Date:2018年1月17日
 * Description:
 * Author:litao
 */
public interface SRoleService {

    Page<SRole> findAll(JSONObject paramJson, Pageable pageable);

    //条件、分页查询
    Page<SRole> listSerach(JSONObject paramJson, String searchValue, Pageable pageable);

    //获取所有系统
    List<SSys> getAllSys();

    //添加角色
    SRole save(SRole sRole);

    //删除角色
    void del(List<String> sRole);

    //获取单个角色
    SRole getRoleByUuid(String uuid);


    /**
     * 根据组织ID获取相关联的岗位信息
     * @return
     */

    Page<SResource> findAllResByRole(JSONObject paramJson, Pageable pageable);

    /**
     * 根据组织ID获取相关联的岗位信息 条件、分页查询
     * @return
     */
    Page<SResource> listSerachResByRole(JSONObject paramJson,String searchValue,Pageable pageable);


    /**
     * 添加组织与岗位关联表
     * @return
     */
    List<SRoleResource> saveRoleRes(List<SRoleResource> sRoleResource);


    /**
     * 删除组织与岗位关联表
     * @return
     */
    void delRoleRes(List<SRoleResource> sRoleResource);
}

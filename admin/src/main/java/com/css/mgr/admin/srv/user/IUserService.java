/**
 * Date:2018年1月17日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.srv.user;

import com.css.mgr.base.dao.pojo.SUser;
import com.css.mgr.base.dao.pojo.SUserOrg;
import com.css.mgr.base.dao.pojo.SUserPost;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Date:2018年1月17日
 * Description:
 * Author:QinMing
 */
public interface IUserService {

	public JSONObject loadSelect();
	
    Page<SUser> findAll(JSONObject paramJson, Pageable pageable);

    //条件、分页查询
    Page<SUser> listSerach(JSONObject paramJson, String searchValue, Pageable pageable);
    
    public Page<Object[]> findAllByOrgId(String orgId, Pageable pageable);
	
    //根据id删除suser数据
	void delById(String userId,List<String> postIds);
	
	//通过用户id找到对应postId
	List<String> findSUserPostByUserId(String userId);
	
	//分页查询
	public Page<Map<String,String>> findAllByOrgIdMap(String orgId,String realName,String activeStatus,String openFlag,Pageable pageable);
	
	//用户id查找postName
	public List<String> getPostsByUserId(String userId);
	
	//通过用户id找到用户对象
	public SUser getSuserById(String userId);
	
	//保存
    public SUser save(SUser suser);
    
    //维护组织用户关系表
    public SUserOrg saveUserOrg(SUserOrg sUserOrg);
    
  
     //添加用户与岗位关联表
    List<SUserPost> saveSUrPost(List<SUserPost> sUserPost);
    
    //根据用户uuid得到该用户下的岗位
	 public JSONArray findUserPosts(String userId);
    
	 //删除用户与组织的关系
	  void delSuserPost(List<SUserPost> suserPost);
    
	  //验证登录名是否是一致的
	public boolean checkLoginName(String loginName);

	/**
	 * 验证是否成功如果成功即注册
	 * @return
	 */
	JSONObject checkLoginNameAndMobile(SUser sUser);
}
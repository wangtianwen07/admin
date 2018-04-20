package com.css.mgr.admin.ctrl.user;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.css.mgr.admin.common.IDUtil;
import com.css.mgr.admin.srv.dict.IDictService;
import com.css.mgr.admin.srv.org.ISOrgService;
import com.css.mgr.admin.srv.post.IPostService;
import com.css.mgr.admin.srv.user.IUserService;
import com.css.mgr.base.CssController;
import com.css.mgr.base.dao.pojo.SOrg;
import com.css.mgr.base.dao.pojo.SUser;
import com.css.mgr.base.dao.pojo.SUserOrg;
import com.css.mgr.base.dao.pojo.SUserPost;
import com.css.mgr.conf.Md5Utils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Controller
@RequestMapping("/user")
public class UserController extends CssController{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("userService")
	private IUserService userService;
	
	@Autowired
	@Qualifier("orgService")
	private ISOrgService orgService;
	
	@Autowired
	@Qualifier("postService")
	private IPostService postService;
	
	@Autowired
	private IDictService dictService;
	
	@RequestMapping(value="/user_dir",method=RequestMethod.GET)
	public String dir(Model model){
		return "admin/user/user_dir";
	}
	
	@SuppressWarnings("unused")
	@RequestMapping(value="/user_tree",method=RequestMethod.GET)
	public String tree(Model model,HttpServletRequest request){
		String orgId=request.getParameter("orgId");
		model.addAttribute("orgTreeHtml",orgService.getTreeHtml());
		return "admin/user/user_tree";
	}
    
	
	@RequestMapping(value="/delUserOrgPost",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> deluserPost(String uuid){
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
		    List<String> findSUserPostByUserId = userService.findSUserPostByUserId(uuid);
			userService.delById(uuid, findSUserPostByUserId);
			map.put("success", "1");
		} catch (Exception e) {
			map.put("", "0");
		}
		return map;
	}
	
	@RequestMapping(value="/get",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> get(String uuid,String orgId){
		logger.info("uuid:"+uuid);		 
		Map<Object,Object> map =new HashMap<Object,Object>();
		try {
		 SUser sUser = userService.getSuserById(uuid);
		 Date issueDate = sUser.getIssueDate();
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");//定义要输出日期字符串的格式
   	     String startTimeSting  = sdf.format(issueDate);
		 SOrg sOrg = orgService.getSOrgByUuid(orgId);
		List<String> postIds = userService.findSUserPostByUserId(uuid);
		if(postIds.size()!=0) {
		String posts=String.join(",",postIds);
	    	map.put("postName", posts);
		}
	    map.put("sUser", sUser);
	    map.put("startTimeSting", startTimeSting);
    	map.put("orgName",sOrg.getName());
    	map.put("success", "1");
		  }catch (Exception e) {
		map.put("error", "0");
	   }
		return map;
	}
	
	@RequestMapping(value="/getOrg",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> get(String orgId){
		logger.info("orgId:"+orgId);		 
		Map<Object,Object> map =new HashMap<Object,Object>();
		try {
		 SOrg sOrg = orgService.getSOrgByUuid(orgId);
    	map.put("orgName",sOrg.getName());
    	map.put("orgId", orgId);
    	map.put("success", "1");
		  }catch (Exception e) {
		map.put("error", "0");
	   }
		return map;
	}
	
	
	@RequestMapping(value="/add",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> add(SUser suser) {
		SUserOrg sUserOrg=new SUserOrg();
		Map<Object,Object> map = new HashMap<Object,Object>();
		    
		try {
			if(suser.getUuid()==null) {
			suser.setUuid(IDUtil.getId());
			suser.setPassword(Md5Utils.md5(suser.getPassword()));
			if(sUserOrg.getUuid()==null) {
				sUserOrg.setUuid(IDUtil.getId());
				sUserOrg.setUserId(suser.getUuid());
				sUserOrg.setOrgId(suser.getOrgId());
				userService.saveUserOrg(sUserOrg);
				}
			}
			userService.save(suser);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	
	@RequestMapping(value="/reloadTree",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> reloadTree(HttpServletRequest request){
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("orgTreeHtml", orgService.getTreeHtml());
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("error", "0");
		}
		return map;
	}
	
	
	@RequestMapping(value="/query",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> query(HttpServletRequest request) {
		Sort.Direction orderDir = Sort.Direction.ASC;
		String columnName = "uuid";
		int draw = Integer.parseInt(request.getParameter("draw"));//请求次数
		int start = Integer.parseInt(request.getParameter("start"));//数据起始位置
		int length = Integer.parseInt(request.getParameter("length"));//数据长度
		String order = request.getParameter("order[0][column]");//获取需要排序的字段索引
		String dir = request.getParameter("order[0][dir]");//获取需要排序字段的排序方向
		String column = request.getParameter("columns["+order+"][data]");//根据索引获取字段名称
		@SuppressWarnings("unused")
		
		String searchValue = request.getParameter("search[value]");//获取用户过滤框里的字符
		String realName = request.getParameter("name");
		String orgId = request.getParameter("orgId");
		String activeStatus=request.getParameter("activeStatus");
		String openFlag=request.getParameter("openFlag");
         
		  
		JSONObject paramJson = new JSONObject();
		paramJson.put("realName", realName);
		paramJson.put("orgId", orgId);
		paramJson.put("activeStatus", activeStatus);
		paramJson.put("openFlag", openFlag);
		
		logger.info("paramJson:"+paramJson);
		if(IDUtil.isNotEmpty(dir) && !dir.equals("asc")) {
			orderDir = Sort.Direction.DESC;
		}
		if(IDUtil.isNotEmpty(column) && column != "") {
			columnName = column;
		}
		//总记录数
		String recordsTotal = "0";
		//过滤后记录数
		String recordsFiltered = "0";
		int page = start / length;
		Sort sort = new Sort(orderDir,columnName);
		Pageable pageable = new PageRequest(page,length,sort);
		 Page<Map<String, String>> userOrgPostPageFiltered=null;
		 Map<Object,Object> pageInfo = new HashMap<Object,Object>();
           userOrgPostPageFiltered = userService.findAllByOrgIdMap(orgId, realName, activeStatus, openFlag, pageable);
           for (Map<String, String> map : userOrgPostPageFiltered.getContent()) {
                map.put("openFlag", dictService.getDictTypeAndCode("USER_STATUS",map.get("openFlag")));  
               	map.put("sex", dictService.getDictTypeAndCode("S_SEX", map.get("sex")));
               	map.put("userType", dictService.getDictTypeAndCode("USER_TYPE", map.get("userType")));
               	map.put("activeStatus", dictService.getDictTypeAndCode("d_active_status",map.get("activeStatus")));
               	map.put("secLevel",dictService.getDictTypeAndCode("fw_d_security_level",map.get("secLevel")));
   			List<String> postsList = userService.getPostsByUserId(map.get("uuid"));
           	String posts=String.join(",",postsList);
           	logger.info(posts);
           	map.put("postName", posts);
 
           }
           	if(userOrgPostPageFiltered!=null && userOrgPostPageFiltered.getTotalElements()>0) {
    			logger.info(">>>>>>postPageFiltered:"+userOrgPostPageFiltered.getTotalElements());
    			recordsFiltered = String.valueOf(userOrgPostPageFiltered.getTotalElements());
    		}
           	if(userOrgPostPageFiltered!=null) {
    		pageInfo.put("data", userOrgPostPageFiltered.getContent());
           	}
    		pageInfo.put("draw", draw);
    		pageInfo.put("recordsTotal", recordsTotal);
    		pageInfo.put("recordsFiltered", recordsFiltered);
		return pageInfo;
	}
	
	
	@RequestMapping(value="/checkLoginName",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> checkLoginNames(String loginName){
		logger.info("loginName:"+loginName);		 
		Map<Object,Object> map =new HashMap<Object,Object>();
		boolean checkLoginName = userService.checkLoginName(loginName); 
		 if(checkLoginName) {
    	  map.put("success", "1");}
		 else{
    		 map.put("error", "0");
    	 }  
		return map;
	}
	
	@RequestMapping(value="/queryAll",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> query1(HttpServletRequest request) {
		Sort.Direction orderDir = Sort.Direction.ASC;
		String columnName = "uuid";
		int draw = Integer.parseInt(request.getParameter("draw"));//请求次数
		int start = Integer.parseInt(request.getParameter("start"));//数据起始位置
		int length = Integer.parseInt(request.getParameter("length"));//数据长度
		String order = request.getParameter("order[0][column]");//获取需要排序的字段索引
		String dir = request.getParameter("order[0][dir]");//获取需要排序字段的排序方向
		String column = request.getParameter("columns["+order+"][data]");//根据索引获取字段名称
		@SuppressWarnings("unused")
		String searchValue = request.getParameter("search[value]");//获取用户过滤框里的字符
		String orgId = request.getParameter("orgId");
		String realName=request.getParameter("realName");
		  
		JSONObject paramJson = new JSONObject();
		paramJson.put("orgId", orgId);
		paramJson.put("realName", realName);
		logger.info("paramJson:"+paramJson);
		if(IDUtil.isNotEmpty(dir) && !dir.equals("asc")) {
			orderDir = Sort.Direction.DESC;
		}
		if(IDUtil.isNotEmpty(column) && column != "") {
			columnName = column;
		}
		//总记录数
		String recordsTotal = "0";
		//过滤后记录数
		String recordsFiltered = "0";
		int page = start / length;
		Sort sort = new Sort(orderDir,columnName);
		Pageable pageable = new PageRequest(page,length,sort);
		 Map<Object,Object> pageInfo = new HashMap<Object,Object>();

			Page<SUser> userPageFilter = userService.findAll(paramJson, pageable);
			if(userPageFilter!=null && userPageFilter.getTotalElements()>0) {
    			logger.info(">>>>>>postPageFiltered:"+userPageFilter.getTotalElements());
    			recordsFiltered = String.valueOf(userPageFilter.getTotalElements());
    		}
    		pageInfo.put("data", userPageFilter.getContent());
    		pageInfo.put("draw", draw);
    		pageInfo.put("recordsTotal", recordsTotal);
    		pageInfo.put("recordsFiltered", recordsFiltered);
	      	return pageInfo;
	}
	
	
	@RequestMapping(value="/queryPost",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> queryPost(HttpServletRequest request) {
		String userId = request.getParameter("userId");
		logger.info("userId:"+userId);
		JSONArray datas = userService.findUserPosts(userId);
		Map<Object,Object> pageInfo = new HashMap<Object,Object>();
		pageInfo.put("data", datas);
		return pageInfo;
	}
	
	@RequestMapping(value="/addPost",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> addPost(String userId,String postIds){
		List<SUserPost> suserPostList=new ArrayList<SUserPost>();
		SUserPost userPost=null;

		for (String postId: postIds.split(",")) {
			if(StringUtils.isNotEmpty(postId)){
				userPost=new SUserPost();
				userPost.setUserId(userId);
				userPost.setPostId(postId);
				userPost.setUuid(IDUtil.getId());
				suserPostList.add(userPost);
			}
		}
		List<SUserPost> sUserPostLists=userService.saveSUrPost(suserPostList);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("suserPostList", sUserPostLists);
			map.put("success", "1");
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	@RequestMapping(value="/delUserPost",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> delOrgPost(String userId,String postIds){
		List<SUserPost> suserPostList=new ArrayList<SUserPost>();
		SUserPost userPost=null;
		for (String postId: postIds.split(",")) {
			if(StringUtils.isNotEmpty(postId)){
				userPost=new SUserPost();
				userPost.setUserId(userId);
				userPost.setPostId(postId);
				suserPostList.add(userPost);
			}
		}
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			userService.delSuserPost(suserPostList);
			map.put("success", "1");
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	@ResponseBody
	@RequestMapping(value="/loadSelect",method=RequestMethod.GET)
	public String loadSelect() {
		return userService.loadSelect().toString();
	}
}
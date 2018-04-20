/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.ctrl.post;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.css.mgr.admin.common.IDUtil;
import com.css.mgr.admin.common.JsonUtil;
import com.css.mgr.admin.srv.dict.IDictService;
import com.css.mgr.admin.srv.post.IPostService;
import com.css.mgr.base.CssController;
import com.css.mgr.base.dao.pojo.SPost;
import com.css.mgr.base.dao.pojo.SRole;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author wangtianwen
 * 2018年1月22日
 */
@Controller
@RequestMapping("/post")
public class PostController extends CssController{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IPostService postService;
	
	@Autowired
	private IDictService dictService;
	
	@RequestMapping(value="/dir",method=RequestMethod.GET)
	public String dir(Model model){
		logger.info(">>>>>>user:"+getUser().getRealName());
		JSONObject dict=new JSONObject();
		dict.put("openflag",dictService.getDictsByTableName("d_openFlag"));
		dict.put("postDuties", dictService.getDictsByTableName("POST_DUTIES"));
		dict.put("roleStatus",dictService.getDictsByTableName("ROLE_STATUS"));
		dict.put("postType", dictService.getDictsByTableName("POST_TYPE"));
		model.addAttribute("dict", dict);
		return "admin/post/post_dir";
	}
	
	//@RequestMapping(value="/del",method=RequestMethod.DELETE)不支持DELETE
	//{"timestamp":1516872251856,"status":403,"error":"Forbidden","message":"Invalid CSRF Token 'null' was found on the request parameter '_csrf' or header 'X-CSRF-TOKEN'.","path":"/post/del"}
	@RequestMapping(value="/del",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> del(SPost post){
		String uuid = post.getUuid();
		logger.info("uuid:"+uuid);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			postService.delByUuid(uuid);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	@RequestMapping(value="/get",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> get(String uuid){
		logger.info("uuid:"+uuid);
		SPost sPost = postService.getSPostByUuid(uuid);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("sPost", sPost);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	@RequestMapping(value="/add",method=RequestMethod.POST)
	@ResponseBody
	public String add(SPost post) {
		if(StringUtils.isEmpty(post.getUuid())) {
			post.setUuid(IDUtil.getId());
		}
		try {
			post = postService.save(post);
		} catch (Exception e) {
		}
		return JsonUtil.success("成功", post).toString();
	}
	
	@RequestMapping(value="/bindRole",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> bindRole(HttpServletRequest request) {
		String roleUuid = request.getParameter("roleUuid");
		String postUuid = request.getParameter("postUuid");
		logger.info(roleUuid+":"+postUuid);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			postService.bindRole(postUuid, roleUuid);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	@RequestMapping(value="/cancelRole",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> cancelRole(HttpServletRequest request){
		String sPostRoleUuid = request.getParameter("sPostRoleUuid");
		logger.info("sPostRoleUuid:"+sPostRoleUuid);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			postService.cancelRole(sPostRoleUuid);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	@RequestMapping(value="/allRole",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> allRole(HttpServletRequest request) {
		List<SRole> datas = postService.findAllRole();
		logger.info("datas:"+datas);
		Map<Object,Object> pageInfo = new HashMap<Object,Object>();
		pageInfo.put("data", datas);
		return pageInfo;
	}
	
	@RequestMapping(value="/queryRole",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> queryRole(HttpServletRequest request) {
		String postId = request.getParameter("postId");
		logger.info("postId:"+postId);
		JSONArray datas = postService.findRelatedRole(postId);
		Map<Object,Object> pageInfo = new HashMap<Object,Object>();
		pageInfo.put("data", datas);
		return pageInfo;
	}
	
	@RequestMapping(value="/query",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> query(HttpServletRequest request) {
		Set<String> keySet = request.getParameterMap().keySet();
		for(String keyName : keySet) {
			logger.info("keyName:"+keyName);
		}
		Direction orderDir = Direction.ASC; 
		String columnName = "uuid";
		int draw = Integer.parseInt(request.getParameter("draw"));//请求次数
		int start = Integer.parseInt(request.getParameter("start"));//数据起始位置
		int length = Integer.parseInt(request.getParameter("length"));//数据长度
		String order = request.getParameter("order[0][column]");//获取需要排序的字段索引
		String dir = request.getParameter("order[0][dir]");//获取需要排序字段的排序方向
		String column = request.getParameter("columns["+order+"][data]");//根据索引获取字段名称
	    String searchValue = request.getParameter("search[value]");//获取用户过滤框里的字符
	    String code = request.getParameter("code");
	    String name = request.getParameter("name");
	    
	    JSONObject paramJson = new JSONObject();
	    paramJson.put("code", code);
	    paramJson.put("name", name);
	    logger.info("paramJson:"+paramJson);
	    if(StringUtils.isNotEmpty(dir) && !dir.equals("asc")) {
			orderDir = Direction.DESC;
		}
		if(StringUtils.isNotEmpty(column)) {
			columnName = column;
		}
		 //总记录数
	    String recordsTotal = "0";
	    //过滤后记录数
	    String recordsFiltered = "";
	    int page = start / length;
	    Sort sort = new Sort(orderDir, columnName);
	    Pageable pageable = new PageRequest(page,length,sort);
	    
	    Page<SPost> postPageAll = postService.findAll(paramJson,pageable);
	    
	    Page<SPost> postPageFiltered = postService.listSerach(paramJson,searchValue, pageable);
	    
		if(postPageAll!=null && postPageAll.getTotalElements()>0) {
			logger.info(">>>>>>postPageAll:"+postPageAll.getTotalElements());
			recordsTotal = String.valueOf(postPageAll.getTotalElements());
		}
		if(postPageFiltered!=null && postPageFiltered.getTotalElements()>0) {
			logger.info(">>>>>>postPageFiltered:"+postPageFiltered.getTotalElements());
			recordsFiltered = String.valueOf(postPageFiltered.getTotalElements());
		}
		Map<Object,Object> pageInfo = new HashMap<Object,Object>();
		pageInfo.put("data", postPageFiltered.getContent());
		pageInfo.put("draw", draw);
		pageInfo.put("recordsTotal", recordsTotal);
		pageInfo.put("recordsFiltered", recordsFiltered);
		return pageInfo;
	}
}

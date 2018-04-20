package com.css.mgr.admin.ctrl.org;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.css.mgr.base.CssController;
import com.css.mgr.base.dao.pojo.OpenFlag;
import com.css.mgr.base.dao.pojo.SOrg;
import com.css.mgr.base.dao.pojo.SOrgPost;
import com.css.mgr.base.dao.pojo.SPost;
import com.css.mgr.base.dao.pojo.SUser;
import com.css.mgr.base.dao.pojo.SUserOrg;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/org")
public class OrgController extends CssController{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@Autowired
	@Qualifier("orgService")
	private ISOrgService orgService;
	
	@Autowired
	private IDictService dictService;

	@RequestMapping(value="/dir",method=RequestMethod.GET)
	public String dir(Model model){
		logger.info(">>>>>>user:"+getUser().getRealName());

		model.addAttribute("orgTreeHtml",orgService.getTreeHtml());
		SOrg sOrg=orgService.findAllByParentId(null).get(0);
		model.addAttribute("unitName",sOrg.getUnitName());
		model.addAttribute("unitCode",sOrg.getUnitCode());
		model.addAttribute("unitId",sOrg.getUnitId());
		model.addAttribute("findSorg",sOrg);
		model.addAttribute("openFlag", OpenFlag.OPEN);
		model.addAttribute("colseFlag",OpenFlag.CLOSE);
		model.addAttribute("orgTypeMgr",SOrg.ORG_TYPE_MGR);
		model.addAttribute("orgTypeBus",SOrg.ORG_TYPE_BUS);
		model.addAttribute("orgTypeUnit",SOrg.ORG_TYPE_UNIT);
		
		JSONObject dict=new JSONObject();
		dict.put("openflag",dictService.getDictsByTableName("d_openFlag"));
		dict.put("orgType", dictService.getDictsByTableName("d_org_type"));
		model.addAttribute("dict", dict);
		return "admin/org/dir";
	}
	@RequestMapping(value="/query",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> query(HttpServletRequest request) {
		Set<String> keySet = request.getParameterMap().keySet();
		for(String keyName : keySet) {
			logger.info("keyName:"+keyName);
		}
		Sort.Direction orderDir = Sort.Direction.ASC;
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
		String parentId = request.getParameter("parentId");
		JSONObject paramJson = new JSONObject();
		paramJson.put("code", code);
		paramJson.put("name", name);
		paramJson.put("parentId", parentId);

		logger.info("paramJson:"+paramJson);
		if(IDUtil.isNotEmpty(dir)&& !dir.equals("asc")) {
			orderDir = Sort.Direction.DESC;
		}
		if(IDUtil.isNotEmpty(column)&& column != "") {
			columnName = column;
		}
		//总记录数
		String recordsTotal = "0";
		//过滤后记录数
		String recordsFiltered = "0";
		int page = start / length;
		Sort sort = new Sort(orderDir, columnName);
		Pageable pageable = new PageRequest(page,length,sort);

		Page<SOrg> orgPageAll = orgService.findAll(paramJson,pageable);

		Page<SOrg> orgPageFiltered = orgService.listSerach(paramJson,searchValue, pageable);


		if(orgPageAll!=null && orgPageAll.getTotalElements()>0) {
			logger.info(">>>>>>orgPageAll:"+orgPageAll.getTotalElements());
			recordsTotal = String.valueOf(orgPageAll.getTotalElements());
		}
		if(orgPageFiltered!=null && orgPageFiltered.getTotalElements()>0) {
			logger.info(">>>>>>postPageFiltered:"+orgPageFiltered.getTotalElements());
			recordsFiltered = String.valueOf(orgPageFiltered.getTotalElements());
		}
		Map<Object,Object> pageInfo = new HashMap<Object,Object>();
		pageInfo.put("data", orgPageFiltered.getContent());
		pageInfo.put("draw", draw);
		pageInfo.put("recordsTotal", recordsTotal);
		pageInfo.put("recordsFiltered", recordsFiltered);
		return pageInfo;
	}

	@RequestMapping(value="/get",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> get(String uuid){
		logger.info("uuid:"+uuid);
		SOrg sOrg = orgService.getSOrgByUuid(uuid);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("sOrg", sOrg);
			map.put("success", "1");
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}

	@RequestMapping(value="/del",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> del(String uuid){
		logger.info("uuid:"+uuid);
		orgService.delByUuid(uuid);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("success", "1");
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}

	@RequestMapping(value="/add",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> add(SOrg sOrg){
		logger.info("sOrg:"+sOrg);
		sOrg=orgService.save(sOrg);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("sOrg", sOrg);
			map.put("success", "1");
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}

	@RequestMapping(value="/getUnitSOrg",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> getUnitSOrg(String parentId){
		SOrg unitSorg=orgService.findSorgByNearAndUnit(parentId);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("unitSorg", unitSorg);
			map.put("success", "1");
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}

	@RequestMapping(value="/refreshOrgTree",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> refreshOrgTree(){
		String orgTreeHtml=orgService.getTreeHtml();
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("orgTreeHtml", orgTreeHtml);
			map.put("success", "1");
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}



	@RequestMapping(value="/queryPost",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> queryPost(HttpServletRequest request) {
		Set<String> keySet = request.getParameterMap().keySet();
		for(String keyName : keySet) {
			logger.info("keyName:"+keyName);
		}
		Sort.Direction orderDir = Sort.Direction.ASC;
		String columnName = "uuid";
		int draw = Integer.parseInt(request.getParameter("draw"));//请求次数
		int start = Integer.parseInt(request.getParameter("start"));//数据起始位置
		int length = Integer.parseInt(request.getParameter("length"));//数据长度
		String order = request.getParameter("order[0][column]");//获取需要排序的字段索引
		String dir = request.getParameter("order[0][dir]");//获取需要排序字段的排序方向
		String column = request.getParameter("columns["+order+"][data]");//根据索引获取字段名称
		String searchValue = request.getParameter("search[value]");//获取用户过滤框里的字符
		String orgId = request.getParameter("orgId");
		JSONObject paramJson = new JSONObject();
		paramJson.put("orgId", orgId);

		logger.info("paramJson:"+paramJson);
		if(StringUtils.isNotEmpty(dir) && !dir.equals("asc")) {
			orderDir = Sort.Direction.DESC;
		}
		if(StringUtils.isNotEmpty(column)) {
			columnName = column;
		}
		//总记录数
		String recordsTotal = "0";
		//过滤后记录数
		String recordsFiltered = "0";
		int page = start / length;
		Sort sort = new Sort(orderDir, columnName);
		Pageable pageable = new PageRequest(page,length,sort);

		Page<SPost> sPostPageAll = orgService.findAllPostByOrg(paramJson,pageable);

		Page<SPost> sPostPageFiltered = orgService.listSerachPostByOrg(paramJson,searchValue, pageable);


		if(sPostPageAll!=null && sPostPageAll.getTotalElements()>0) {
			logger.info(">>>>>>sPostPageAll:"+sPostPageAll.getTotalElements());
			recordsTotal = String.valueOf(sPostPageAll.getTotalElements());
		}
		if(sPostPageFiltered!=null && sPostPageFiltered.getTotalElements()>0) {
			logger.info(">>>>>>postPageFiltered:"+sPostPageFiltered.getTotalElements());
			recordsFiltered = String.valueOf(sPostPageFiltered.getTotalElements());
		}
		Map<Object,Object> pageInfo = new HashMap<Object,Object>();
		pageInfo.put("data", sPostPageFiltered.getContent());
		pageInfo.put("draw", draw);
		pageInfo.put("recordsTotal", recordsTotal);
		pageInfo.put("recordsFiltered", recordsFiltered);
		return pageInfo;
	}

	@RequestMapping(value="/addPost",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> addPost(String orgId,String postIds){
		List<SOrgPost> sorgPostList=new ArrayList<SOrgPost>();
		SOrgPost sop=null;

		for (String postId: postIds.split(",")) {
			if(StringUtils.isNotEmpty(postId)){
				sop=new SOrgPost();
				sop.setOrgId(orgId);
				sop.setPostId(postId);
				sop.setUuid(IDUtil.getId());
				sorgPostList.add(sop);
			}
		}
		List<SOrgPost> sopList=orgService.saveSprgPost(sorgPostList);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("sOrgPostList", sopList);
			map.put("success", "1");
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}


	@RequestMapping(value="/delOrgPost",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> delOrgPost(String orgId,String postIds){
		List<SOrgPost> sorgPostList=new ArrayList<SOrgPost>();
		SOrgPost sop=null;

		for (String postId: postIds.split(",")) {
			if(StringUtils.isNotEmpty(postId)){
				sop=new SOrgPost();
				sop.setOrgId(orgId);
				sop.setPostId(postId);
				sorgPostList.add(sop);
			}
		}
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			orgService.delSorgPost(sorgPostList);
			map.put("success", "1");
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}




	@RequestMapping(value="/queryUser",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> queryUser(HttpServletRequest request) {
		Set<String> keySet = request.getParameterMap().keySet();
		for(String keyName : keySet) {
			logger.info("keyName:"+keyName);
		}
		Sort.Direction orderDir = Sort.Direction.ASC;
		String columnName = "uuid";
		int draw = Integer.parseInt(request.getParameter("draw"));//请求次数
		int start = Integer.parseInt(request.getParameter("start"));//数据起始位置
		int length = Integer.parseInt(request.getParameter("length"));//数据长度
		String order = request.getParameter("order[0][column]");//获取需要排序的字段索引
		String dir = request.getParameter("order[0][dir]");//获取需要排序字段的排序方向
		String column = request.getParameter("columns["+order+"][data]");//根据索引获取字段名称
		String searchValue = request.getParameter("search[value]");//获取用户过滤框里的字符
		String orgId = request.getParameter("orgId");
		JSONObject paramJson = new JSONObject();
		paramJson.put("orgId", orgId);

		logger.info("paramJson:"+paramJson);
		if(StringUtils.isNotEmpty(dir)&& !dir.equals("asc")) {
			orderDir = Sort.Direction.DESC;
		}
		if(StringUtils.isNotEmpty(column)) {
			columnName = column;
		}
		//总记录数
		String recordsTotal = "0";
		//过滤后记录数
		String recordsFiltered = "0";
		int page = start / length;
		Sort sort = new Sort(orderDir, columnName);
		Pageable pageable = new PageRequest(page,length,sort);

		Page<SUser> sUserPageAll = orgService.findAllUserByOrg(paramJson,pageable);

		Page<SUser> sUserPageFiltered = orgService.listSerachUserByOrg(paramJson,searchValue, pageable);


		if(sUserPageAll!=null && sUserPageAll.getTotalElements()>0) {
			logger.info(">>>>>>sPostPageAll:"+sUserPageAll.getTotalElements());
			recordsTotal = String.valueOf(sUserPageAll.getTotalElements());
		}
		if(sUserPageFiltered!=null && sUserPageFiltered.getTotalElements()>0) {
			logger.info(">>>>>>postPageFiltered:"+sUserPageFiltered.getTotalElements());
			recordsFiltered = String.valueOf(sUserPageFiltered.getTotalElements());
		}
		Map<Object,Object> pageInfo = new HashMap<Object,Object>();
		pageInfo.put("data", sUserPageFiltered.getContent());
		pageInfo.put("draw", draw);
		pageInfo.put("recordsTotal", recordsTotal);
		pageInfo.put("recordsFiltered", recordsFiltered);
		return pageInfo;
	}

	@RequestMapping(value="/addUser",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> addUser(String orgId,String userIds){
		List<SUserOrg> sorgUserList=new ArrayList<SUserOrg>();
		SUserOrg suo=null;

		for (String userId: userIds.split(",")) {
			if(StringUtils.isNotEmpty(userId)){
				suo=new SUserOrg();
				suo.setOrgId(orgId);
				suo.setUserId(userId);
				suo.setUuid(IDUtil.getId());
				sorgUserList.add(suo);
			}
		}
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			List<SUserOrg> suoList=orgService.saveSprgUser(sorgUserList);
			map.put("sUserOrgList", suoList);
			map.put("success", "1");
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}


	@RequestMapping(value="/delOrgUser",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> delOrgUser(String orgId,String userIds){
		List<SUserOrg> sorgUserList=new ArrayList<SUserOrg>();
		SUserOrg suo=null;

		for (String userId: userIds.split(",")) {
			if(StringUtils.isNotEmpty(userId)){
				suo=new SUserOrg();
				suo.setOrgId(orgId);
				suo.setUserId(userId);
				sorgUserList.add(suo);
			}
		}
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			orgService.delSorgUser(sorgUserList);
			map.put("success", "1");
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
}

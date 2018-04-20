/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.ctrl.unit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.css.mgr.admin.srv.unit.IUnitService;
import com.css.mgr.base.dao.pojo.OpenFlag;
import com.css.mgr.base.dao.pojo.SUnit;
import com.css.mgr.base.dao.pojo.SUser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author wangtianwen
 * 2018年2月6日
 */
@Controller
@RequestMapping(value="/unit")
public class UnitController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IUnitService unitService;
	
	@RequestMapping(value="/getUnitTree",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> getUnitTree() {
		JSONArray unitTree = unitService.getUnitTree();
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("unitTree", unitTree);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	@RequestMapping(value="/unit_tree",method=RequestMethod.GET)
	public String tree(Model model,HttpServletRequest request){
		String treeHtml = unitService.getTreeHtml();
		logger.info("单位树tree:"+treeHtml);
		model.addAttribute("unitTreeHtml",treeHtml);
		return "admin/unit/unit_tree";
	}
	
	@RequestMapping(value="/unit_dir",method=RequestMethod.GET)
	public String dir(Model model){
		return "admin/unit/unit_dir";
	}
	
	@RequestMapping(value="/refreshUnitTree",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> refreshUnitTree(){
		String unitTreeHtml=unitService.getTreeHtml();
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("unitTreeHtml", unitTreeHtml);
			map.put("success", "1");
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	/**
	 * 查询当前功能下子功能，不包含孙子、重孙。。
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/query",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> query(HttpServletRequest request) {
		Direction orderDir = Direction.ASC; 
		String columnName = "uuid";
		int draw = Integer.parseInt(request.getParameter("draw"));//请求次数
		int start = Integer.parseInt(request.getParameter("start"));//数据起始位置
		int length = Integer.parseInt(request.getParameter("length"));//数据长度
		String order = request.getParameter("order[0][column]");//获取需要排序的字段索引
		String dir = request.getParameter("order[0][dir]");//获取需要排序字段的排序方向
		String column = request.getParameter("columns["+order+"][data]");//根据索引获取字段名称
	    String searchValue = request.getParameter("search[value]");//获取用户过滤框里的字符
	    String unitCode = request.getParameter("unitCode");
	    String unitName = request.getParameter("unitName");
	    String openFlag = request.getParameter("openFlag");
	    String parentId = request.getParameter("parentId");
	    if(StringUtils.isEmpty(parentId) || parentId.equals("0")) parentId=null;
	    if(StringUtils.isEmpty(openFlag)) openFlag="0";
	    JSONObject paramJson = new JSONObject();
	    paramJson.accumulate("unitCode", unitCode);
	    paramJson.accumulate("unitName", unitName);
	    paramJson.accumulate("openFlag", openFlag);
	    paramJson.accumulate("parentId", parentId);
	    logger.info("paramJson:"+paramJson);
	    if(dir!="" && !dir.equals("asc")) {
			orderDir = Direction.DESC;
		}
		if(column!=null && column != "") {
			columnName = column;
		}
		 //总记录数
	    String recordsTotal = "0";
	    //过滤后记录数
	    String recordsFiltered = "";
	    int page = start / length;
	    Sort sort = new Sort(orderDir, columnName);
	    Pageable pageable = new PageRequest(page,length,sort);
	    
	    Page<SUnit> unitPageAll = unitService.findAll(paramJson,pageable);
	    
	    Page<SUnit> unitPageFiltered = unitService.listSerach(paramJson,searchValue, pageable);
	    
		if(unitPageAll!=null && unitPageAll.getTotalElements()>0) {
			logger.info(">>>>>>PageAll:"+unitPageAll.getTotalElements());
			recordsTotal = String.valueOf(unitPageAll.getTotalElements());
		}
		if(unitPageFiltered!=null && unitPageFiltered.getTotalElements()>0) {
			logger.info(">>>>>>PageFiltered:"+unitPageFiltered.getTotalElements());
			recordsFiltered = String.valueOf(unitPageFiltered.getTotalElements());
		}
		Map<Object,Object> pageInfo = new HashMap<Object,Object>();
		pageInfo.put("data", unitPageFiltered.getContent());
		pageInfo.put("draw", draw);
		pageInfo.put("recordsTotal", recordsTotal);
		pageInfo.put("recordsFiltered", recordsFiltered);
		return pageInfo;
	}
	
	@RequestMapping(value="/add",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> add(SUnit unit) {
		Map<Object,Object> map = new HashMap<Object,Object>();
		if(StringUtils.isEmpty(unit.getUuid())) {//添加
			unit.setUuid(IDUtil.getId());
		} 
		try {
			if(StringUtils.isEmpty(unit.getOpenFlag()) || !unit.getOpenFlag().equals(OpenFlag.CLOSE) || !unit.getOpenFlag().equals(OpenFlag.OPEN)) {
				unit.setOpenFlag(OpenFlag.OPEN);
			}
			if(StringUtils.isEmpty(unit.getParentId()) || unit.getParentId().equals("0")) {
				unit.setParentId(null);
			}
			unit = unitService.save(unit);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	@RequestMapping(value="/del",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> del(SUnit unit){
		String uuid = unit.getUuid();
		logger.info("uuid:"+uuid);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			unitService.del(uuid);
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
		SUnit unit = unitService.get(uuid);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("sUnit", unit);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	@RequestMapping(value="/queryMgr",method=RequestMethod.GET)
	@ResponseBody
	public String queryMgr(HttpServletRequest request) {
		String unitId = request.getParameter("unitId");
		String mgrName = request.getParameter("mgrName");
		logger.info("unitId:"+unitId+" mgrName:"+mgrName);
		JSONArray datas = unitService.findRelatedMgr(unitId,mgrName);
		JSONObject pageInfo =new JSONObject();
		pageInfo.put("data", datas);
		return pageInfo.toString();
	}
	
	@RequestMapping(value="/cancelMgr",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> cancelMgr(HttpServletRequest request){
		String mgrUuid = request.getParameter("mgrUuid");
		logger.info("mgrUuid:"+mgrUuid);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			unitService.cancelMgr(mgrUuid);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	@RequestMapping(value="/allMgr",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> allMgr(HttpServletRequest request) {
		String loginName = request.getParameter("loginName");
		String realName = request.getParameter("realName");
		logger.info("loginName:"+loginName+" realName:"+realName);
		List<SUser> datas = unitService.findAllMgr(loginName,realName);
		logger.info("datas:"+datas);
		Map<Object,Object> pageInfo = new HashMap<Object,Object>();
		pageInfo.put("data", datas);
		return pageInfo;
	}
	
	@RequestMapping(value="/bindMgr",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> bindMgr(HttpServletRequest request) {
		String mgrUuid = request.getParameter("mgrUuid");
		String unitId = request.getParameter("unitId");
		logger.info(mgrUuid+":"+unitId);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			unitService.bindMgr(mgrUuid, unitId);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
}

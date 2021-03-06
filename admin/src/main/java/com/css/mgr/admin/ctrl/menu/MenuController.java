/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.ctrl.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.css.mgr.admin.srv.dict.IDictService;
import com.css.mgr.base.dao.pojo.SDict;
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
import com.css.mgr.admin.srv.func.IFuncService;
import com.css.mgr.admin.srv.menu.IMenuService;
import com.css.mgr.base.CssController;
import com.css.mgr.base.dao.pojo.OpenFlag;
import com.css.mgr.base.dao.pojo.SFunc;
import com.css.mgr.base.dao.pojo.SMenu;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author wangtianwen
 * 2018年2月2日
 */
@Controller
@RequestMapping(value="/menu")
public class MenuController extends CssController{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IMenuService menuService;
	
	@Autowired
	private IFuncService funcService;

	@Autowired
	private IDictService dictService;
	
	@RequestMapping(value="/menu_tree",method=RequestMethod.GET)
	public String tree(Model model,HttpServletRequest request){
		String treeHtml = menuService.getTreeHtml();
		logger.info("菜单树tree:"+treeHtml);
		model.addAttribute("menuTreeHtml",treeHtml);
		return "admin/menu/menu_tree";
	}
	
	@RequestMapping(value="/menu_dir",method=RequestMethod.GET)
	public String dir(Model model){
		List<SDict> sDictList=dictService.getDictType("MDI_ICONS");
		model.addAttribute("sDictList",sDictList);
		
		JSONObject dict=new JSONObject();
		dict.put("openflag",dictService.getDictsByTableName("d_openFlag"));
		model.addAttribute("dict", dict);
		return "admin/menu/menu_dir";
	}
	
	@RequestMapping(value="/refreshMenuTree",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> refreshMenuTree(){
		String menuTreeHtml=menuService.getTreeHtml();
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("menuTreeHtml", menuTreeHtml);
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
	    String name = request.getParameter("name");
	    String openflag = request.getParameter("openflag");
	    String parentId = request.getParameter("parentId");
	    if(StringUtils.isEmpty(parentId) || parentId.equals("0")) parentId=null;
	    if(StringUtils.isEmpty(openflag)) openflag="0";
	    JSONObject paramJson = new JSONObject();
	    paramJson.accumulate("name", name);
	    paramJson.accumulate("openflag", openflag);
	    paramJson.accumulate("parentId", parentId);
	    logger.info("paramJson:"+paramJson);
	    if(StringUtils.isNotEmpty(dir) && !dir.equals("asc")) {
			orderDir = Direction.DESC;
		}
		if(StringUtils.isNotEmpty(column) && column != "") {
			columnName = column;
		}
		 //总记录数
	    String recordsTotal = "0";
	    //过滤后记录数
	    String recordsFiltered = "";
	    int page = start / length;
	    Sort sort = new Sort(orderDir, columnName);
	    Pageable pageable = new PageRequest(page,length,sort);
	    
	    Page<SMenu> menuPageAll = menuService.findAll(paramJson,pageable);
	    
	    Page<SMenu> menuPageFiltered = menuService.listSerach(paramJson,searchValue, pageable);
	    
		if(menuPageAll!=null && menuPageAll.getTotalElements()>0) {
			logger.info(">>>>>>menuPageAll:"+menuPageAll.getTotalElements());
			recordsTotal = String.valueOf(menuPageAll.getTotalElements());
		}
		if(menuPageFiltered!=null && menuPageFiltered.getTotalElements()>0) {
			logger.info(">>>>>>menuPageFiltered:"+menuPageFiltered.getTotalElements());
			recordsFiltered = String.valueOf(menuPageFiltered.getTotalElements());
		}
		Map<Object,Object> pageInfo = new HashMap<Object,Object>();
		pageInfo.put("data", menuPageFiltered.getContent());
		pageInfo.put("draw", draw);
		pageInfo.put("recordsTotal", recordsTotal);
		pageInfo.put("recordsFiltered", recordsFiltered);
		return pageInfo;
	}
	
	@RequestMapping(value="/add",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> add(SMenu menu) {
		Map<Object,Object> map = new HashMap<Object,Object>();
		if(StringUtils.isEmpty(menu.getUuid())) {//添加
			menu.setUuid(IDUtil.getId());
		} 
		try {
			if(StringUtils.isEmpty(menu.getOpenflag())) {
				menu.setOpenflag(OpenFlag.OPEN);
			}
			if(StringUtils.isEmpty(menu.getParentId()) || menu.getParentId().equals("0")) {
				menu.setParentId(null);
			}
			if(StringUtils.isNotEmpty(menu.getFuncId())) {
				String sysId = menuService.getSysIdByFuncId(menu.getFuncId());
				menu.setSysId(sysId);
			}
			menu = menuService.save(menu);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	@RequestMapping(value="/del",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> del(SMenu menu){
		String uuid = menu.getUuid();
		logger.info("uuid:"+uuid);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			menuService.del(uuid);
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
		SMenu menu = menuService.get(uuid);
		SFunc func = funcService.getSFuncByUuid(menu.getFuncId());
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("sMenu", menu);
			map.put("sFunc", func);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	@RequestMapping(value="/getFuncTree",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> getFuncTree() {
		JSONArray funcTree = menuService.getFuncTree();
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			map.put("funcTree", funcTree);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
}

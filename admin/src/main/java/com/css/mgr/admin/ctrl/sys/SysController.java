package com.css.mgr.admin.ctrl.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.hibernate.internal.util.StringHelper;
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
import com.css.mgr.admin.srv.dict.IDictService;
import com.css.mgr.admin.srv.sys.ISysService;
import com.css.mgr.base.CssController;
import com.css.mgr.base.dao.pojo.SSys;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/sys")
public class SysController extends CssController{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());  
	
	@Autowired
	private ISysService sysService;
	
	@Autowired
	private IDictService dictService;
	
	@RequestMapping(value="/dir",method=RequestMethod.GET)
	public String dir(Model model){
		logger.info(">>>>>>user:"+getUser().getRealName());
		JSONObject dict=new JSONObject();
		dict.put("openflag",dictService.getDictsByTableName("d_openFlag"));
		model.addAttribute("dict", dict);
		//
		return "admin/sys/dir";
	}
	
	@RequestMapping(value="/del",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> del(SSys sSys){
		String uuid = sSys.getUuid();
		logger.info("id:"+uuid);
		Map<Object,Object> map = new HashMap<Object,Object>();
		try {
			sysService.delById(uuid);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/deletemore",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> delMore(String ids){ 
		logger.info("ids:"+ids);
		Map<Object,Object> map = new HashMap<Object,Object>();
		List list=IDUtil.strToList(ids);
		try { 
			  for (int i = 0; i < list.size(); i++) {
				  System.out.println(list.get(i));
				  sysService.delById((String)list.get(i));
			}
			map.put("success", "1");	
			} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	
	Map<Object,Object> map1 = new HashMap<Object,Object>();
	
	@RequestMapping(value="/get",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> get(String uuid){
		logger.info("uuid:"+uuid);
		SSys sSys = sysService.getSAppById(uuid);
		
		try {
			map1.put("sSys", sSys);
			map1.put("success", "1");	
		} catch (Exception e) {
			map1.put("success", "0");
		}
		return map1;
	}
	
	
	@RequestMapping(value="/add",method=RequestMethod.POST)
	@ResponseBody
	public Map<Object,Object> add(SSys sSys) {
		Map<Object,Object> map = new HashMap<Object,Object>();
		if(StringUtils.isEmpty(sSys.getUuid())) {
			sSys.setUuid(IDUtil.getId());
		}
		try {
			sSys = sysService.save(sSys);
			map.put("success", "1");	
		} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}
	
	
	@RequestMapping(value="/getUuid",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> getUuid(String  uuid) {
		Map<Object,Object> map = new HashMap<Object,Object>();
		SSys sys = sysService.getSAppById(uuid);
		if(sys!=null)
	    map.put("success", "0");
		return map;
	}
	
	
	
	
	
	@RequestMapping(value="/updOpenFlag",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> updSta(String ids,String openFlag) {
		logger.info("ids:"+ids);
		Map<Object,Object> map = new HashMap<Object,Object>();
		@SuppressWarnings("rawtypes")
		List list=IDUtil.strToList(ids);
		System.out.println(list);
		try { 
			  for (int i = 0; i < list.size(); i++) {
				  SSys sSys = sysService.getSAppById((String)list.get(i));
				  sSys.setOpenFlag(openFlag);
				  sysService.save(sSys);
			}
			map.put("success", "1");	
			} catch (Exception e) {
			map.put("success", "0");
		}
		return map;
	}

	@RequestMapping(value="/queryAll",method=RequestMethod.GET)
	@ResponseBody
	public Map<Object,Object> query(HttpServletRequest request) {
		@SuppressWarnings("unused")
		Set<String> keySet = request.getParameterMap().keySet();
		Direction orderDir = Direction.ASC; 
		String columnName = "uuid";
		int draw = Integer.parseInt(request.getParameter("draw"));//请求次数
		int start = Integer.parseInt(request.getParameter("start"));//数据起始位置
		int length = Integer.parseInt(request.getParameter("length"));//数据长度
		String order = request.getParameter("order[0][column]");//获取需要排序的字段索引
		String dir = request.getParameter("order[0][dir]");//获取需要排序字段的排序方向
		String column = request.getParameter("columns["+order+"][data]");//根据索引获取字段名称
	    String searchValue = request.getParameter("search[value]");//获取用户过滤框里的字符
	    String uuid = request.getParameter("uuid");
	    String name = request.getParameter("name");
	    
	    JSONObject paramJson = new JSONObject();
	    paramJson.put("uuid", uuid);
	    paramJson.put("name", name);
	    logger.info("paramJson:"+paramJson);
	    if(IDUtil.isNotEmpty(dir) && !dir.equals("asc")) {
			orderDir = Direction.DESC;
		}
		if(StringHelper.isNotEmpty(column)) {
			columnName = column;
		}
		 //总记录数
	    String recordsTotal = "0";
	    //过滤后记录数
	    String recordsFiltered = "";
	    int page = start / length;
	    Sort sort = new Sort(orderDir, columnName);
	    Pageable pageable = new PageRequest(page,length,sort);
	    
	    Page<SSys> sSysPageAll = sysService.findAll(paramJson,pageable);
	    
	    Page<SSys> postPageFiltered = sysService.listSerach(paramJson,searchValue, pageable);
	    
		if(sSysPageAll!=null && sSysPageAll.getTotalElements()>0) {
			logger.info(">>>>>>postPageAll:"+sSysPageAll.getTotalElements());
			recordsTotal = String.valueOf(sSysPageAll.getTotalElements());
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

package com.css.mgr.admin.ctrl.log;

import com.css.mgr.admin.common.IDUtil;
import com.css.mgr.admin.srv.log.SLogService;
import com.css.mgr.base.CssController;
import com.css.mgr.base.dao.pojo.*;
import net.sf.json.JSONObject;
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

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
@RequestMapping("/log")
public class LogController extends CssController{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("logService")
	private SLogService sLogService;


	@RequestMapping(value="/dir",method=RequestMethod.GET)
	public String dir(Model model){
		logger.info(">>>>>>user:"+getUser().getRealName());
		return "admin/log/dir";
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
		String userName = request.getParameter("userName");
		String sTime = request.getParameter("sTime");
		String eTime = request.getParameter("eTime");

		JSONObject paramJson = new JSONObject();
		paramJson.put("userName", userName);
		paramJson.put("sTime", sTime);
		paramJson.put("eTime", eTime);

		logger.info("paramJson:"+paramJson);
		if(IDUtil.isNotEmpty(dir)&& !dir.equals("asc")) {
			orderDir = Sort.Direction.DESC;
		}
		if(IDUtil.isNotEmpty(column)) {
			columnName = column;
		}
		//总记录数
		String recordsTotal = "0";
		//过滤后记录数
		String recordsFiltered = "0";
		int page = start / length;
		Sort sort = new Sort(orderDir, columnName);
		Pageable pageable = new PageRequest(page,length,sort);

		Page<SLog> logPageAll = sLogService.findAll(paramJson,pageable);

		Page<SLog> logPageFiltered = sLogService.listSerach(paramJson,searchValue, pageable);

		if(logPageAll!=null && logPageAll.getTotalElements()>0) {
			logger.info(">>>>>>logPageAll:"+logPageAll.getTotalElements());
			recordsTotal = String.valueOf(logPageAll.getTotalElements());
		}
		if(logPageFiltered!=null && logPageFiltered.getTotalElements()>0) {
			logger.info(">>>>>>logPageFiltered:"+logPageFiltered.getTotalElements());
			recordsFiltered = String.valueOf(logPageFiltered.getTotalElements());
		}
		Map<Object,Object> pageInfo = new HashMap<Object,Object>();
		pageInfo.put("data", logPageFiltered.getContent());
		pageInfo.put("draw", draw);
		pageInfo.put("recordsTotal", recordsTotal);
		pageInfo.put("recordsFiltered", recordsFiltered);
		return pageInfo;
	}

}

/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.ctrl.msg;

import java.util.HashMap;
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

import com.css.mgr.admin.dao.pojo.SMsg;
import com.css.mgr.admin.srv.dict.IDictService;
import com.css.mgr.admin.srv.msg.IMsgService;
import com.css.mgr.base.CssController;

import net.sf.json.JSONObject;

/**
 * @author wangtianwen
 * 2018年3月8日
 */
@Controller
@RequestMapping("/msg")
public class MsgController extends CssController{
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private IMsgService msgService;
	
	@Autowired
	private IDictService dictService;
	
	@RequestMapping(value="/dir",method=RequestMethod.GET)
	public String dir(Model model) {
		JSONObject dict=new JSONObject();
		dict.put("msgReadStatus",dictService.getDictsByTableName("MSG_READ_STATUS"));
		model.addAttribute("dict", dict);
		return "admin/msg/msg_dir";
	}
	
	@ResponseBody
	@RequestMapping(value = "/updStatus", method = RequestMethod.POST)
	public String updStatus(String msgUuid) {
		return msgService.updMsgStatus(msgUuid).toString();
	}
	
	@ResponseBody
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(SMsg item) {
		return msgService.addMsg(item).toString();
	}

	@ResponseBody
	@RequestMapping(value = "/del", method = RequestMethod.POST)
	public String del(String ids) {
		return msgService.delMsg(ids).toString();
	}

	@ResponseBody
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public String get(String uuid) {
		return msgService.getMsg(uuid).toString();
	}

	
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
	    String title = request.getParameter("title");
	    String userName = request.getParameter("userName");
	    
	    JSONObject paramJson = new JSONObject();
	    paramJson.put("title", title);
	    paramJson.put("userName", userName);
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
	    
	    Page<SMsg> msgPageAll = msgService.findAll(paramJson,pageable);
	    
	    Page<SMsg> msgPageFiltered = msgService.listSerach(paramJson,searchValue, pageable);
	    
		if(msgPageAll!=null && msgPageAll.getTotalElements()>0) {
			logger.info(">>>>>>msgPageAll:"+msgPageAll.getTotalElements());
			recordsTotal = String.valueOf(msgPageAll.getTotalElements());
		}
		if(msgPageFiltered!=null && msgPageFiltered.getTotalElements()>0) {
			logger.info(">>>>>>msgPageFiltered:"+msgPageFiltered.getTotalElements());
			recordsFiltered = String.valueOf(msgPageFiltered.getTotalElements());
		}
		Map<Object,Object> pageInfo = new HashMap<Object,Object>();
		pageInfo.put("data", msgPageFiltered.getContent());
		pageInfo.put("draw", draw);
		pageInfo.put("recordsTotal", recordsTotal);
		pageInfo.put("recordsFiltered", recordsFiltered);
		return pageInfo;
	}
}

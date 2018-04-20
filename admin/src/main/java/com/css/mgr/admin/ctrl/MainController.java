/**
 * Date:2018年1月18日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.ctrl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.css.mgr.admin.srv.msg.IMsgService;
import com.css.mgr.admin.srv.org.ISOrgService;
import com.css.mgr.base.CssController;
import com.css.mgr.base.dao.pojo.MenuModel;
import com.css.mgr.base.dao.pojo.SUser;

import net.sf.json.JSONObject;

/**
 * Date:2018年1月18日
 * Description:
 * Author:QinMing
 */
@Controller
public class MainController extends CssController{
	@Autowired
	private ISOrgService orgService;
	@Value("${css.server.indexPage}")
	private String indexPage;
	@Autowired
	private IMsgService msgService;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());  
	@RequestMapping("/main")
	public String main(Model model){
		//
		List<MenuModel> menu=getMenu();
		//
		System.out.println(menu.size());
		//
		model.addAttribute("menu", menu);
		//
		setUserInfot(model);
		//设置消息推送
		setMsgInfo(model);
		//
		return "index";
	}
	@RequestMapping("/")
	public String index(){
		//
		return "forward:"+indexPage;
	}
	@RequestMapping("/user/info")
	public String userinfo(){
		JSONObject jo =JSONObject.fromObject(getUser());
		return jo.toString();
	}
	private void setMsgInfo(Model model) {
		model.addAttribute("sMsg", msgService.findMsgByUserId(getUser().getUuid()));
	}
	private void setUserInfot(Model model){
		SUser user=getUser();
		model.addAttribute("user", user);
		//
		model.addAttribute("org", orgService.find(user.getOrgId()));
		//
		logger.info(">>>>>>user:"+user.getRealName());
	}
	@RequestMapping("/first")
	public String first(Model model){
		//
		setUserInfot(model);
		//
		return "first";
	}
	@RequestMapping("/htmltpl/{html}")
	public String htmltpl(@PathVariable("html") String html){
		//
		return "htmltpl/"+html;
	}
}

/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.user;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.css.App;
import com.css.mgr.admin.dao.repository.SUserDao;

/**
 * @author wangtianwen 2018年2月5日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class) // 指定spring-boot的启动类
public class UserTest {
	private static Logger logger = LoggerFactory.getLogger(UserTest.class);
	@Autowired
	private SUserDao sUserDao;

	/**
	 * 若需要对进行如下查询，使用JPA是无法直接实现的，因为JPQL不提供类似这样的函数GROUP_CONCAT，可灵活实现。
	 * SELECT
			u.SUSER_ID,			
			u.LOGIN_NAME,
			o.`NAME` AS orgName,
			GROUP_CONCAT(p.`NAME`) AS postName
		FROM
			s_user u,
			s_org o,
			s_user_post up,
			s_post p,
			s_user_org uo
		WHERE uo.SUSER_ID = u.SUSER_ID and o.SORG_ID = uo.SORG_ID
			and up.SUSER_ID = u.SUSER_ID and up.SPOST_ID = p.SPOST_ID
			AND o.SORG_ID = '8a8d81bc44fc43890144fc438d240000'
		group by u.SUSER_ID
		limit 0,1;
	 * 分页+多表查询+字符串拼接
	 */
	/*@Test
	public void findByRankTest() {
		String orgId = "8a8d81bc44fc43890144fc438d240000";
		Pageable pageable = new PageRequest(1,1);
		Page<Map<String,String>> page = sUserDao.findAllByOrgIdMap(orgId,pageable);
		for(Map<String,String> map :page.getContent()) {
			List<String> postsList = sUserDao.getPostsByUserId(map.get("uuid"));
			String posts = String.join(",", postsList);
			map.put("postName", posts);
		}
		logger.info("TotalElements:"+page.getTotalElements());
		logger.info("pageContent:"+page.getContent());
	}*/
	/**
	 * [{orgName=深圳市总工会, postName=, uuid=1dd6d1b6f4624e4ce830408415681577}, 
		{orgName=深圳市总工会, postName=, uuid=4028812e473e19d101473e1d56bc0001}, 
		{orgName=深圳市总工会, postName=, uuid=4028812e483eb4e101483ebb0f120000}, 
		{orgName=深圳市总工会, postName=签收岗,业务办理,工会信息管理员, uuid=402881414c26d448014c45c7d02a000b},
		 {orgName=深圳市总工会, postName=, uuid=402881414c26d448014c45c7ea99000d}, 
		{orgName=深圳市总工会, postName=, uuid=402881414c26d448014c45c80870000f}, 
		{orgName=深圳市总工会, postName=, uuid=402881414c26d448014c45cc35c60014}, 
		{orgName=深圳市总工会, postName=, uuid=402881414c26d448014c45cc4ebf0016},
		 {orgName=深圳市总工会, postName=, uuid=402881414c26d448014c45cc6a490018}, 
		{orgName=深圳市总工会, postName=工会信息管理员, uuid=402881464756dbad014756de1c7a0002}]
	 */
	
}

/**
 * Copyrigth(c) Css Team
 * All rights reserved
 */
package com.css.mgr.admin.unit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.css.App;
import com.css.mgr.admin.srv.unit.IUnitService;

/**
 * @author wangtianwen 2018年2月5日
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class) // 指定spring-boot的启动类
public class UnitTest {
	private static Logger logger = LoggerFactory.getLogger(UnitTest.class);
	@Autowired
	private IUnitService unitService;

	
	@Test
	public void delTest() {
		unitService.del("lgtw");
	}
}

/**
 * Date:2018年2月24日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.common;

import com.alibaba.druid.util.StringUtils;

/**
 * Date:2018年2月24日
 * Description:
 * Author:QinMing
 */
public class StringUtil {
	public static String[] strToArray(String ids){
		String[] sd=ids.split(",");
		return sd;
	}
	public static boolean isEmpty(String id){
		return StringUtils.isEmpty(id);
	}
}

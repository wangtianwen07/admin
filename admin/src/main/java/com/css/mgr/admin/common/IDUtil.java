package com.css.mgr.admin.common;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @author wangtianwen 2018年1月25日
 */
public class IDUtil {
	public static String getId() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static List<String> strToList(String str) {
		if (str != null) {
			return Arrays.asList(str.split(","));
		}
		return null;
	}

	public static boolean isNotEmpty(String string) {
		return (string != null) && (string.length() > 0);
	}
}

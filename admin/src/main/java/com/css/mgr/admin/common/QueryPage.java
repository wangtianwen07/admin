/**
 * Date:2018年3月22日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.common;

/**
 * Date:2018年3月22日
 * Description:
 * Author:QinMing
 */
public class QueryPage {
	protected int start=0;
	protected int length=15;
	protected String orderColumn;
	protected String orderDir;
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getOrderColumn() {
		return orderColumn;
	}
	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}
	public String getOrderDir() {
		return orderDir;
	}
	public void setOrderDir(String orderDir) {
		this.orderDir = orderDir;
	}
	
}

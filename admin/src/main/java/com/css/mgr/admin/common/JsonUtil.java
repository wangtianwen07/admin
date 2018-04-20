/**
 * Date:2018年2月24日
 * Description:
 * Author:QinMing
 *
 */
package com.css.mgr.admin.common;

import java.io.Serializable;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;

/**
 * Date:2018年2月24日
 * Description:
 * Author:QinMing
 */
public class JsonUtil {
	public static final String STATE_ERROR="1";
	public static final String STATE_SUCCESS="0";
	public static JSONObject error(String msg){
		return toJson(STATE_ERROR,msg,null);
	}
	/**
	 * 
	 * @param code
	 * @param msg
	 * @param jsonObj json对象或者数组
	 * @return
	 */
	public static JSONObject success(String code,String msg,Object jsonObj) {
		JSONObject jo=new JSONObject();
		jo.put("state",code);
		jo.put("msg",msg);
		jo.put("data",jsonObj);
		return jo;
	}
	public static JSONObject success(String msg,Object data){
		return toJson(STATE_SUCCESS,msg,data);
	}
	public static JSONObject success(String msg){
		return toJson(STATE_SUCCESS,msg,null);
	}
	public static JSONObject toJsonEx(String code,String msg,Object data,String ...ex){
		JSONObject jo=new JSONObject();
		jo.put("msg",msg);
		JsonConfig config = new JsonConfig();
		config.setExcludes(ex);
		if(data!=null){
			if(JSONUtils.isArray(data)) {
				jo.put("data",JSONArray.fromObject(data,config));	
			} else {
				jo.put("data",JSONObject.fromObject(data,config));
			}
		}
		jo.put("state",code);
		return jo;
	}
	public static JSONObject toJson(String code,String msg,Object data){
		JSONObject jo=new JSONObject();
		jo.put("msg",msg);
		if(data!=null){
			if(JSONUtils.isArray(data)) {
				jo.put("data",JSONArray.fromObject(data));	
			} else {
				jo.put("data",JSONObject.fromObject(data));
			}
		}
		jo.put("state",code);
		return jo;
	}
	public static JSONArray toJson(List<? extends Serializable> datas){
		JSONArray ja=new JSONArray();
		if(datas==null || datas.size()<1)return ja;
		for(Serializable b:datas){
			ja.add(JSONObject.fromObject(b));
		}
		return ja;
	}
	public static JSONArray toJsonEx(List<? extends Serializable> datas,String ...ex){
		JsonConfig config = new JsonConfig();
		config.setExcludes(ex);
		JSONArray ja=new JSONArray();
		if(datas==null || datas.size()<1)return ja;
		for(Serializable b:datas){
			ja.add(JSONObject.fromObject(b,config));
		}
		return ja;
	}
	/**
	 * 
	 * @param obj ：extends Serializable的对象
	 * @param ex
	 * @return
	 */
	public static JSONObject toJsonEx(Object obj,String ...ex){
		JsonConfig config = new JsonConfig();
		config.setExcludes(ex);
		JSONObject ja = new JSONObject();
		if(obj==null) return ja;
		ja = JSONObject.fromObject(obj,config);
		return ja;
	}
}

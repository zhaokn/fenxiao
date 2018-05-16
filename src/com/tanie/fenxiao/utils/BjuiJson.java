package com.tanie.fenxiao.utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class BjuiJson {
	public static String json(String statusCode, String message, String tabid,
			String dialogid, String divid, String closeCurrent, String forward,
			String forwardConfirm) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("statusCode", statusCode);
		json.put("message", message);
		json.put("tabid", tabid);
		json.put("dialogid", dialogid);
		json.put("divid", divid);
		json.put("closeCurrent", closeCurrent);
		json.put("forward", forward);
		json.put("forwardConfirm", forwardConfirm);
		return json.toString();
	}
	public static String jsonObj(String statusCode, String message, Map data) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("statusCode", statusCode);
		json.put("message", message);
		json.put("data", data);
		return json.toString();
	}
}

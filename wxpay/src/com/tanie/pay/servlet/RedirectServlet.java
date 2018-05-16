package com.tanie.pay.servlet;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import net.sf.json.JSONObject;

import com.unstoppedable.common.CommonUtil;
import com.unstoppedable.common.Configure;
import com.unstoppedable.common.Sha1Util;
import com.unstoppedable.common.Signature;
import com.unstoppedable.common.XMLParser;
import com.unstoppedable.notify.PayNotifyData;
import com.unstoppedable.notify.PayNotifyTemplate;
import com.unstoppedable.notify.PaySuccessCallBack;
import com.unstoppedable.protocol.UnifiedOrderReqData;
import com.unstoppedable.service.WxPayApi;

/**
 * Servlet implementation class PayCallServlet
 */
public class RedirectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public RedirectServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String no = request.getParameter("no");
		Configure config = new Configure();
		String appId = config.getAppid();
		String appSecret = config.getAppSecret();
		
		String code = request.getParameter("code");
		String openId = null;
		
		// 获取OPENID后调用统一支付接口https://api.mch.weixin.qq.com/pay/unifiedorder
		String URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
			+ appId + "&secret=" + appSecret + "&code=" + code
			+ "&grant_type=authorization_code";

		JSONObject jsonObject = CommonUtil.httpsRequest(URL, "GET", null);
		if (null != jsonObject) {
			openId = jsonObject.getString("openid");
		}
		
		System.out.println("##########   OPENID:"+openId);
		response.sendRedirect(request.getContextPath()+"/user/settle?no=" + no+"&openId="+openId);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

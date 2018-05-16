package com.tanie.pay.servlet;


import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.unstoppedable.common.Configure;
import com.unstoppedable.common.Sha1Util;

/**
 * Servlet implementation class PayCallServlet
 */
public class InitPayOpenIdServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public InitPayOpenIdServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("goin pay setup");
		Configure config = new Configure();
		//WxPayBean wpb = new WxPayBean();
		//账号及商户相关参数
		String appId = config.getAppid();//wpb.getAppid();
		String redirectUri = config.getRedirectUri();
		//授权后要跳转的链接所需的参数一般有会员号，金额，订单号之类，
		//最好自己带上一个加密字符串将金额加上一个自定义的key用MD5签名或者自己写的签名,
		//比如 Sign = %3D%2F%CS% 
		String orderNo=appId+Sha1Util.getTimeStamp();
		redirectUri = redirectUri+"?userId=test&orderNo="+orderNo;
		//URLEncoder.encode 后可以在backUri 的url里面获取传递的所有参数
		redirectUri = URLEncoder.encode(redirectUri);
		//scope 参数视各自需求而定，这里用scope=snsapi_base 不弹出授权页面直接授权目的只获取统一支付接口的openid
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
		"appid=" + appId+"&redirect_uri=" + redirectUri+
		"&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
		System.out.println("#######  url:"+url);
		response.sendRedirect(url);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

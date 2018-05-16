package com.tanie.pay.servlet;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.json.JSONObject;

import org.xml.sax.SAXException;

import com.unstoppedable.common.Configure;
import com.unstoppedable.common.Sha1Util;
import com.unstoppedable.common.Signature;
import com.unstoppedable.protocol.UnifiedOrderReqData;
import com.unstoppedable.service.WxPayApi;

/**
 * 进入订单支付环节
 */
public class GoinPayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoinPayServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@SuppressWarnings("static-access")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Configure config = new Configure();
		String orderNo = String.valueOf(new Date().getTime());//request.getParameter("orderNo");// 订单号
		String body="商品描述,最好加上用户信息。";//request.getParameter("body");
		String total_fee="1";//request.getParameter("total_fee");// 订单金额，转换公式：金额  x 100，默认1为0.01元
		
		String openId = config.getOpenId();//Configure.getOpenId();
		String appId = config.getAppid();
		String mchId = config.getMchid();
		String spbill_create_ip = request.getRemoteAddr();
		String notifyUri = config.getNotifyUri();
		String prepay_id = "";
		String nonce_str = "";
		//===============  之上获取openid可以单独处理，根据长时间测试openid是特定的
		System.out.println("#######  openid:"+openId);
		UnifiedOrderReqData reqData = 
			new UnifiedOrderReqData.UnifiedOrderReqDataBuilder(appId,mchId,body, orderNo, Integer.valueOf(total_fee), spbill_create_ip,notifyUri, "JSAPI").setOpenid(openId).build();
		System.out.println(reqData.toString());
		System.out.println("#######  reqData:"+reqData);
		nonce_str = reqData.getNonce_str();
		try {
			Map<String, Object> resultMap = WxPayApi.UnifiedOrder(reqData);
			System.out.println("#######  resultMap:"+resultMap);
			prepay_id = (String)resultMap.get("prepay_id");
			if (prepay_id.equals("")) {
				request.setAttribute("ErrorMsg", "统一支付接口获取预支付订单出错");
				response.sendRedirect("/pay/error.jsp");
			}	
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		SortedMap<String, String> finalpackage = new TreeMap<String, String>();
		String appid2 = appId;
		String timestamp = Sha1Util.getTimeStamp();
		String nonceStr2 = nonce_str;
		String prepay_id2 = "prepay_id=" + prepay_id;
		String packages = prepay_id2;
		finalpackage.put("appId", appid2);
		finalpackage.put("timeStamp", timestamp);
		finalpackage.put("nonceStr", nonceStr2);
		finalpackage.put("package", packages);
		finalpackage.put("signType", "MD5");
		String finalsign = "";
		finalsign = Signature.createSign(finalpackage);
		System.out.println("/pay/pay.jsp?appid=" + appid2 + "&timeStamp="
				+ timestamp + "&nonceStr=" + nonceStr2 + "&package=" + packages
				+ "&sign=" + finalsign);
		if (!response.isCommitted()) {
			Map map = new HashMap();
			map.put("appId", appid2);
			map.put("timeStamp", timestamp);
			map.put("nonceStr", nonceStr2);
			map.put("package", packages);
			map.put("signType", "MD5");
			map.put("paySign", finalsign);
			JSONObject json = JSONObject.fromObject(map);
			System.out.println("返回数据---" + json);
			request.setAttribute("jsonData", json);
			// response.sendRedirect("pay.jsp?appid=" + appid2 + "&timeStamp="
			// + timestamp + "&nonceStr=" + nonceStr2 + "&packages="
			// + packages + "&sign=" + finalsign);
			response.sendRedirect("/pay/pay.jsp?jsonData=" + json);

		}
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}

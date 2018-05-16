package com.unstoppedable.service;

import com.unstoppedable.common.Configure;
import com.unstoppedable.common.HttpService;
import com.unstoppedable.common.JsonUtil;
import com.unstoppedable.common.XMLParser;
import com.unstoppedable.protocol.UnifiedOrderReqData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by hupeng on 2015/7/28.
 */
public class WxPayApi {

    private static Log logger = LogFactory.getLog(WxPayApi.class);

    public static Map<String, Object> UnifiedOrder(UnifiedOrderReqData reqData) throws IOException, SAXException, ParserConfigurationException {
        String res = HttpService.doPost(Configure.UNIFIED_ORDER_API, reqData);
        logger.debug("UnifiedOrder get response:" + res);
        return XMLParser.getMapFromXML(res);
    }

    public static String getOpenid(String appid, String appSecret, String code) throws Exception {
        String requestUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + appid
                + "&secret="
                + appSecret
                + "&code=" + code + "&grant_type=authorization_code";
        String res = HttpService.doGet(requestUrl);
        System.out.println(res);
        Map<String, Object> resultMap = JsonUtil.fromJson(res, HashMap.class);
        if (resultMap.get("openid") == null) {
            return null;
        }

        return resultMap.get("openid").toString();
    }

    public static void main(String[] args) throws Exception {
    	int randNum = new Random(300).nextInt(1);
    	System.out.println(randNum);
    	String time = String.valueOf(new Date().getTime())+randNum;
        Configure config = new Configure();
        System.out.println();
        System.out.println(config.getOpenId());
        System.out.println(config.getAppSecret());

    	UnifiedOrderReqData reqData = new UnifiedOrderReqData.UnifiedOrderReqDataBuilder(
                config.getAppid(),
                config.getMchid(),
    			"测试商品",
    			time,
    			1, 
    			"221.13.60.34", 
    			Configure.getNotifyUri(), 
    			"JSAPI").setOpenid("o2rTjwDSNRVeKF_lhvh6XS-bLC1A").build();
    	 System.out.println(UnifiedOrder(reqData));
        //UnifiedOrderReqData reqData = new UnifiedOrderReqData.UnifiedOrderReqDataBuilder("appid", "mch_id","body", "out_trade_no", 1, "spbill_create_ip", "notify_url", "JSAPI").setOpenid("openid").build();
      /*  UnifiedOrderReqData reqData = new UnifiedOrderReqData.UnifiedOrderReqDataBuilder(Configure.getAppid(), Configure.getMchid(),
                "购买商品测试", orderNo, 1, "", "http://www.baidu.com", "JSAPI").setOpenid("openid").build();        
        System.out.println(UnifiedOrder(reqData));*/


    }
}

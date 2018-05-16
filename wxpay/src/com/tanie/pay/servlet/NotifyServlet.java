package com.tanie.pay.servlet;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import javax.security.auth.login.Configuration;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.unstoppedable.common.CommonUtil;
import com.unstoppedable.notify.PayNotifyData;
import com.unstoppedable.notify.PayNotifyTemplate;
import com.unstoppedable.notify.PaySuccessCallBack;




public class NotifyServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader((ServletInputStream)request.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while((line = br.readLine())!=null){
            sb.append(line);
            System.out.println("###  "+line);
        }
        //sb为微信返回的xml
        System.out.println(sb.toString());
        
        
        PayNotifyTemplate payNotifyTemplate = new PayNotifyTemplate(sb.toString());
        String responseXml = payNotifyTemplate.execute(new PaySuccessCallBack() {
            @Override
            public void onSuccess(PayNotifyData data) {
                /*<xml><appid><![CDATA[wx3289da9eb52f5576]]></appid>
                <bank_type><![CDATA[CFT]]></bank_type>
                <cash_fee><![CDATA[1]]></cash_fee>
                <fee_type><![CDATA[CNY]]></fee_type>
                <is_subscribe><![CDATA[N]]></is_subscribe>
                <mch_id><![CDATA[1300480401]]></mch_id>
                <nonce_str><![CDATA[262vgcnec1l952tp8nq8z414vzvbqxuq]]></nonce_str>
                <openid><![CDATA[o2rTjwBQ6xS9BSnDgf8pdYJPNvgE]]></openid>
                <out_trade_no><![CDATA[wx3289da9eb52f55761451659687]]></out_trade_no>
                <result_code><![CDATA[SUCCESS]]></result_code>
                <return_code><![CDATA[SUCCESS]]></return_code>
                <sign><![CDATA[99B90242B702C0930678282FA602BFBA]]></sign>
                <time_end><![CDATA[20160101224847]]></time_end>
                <total_fee>1</total_fee>
                <trade_type><![CDATA[JSAPI]]></trade_type>
                <transaction_id><![CDATA[1000530403201601012451722353]]></transaction_id>
                </xml>*/
            	
            	
            	System.out.println(data);
                System.out.println(data.getOut_trade_no() + "pay success!");
                String sql = "INSERT INTO pay_record(totalFee,timeEnd,transactionId,outTradeNo,resultCode,returnCode,bankType,cashFee,feeType,isSubscribe,nonceStr,SIGN,tradeType,createDate,deleted)" +
        		" VALUES('"+data.getTotal_fee()+"','"+data.getTime_end()+"','"+data.getTransaction_id()+"','"+data.getOut_trade_no()+"','"+data.getResult_code()+"','"+data.getReturn_code()+"'," +
        				"'"+data.getBank_type()+"','"+data.getCash_fee()+"','"+data.getCash_fee_type()+"','"+data.getIs_subscribe()+"','"+data.getNonce_str()+"','"+data.getSign()+"','"+data.getTrade_type()+"','"+CommonUtil.createDate()+"',0)";

                
            }
        });
        
       
		/*try {
			Long id = (Long)JdbcHelper.insertWithReturnPrimeKey(sql);
			System.out.println(id);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}*/
       
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}

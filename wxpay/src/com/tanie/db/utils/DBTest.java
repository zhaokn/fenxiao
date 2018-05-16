package com.tanie.db.utils;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class DBTest {

	
	public static String createDate() {
		java.text.SimpleDateFormat d = new java.text.SimpleDateFormat();
		d.applyPattern("yyyy-MM-dd hh:mm:ss");
		java.util.Date nowdate = new java.util.Date();
		String date = d.format(nowdate);
		return date;
	}

	
	public static void main(String[] args) {
		
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
		
		String sql = "INSERT INTO pay_record(totalFee,timeEnd,transactionId,outTradeNo,resultCode,returnCode,bankType,cashFee,feeType,isSubscribe,nonceStr,SIGN,tradeType,createDate,deleted)" +
				" VALUES('1','20160101224847','1000530403201601012451722353','wx3289da9eb52f55761451659687','SUCCESS','SUCCESS','CFT','1','CNY','N','262vgcnec1l952tp8nq8z414vzvbqxuq','99B90242B702C0930678282FA602BFBA','JSAPI','"+createDate()+"',0)";
		
		try {
			Long id = (Long)JdbcHelper.insertWithReturnPrimeKey(sql);
			System.out.println(id);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		//######################      查询                  ######################
		try {
			List<Map> list = JdbcHelper.query("select * from pay_record");
			System.out.println(list);
			for (Map p : list) {
				System.out.println(p.get("name"));
				System.out.println(p.get("password"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}

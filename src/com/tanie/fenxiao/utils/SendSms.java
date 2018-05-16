package com.tanie.fenxiao.utils;

import java.io.IOException;

public class SendSms {
	public static void send(int code, String phone) throws IOException {
		String strUrl = ResourcesConfiguration.getInstance().getValue("smsUrl");
//		accountSid=a14f6bfd43ce44c9b019de57f4e2de4b&smsContent=【秒嘀科技】您的验证码是345678，30分钟输入有效。
//    &to=13896543210&timestamp=20150821100312&sig=a14f6bfd43ue44c9b019du57f4e2ee4r&respDataType=JSON
		StringBuffer sb = new StringBuffer();
		sb.append("accountSid="+Config.ACCOUNT_SID);

		sb.append("&param="+code);
		sb.append("&templateid=208014650");
		sb.append("&to="+phone);
		sb.append(HttpUtil.createCommonParam());
		sb.append("&respDataType="+Config.RESP_DATA_TYPE);
		 HttpUtil.post(strUrl, String.valueOf(sb));

//		BufferedReader in = null;
//		try {
//			URL realUrl = new URL(strUrl + "&content=" + content + "&phone="
//					+ phone);
//
//			URLConnection connection = realUrl.openConnection();
//
//			connection.setRequestProperty("accept", "*/*");
//			connection.setRequestProperty("connection", "Keep-Alive");
//			connection.setRequestProperty("user-agent",
//					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
//
//			connection.connect();
//
//			Map map = connection.getHeaderFields();
//
//			in = new BufferedReader(new InputStreamReader(
//					connection.getInputStream()));
//			String line;
//			while ((line = in.readLine()) != null) {
//				result = result + line;
//			}
//		} catch (Exception e) {
//			System.out.println("发送GET请求出现异常！" + e);
//			e.printStackTrace();
//			try {
//				if (in != null)
//					in.close();
//			} catch (Exception e2) {
//				e2.printStackTrace();
//			}
//		} finally {
//			try {
//				if (in != null)
//					in.close();
//			} catch (Exception e2) {
//				e2.printStackTrace();
//			}
//		}
	}

//	public static void main(String[] args) {
//		try {
//			send("测试发送短信【易联通】", "18705080053");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}

package com.tanie.pay.servlet;

import com.unstoppedable.common.Configure;

public class Test {

	public static void main(String[] args) {
		Configure config = new Configure();
		System.out.println(config.getAppid());
		System.out.println(config.getOpenId());
		System.out.println(config.getAppSecret());
	}
}

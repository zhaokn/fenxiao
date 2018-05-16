package com.tanie.db.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DbConfig {
	
	private String driver;
	private String url;
	private String userName;
	private String password;
	
	public DbConfig() {
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("database.properties");
		Properties p=new Properties();
		try {
			p.load(inputStream);
			this.driver=p.getProperty("jdbc.driverClass");
			this.url=p.getProperty("jdbc.jdbcUrl");
			this.userName=p.getProperty("jdbc.user");
			this.password=p.getProperty("jdbc.password");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public String getDriver() {
		return driver;
	}
	public String getUrl() {
		return url;
	}
	public String getUserName() {
		return userName;
	}
	public String getPassword() {
		return password;
	}
	
	

}

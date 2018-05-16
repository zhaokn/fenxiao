package com.tanie.db.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class JdbcUnits {

	private static String url ;
	private static String userName ;
	private static String password;
	
	private static String driver;

	static {
		
		DbConfig config=new DbConfig();
		url=config.getUrl();
		userName=config.getUserName();
		password=config.getPassword();
		driver=config.getDriver();
		
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static Connection getConnection() throws SQLException {
		Connection conn = null;
		conn = DriverManager.getConnection(url, userName, password);
		return conn;
	}

	private static void freeConnection(Connection conn) {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void freeStatement(Statement statement) {
		try {
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void freeResultSet(ResultSet rs) {
		try {
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void free(Connection conn, Statement statement, ResultSet rs) {
		if (rs != null) {
			freeResultSet(rs);
		}
		if (statement != null) {
			freeStatement(statement);
		}
		if (conn != null) {
			freeConnection(conn);
		}
	}

}

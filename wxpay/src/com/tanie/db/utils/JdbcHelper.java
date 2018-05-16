package com.tanie.db.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcHelper {

	private static Connection conn = null;
	private static PreparedStatement preparedStatement = null;
	private static CallableStatement callableStatement = null;

	@SuppressWarnings("rawtypes")
	public static List query(String sql) throws SQLException {

		ResultSet rs = null;
		try {
			getPreparedStatement(sql);
			rs = preparedStatement.executeQuery();
			return ResultToListMap(rs);
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			free(rs);
		}

	}

	@SuppressWarnings("rawtypes")
	public static List query(String sql, Object... paramters)
			throws SQLException {

		ResultSet rs = null;
		try {
			getPreparedStatement(sql);

			for (int i = 0; i < paramters.length; i++) {
				preparedStatement.setObject(i + 1, paramters[i]);
			}
			rs = preparedStatement.executeQuery();
			return ResultToListMap(rs);
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			free(rs);
		}
	}

	public static Object getSingle(String sql) throws SQLException {
		Object result = null;
		ResultSet rs = null;
		try {
			getPreparedStatement(sql);
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				result = rs.getObject(1);
			}
			return result;
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			free(rs);
		}

	}

	public static Object getSingle(String sql, Object... paramters)
			throws SQLException {
		Object result = null;
		ResultSet rs = null;
		try {
			getPreparedStatement(sql);

			for (int i = 0; i < paramters.length; i++) {
				preparedStatement.setObject(i + 1, paramters[i]);
			}
			rs = preparedStatement.executeQuery();
			if (rs.next()) {
				result = rs.getObject(1);
			}
			return result;
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			free(rs);
		}
	}

	public static int update(String sql) throws SQLException {

		try {
			getPreparedStatement(sql);

			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			free();
		}
	}

	public static int update(String sql, Object... paramters)
			throws SQLException {
		try {
			getPreparedStatement(sql);

			for (int i = 0; i < paramters.length; i++) {
				preparedStatement.setObject(i + 1, paramters[i]);
			}
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			free();
		}
	}

	public static Object insertWithReturnPrimeKey(String sql)
			throws SQLException {
		ResultSet rs = null;
		Object result = null;
		try {
			conn = JdbcUnits.getConnection();
			preparedStatement = conn.prepareStatement(sql,
					PreparedStatement.RETURN_GENERATED_KEYS);
			preparedStatement.execute();
			rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				result = rs.getObject(1);
			}
			return result;
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}

	public static Object insertWithReturnPrimeKey(String sql,
			Object... paramters) throws SQLException {
		ResultSet rs = null;
		Object result = null;
		try {
			conn = JdbcUnits.getConnection();
			preparedStatement = conn.prepareStatement(sql,
					PreparedStatement.RETURN_GENERATED_KEYS);
			for (int i = 0; i < paramters.length; i++) {
				preparedStatement.setObject(i + 1, paramters[i]);
			}
			preparedStatement.execute();
			rs = preparedStatement.getGeneratedKeys();
			if (rs.next()) {
				result = rs.getObject(1);
			}
			return result;
		} catch (SQLException e) {
			throw new SQLException(e);
		}

	}

	@SuppressWarnings("rawtypes")
	public static List callableQuery(String procedureSql) throws SQLException {
		ResultSet rs = null;
		try {
			getCallableStatement(procedureSql);
			rs = callableStatement.executeQuery();
			return ResultToListMap(rs);
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			free(rs);
		}
	}

	@SuppressWarnings("rawtypes")
	public static List callableQuery(String procedureSql, Object... paramters)
			throws SQLException {
		ResultSet rs = null;
		try {
			getCallableStatement(procedureSql);

			for (int i = 0; i < paramters.length; i++) {
				callableStatement.setObject(i + 1, paramters[i]);
			}
			rs = callableStatement.executeQuery();
			return ResultToListMap(rs);
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			free(rs);
		}
	}

	public static Object callableGetSingle(String procedureSql)
			throws SQLException {
		Object result = null;
		ResultSet rs = null;
		try {
			getCallableStatement(procedureSql);
			rs = callableStatement.executeQuery();
			while (rs.next()) {
				result = rs.getObject(1);
			}
			return result;
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			free(rs);
		}
	}

	public static Object callableGetSingle(String procedureSql,
			Object... paramters) throws SQLException {
		Object result = null;
		ResultSet rs = null;
		try {
			getCallableStatement(procedureSql);

			for (int i = 0; i < paramters.length; i++) {
				callableStatement.setObject(i + 1, paramters[i]);
			}
			rs = callableStatement.executeQuery();
			while (rs.next()) {
				result = rs.getObject(1);
			}
			return result;
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			free(rs);
		}
	}

	public static Object callableWithParamters(String procedureSql)
			throws SQLException {
		try {
			getCallableStatement(procedureSql);
			callableStatement.registerOutParameter(0, Types.OTHER);
			callableStatement.execute();
			return callableStatement.getObject(0);

		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			free();
		}

	}

	public static int callableUpdate(String procedureSql) throws SQLException {
		try {
			getCallableStatement(procedureSql);
			return callableStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			free();
		}
	}

	public static int callableUpdate(String procedureSql, Object... parameters)
			throws SQLException {
		try {
			getCallableStatement(procedureSql);
			for (int i = 0; i < parameters.length; i++) {
				callableStatement.setObject(i + 1, parameters[i]);
			}
			return callableStatement.executeUpdate();
		} catch (SQLException e) {
			throw new SQLException(e);
		} finally {
			free();
		}
	}

	public static int[] batchUpdate(List<String> sqlList) {

		int[] result = new int[] {};
		Statement statenent = null;
		try {
			conn = JdbcUnits.getConnection();
			conn.setAutoCommit(false);
			statenent = conn.createStatement();
			for (String sql : sqlList) {
				statenent.addBatch(sql);
			}
			result = statenent.executeBatch();
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				throw new ExceptionInInitializerError(e1);
			}
			throw new ExceptionInInitializerError(e);
		} finally {
			free(statenent, null);
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List ResultToListMap(ResultSet rs) throws SQLException {
		List list = new ArrayList();
		while (rs.next()) {
			ResultSetMetaData md = rs.getMetaData();
			Map map = new HashMap();
			for (int i = 1; i < md.getColumnCount(); i++) {
				map.put(md.getColumnLabel(i), rs.getObject(i));
			}
			list.add(map);
		}
		return list;
	}

	private static void getPreparedStatement(String sql) throws SQLException {
		conn = JdbcUnits.getConnection();
		preparedStatement = conn.prepareStatement(sql);
	}

	private static void getCallableStatement(String procedureSql)
			throws SQLException {
		conn = JdbcUnits.getConnection();
		callableStatement = conn.prepareCall(procedureSql);
	}

	public static void free(ResultSet rs) {

		JdbcUnits.free(conn, preparedStatement, rs);
	}

	public static void free(Statement statement, ResultSet rs) {
		JdbcUnits.free(conn, statement, rs);
	}

	public static void free() {

		free(null);
	}

}

package com.homekey.core.storage.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.homekey.core.Logs;
import com.homekey.core.log.L;
import com.homekey.core.storage.Database;
import com.homekey.core.storage.DatabaseTable;

public class SqliteDatabase extends Database {
	private Connection conn;
	
	public SqliteDatabase(String databaseName) {
		super(databaseName);
	}
	
	public SqliteDatabase() {
		super();
	}
	
	@Override
	public void close() {
		try {
			L.getLogger(Logs.HOMEKEY).log("Shutting down database");
			conn.close();
		} catch (SQLException ex) {
			System.err.println("close(): Couldn't close database connection.");
		}
	}
	
	@Override
	public void addRow(String table, String[] columns, Object[] values) {
		String sql = "INSERT INTO " + table + "(";
		
		sql += SqliteUtil.merge(columns, 0, ", ");
		sql = sql.substring(0, sql.length() - 2) + ") VALUES(";
		sql += SqliteUtil.merge(columns.length, "?, ");
		sql = sql.substring(0, sql.length() - 2) + ");";
		
		PreparedStatement stat;
		
		try {
			stat = conn.prepareStatement(sql);
			
			for (int i = 0; i < columns.length; i++) {
				stat.setObject(i + 1, values[i]);
			}
			stat.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void updateRow(String table, String[] updateColumns, Object[] updateValues, String whereColumn, Object whereValue) {
		String sql = "UPDATE " + table + " SET ";
		
		sql = sql + SqliteUtil.merge(updateColumns, 0, " = ?, ");
		
		sql = sql.substring(0, sql.length() - 2) + " WHERE " + whereColumn + " = ?;";
		
		try {
			PreparedStatement stat = conn.prepareStatement(sql);
			
			for (int i = 0; i < updateColumns.length; i++) {
				stat.setObject(i + 1, updateValues[i]);
			}
			stat.setObject(updateColumns.length+1, whereValue);
			stat.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Object[] getRow(String table, String[] selectColumns, String whereColumn, Object whereValue) {
		Object[] values = null;
		String sql = "SELECT ";
		sql += SqliteUtil.merge(selectColumns, 0, ", ");
		sql = sql.substring(0, sql.length() - 2) + " FROM " + table + " WHERE " + whereColumn + " = ?;";
		
		PreparedStatement stat;
		
		try {
			stat = conn.prepareStatement(sql);
			stat.setObject(1, whereValue);
			ResultSet rs = stat.executeQuery();
			
			if (rs.next()) {
				values = new Object[selectColumns.length];
				try {
					for (int i = 0; i < selectColumns.length; i++) {
						values[i] = rs.getObject(i + 1);
					}
				} finally {
					rs.close();
				}
			}
		} catch (SQLException ex) {
			L.e("Couldn't execute SQL query.");
		}
		
		return values;
	}
	
	@Override
	public boolean tableExists(String name) {
		return executeScalar("SELECT COUNT(name) FROM sqlite_master WHERE type='table' AND name='" + name + "';") > 0;
	}
	
	@Override
	public Object getField(String table, String selectColumn, String whereColumn, Object whereValue) {
		Object[] fields = getRow(table, new String[] { selectColumn }, whereColumn, whereValue);
		return fields == null ? null : fields[0];
	}
	
	@Override
	public Object getTopFieldOrderByDescending(String table, String column, String orderByColumn) {
		Object value = null;
		String sql = "SELECT " + column + " FROM " + table + " ORDER BY " + orderByColumn + " DESC LIMIT 1";
		
		try {
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(sql);
			
			rs.next();
			try {
				value = rs.getObject(1);
			} finally {
				rs.close();
			}
		} catch (SQLException ex) {
			System.err.println("loadDevice(): Couldn't execute SQL query.");
		}
		
		return value;
	}
	
	@Override
	protected void open() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException ex) {
			System.err.println("open(): Couldn't load SQlite JDBC driver.");
		}
		
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
		} catch (SQLException ex) {
			System.err.println("open(): Couldn't open database named " + databaseName + ".");
		}
	}
	
	@Override
	public void createTable(String name, DatabaseTable table) {
		String sql = "CREATE TABLE " + name + " (Id INTEGER PRIMARY KEY AUTOINCREMENT, ";
		
		for (int i = 0; i < table.getColumnCount(); i++) {
			sql += table.getName(i) + " " + SqliteUtil.convertToSqlRepresentation(table.getType(i)) + ", ";
		}
		
		sql = sql.substring(0, sql.length() - 2) + ");";
		executeUpdate(sql);
	}
	
	private void executeUpdate(String sql) {
		Statement stat;
		
		try {
			stat = conn.createStatement();
			stat.executeUpdate(sql);
		} catch (SQLException ex) {
			System.err.println("executeUpdate(): Couldn't execute SQL command.");
		}
	}
	
	private int executeScalar(String sql) {
		Statement stat;
		int count = 0;
		
		try {
			stat = conn.createStatement();
			
			ResultSet rs = stat.executeQuery(sql);
			rs.next();
			count = rs.getInt(1);
			rs.close();
		} catch (SQLException ex) {
			System.err.println("executeScalar(): Couldn't execute SQL query.");
		}
		
		return count;
	}
}

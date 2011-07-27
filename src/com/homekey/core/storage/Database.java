package com.homekey.core.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.homekey.core.device.Device;

public class Database {
	private final String DEFAULT_DATABASE_NAME = "homekey.db";
	private final String SENSOR_PREFIX = "D_";
	
	private String databaseName;
	private Connection conn;
	
	public Database() {
		databaseName = DEFAULT_DATABASE_NAME;
		init();
		open();
		ensureSystemTables();
	}
	
	public Database(String name) {
		databaseName = name;
		init();
		open();
		ensureSystemTables();
	}
	
	public void ensureDevice(Device device) {
		boolean exists = executeScalar("SELECT COUNT(internalid) FROM devices WHERE internalid = '" + device.getInternalId() + "';") > 0;
		
		if (exists) {
			loadDevice(device);
		}
		else {
			addDevice(device);
			createTableFor(device);
		}
	}
	
	private void loadDevice(Device device) {
		Statement stat;
		
		try {
			stat = conn.createStatement();
			
			ResultSet rs = stat.executeQuery("SELECT * FROM devices WHERE internalid = '" + device.getInternalId() + "';");
			rs.next();
			
			try {
				if (!rs.getString("type").equals(device.getClass().getSimpleName())) {
					throw new ClassCastException("The device sent to function was of type " + device.getClass().getSimpleName() + ", but corresponding object in database was of type " + rs.getString("type") + ".");
				}
				
				device.setId(rs.getInt("id"));
				device.setName(rs.getString("name"));
				device.setActive(rs.getBoolean("active"));
			} finally {
				rs.close();
			}
		} catch (SQLException ex) {
			System.err.println("loadDevice(): Couldn't execute SQL query.");
		}
	}
	
	public void putRow(int id, DataRow row) {
		
	}
	
	public void close() {
		try {
			System.out.println("Shutting down database");
			conn.close();
		} catch (SQLException ex) {
			System.err.println("close(): Couldn't close database connection.");
		}
	}
	
	private void addDevice(Device device) {
		PreparedStatement stat;
		
		try {
			stat = conn.prepareStatement("INSERT INTO devices(internalid, type, name, added, active) VALUES(?, ?, ?, ?, ?);");
			stat.setString(1, device.getInternalId());
			stat.setString(2, device.getClass().getSimpleName());
			stat.setString(3, device.getName());
			stat.setDate(4, new java.sql.Date(device.getAdded().getTime()));
			stat.setBoolean(5, device.isActive());
			stat.execute();
		} catch (SQLException ex) {
			System.err.println("addDevice(): Couldn't execute INSERT SQL.");
		}
		
		device.setId(executeScalar("SELECT id FROM devices WHERE internalid = '" + device.getInternalId() + "';"));
	}
	
	private void createTableFor(Device device) {
		DatabaseTable table = device.getTableDesign();
		String tableName = SENSOR_PREFIX + device.getClass().getSimpleName() + "_" + device.getId();
		String sql = "CREATE TABLE " + tableName + " (Id INTEGER PRIMARY KEY AUTOINCREMENT, ";
		
		for (int i = 0; i < table.getColumnCount(); i++) {
			sql += table.getName(i) + " " + convertToSqlRepresentation(table.getType(i)) + ", ";
		}
		
		sql = sql.substring(0, sql.length() - 2) + ");";
		executeUpdate(sql);
	}
	
	private String convertToSqlRepresentation(ColumnType type) {
		switch (type) {
		case Integer:
			return "INTEGER";
		case DateTime:
			return "DATETIME";
		case Float:
			return "REAL";
		case String:
			return "TEXT";
		case Boolean:
			return "BOOLEAN";
		default:
			throw new IllegalArgumentException();
		}
	}
	
	private void init() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException ex) {
			System.err.println("init(): Couldn't load SQlite JDBC driver.");
		}
	}
	
	private void open() {
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + databaseName);
		} catch (SQLException ex) {
			System.err.println("open(): Couldn't open database named " + databaseName + ".");
		}
	}
	
	private void ensureSystemTables() {
		if (!tableExists("devices")) {
			executeUpdate("CREATE TABLE devices(id INTEGER PRIMARY KEY AUTOINCREMENT, internalid TEXT, type TEXT, name TEXT, added DATETIME, active BOOLEAN)");
		}
	}
	
	private boolean tableExists(String tableName) {
		return executeScalar("SELECT COUNT(name) FROM sqlite_master WHERE type='table' AND name='" + tableName + "';") > 0;
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

package com.homeki.core.storage.sqlite.versions;

import com.homeki.core.storage.sqlite.SqliteDatabaseVersion;

public class To0_0_20 extends SqliteDatabaseVersion {
	public To0_0_20(String databasePath) {
		super("0.0.20", databasePath);
	}

	@Override
	public void run() {
		executeUpdate("CREATE TABLE t_devices(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				  "internalid STRING, " +
				  "type STRING, " +
				  "name STRING, " + 
				  "added DATETIME, " +
				  "active BOOLEAN);");
		executeUpdate("INSERT INTO t_devices SELECT id, internalid, type, name, added, active FROM devices;");
		executeUpdate("DROP TABLE devices;");
		executeUpdate("CREATE TABLE devices(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				  "internalid STRING, " +
				  "type STRING, " +
				  "name STRING, " +
				  "description STRING, " +
				  "added DATETIME, " +
				  "active BOOLEAN);");
		executeUpdate("INSERT INTO devices SELECT id, internalid, type, name, '', added, active FROM t_devices; ");
		executeUpdate("DROP TABLE t_devices;");
	}
}
package com.homeki.core.storage.sqlite;

import java.util.ArrayList;
import java.util.List;

import com.homeki.core.log.L;
import com.homeki.core.storage.sqlite.versions.To0_0_20;

public class SqliteDatabaseUpgrader extends SqliteTable {
	private List<SqliteDatabaseVersion> versions;
	private SqliteDatabaseVersion fromVersion;
	
	public SqliteDatabaseUpgrader(String databasePath, String fromVersion) {
		super(databasePath);
		this.versions = new ArrayList<SqliteDatabaseVersion>();
		this.fromVersion = new SqliteDatabaseVersion(fromVersion, databasePath);
	}
	
	public boolean execute() {
		versions.add(new To0_0_20(databasePath));
		
		boolean upgradeFromNow = false;
		
		for (int i = 0; i < versions.size(); i++) {
			SqliteDatabaseVersion v = versions.get(i);
			
			if (v.compareTo(fromVersion) > 0)
				upgradeFromNow = true;
			
			if (upgradeFromNow) {
				v.run();
				L.i("Upgraded database to version " + v + ".");
			}
		}
		
		return upgradeFromNow;
	}
}
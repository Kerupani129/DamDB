package net.kerupani129.damdb;

import java.sql.*;

// 
// SqlUtils クラス
// 
public class SqlUtils {
	
	// 
	// コンストラクタ
	// 
	private SqlUtils() {}
	
	// 
	// テーブル 存在チェック
	// 
	public static boolean tableExists(Statement stmt, String name) throws SQLException {
		
		ResultSet rs = stmt.executeQuery(
			"SELECT count(*) FROM sqlite_master WHERE type == 'table' and name == '" + name.replace("'", "''") + "';"
		);
		rs.next();
		return (0 == rs.getInt("count(*)"))?(false):(true);
		
		/*
		return stmt.executeQuery(
			"SELECT\n" +
			"	CASE\n" +
			"		WHEN EXISTS(SELECT 1 FROM sqlite_master WHERE type == 'table' and name == '" + name.replace("'", "''") + "')\n" +
			"			THEN 1\n" +
			"		ELSE 0\n" +
			"	END AS 'exists'\n" +
			";"
		).getBoolean("exists");
		*/
		
	}
	
}

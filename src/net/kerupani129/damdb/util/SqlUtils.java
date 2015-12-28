package net.kerupani129.damdb.util;

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
			"SELECT\n" +
			"	CASE\n" +
			"		WHEN EXISTS(SELECT 1 FROM sqlite_master WHERE type == 'table' and name == '" + name.replace("'", "''") + "')\n" +
			"			THEN 1\n" +
			"		ELSE 0\n" +
			"	END AS 'exists'\n" +
			";"
		);
		rs.next();
		return rs.getBoolean("exists");
		
	}
	
}

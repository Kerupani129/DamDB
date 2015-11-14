package net.kerupani129.damdb;

import java.io.*;
import java.util.*;
import java.net.*;
import java.sql.*;
import java.nio.file.*;

import org.jsoup.*;
import org.jsoup.nodes.*;

import net.kerupani129.damdb.util.*;

// 
// Damtomo クラス
// 
public class Damtomo {
	
	// 定数
	/* ファイル・ディレクトリ */
	private static final String dbDir = "../db";
	private static final String dbName = "dam";
	/* 通信 */
	public static final int timeout = 10000;
	
	// 変数
	/* データベース */
	private java.sql.Connection con;
	private Statement stmt;
	/* ユーザー情報 */
	private String damtomoId;
	private String cdmCardNo;
	private String name;
	/* 採点情報 */
	private final MarkingDx markingDx;
	
	// 
	// コンストラクタ
	// 
	public Damtomo() throws ClassNotFoundException, SQLException {
		
		// ディレクトリがなければ作成
		File dir = new File(this.dbDir);
		if (!dir.exists()) dir.mkdir();
		
		// データベース に接続
		Class.forName("org.sqlite.JDBC");
		this.con = DriverManager.getConnection("jdbc:sqlite:" + Paths.get(this.dbDir, this.dbName + ".db"));
		this.stmt = this.con.createStatement();
		
		// データベース から ユーザー情報 を取得
		this.getProfile();
		
		// データベース のバージョンチェック・修正
		if (this.ready()) {
			this.fitVersion();
		}
		
		// 採点情報用のインスタンス初期化
		this.markingDx = new MarkingDx(this);
		
	}
	
	// 
	// プロフィールページ url から ユーザー情報 を取得し 
	// データベース に ユーザー情報 書き込み
	// 
	public void setProfile(String url) throws IOException, URISyntaxException, SQLException {
		
		// プフィールページ  から情報取得
		Document doc = Jsoup.connect(url).timeout(this.timeout).get();
		this.damtomoId = NetUtils.getQueryMap(new URL(url).getQuery()).get("damtomoId"); // 暗号化された damtomoId
		this.cdmCardNo = doc.getElementById("cdmCardNo").attr("value"); // 暗号化された cdmCardNo
		this.name = doc.select("div.name > p > span").first().ownText(); // 表示名
		
		// データベース に ユーザー情報 保存
		this.stmt.executeUpdate(
			"CREATE TABLE IF NOT EXISTS Profile(key text primary key, value);\n" +
			"REPLACE INTO Profile VALUES('damtomoId', '" + this.damtomoId.replace("'", "''") + "');\n" +
			"REPLACE INTO Profile VALUES('cdmCardNo', '" + this.cdmCardNo.replace("'", "''") + "');\n" +
			"REPLACE INTO Profile VALUES('name', '" + this.name.replace("'", "''") + "');"
		);
		
	}
	
	// 
	// データベース から ユーザー情報 取得
	// 
	private void getProfile() throws SQLException {
		
		// テーブル 存在チェック
		if (!SqlUtils.tableExists(this.stmt, "Profile")) return;
		
		// データベース から ユーザー情報 取得
		this.damtomoId = this.stmt.executeQuery(
			"SELECT (SELECT value FROM Profile WHERE key == 'damtomoId') AS value;"
		).getString("value");
		this.cdmCardNo = this.stmt.executeQuery(
			"SELECT (SELECT value FROM Profile WHERE key == 'cdmCardNo') AS value;"
		).getString("value");
		this.name = this.stmt.executeQuery(
			"SELECT (SELECT value FROM Profile WHERE key == 'name') AS value;"
		).getString("value");
		
	}
	
	// 
	// バージョン情報を確認し、必要があれば変換する
	// 
	public void fitVersion() throws SQLException {
		
		// バージョン変換
		FitVersion.fit(this);
		
	}
	
	
	// 
	// データベース クローズ
	// 
	// 本当は、finalize() オーバーライドして呼び出したり、
	// クローズされているかどうかフラグで管理した方がいいのかしら
	// 
	public void close() throws SQLException {
		
		// データベース クローズ
		this.stmt.close();
		this.con.close();
		
	}
	
	// 
	// このオブジェクトが使用可能か調べる
	// 
	public boolean ready() throws SQLException {
		
		if (this.stmt.isClosed() || this.con.isClosed()) return false;
		
		if (this.damtomoId == null || this.cdmCardNo == null || this.name == null) return false;
		
		return true;
		
	}
	
	// 
	// Statement を取得する
	// 
	Statement getStatement() {
		return this.stmt;
	}
	
	// 
	// damtomoId を取得する
	// 
	public String getDamtomoId() {
		return this.damtomoId;
	}
	
	// 
	// cdmCardNo を取得する
	// 
	public String getCdmCardNo() {
		return this.cdmCardNo;
	}
	
	// 
	// name を取得する
	// 
	public String getName() {
		return this.name;
	}
	
	// 
	// name を設定する
	// 
	// 引数が null の時は何もしない
	// 
	public void setName(String name) throws SQLException {
		
		if (name == null) return;
		
		this.stmt.executeUpdate(
			"CREATE TABLE IF NOT EXISTS Profile(key text primary key, value);\n" +
			"REPLACE INTO Profile VALUES('name', '" + name.replace("'", "''") + "');"
		);
		
		this.name = name;
		
	}
	
	// 
	// MarkingDx オブジェクト 取得
	// 
	public MarkingDx getMarkingDx() {
		return markingDx;
	}
	
}

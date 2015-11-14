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
// Damtomo �N���X
// 
public class Damtomo {
	
	// �萔
	/* �t�@�C���E�f�B���N�g�� */
	private static final String dbDir = "../db";
	private static final String dbName = "dam";
	/* �ʐM */
	public static final int timeout = 10000;
	
	// �ϐ�
	/* �f�[�^�x�[�X */
	private java.sql.Connection con;
	private Statement stmt;
	/* ���[�U�[��� */
	private String damtomoId;
	private String cdmCardNo;
	private String name;
	/* �̓_��� */
	private final MarkingDx markingDx;
	
	// 
	// �R���X�g���N�^
	// 
	public Damtomo() throws ClassNotFoundException, SQLException {
		
		// �f�B���N�g�����Ȃ���΍쐬
		File dir = new File(this.dbDir);
		if (!dir.exists()) dir.mkdir();
		
		// �f�[�^�x�[�X �ɐڑ�
		Class.forName("org.sqlite.JDBC");
		this.con = DriverManager.getConnection("jdbc:sqlite:" + Paths.get(this.dbDir, this.dbName + ".db"));
		this.stmt = this.con.createStatement();
		
		// �f�[�^�x�[�X ���� ���[�U�[��� ���擾
		this.getProfile();
		
		// �f�[�^�x�[�X �̃o�[�W�����`�F�b�N�E�C��
		if (this.ready()) {
			this.fitVersion();
		}
		
		// �̓_���p�̃C���X�^���X������
		this.markingDx = new MarkingDx(this);
		
	}
	
	// 
	// �v���t�B�[���y�[�W url ���� ���[�U�[��� ���擾�� 
	// �f�[�^�x�[�X �� ���[�U�[��� ��������
	// 
	public void setProfile(String url) throws IOException, URISyntaxException, SQLException {
		
		// �v�t�B�[���y�[�W  ������擾
		Document doc = Jsoup.connect(url).timeout(this.timeout).get();
		this.damtomoId = NetUtils.getQueryMap(new URL(url).getQuery()).get("damtomoId"); // �Í������ꂽ damtomoId
		this.cdmCardNo = doc.getElementById("cdmCardNo").attr("value"); // �Í������ꂽ cdmCardNo
		this.name = doc.select("div.name > p > span").first().ownText(); // �\����
		
		// �f�[�^�x�[�X �� ���[�U�[��� �ۑ�
		this.stmt.executeUpdate(
			"CREATE TABLE IF NOT EXISTS Profile(key text primary key, value);\n" +
			"REPLACE INTO Profile VALUES('damtomoId', '" + this.damtomoId.replace("'", "''") + "');\n" +
			"REPLACE INTO Profile VALUES('cdmCardNo', '" + this.cdmCardNo.replace("'", "''") + "');\n" +
			"REPLACE INTO Profile VALUES('name', '" + this.name.replace("'", "''") + "');"
		);
		
	}
	
	// 
	// �f�[�^�x�[�X ���� ���[�U�[��� �擾
	// 
	private void getProfile() throws SQLException {
		
		// �e�[�u�� ���݃`�F�b�N
		if (!SqlUtils.tableExists(this.stmt, "Profile")) return;
		
		// �f�[�^�x�[�X ���� ���[�U�[��� �擾
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
	// �o�[�W���������m�F���A�K�v������Εϊ�����
	// 
	public void fitVersion() throws SQLException {
		
		// �o�[�W�����ϊ�
		FitVersion.fit(this);
		
	}
	
	
	// 
	// �f�[�^�x�[�X �N���[�Y
	// 
	// �{���́Afinalize() �I�[�o�[���C�h���ČĂяo������A
	// �N���[�Y����Ă��邩�ǂ����t���O�ŊǗ��������������̂�����
	// 
	public void close() throws SQLException {
		
		// �f�[�^�x�[�X �N���[�Y
		this.stmt.close();
		this.con.close();
		
	}
	
	// 
	// ���̃I�u�W�F�N�g���g�p�\�����ׂ�
	// 
	public boolean ready() throws SQLException {
		
		if (this.stmt.isClosed() || this.con.isClosed()) return false;
		
		if (this.damtomoId == null || this.cdmCardNo == null || this.name == null) return false;
		
		return true;
		
	}
	
	// 
	// Statement ���擾����
	// 
	Statement getStatement() {
		return this.stmt;
	}
	
	// 
	// damtomoId ���擾����
	// 
	public String getDamtomoId() {
		return this.damtomoId;
	}
	
	// 
	// cdmCardNo ���擾����
	// 
	public String getCdmCardNo() {
		return this.cdmCardNo;
	}
	
	// 
	// name ���擾����
	// 
	public String getName() {
		return this.name;
	}
	
	// 
	// name ��ݒ肷��
	// 
	// ������ null �̎��͉������Ȃ�
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
	// MarkingDx �I�u�W�F�N�g �擾
	// 
	public MarkingDx getMarkingDx() {
		return markingDx;
	}
	
}

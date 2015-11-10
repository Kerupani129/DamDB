package net.kerupani129.damdb;

import java.io.*;
import java.util.*;
import java.sql.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import org.jsoup.parser.*;

// 
// MarkingDx �N���X
// 
public class MarkingDx {
	
	// �ϐ�
	private Damtomo damtomo;
	
	// 
	// �R���X�g���N�^
	// 
	public MarkingDx(Damtomo damtomo) {
		this.damtomo = damtomo;
	}
	
	// 
	// �̓_�����X�V����
	// 
	public void update() throws IOException, SQLException {
		
		// �f�[�^�x�[�X �Ƀe�[�u�����Ȃ���΍쐬
		this.damtomo.getStatement().executeUpdate(
			"CREATE TABLE IF NOT EXISTS LastUpdateDate(name text primary key, date text);\n" +
			"INSERT OR IGNORE INTO LastUpdateDate VALUES('MarkingDx', '0000-00-00 00:00:00');\n" +
			"CREATE TABLE IF NOT EXISTS MarkingDx(\n" +
			"	requestNo text,\n" +
			"	artist text,\n" +
			"	contents text,\n" +
			"	\n" +
			"	play integer,\n" +
			"	reportCommentNo integer,\n" +
			"	\n" +
			"	chartTotalMilliPoint integer,\n" +
			"	chartInterval integer,\n" +
			"	chartStability integer,\n" +
			"	chartExpressiveness integer,\n" +
			"	chartVibrateLongtone integer,\n" +
			"	chartRhythm integer,\n" +
			"	\n" +
			"	highPitch integer,\n" +
			"	lowPitch integer,\n" +
			"	highTessitura integer,\n" +
			"	lowTessitura integer,\n" +
			"	\n" +
			"	modulation integer,\n" +
			"	measure integer,\n" +
			"	sob integer,\n" +
			"	fall integer,\n" +
			"	timing integer,\n" +
			"	longTone integer,\n" +
			"	vibrato integer,\n" +
			"	vibratoType integer,\n" +
			"	vibratoSumDeciSeconds integer,\n" +
			"	\n" +
			"	averageTotalMilliPoint integer,\n" +
			"	averagePitch integer,\n" +
			"	averageStability integer,\n" +
			"	averageExpressiveness integer,\n" +
			"	averageVibrateLongtone integer,\n" +
			"	averageRhythm integer,\n" +
			"	\n" +
			"	lastMilliPoint integer,\n" +
			"	\n" +
			"	date text primary key\n" +
			");"
		);
		
		// �\���p�� �ŏI�X�V���� �擾
		ResultSet rs = this.damtomo.getStatement().executeQuery(
			"SELECT date FROM LastUpdateDate WHERE name == 'MarkingDx';" /* �{���͂����ƃt�H�[�}�b�g�����ق����ǂ����� */
		);
		rs.next();
		String date_old = rs.getString("date");
		
		// XML �� 1 �y�[�W���ƂɎ擾
		pageLoop: for (int i = 1, totalPage = 1; i <= totalPage; i++) {
			
			// XML �� 1 �y�[�W �擾
			Document doc = Jsoup.connect("http://www.clubdam.com/app/damtomo/membership/MarkingDxListXML.do")
				.data("cdmCardNo", this.damtomo.getCdmCardNo())
				.data("enc", "utf-8")
				.data("pageNo", String.valueOf(i))
				.data("UTCserial", String.valueOf(new java.util.Date().getTime()))
				.timeout(Damtomo.timeout).parser(Parser.xmlParser()).get();
			
			// XML �`�F�b�N
			if (!"0000".equals(doc.select("document > result > statusCode").last().ownText())) {
				throw new IllegalStateException("�G���[: XML ���ُ�ł�: " + doc.select("document > result > message").last().ownText() + ": ");
			}
			
			// �y�[�W�� �`�F�b�N
			totalPage = Integer.parseInt(doc.select("document > data > totalPage").last().ownText());
			
			// �y�[�W���̍̓_���ʏ���
			Elements markings = doc.select("document > list > data > marking");
			
			for (Element marking: markings) {
				
				if (!this.parseElement(marking)) break pageLoop;
				
			}
			
		}
		
		// �����ɍX�V���I������� �ŏI�X�V���� �ۑ�
		this.damtomo.getStatement().executeUpdate(
			"REPLACE INTO LastUpdateDate VALUES(\n" +
			"	'MarkingDx',\n" +
			"	case\n" +
			"		when (select count(*) from (select * from MarkingDx limit 1)) == 0 then\n" +
			"			'0000-00-00 00:00:00'\n" +
			"		else\n" +
			"			(select max(date) from MarkingDx)\n" +
			"	end\n" +
			");"
		);
		
		// �\���p�� �ŏI�X�V���� �擾
		rs = this.damtomo.getStatement().executeQuery(
			"SELECT date FROM LastUpdateDate WHERE name == 'MarkingDx';" /* �{���͂����ƃt�H�[�}�b�g�����ق����ǂ����� */
		);
		rs.next();
		String date_new = rs.getString("date");
		
		// test
		System.out.print("MarkingDx �X�V: ");
		if (date_new.equals(date_old)) {
			System.out.println("�Ȃ�");
		} else if ("0000-00-00 00:00:00".equals(date_old)) {
			System.out.println(" - " + date_new);
		} else {
			System.out.println(date_old + " - " + date_new);
		}
		System.out.println();
		
	}
	
	// 
	// 1 �̍̓_�����p�[�X���� �f�[�^�x�[�X�ɒǉ�
	// 
	// �f�[�^�x�[�X�ɒǉ������ꍇ�� true �A
	// ���t�I�ɍX�V���I�����ׂ����́A�f�[�^��ǉ������� false ��Ԃ�
	// 
	boolean parseElement(Element marking) throws SQLException {
		
		// �e�l���p�[�X
		String requestNo = marking.attr("requestNo");
		String artist    = marking.attr("artist");
		String contents  = marking.attr("contents");
		
		String play = marking.attr("play");
		String reportCommentNo = marking.attr("reportCommentNo");
		
		String chartTotalPoint = marking.ownText();
		String chartInterval        = marking.attr("chartInterval");
		String chartStability       = marking.attr("chartStability");
		String chartExpressiveness  = marking.attr("chartExpressiveness");
		String chartVibrateLongtone = marking.attr("chartVibrateLongtone");
		String chartRhythm          = marking.attr("chartRhythm");
		
		String highPitch = marking.attr("highPitch");
		String lowPitch  = marking.attr("lowPitch");
		String highTessitura = marking.attr("highTessitura");
		String lowTessitura  = marking.attr("lowTessitura");
		
		String modulation            = marking.attr("modulation");
		String measure               = marking.attr("measure");
		String sob                   = marking.attr("sob");
		String fall                  = marking.attr("fall");
		String timing                = marking.attr("timing");
		String longTone              = marking.attr("longTone");
		String vibrato               = marking.attr("vibrato");
		String vibratoType           = marking.attr("vibratoType");
		String vibratoSumSeconds = marking.attr("vibratoSumSeconds");
		
		String averageTotalMilliPoint = marking.attr("averageTotalPoint");
		String averagePitch           = marking.attr("averagePitch");
		String averageStability       = marking.attr("averageStability");
		String averageExpressiveness  = marking.attr("averageExpressiveness");
		String averageVibrateLongtone = marking.attr("averageVibrateLongtone");
		String averageRhythm          = marking.attr("averageRhythm");
		
		String lastMilliPoint = marking.attr("lastPoint");
		
		String date = marking.attr("date");
		
		// ���t�`�F�b�N
		ResultSet rs = this.damtomo.getStatement().executeQuery(
			"SELECT\n" +
			"	CASE\n" +
			"		WHEN replace('" + date.replace("'", "''") + "', '/', '-') <= (SELECT date FROM LastUpdateDate WHERE name == 'MarkingDx') THEN\n" + /* �{���͂����ƃt�H�[�}�b�g�����ق����ǂ����� */
			"			1\n" +
			"		ELSE\n" +
			"			0\n" +
			"	END AS 'exists'\n" +
			";"
		);
		rs.next();
		if (rs.getBoolean("exists")) return false;
		
		// �f�[�^�x�[�X�ɒǉ�
		// �O��ɍX�V���Ɏ��s�������ȂǁA�f�[�^���d������\��������̂ŁA"INSERT OR IGNORE"
		this.damtomo.getStatement().executeUpdate(
			"INSERT OR IGNORE INTO MarkingDx VALUES(\n" +
			"	'" + requestNo.replace("'", "''") + "',\n" +
			"	'" + artist.replace("'", "''") + "',\n" +
			"	'" + contents.replace("'", "''") + "',\n" +
			"	\n" +
			"	'" + play.replace("'", "''") + "',\n" +
			"	'" + reportCommentNo.replace("'", "''") + "',\n" +
			"	\n" +
			"	'" + chartTotalPoint.replace("'", "''") + "' * 1000,\n" +
			"	'" + chartInterval.replace("'", "''") + "',\n" +
			"	'" + chartStability.replace("'", "''") + "',\n" +
			"	'" + chartExpressiveness.replace("'", "''") + "',\n" +
			"	'" + chartVibrateLongtone.replace("'", "''") + "',\n" +
			"	'" + chartRhythm.replace("'", "''") + "',\n" +
			"	\n" +
			"	'" + highPitch.replace("'", "''") + "',\n" +
			"	'" + lowPitch.replace("'", "''") + "',\n" +
			"	'" + highTessitura.replace("'", "''") + "',\n" +
			"	'" + lowTessitura.replace("'", "''") + "',\n" +
			"	\n" +
			"	'" + modulation.replace("'", "''") + "',\n" +
			"	'" + measure.replace("'", "''") + "',\n" +
			"	'" + sob.replace("'", "''") + "',\n" +
			"	'" + fall.replace("'", "''") + "',\n" +
			"	'" + timing.replace("'", "''") + "',\n" +
			"	'" + longTone.replace("'", "''") + "',\n" +
			"	'" + vibrato.replace("'", "''") + "',\n" +
			"	'" + vibratoType.replace("'", "''") + "',\n" +
			"	'" + vibratoSumSeconds.replace("'", "''") + "' * 10,\n" +
			"	\n" +
			"	'" + averageTotalMilliPoint.replace("'", "''") + "',\n" +
			"	'" + averagePitch.replace("'", "''") + "',\n" +
			"	'" + averageStability.replace("'", "''") + "',\n" +
			"	'" + averageExpressiveness.replace("'", "''") + "',\n" +
			"	'" + averageVibrateLongtone.replace("'", "''") + "',\n" +
			"	'" + averageRhythm.replace("'", "''") + "',\n" +
			"	\n" +
			"	'" + lastMilliPoint.replace("'", "''") + "',\n" +
			"	\n" +
			"	replace('" + date.replace("'", "''") + "', '/', '-')\n" + /* �{���͂����ƃt�H�[�}�b�g�����ق����ǂ����� */
			");"
		);
		
		return true;
		
	}
	
}

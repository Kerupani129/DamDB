package net.kerupani129.damdb;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

//
// MarkingDx クラス
//
public class MarkingDx {

	// 変数
	private Damtomo damtomo;

	//
	// コンストラクタ
	//
	public MarkingDx(Damtomo damtomo) {
		this.damtomo = damtomo;
	}

	//
	// 採点情報を更新する
	//
	public void update() throws IOException, SQLException {

		// データベース にテーブルがなければ作成
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
			");\n" +
			"CREATE TABLE IF NOT EXISTS Song(requestNo text primary key, artist text, contents text);\n" +
			"CREATE TABLE IF NOT EXISTS Tag(requestNo text, tag text, unique(requestNo, tag));\n" +
			"CREATE TRIGGER IF NOT EXISTS TriggerSongOnMarkingDx AFTER INSERT ON MarkingDx\n" +
			"	BEGIN\n" +
			"		REPLACE INTO Song VALUES(NEW.requestNo, NEW.artist, NEW.contents);\n" +
			"	END\n" +
			";"
		);

		// 表示用に 最終更新日時 取得
		ResultSet rs = this.damtomo.getStatement().executeQuery(
			"SELECT date FROM LastUpdateDate WHERE name == 'MarkingDx';" /* 本当はちゃんとフォーマットしたほうが良いかも */
		);
		rs.next();
		String date_old = rs.getString("date");

		// XML を 1 ページごとに取得
		pageLoop: for (int i = 1, totalPage = 1; i <= totalPage; i++) {

			// XML を 1 ページ 取得
			Document doc = Jsoup.connect("http://www.clubdam.com/app/damtomo/membership/MarkingDxListXML.do")
				.data("cdmCardNo", this.damtomo.getCdmCardNo())
				.data("enc", "utf-8")
				.data("pageNo", String.valueOf(i))
				.data("UTCserial", String.valueOf(new java.util.Date().getTime()))
				.timeout(Damtomo.timeout).parser(Parser.xmlParser()).get();

			// XML チェック
			if (!"0000".equals(doc.select("document > result > statusCode").last().ownText())) {
				throw new IllegalStateException("エラー: XML が異常です: " + doc.select("document > result > message").last().ownText() + ": ");
			}

			// ページ数 チェック
			totalPage = Integer.parseInt(doc.select("document > data > totalPage").last().ownText());

			// ページ内の採点結果処理
			Elements markings = doc.select("document > list > data > marking");

			for (Element marking: markings) {

				if (!this.parseElement(marking)) break pageLoop;

			}

		}

		// 無事に更新が終わったら 最終更新日時 保存
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

		// 表示用に 最終更新日時 取得
		rs = this.damtomo.getStatement().executeQuery(
			"SELECT date FROM LastUpdateDate WHERE name == 'MarkingDx';" /* 本当はちゃんとフォーマットしたほうが良いかも */
		);
		rs.next();
		String date_new = rs.getString("date");

		// test
		System.out.print("MarkingDx 更新: ");
		if (date_new.equals(date_old)) {
			System.out.println("なし");
		} else if ("0000-00-00 00:00:00".equals(date_old)) {
			System.out.println(" - " + date_new);
		} else {
			System.out.println(date_old + " - " + date_new);
		}
		System.out.println();

	}

	//
	// 1 つの採点情報をパースして データベースに追加
	//
	// データベースに追加した場合は true 、
	// 日付的に更新を終了すべき時は、データを追加せずに false を返す
	//
	boolean parseElement(Element marking) throws SQLException {

		// 各値をパース
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

		// 日付チェック
		ResultSet rs = this.damtomo.getStatement().executeQuery(
			"SELECT\n" +
			"	CASE\n" +
			"		WHEN replace('" + date.replace("'", "''") + "', '/', '-') <= (SELECT date FROM LastUpdateDate WHERE name == 'MarkingDx') THEN\n" + /* 本当はちゃんとフォーマットしたほうが良いかも */
			"			1\n" +
			"		ELSE\n" +
			"			0\n" +
			"	END AS 'exists'\n" +
			";"
		);
		rs.next();
		if (rs.getBoolean("exists")) return false;

		// データベースに追加
		// 前回に更新中に失敗した時など、データが重複する可能性があるので、"INSERT OR IGNORE"
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
			"	replace('" + date.replace("'", "''") + "', '/', '-')\n" + /* 本当はちゃんとフォーマットしたほうが良いかも */
			");"
		);

		return true;

	}

}

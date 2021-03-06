// 精密採点DX 履歴管理ソフト ver.0.2
// Copyright (c) 2015 Kerupani129
// このソフトウェアは、Apache 2.0 ライセンス で配布されている製作物が含まれています。


【 ソフト名 】精密採点DX 履歴管理ソフト
【バージョン】ver.0.2
【 製 作 者 】Kerupani129
【  種  別  】フリーソフト
【 開発環境 】Java 8
【 動作環境 】未確認 (少なくとも Windows 7 では動作確認済み)
【最終更新日】2015/07/13


■目次
１．はじめに
２．使い方
３．ファイル構成
４．免責
５．今後の予定
６．バグ
７．使用しているライブラリなど
８．バージョン履歴

■１．はじめに
	「精密採点DX」の履歴を「DAM★とも」からダウンロードして、
	「SQLite」のデータベースとして保存するソフトです。
	
	「いちいちネットワークアクセスさせんじゃねぇよ、ローカルに落とさせろ！」
	「自分で SQL 文発行したいんじゃー！！」
	という人にぴったりです (^^;
	
	作りかけにつき、現バージョンでは、
	「管理ソフト」と言って置きながら、「保存」する機能しかないです (^^;

■２．使い方
	(i). 履歴の保存
		
		初回使用時は、コマンドラインで
		
			> damdb [DAM★とも のプロフィールページの URL]
		
		と実行してください。
		
		例: 
		
			> damdb http://www.clubdam.com/app/damtomo/member/info/Profile.do?damtomoId=MzA4MzY0NjQ
		
		2回目以降は
		
			> damdb
		
		だけで情報を更新します。
		
		※ 2 回目以降に違う URL を指定しても、ユーザーを変更できません
		※ ユーザーを変更したい場合は、後述のデータベースファイルを削除するか、移動させてください
		
		※jar ファイルを直接実行する場合はカレントディレクトリを jar ファイルの場所にしてください。
		
	(ii). 履歴の管理
		
		現バージョンでは管理機能がないので、別途、SQLite を導入してください (^^;
		データベースのファイルは、カレントディレクトリ下に作成される「db」フォルダ内にあります。
		テーブル構成やカラムの意味は察してください (^^;
		(今後、一目見て分かるような表示にする予定) 
		
		> SQLite Home Page
		> https://www.sqlite.org/
		
■３．ファイル構成
	db/			データベースファイルが保存されるフォルダ (自動生成される)
	lib/		このソフトウェアが利用するライブラリが入っているフォルダ
	code.bat	(おまけ) 実行するだけでコマンドプロンプトの文字コード (Shift_JIS / UTF-8) を切り替える
	damdb.bat	Windows 上からスグ使えるようにするためのバッチファイル
	damdb.jar	プログラム本体
	readme.txt	このファイル
	
■４．免責
	これはフリーソフトなので、使用して、いかなる直接的・間接的損害が起こっても、
	作者ならびに作者と関係する団体・人物は一切の責任を負いません
	
	作者は、DAM (カラオケ) の開発元である「株式会社 第一興商」とは関係ありません
	作者は、SQLite の開発元である「D. Richard Hipp」とは関係ありません
	
■５．今後の予定
	(i). 保存する関連
		精密集計DX からも履歴データを落とせるようにする
	
	(ii). 楽曲管理関連
		(自身の) 楽曲別最高点や最高点更新日、最終歌唱日などを見れるようにする
		楽曲にタグ付けをして、楽曲単位での管理をしやすくする
		楽曲ごとのニガテな項目を表示する (安定性とか音程とかリズムとか)
		楽曲ごとの最適なキーを調べる (自動判別/手動設定)
	
	(iii). その他管理関連
		500 点満点の点数も扱えるようにする
		総合点・ビブラート秒の少数表示
		ビブラートタイプ・音域・分析レポートの文字列化
		各カラムが何の値かわかるようにする
		項目別平均点やその他集計データを見れるようにする
	
	(iv). よりカンタンに便利に
		SQLite で簡単に操作できるよう、上記の一部の機能を ビュー や トリガー にしておく
		コマンドライン や GUI から上記の機能を使えるようにする
		Linux で使えるようにする (jar を直接実行で動くかもしれないけど、まだ未確認)
		Android で使えるようにする (小声
	
■６．バグ
		初めて作成したデータベースファイルが ver.0.1 のものと判断され、
		「データベース バージョン アップデート: 0.1 -> 0.2」
		と表示されてしまう (^^;
		(表示がバグっているだけで、内部の処理は問題なしです
	
■７．使用しているライブラリなど
	SQLite JDBC Driver https://bitbucket.org/xerial/sqlite-jdbc/
		The Apache License version 2.0 http://www.apache.org/licenses/LICENSE-2.0
		Copyright (c) 2009 Taro L. Saito
		
	Jsoup https://jsoup.org/
		The MIT License http://jsoup.org/license
		Copyright (c) 2009 - 2013 Jonathan Hedley (jonathan@hedley.net)
	
■８．バージョン履歴
	2015/07/12 ver.0.2
		おそらく readme.txt の誤植を修正。その他にもちょくちょく修正
		日付情報を SQLite に合わせた形式にしていなかったので修正
		2 回目以降の履歴保存時に、最終更新日時までの分だけダウンロードするようにした
		以前のバージョンで保存したデータベースファイルを自動修正してそのままデータを引き継げるようにした
	2015/06/29 ver.0.1
		とりあえず保存できるようになったので試しに公開 (^^;

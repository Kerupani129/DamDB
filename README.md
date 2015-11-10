# DamDB
カラオケ DAM での歌唱履歴を管理するプログラムです。 (非公式)

## はじめに
「精密採点DX」の履歴を「DAM★とも」からダウンロードして、
「SQLite」のデータベースとして保存するプログラムです。

作りかけにつき、現バージョンでは、
「管理する」と言って置きながら、「保存」する機能しかないです (^^;

## ビルド
test.bat を実行すると、bin フォルダ内に jar ファイルが作成されます。

## 使い方
### (i). 履歴の保存
初回使用時は、コマンドラインで

    damdb [DAM★とも のプロフィールページの URL]

と実行してください。

例: 

    damdb http://www.clubdam.com/app/damtomo/member/info/Profile.do?damtomoId=MzA4MzY0NjQ

2回目以降は

    damdb

だけで情報を更新します。

※ 2 回目以降に違う URL を指定しても、ユーザーを変更できません
※ ユーザーを変更したい場合は、後述のデータベースファイルを削除するか、移動させてください

※jar ファイルを直接実行する場合はカレントディレクトリを jar ファイルの場所にしてください。

### (ii). 履歴の管理
現バージョンでは管理機能がないので、別途、SQLite を導入してください (^^;
データベースのファイルは、カレントディレクトリ下に作成される「db」フォルダ内にあります。
テーブル構成やカラムの意味は察してください (^^;
(今後、テーブルやカラムの説明を追加予定) 

> SQLite Home Page
> <https://www.sqlite.org/>

## 使用ライブラリ
* SQLite JDBC Driver <https://github.com/xerial/sqlite-jdbc>
  * The Apache License version 2.0 <https://github.com/xerial/sqlite-jdbc/blob/master/LICENSE>
  * Copyright (c) 2009 Taro L. Saito
* Jsoup <https://jsoup.org/>
  * The MIT License <http://jsoup.org/license>
  * Copyright (c) 2009 - 2013 Jonathan Hedley (<jonathan@hedley.net>)

## ライセンス & コピーライト
Copyright (c) 2015 Kerupani129 and licensed under The MIT License.

## バージョン履歴
* 2015/07/12 ver.0.2
  * おそらく readme\_old.txt の誤植を修正。その他にもちょくちょく修正
  * 日付情報を SQLite に合わせた形式にしていなかったので修正
  * 2 回目以降の履歴保存時に、最終更新日時までの分だけダウンロードするようにした
  * 以前のバージョンで保存したデータベースファイルを自動修正してそのままデータを引き継げるようにした
* 2015/06/29 ver.0.1
  * とりあえず保存できるようになったので試しに公開 (^^;

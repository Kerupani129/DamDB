package net.kerupani129.damdb;

import java.io.*;
import java.sql.*;
import java.net.*;

// 
// メインクラス
// 
public class Main {
	
	// 
	// メインメソッド
	// 
	public static void main(String[] args) {
		
		System.out.println(
			"// " + Meta.name + " ver." + Meta.version + "\n" +
			"// " + Meta.copyright + "\n" +
			"// このソフトウェアは、Apache 2.0 ライセンス で配布されている製作物が含まれています。\n"
		);
		
		try {
			
			// データベース準備
			Damtomo damtomo = new Damtomo();
			
			if (!damtomo.ready()) {
				if (args.length == 0) {
					System.out.println("プロフィール URL を指定してください");
					damtomo.close();
					return;
				} else {
					damtomo.setProfile(args[0]);
				}
			}
			
			// プロフィール情報 表示
			System.out.println("■Profile");
			System.out.println("damtomoId = " + damtomo.getDamtomoId());
			System.out.println("cdmCardNo = " + damtomo.getCdmCardNo());
			System.out.println("     name = " + damtomo.getName());
			System.out.println();
			
			// 精密採点DX 処理
			MarkingDx dx = damtomo.getMarkingDx();
			dx.update();
			
			// データベースクローズ
			damtomo.close();
			
		} catch (URISyntaxException e) {
			throw new IllegalStateException("エラー: URL が異常です: ", e);
		} catch (IOException e) {
			throw new IllegalStateException("エラー: 入出力エラーが発生しました: ", e);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("エラー: データベースを利用できません: ", e);
		} catch (SQLException e) {
			throw new IllegalStateException("エラー: データベースのアクセスに失敗しました: ", e);
		} catch (Exception e) {
			throw new IllegalStateException("エラー: 予期せぬエラーが発生しました: ", e);
		}
		
	}
	
}

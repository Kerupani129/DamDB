package net.kerupani129.damdb;

import java.util.*;
import java.util.regex.*;

// 
// NetUtils クラス
// 
public class NetUtils {
	
	// 
	// コンストラクタ
	// 
	private NetUtils() {}
	
	// 
	// URL の クエリ文字列 を Map<String, String> に変換
	// 
	public static Map<String, String> getQueryMap(String query) {
		
		// "&" 区切り
		Map<String, String> map = new HashMap<String, String>();
		String[] params = query.split(Pattern.quote("&"));
		
		// "=" 区切り
		for (String param: params) {
			String[] splitted = param.split(Pattern.quote("="));
			
			if (splitted.length == 1) {
				map.put(splitted[0], null);
			} else {
				map.put(splitted[0], splitted[1]);
			}
		}
		
		return map;
	}
	
}

package net.kerupani129.damdb;

import java.util.*;
import java.util.regex.*;

// 
// NetUtils �N���X
// 
public class NetUtils {
	
	// 
	// �R���X�g���N�^
	// 
	private NetUtils() {}
	
	// 
	// URL �� �N�G�������� �� Map<String, String> �ɕϊ�
	// 
	public static Map<String, String> getQueryMap(String query) {
		
		// "&" ��؂�
		Map<String, String> map = new HashMap<String, String>();
		String[] params = query.split(Pattern.quote("&"));
		
		// "=" ��؂�
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

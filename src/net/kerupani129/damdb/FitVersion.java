package net.kerupani129.damdb;

import java.sql.*;

// 
// FitVersion �N���X
// 
public class FitVersion {
	
	// 
	// �R���X�g���N�^
	// 
	private FitVersion() {}
	
	// 
	// �f�[�^�x�[�X�̃o�[�W���������킹��
	// 
	public static void fit(Damtomo damtomo, String version) throws SQLException {
		
		if (compareVersions("0.2", version) > 0) convert_0_1_to_0_2(damtomo);
		
	}
	
	// 
	// 0.1 -> 0.2
	// 
	static void convert_0_1_to_0_2(Damtomo damtomo) throws SQLException {
		
		// �f�[�^�x�[�X �ɕύX
		if (SqlUtils.tableExists(damtomo.getStatement(), "MarkingDx")) {
			damtomo.getStatement().executeUpdate(
				"UPDATE MarkingDx SET date = replace(date, '/', '-');"
			);
		}
		
		// test
		System.out.println("�f�[�^�x�[�X �o�[�W���� �A�b�v�f�[�g: 0.1 -> 0.2");
		System.out.println();
		
	}
	
	// 
	// �o�[�W���������� (�����ƃs���I�h�̂�) ���r����
	// 
	// v1 >  v2 �̏ꍇ�� 1
	// v1 == v2 �̏ꍇ�� 0
	// v1 <  v2 �̏ꍇ�� -1
	// ��Ԃ�
	// 
	static int compareVersions(String v1, String v2) {
		
		String[] a1 = v1.split("\\Q.\\E");
		String[] a2 = v2.split("\\Q.\\E");
		
		int max = Math.max(a1.length, a2.length);
		
		for (int i = 0; i < max; i++) {
			
			if (((i < a1.length)?(Integer.parseInt(a1[i])):(0)) > ((i < a2.length)?(Integer.parseInt(a2[i])):(0))) return 1;
			if (((i < a1.length)?(Integer.parseInt(a1[i])):(0)) < ((i < a2.length)?(Integer.parseInt(a2[i])):(0))) return -1;
			
		}
		
		return 0;
		
	}
	
}

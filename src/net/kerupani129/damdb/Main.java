package net.kerupani129.damdb;

import java.io.*;
import java.sql.*;
import java.net.*;

// 
// ���C���N���X
// 
public class Main {
	
	// 
	// ���C�����\�b�h
	// 
	public static void main(String[] args) {
		
		System.out.println(
			"// " + Meta.name + " ver." + Meta.version + "\n" +
			"// " + Meta.copyright + "\n" +
			"// ���̃\�t�g�E�F�A�́AApache 2.0 ���C�Z���X �Ŕz�z����Ă��鐻�앨���܂܂�Ă��܂��B\n"
		);
		
		try {
			
			// �f�[�^�x�[�X����
			Damtomo damtomo = new Damtomo();
			
			if (!damtomo.ready()) {
				if (args.length == 0) {
					System.out.println("�v���t�B�[�� URL ���w�肵�Ă�������");
					damtomo.close();
					return;
				} else {
					damtomo.setProfile(args[0]);
				}
			}
			
			// �v���t�B�[����� �\��
			System.out.println("��Profile");
			System.out.println("damtomoId = " + damtomo.getDamtomoId());
			System.out.println("cdmCardNo = " + damtomo.getCdmCardNo());
			System.out.println("     name = " + damtomo.getName());
			System.out.println();
			
			// �����̓_DX ����
			MarkingDx dx = damtomo.getMarkingDx();
			dx.update();
			
			// �f�[�^�x�[�X�N���[�Y
			damtomo.close();
			
		} catch (URISyntaxException e) {
			throw new IllegalStateException("�G���[: URL ���ُ�ł�: ", e);
		} catch (IOException e) {
			throw new IllegalStateException("�G���[: ���o�̓G���[���������܂���: ", e);
		} catch (ClassNotFoundException e) {
			throw new IllegalStateException("�G���[: �f�[�^�x�[�X�𗘗p�ł��܂���: ", e);
		} catch (SQLException e) {
			throw new IllegalStateException("�G���[: �f�[�^�x�[�X�̃A�N�Z�X�Ɏ��s���܂���: ", e);
		} catch (Exception e) {
			throw new IllegalStateException("�G���[: �\�����ʃG���[���������܂���: ", e);
		}
		
	}
	
}

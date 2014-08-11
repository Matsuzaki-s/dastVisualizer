package a.slab.blockeditor;

import javax.swing.UIManager;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import controller.WorkspaceController;

/*
 * BlockEditor Application
 * 
 * 2011/10/25 version 1.0.0 �����[�X
 * 2011/10/25 version 1.0.1 �_�v���G�f�B�^��Project��I�������Ƃ��A �t���[���̖��O�ɈȑO�I�����Ă���Java�t�@�C�����\�������s����C���B
 * 2011/10/25 version 1.0.2 �_�v���G�f�B�^��Project��I�������Ƃ��ABlockEditor����Save�{�^����������Run�{�^�����������Ƃ�NullPointer�̃G���[���\�������s����C���B 
 * 2011/10/25 version 1.0.3 �_�v���G�f�B�^��Java�̃R���p�C�������s�����Ƃ��ABlockEditor�̃^�C�g����Java�̃R���p�C�������s�������Ƃ�\������悤�ɂ����B
 * 2011/10/25 version 1.0.4 �A������Ă��Ȃ��u���b�N��Block�G���[������Ƃ��ɂ��G���[���\������Ă��܂��s����C���B
 * 2011/10/25 version 1.0.5 Block��Syntax�G���[������Ƃ��ɕ\�������_�C�A���O�̕������C���B 
 * 2011/10/25 version 1.0.6 ���g���Ȃ��߂����Ƃ��AJava����Block�ɕϊ�����Ȃ��s����C���B 
 * 2011/10/25 version 1.0.7 Block����Java�\�[�X�ɕϊ����邽�тɁA��s�������Ă����s����C���B 
 * 2011/10/25 version 1.0.8 BlockEditor�́u�i�߂�v�u���b�N�Ɓu�߂��v�u���b�N�̏����l��50�ɏC���B 
 * 2011/10/25 version 1.0.9 BlockEditor�́u�߂��v�u���b�N�̃��x�����u�߂�v�ɏC���B 
 * 2011/10/25 version 1.0.10 BlockEditor�́u�E�։��v�u���b�N�Ɓu���։񂷁v�u���b�N�̃p���b�g�̈ʒu���C���B 
 * 2011/10/25 version 1.0.11 BlockEditor�́u�E�։��v�u���b�N�̃��x�����u�E���񂷁v�ɏC���B 
 * 2011/10/25 version 1.0.12 BlockEditor�́u�y���̐F��ς���v�u���b�N�̈����̃��x�����u�F�v�ɂ����B 
 * 2011/10/25 version 1.0.13 BlockEditor�́u�y���̐F��ς���v�u���b�N�̏����l�̃��x�����p��ɂȂ��Ă���̂��C���B 
 * 2011/10/25 version 1.0.14 BlockEditor�́u�R���\�[���ɏo�͂���v�u���b�N�̏����l�̃��x�����u�����������v�ɏC���B 
 * 2011/10/25 version 1.0.15 BlockEditor�́uSave�v�{�^�����uSave��Java�o�́v�ɁA�uRun�v�{�^�����uSave��Java�o�͂��Ď��s�v�ɏC���B
 * 2011/10/25 version 1.0.16 Java����Block�ɕϊ������Ƃ��A�u���b�N�̈ʒu���E�Ɋ�肷���Ă���̂��C���B
 * 2011/10/25 version 1.0.17 BlockEditor�̃��[�N�X�y�[�X�ɂ���Overview���\���ɂ����B 
 * 2011/10/25 version 1.1.18 �u�y���̐F��ς���v�u���b�N�ɐF�w��ȊO�̃u���b�N������Ȃ��悤�ɂ����B 
 * 2011/10/25 version 1.1.0 BlockEditor�̕���u���b�N�iif�j�͎g��Ȃ��悤�ɏC���BJava����Block�ɕϊ���Aelse�߂��g�������Ȃ�\�������邽�߁B
 * 2011/10/25 version 1.1.1 BlockEditor�̕ϐ��̑���u���b�N�ő������l��Java�\�[�X�R�[�h�ɔ��f����Ȃ������C���B
 * 2011/10/25 version 1.1.2 Java����Block�ɕϊ�����Ƃ��A�}�C�i�X�̒l���ϊ�����Ȃ������C���B 
 * 2011/10/25 version 1.1.3 Java����Block�ɕϊ�����Ƃ��Arandom�֐��̌`�����������Ȃ���ƁA�������u���b�N������Ȃ������C���B
 * 2011/10/25 version 1.1.4 Java����Block�ɕϊ�����Ƃ��Aelse if(�E�E�E)����͂���Ȃ������C���B
 * 2011/10/25 version 1.1.5 BlockEditor�̃{�^���ŁA�uSave��Java�o�́v���uJava�o�́v�ɁA�uSave��Java�o�͂��Ď��s�v���uJava�o�͂��Ď��s�v�ɂ���
 * 2011/10/25 version 1.1.6 �t�@�N�g���[�̃J�e�S���ŁA�u�ϐ��v���폜���A�u�ϐ��̒�`�v�Ɓu�ϐ��̓ǂݏ����v��ǉ������B
 * 2011/10/25 version 1.1.7 ��r�u���b�N�ŁA�u���b�N�����S�ɑg�܂�Ă��Ȃ��Ƃ��̃G���[�_�C�A���O�̕������\������Ȃ������C�������B
 * 2011/10/25 version 1.1.8 Java����Block�ɕϊ�����Ƃ��ABlockEditor�Ń��[�J���ϐ��u���b�N�̌��ɂ���u���b�N����������Ȃ������C�������B 
 * 2011/10/25 version 1.2.0 Postfix�u���b�N�i�u�ϐ��v�𑝂₷�u���b�N�j��ǉ������B 
 * 2011/10/25 version 1.2.1 remainder�u���b�N��java�ɕϊ�����Ȃ������C�������B 
 * 2011/10/29 version 1.3.0 �u���b�N�̐܂肽���݋@�\�ǉ��B
 * 2011/10/29 version 1.3.1 �u���b�N�̕����@�\�ǉ��B
 * 2011/10/29 version 1.3.2 ��ȏ�̏����������ݒ�ł���悤�ɏC���B 
 * 2011/10/29 version 1.3.3 �ϐ����ɋ󔒁A�L�����g���Ȃ��悤�ɏC���B
 * 2011/10/29 version 1.3.4 ������Ɂw'�x�A�w"�x�A�w\�x������ƃG�X�P�[�v����悤�ɏC���B
 * 2011/11/17 version 1.3.5 �u���b�N�̕����@�\�̕s��C���B
 * 2011/11/18 version 1.3.6 �ϐ��̒l�u���b�N��ϐ���`�u���b�N���琶������悤�ɏC���B
 * 2011/11/18 version 1.3.7 �ϐ��u���b�N���C���B
 * 2011/11/19 version 1.4.0 �I�u�W�F�N�g�ϐ��u���b�N��ǉ��B
 * 2011/11/19 version 1.4.1 �I�u�W�F�N�g�����u���b�N��ǉ��B
 * 2011/11/20 version 1.4.2 �I�u�W�F�N�g�̃��\�b�h�Q�ƃu���b�N��ǉ��B
 * 2011/11/23 version 1.5.0 �_�v���G�f�B�^�ɑg�ݍ���
 * 2011/11/23 version 1.5.1 ���Ƃł̂�������
 * 2012/09/27 version 2.0.0 �o�O�C��(���V)
 * 							�V�@�\�ǉ��D(elseif���̃��o�[�X�Cfor���̃��o�[�X)
 * 2012/09/27 version 2.0.1 �����R�[�h����mac�œ����Ȃ������C����
 * 2012/10/03 version 2.1.0 �����R�[�h��������
 * 							BlockEditor <--> Converter�Ԃ�XML�t�@�C����UTF-8�œ��ꂷ��DJava�t�@�C���͔C�ӂŁC�O������ݒ肷��D
 * 2012/10/03 version 2.1.1 �����R�[�h���̃o�O��fix <-�C������Ă��Ȃ�
 * 							CommentGetter�ōs���𐔂���ۂ̋@��ˑ����s�R�[�h�̖�� 
 * 2012/10/03 version 2.1.2 �E�C���h�E�^�C�g�������̕s����C���i���j
 * 2012/10/03 version 2.1.3 �����R�[�h���̃o�O����fix CommentGetter�̃A���S���Y����ύX
 * 2012/10/03 version 2.1.4 �����R�[�h���̃o�O���āXfix block->Java��XML��ǂݍ��ލۂ̕����R�[�h�w��Y��
 * 2012/10/03 version 2.1.5 �����R�[�h���̃o�O���3fix �����R�[�h�w����@�ύX �iMountain Lion, JDK1.7�̑g�ݍ��킹�œ��삵�Ȃ��j
 * 							mac�ŕϐ����o�Ȃ����͉��������D
 * 2012/10/03 version 2.1.6 lightGray, darkGray�ŃG���[���o������C���D
 * 2012/10/03 version 2.1.7 �ڑ������d�Ȃ����Ƃ��ɏ����Ă��܂��o�O���C���D�iSound�N���X�j
 * 2012/10/03 version 2.1.8 
 * ���1�@j->b���ău���b�N���u���b�N�Ԃɑ}������Ɨ�O���ł���̉��}���u�D
 * Exception in thread "AWT-EventQueue-0" java.lang.RuntimeException: trying to link a plug that's already connected somewhere.
	at codeblocks.BlockLink.connect(BlockLink.java:159)
�@�@ ����=>2��ڈȍ~�̍\�z�ŁC�ԂɃu���b�N��}�����悤�Ƃ���Əo��D
   blockEditor.resetWorkspace();�͌Ă΂�Ă�
   // blockEditor.resetLanguage();
   // blockEditor.setLangDefDirty(true);
      ���ĂԂƁC�P��ڂ���o��D
   workspace��listener����������Ă��炸�C  resetLanguage()�ŁC�Q�߂̃n���h�����o�^����Ă��܂����Ƃ����D
 *�@���2 �v���_�E�����j���[���h���b�O����Ɨ�O�C->�͂��Ȃ��悤�ɉ��}���u
 * 2012/10/07 version 2.1.9 ���ۉ��u���b�N������Ƃ��ɐe�̑傫�����ς��Ȃ����̏C���D
 * 	�E��̒��ۉ��u���b�N���J�����Ƃ��̓�����C�����ꂽ�D
 * 2012.10.10 version 2.1.10
 * �EJava�ŃR�����g�ɃX�y�[�X�������\������D
 * 		XML�͂n�j�CBlock�܂łn�j�CBlockLabel�܂łn�j�CBlockWidgetOK
 * 	    LabelWidget#updateLabelText() �ɂĉ���
 * �E���ۉ��u���b�N�̊J��Ԃ����ɂ��ǂ�D
 * 	Block#getSaveString�R�����g�A�E�g����Ă����D����
 * 	2012.10.10 version 2.1.11
 * 		�ȉ��̃o�O���C��
 * 		�Ecolor(java.awt.Color.lightGray);�@�i�F�̕ύX�j�@�������ԂŁAOpenBlockEditor������ƐF�u���b�N�����܂����f����Ȃ��iint�^�̒l�u���b�N�ɂȂ�j
 * 		�Eboolean�^�̕ϐ������E�E�E�Ƃ����u���b�N���g�p����ƃf�t�H���g�œ����Ă��鏉���l��true�i�l�u���b�N���Ɛ^�U�j
 *�@	2012.10.16 version 2.1.12
 * 		�E �I�H�Ȃǂ̋L��������悤�ɂ���
 * 		�ELabelWidget#BlockLabelTextField
 *  2012.10.18 version 2.2.0
 *  	�E���ƒ��ɔ����@�������ۉ��u���b�N�̒��̃u���b�N�ɁC���̒��ۉ��u���b�N���������Ă��܂��o�O���C��
 * 		�EBlockLinkChecker#getLink
 *  2012.10.21 version 2.3.0
 *  	�E�X�N���[���V���b�g�@�\
 *  2012.10.21 version 2.3.1
 *  	�E�X�y�[�X����n�܂钊�ۉ��u���b�NLabel��ύX�ł��Ȃ������C��
 *  2012.10.21 version 2.3.2
 *  	�E�X�N���[���V���b�g�@�\�C�u���b�N����Ă���Ƃ����C���g���X�y�[�X�v�Z���Ă��܂������C��
 *  2012.10.23 version 2.4.0
 *  	�EtoJavaRun��p�~ compile��ʌɍ쐬
 *  	�EtoJava�ŃR���p�C�����Ă����̂�p�~
 *  2012.10.23 version 2.4.1
 *  	�Eif(); while(); �̏����@�i�r���j
 *  2012.10.23 version 2.5.0
 *  	�E while();���o���Ă��Ȃ����������C���D
 *    	�Edirty��Ԃ̒ǉ��i���j�C�ϐ��̏����l���x�����C�ω����Ȃ������Ƃ���notify���������C���D
 *    	�E�ϐ����錾����Ă��Ȃ��G���[�ŁC���O���l�����Ă��Ȃ������̂ŏC���D
 *  2012.10.29 version 2.6.0
 *  	�Einstance��method call���CJ->B�Őڑ������������Ȃ�����C�� ExCallMethodModel#print()�@
 *  2012.10.29 version 2.6.1
 *  	�E lang_def.xml���I�u�W�F�N�g�w���łɏC���D
 *  2012.10.30 2.6.2
 *  	�E �I�u�W�F�N�g�u���b�N�̃R���e�L�X�g���j���[�̕����ύX
 *  2012.10.31 2.6.3
 *  	�ESS���j���[�ŕ���
 *  2012.11.1 2.7.0
 *  	�E J->B ConnectorType�����̕ύX�Cint�ȊO�̐ڑ��R�l�N�^�����������Ȃ�bugfix
 *  	�EObject�̉E���j���[�N���b�N�ŏ������݁C�l
 *  	�Elang_def.xml�����������iObject�̏������݁C�l�j�ɑΉ��Clooks�Ή�
 *  2012.11.4 2.8.0
 *  	�E���\�b�h�ďo���̖߂�l�̌v�Z�̐V�݌v�i���܂ł�number�^�݂̂������j
 *  2012.11.4 2.8.1
 *  	�E���\�b�h�o�^
 *  2012.11.4 2.8.2
 *  	�Esignature�̈قȂ郁�\�b�h�̈����C�����̐��őΉ�
 *  2012.11.6 2.9.0
 *  	�EBlock->Java�ɑΉ��D�啝�ȕύX�i�A�h�z�b�N�Ȏ���������j
 *  	�EJava->Block��number�ȊO�̖߂�l�ɑΉ��D�啝�ȕύX(CallGetterMethodModel)�i�A�h�z�b�N�Ȏ���������j
 *  2012.11.6 2.9.1
 *  	�ESoundTurtle�ɑΉ�
 *  2012.11.6 2.9.2
 *  	�E�����̕s����C���C�u���b�N�J�e�S���̐���
 *  2012.11.7 2.9.3
 *  	�E���ۉ��u���b�N�̒��ɂ���ϐ����R�s�[�����Ƃ��̖��̉���i���{�I�ł͂Ȃ��j
 *  	�Ett.text(tt.getText());�ŁC�ŏ���CallGetterMethodModel�̃R�l�N�^�����������Ȃ���̏C�� (CallGetterMethod#getType()���\�b�h�̏C��)
 *  	�E�u���b�N�J�e�S���̒���
 *  2012.11.7 2.9.4
 *  	�ESpecialBlock�ɑΉ�
 *  2012.11.7 2.9.5
 *  	�E9, 10�͂̃e�X�g�ɂ�����o�O�C��
 *  		�EisShow, show���̉��P setShow�ǉ�
 *  		�Ewarp(mouseX())�ŃG���[�̃o�O���C��
 *  2012.11.7 2.9.6
 *  	�EB->J�ŃG���[���o���Ƃ��̃_�C�A���O��ύX(Description��������)
 *  	�E�R���X�g���N�^�����������Ƃ��̖����G���[�i�G���[���o���C�����𖳎����邵�悤�ɂ������C��������ĂȂ��G���[�̕����ǂ����j
 *  	�ESoundTurtle�̂��ׂẴ��\�b�h�ɑΉ��D
 *  2012.11.8 2.9.7�@���ƒ�
 *  	�Eelse if�u���b�N�ŏo�͂ł���悤�ɉ��ǁD
 *  2012.11.13 2.10.0 CUI�ւ̑Ή�
 *  	�ESpecial-Expression�u���b�N
 *  	�Edouble�^�̓���
 *  	�E�e��ϊ��u���b�N
 *  2012.11.13 2.10.1 CUI�ւ̑Ή�(2)
 *  	�ECUI�J�e�S���CSysout�Ȃǂ̃u���b�N�ƕϊ�
 *  	�EScanner�u���b�N�ƕϊ�
 *  2012.11.13 2.10.2 CUI�ւ̑Ή�(3)
 *  	�Ecast��J->B�����܂��ł��Ă��Ȃ������̂ŏC��
 *  	�EScanner�u���b�N�ƕϊ�
 *  2012.11.13 2.10.2 CUI�ւ̑Ή�(4)
 *  	�Edouble�̉��Z�u���b�N��ǉ�
 *  2012.11.14 2.10.3 CUI�ւ̑Ή�(5)
 *  	�E�S�̓I�ȃo�O�C��
 *  	�ESystem.out.println()���̓��ʑΉ�
 *  	�Escanner.getInt()�̎���
 *  2012.11.14 2.10.4 CUI�ւ̑Ή�(6)
 *  	�Et.fd(100)��forward�����Ƃ��C�]�v��;�����D  ->�C��
 *		�Ewindow.warp()��߂��Ɨ]�v�ȁG��warp������D  ->�C��
 *		�E;�i��u���b�N�j������ƁC�����D  ->�C��
 *	2012.11.14 2.10.6 CUI�ւ̑Ή�(6)
 *		�E xml�t�@�C������->Turtle, CUI�����ϊ�
 *		�Escanner.nextString()�ł͂Ȃ��Cnext()->�Ή�
 *		�E������ɕϊ��Cpoly�ł΃��j���[�Ɏc���Ă����D
 *		�E���w�֐��ɑ����Ή��D
 *	2012.11.14 2.10.7 CUI�ւ̑Ή�(7)
 *		�E �L���X�g��J->B�ɑΉ�
 *	2012.11.14 2.10.8 CUI�ւ̑Ή�(8)
 *		�E������ւ̃L���X�g����double�^���������������̂��C���iElementModel��type�����v���O�����̃o�O�j
 *		�Enext()�ł͂Ȃ��CnextLine()�D nextDouble()�ǉ��ŋ��ȏ��ɑΉ��D
 *		�E���\�b�h���s�u���b�N�idouble�^�j��ǉ�
 *	2012.11.14 2.10.9 CUI�ւ̑Ή�(9)
 *		�E���������Z���ł��Ă��Ȃ����������C��
 *		�Edouble�^�ւ̕ϊ����܂ޒ��u���Z���ł��Ȃ����������C���D
 *		�E������A�����ւ̗U��(ExInfixModel)�̕s����C��
 *	2012.11.14 2.10.10 CUI�ւ̑Ή�(9)
 *		�E�J�e�S���̐���
 *	2012.11.14 2.10.11 CUI�ւ̑Ή�(10)
 *		�E<=���łȂ��D<���������łȂ��@����
 *		�Edouble�^�̃C���N�������g���Z�q�@Block->Java, Java->Block�@���ł��Ȃ� ����
 *	2012.11.14 2.10.12 CUI�ւ̑Ή�(11)
 *		�E�f�t�H���g��ID�ԍ������肸�C���A�s�\�ȃG���[�ƂȂ�����C��
 *	2012.11.14 2.10.13 CUI�ւ̑Ή�(12)
 *		�EcallDoubleMethod()����������Ă��炸�Cscanner.nextDouble()��B->J���o���Ȃ���������
 *	2012.11.23 2.10.14 CUI��T�� 
 *		�ECUI�J�e�S���ɁCCUI�ł͗��p�ł��Ȃ�random()���������̂ō폜�D
 *		�Egenus�֌W��xml��`�t�@�C���̐���
 *		�EhashCode()�̎���
 *	2012.11.23 2.10.15 CUI��T�� 
 *		�ENot�@(!) �̎���
 *		�ESpecialExpression�̍쐬�ꏊ��ύX���CStatement�������̗�O�ł͂Ȃ��CExpression�������̗�O����SpecialExpression�ɂ���悤�ɂ����D
 *	2012.11.23 2.10.16 CUI��T�� 
 *		�Eequals-boolean, not-equals-boolean �̎���
 *		�Eequals-string �̎����Dequals�Ŕ�r����悤�ɍH�v���Ă���D
 *  2012.11.23 2.11.0 ���\�b�h
 *  	�E�R���e�L�X�g���j���[�̃N���G�C�^�[�n�̐����ƃR�[�h���ʉ�
 *  	�E���\�b�h�֌W�̃u���b�N�̐���
 *  	�E���\�b�h���Ăяo����R���e�L�X�g���j���[
 *  	�E�������Q�Ƃł���R���e�L�X�g���j���[
 *  	�E�����̓f���o���C�V�������\�b�h�̓f���o��
 *  2012.11.24 2.11.1 ���\�b�h
 *  	�E�����ɏ������݂��ł���悤�ɂ����i�s���A��j
 *  	�E���\�b�h�i�����j����̋A����o����悤�ɂ����D
 *  2012.11.24 2.11.2 ���\�b�h
 *  	�E���������ꍇ��J->B���o���Ă��Ȃ������̂�bugfix
 *  	�E�߂�l�ɑΉ��i�s���A��n�j�j
 *  2012.11.24 2.11.3 ���\�b�h
 *  	�E���\�b�h�̈ʒu 100�h�b�g���E�ɂ��炷
 *  2012.11.24 2.11.4 ���\�b�h
 *  	�E���\�b�h���������Ƃ��CJava�������D
 *  2012.11.24 2.11.5 ���\�b�h
 *  	�Ebugfix ���\�b�h���������Ƃ��CJava�������D �ŁCstatic main�������Ă��܂��Ă����D
 *  	�Ebugfix B->J�Ń��[�U���\�b�h��Expression�Ƃ��ČĂ񂾏ꍇ��;�����Ă��܂��D
 *  	�E������J->B�ň������������Ă��܂�(2.11.4�ő΍􂵂����肾�������Ȃ����)
 *  2012.11.24 2.11.6 ��A�e�X�g�Ńo�O���
 *  	�Ebugfix StringToDouble�̌^������Ă����D�P���~�X
 *  	�Ebugfix ������A�����S�R�o���Ȃ��Ȃ��Ă����D==, !=���͂˂�R�[�h��+�ł������Ă��܂��Ă����D 
 *  	�Ebugfix double�^�ւ̃L���X�g��()�����Ă��炸�CB->J�ňӖ����ς���Ă��܂��Ă����D
 *  	�Ebugfix scanner->next()�œ����Ă��Ȃ�����
 *  2012.11.24 2.11.7 
 *  	�Ebugfix �F���g���Ȃ��Ȃ��Ă����̂ŏC���D
 *  	�EFlowViewer�o������悤�� getLabel()������
 *  2012.11.25 2.11.8 
 *  	�Ebugfix double�^�̈������o�͂���Ȃ������C��
 *  	�Ebugfix "a" + "b" .hashCode()���o���Ȃ������C��
 *  	�Ebugfix CUI��turtle��random����������C��
 *  2013.01.08 2.11.9
 *  	�EListTurtle�Ȃǂ̃p�����^���C�Y�h�N���X�ɏ��Ή�
 *  	�EListTurtle�ɑΉ���
 *  2013.01.09 2.12.0
 *  	�EListTurtle�ɑΉ�
 *  	�E�I�u�W�F�N�g�V�X�e����啝���V
 *  2013.01.09 2.13.0
 *  	�EdoWhile, break, continue�ɑΉ��@
 *  2013.01.09 2.13.1
 *  	�E�u���₷�v�̒l����ԃo�O�̏C���C
 *  	�EListTurtle�̂قƂ�ǂ̃��\�b�h�ɑΉ�
 *  	�ECardTurtle�̃��\�b�h�ɑΉ��@
 *  2013.09.26 2.14.0 Ohata
 *  	�E�C���X�^���X�ϐ��̒ǉ�
 *  	�E�ʒu���̒ǉ�
 *  	�E���\�b�h�̊J��Ԃ̒ǉ��i�����@�o�O����j
 *  	�E�ϐ��n�C���C�g�̒ǉ�
 *  	�E�Q�b�^�[/�Z�b�^�[/�R���X�g���N�^�̒ǉ�
 *  2013.09.28 2.14.1 Ohata
 *  	�E�n�C���C�g����Ȃ��u���b�N��������������C��
 *  	�E���ۉ��u���b�N��������Null Pointer Exception��������������C��
 * 	 	�Ejava����u���b�N�𐶐����Aprivate�ϐ��̃��C���i���o�[��xml�ɏ����o���ۂ̖����C��
 *  2013.10.01 2.14.2 Ohata
 *  	�E�n�C���C�g�Ώۂ��������݃u���b�N�A�Q�ƃu���b�N�݂̂ɕύX
 *  	�E�C���X�^���X�ϐ��A�I�u�W�F�N�g�ϐ��̉B��			
 *  2013.10.05 2.14.3 Ohata	
 *  	�E�n�C���C�g�̑Ώۂ�ǉ�
 *  		�E�������݃u���b�N�A�l�u���b�N�̎Q�ƌ����n�C���C�g�Ώۂɒǉ�
 *  	�E�֐��𒴂������[�J���ϐ��̗��p���\�������̂�s�\��
 *  	�E�A�j���[�V��������Ώۂ�ǉ�	
 *  		�E�X�R�[�v�O�̕ϐ��̏������݃u���b�N�A�l�u���b�N�𗘗p���悤�Ƃ����ꍇ	
 *  2013.10.09 2.14.4 Ohata
 *  	�E�X�R�[�v�`�F�b�N�̒ǉ��̓r��
 *  	�E�A�j���[�V��������i�c�ړ��j�̒ǉ�
 *  	�E�n�C���C�g����̒ǉ��i�ُ�u���b�N�̐ԃn�C���C�g�j
 *  	�E�A�j���[�V��������̔������i�u���b�N�̑傫���ł̈ړ����A�萔�ɕύX�j
 *  2013.10.10 2.14.5 Ohata
 *  	�E�֐��̈������ЂƂ̌^�����錾�ł��Ȃ��Ȃ����s����C��
 *  2013.10.12 2.15.0 Ohata
 *  	�E�X�R�[�v�`�F�b�N�@�\�̒ǉ�
 *  2013.10.12 2.15.1 Ohata
 *  	�E�X�R�[�v�`�F�b�N�@�����Ă���u���b�N���̍ŏ��ŕϐ��錾����Ă���ꍇ�ɐ����������ł��Ȃ������C��
 *  2013.10.21 2.15.2 Ohata
 *  	�E�X�R�[�v�`�F�b�N�@��b�e�X�g�I����
 *  
 * <TODO>
 * �E�R�����g��xml�̃^�O������ƃG���[�D
 * �E�����̎g�p���ɃX�R�[�v�̃`�F�b�N
 * �EObject�̈���
 * �E������J->B�ň������������Ă��܂�(2.11.4�ő΍􂵂����肾�������Ȃ����)
 * 
 * �E ExCallMethodModel#print() getConnectorId()����̃R�[�h���C�����邱�ƁD�T�O�𐮗�����K�v������D
 * �Eif(); while(); �̏����i���S�Ɂj ->��̂n�j
 * �Edirty��Ԃ̒ǉ��i���S�Ɂj ->��̂n�j
 * �E�����R�[�h�֘A�C�p�b�P�[�W�Ԉˑ��ƃN���X�\�����������������邱��
 * �E�����֌W�̓���`�F�b�N���邱�ƁD 
 * �Ejava version "1.6.0_35"�@���{��̕��������i�v���O�������E�G���[�{���j�͂���܂���ł����B
 * �Ejava version "1.7.0_07"�@���{��̕��������� RonproEditor.jar ���_�u���N���b�N���ċN�������Ƃ��Ɍ����܂����B�R�}���h�v�����v�g������s�����Ƃ��͕����������܂���ł����B
 * 
 */
public class SBlockEditor {

	// frame name and version infomation
	public final static String APP_NAME = "Block Editor";
	public final static String VERSION = "2.15.1";

	public static final String ENCODING_BLOCK_XML = "UTF-8";
	public static final boolean DEBUG = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SBlockEditor().test();
	}

	void test() {
		initializeLookAndFeel();
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// Create a new WorkspaceController
				WorkspaceController wc = new WorkspaceController(
						"support/images/");

				wc.setLangDefFilePath("support/lang_def.xml");
				wc.loadFreshWorkspace();
				wc.createAndShowGUIForTesting(wc, "SJIS");
			}
		});
	}

	private void initializeLookAndFeel() {
		try {
			UIManager.setLookAndFeel(WindowsLookAndFeel.class.getName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}

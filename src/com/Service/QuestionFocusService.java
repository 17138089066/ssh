package com.Service;

import java.util.List;

import com.Entity.QuestionFocus;

public interface QuestionFocusService {
	 //��ע����
	 void savaQuestionFocus(QuestionFocus questionFocus);
	 //ȡ����ע����
	 void deleteQuestionFocus(Integer user_id, Integer question_id);
	//�鿴ʱ���Ѿ���ע�˸�����
	 List<QuestionFocus> search(Integer user_id, Integer question_id);
	 //�õ�ĳ����������й�ע
	 List<QuestionFocus> getOneQuestionFocusAll(Integer question_id);
	 //ɾ��ĳһ����ע  ����ID
	 void deleteQueestionFocusById(Integer id);
}

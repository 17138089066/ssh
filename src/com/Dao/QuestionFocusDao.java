package com.Dao;


 
import java.util.List;

import com.Entity.QuestionFocus;
 

public interface QuestionFocusDao {
		//��ע����
		void savaQuestionFocus(QuestionFocus questionFocus);
		//ȡ����ע����
		void deleteQuestionFocus(Integer user_id, Integer question_id);
		//�鿴ʱ���Ѿ���ע�˸�����
		List<QuestionFocus> search(Integer user_id, Integer question_id);
		 List<QuestionFocus> getOneQuestionFocusAll(Integer question_id);
		 //ɾ��ĳһ����ע  ����ID
		 void deleteQueestionFocusById(Integer id);
}

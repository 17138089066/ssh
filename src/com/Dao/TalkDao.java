package com.Dao;

import java.util.List;

import com.Entity.Answer;
import com.Entity.AnswerGreat;
import com.Entity.Question;
 

public interface TalkDao {
	  //����������
	  void saveQuestion(Question question);
	//ɾ��question
	  void deleteQuestion(Integer questionId);
	 //�õ� ���� ���ϵ�еĻش� Ȼ��õ����û�  ����ҹ̸ jsp  չʾ
	  List<Answer> getBestAnswerTop();
	  //�õ�һ���ش𣨲����ظ���
	  List<Answer> getAnswerExcludeSon(Integer id);
	  //��������
	  List<Question> getQuestionAllByTime();
	//���ŵģ���������ģ�
	  List<Question> getQuestionElite();
	//�û��Ƽ�  �õ���ص�cacheTag��question
	 //���õ�question�ģ������Ϳ�  ruoû�е��� ���߻�û��cachetag
	  List<Question> getQuestionOrderByFocus();
	
	//�õ�һ�����������
	  Question getQuestionDetailById(Integer id);
	//����һ���ظ�
	  void saveAnswer(Answer answer);
	  //ɾ��һ���ش�
	  void deleteAnswerById(Integer id);
	//�õ�һ����������д�
	  List<Answer> getAnswerAllToOneQuestion(Integer id);
	  //�õ�һ�������answer  Ϊ��admin ɾ��
	  Answer getOneAnswerDetailById(Integer id);
	 
	  //�õ�ĳ���˵Ļش�Ծ����һ������
	  List<Answer> getOneAnswerToOneQuestion(Integer user_id, Integer question_id);
	  
	//�õ�һ��father��Ϊ��չ����son��
	  Answer getOneFatherAnswer(Integer id);
	//�õ����е�һ��father������son
	  List<Answer> getSonAllOfFather(Integer id);
	//����Ƿ�����
	  List<AnswerGreat> checkIsGreat(Integer user_id, Integer answer_id);
	//�������
	  void saveAnswerGreat(AnswerGreat answerGreat);
	//ɾ������
	  void deleteAnswerGreat(Integer user_id,Integer answer_id);

	  //������ҳ��ʾ   �õ�Ҫ���û������лش�
	  List<Answer> getOneUserOfAllAnswer(Integer user_id);
	  
}

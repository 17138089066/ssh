package com.Dao;

import java.util.List;

import com.Entity.Category1;
import com.Entity.Category2;
import com.Entity.Question;
import com.Entity.Sport;
import com.Entity.SportComment;
import com.Entity.SportReply;
import com.Entity.UserArticle;

public interface SportDao {
	//���澰��
	  void saveSport(Sport sport);
	 // ɾ��sport
	  void deleteSport(Integer sportId);
	//��sport������ҳ��  ���� �����㽭  ��԰���  ������  ��������
	  List<Sport> getSportAll(String category1, String category2, String category3);
	  
	  List<Sport> getSportAllByTime();
	  
	//�õ�����
	  Sport getSportDetail(Integer id);
	
	  //�������ڰ���sport��content�����û��ؼ���
	  List<Sport> searchSportByDefault(String h);
	//���չؼ�����������
	  List<UserArticle> searchUserArticle(String h);
	  //���չؼ��� ��question
	  List<Question> searchQuestion(String h);
	  
	//�õ��㽭  ��id  (����id)
	  Category1 getCategory1Id(String name);
	  Category2 getCategory2Id(String name);
	//�������ۣ�һ����
	  void saveSportComment(SportComment sportComment);
	//�õ�һ������
	  List<SportComment> getSportComment(Integer id);
	//�õ���������
	  List<SportReply> getSportReply(Integer id);
	//�����������
	  void saveSportReply(SportReply sportReply);
	  //ɾ��һ������
	 void deleteSportComment(Integer id);
	  //ɾ����������
	  void deleteSportReply(Integer id);
	  //��DAOcomment Ϊ��admin��ɾ��
	  SportComment getSportCommentById(Integer id);
	  
	
	
	//�õ����㰴�����ֵ������
	List<Sport> getSportAllOrderBySocre();
	
}

package com.Dao;

import java.util.List;
 
import com.Entity.UserArticle;
import com.Entity.UserArticleComment;
import com.Entity.UserArticleReply;

public interface ArticleDao {
	//�����������
	 List<UserArticle> getArticleAllByPageView();
	//��ʱ��
	 List<UserArticle> getArticleAllByTime();
	//��������
	 List<UserArticle> getArticleAllByGreatCount();
	
	 //�õ�һ�����ߵ���������
	 List<UserArticle> getArticleAllByUserId(Integer user_id);
	 //�õ�use��������
	 List<UserArticle> getUserOtherArticle(Integer user_id, Integer article_id);
	 
	//�õ�һƬ������ϸ
	 UserArticle getArticleDetail(Integer id);
	 
	 //�õ�ͨ����˵ĺ�û�����article
	 List<UserArticle> getExaminationAdopt();
	 List<UserArticle> getExaminationLoading();
	 
 
	//��������
	 void saveUserArticleComment(UserArticleComment userArticleComment); 
	 //�õ�һ������
	 List<UserArticleComment> getUserArticleComment(Integer id);
	 //�õ���������
	 List<UserArticleReply> getUserArticleReply(Integer id);
	 //�����������
	 void saveUserArticleReply(UserArticleReply userArticleReply);
	 //�õ�һ������ ��adminɾ�����ݣ�
	 UserArticleComment getUserArticleCommentById(Integer id);
	 
	
	//�õ����ۺϵ÷ֵ�top3
	 List<UserArticle> getUserArticleOrderByScore();
 
}

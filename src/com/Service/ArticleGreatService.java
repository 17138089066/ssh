package com.Service;

import java.util.List;

import com.Entity.ArticleGreat;

public interface ArticleGreatService {
	//����Ƿ��ѵ���
		  List<ArticleGreat> checkIsGreat(Integer article_id,Integer user_id);
		//ɾ��������Ϣ dui����ĳ����
		  void deleteGreat(Integer userArticle_id,Integer user_id);
		//���������Ϣ
		  void saveGreat(ArticleGreat articleGreat);
		//��ɾ�����µ�ʱ�� ɾ�����޵Ĺ�ϵ
		  void deleteArticleToGreat(Integer userArticle_id);
}

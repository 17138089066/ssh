package com.Service;

import java.util.List;

import com.Entity.PageView;
import com.Entity.SpecialScheme;
import com.Entity.SpecialSchemeUser;
import com.Entity.User;

public interface AdminStatisticsService {
	//�õ�ÿ��������Ϣ
	 List<PageView> getAllOrderByTime();
	 //����ÿ���������Ϣ
     void savePageView(PageView pageView);
   //admin ���user  �б�
 	List<User> adminGetUserAll();
 	//�����ر�߻�
 	void saveSpecialScheme(SpecialScheme specailScheme);
	//�õ����е�
	List<SpecialScheme> getSchenmeListAll();
	//�õ�һ����detail
	SpecialScheme getSpecialSchemeDetail(Integer id);
	//�����û�����
	 void saveSpecialSchemeUser(SpecialSchemeUser specialSchemeUser);
	//�õ��û��������
	 List<SpecialSchemeUser> getSchemeUserAll();
}

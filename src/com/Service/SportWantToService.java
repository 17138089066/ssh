package com.Service;

import java.util.List;

import com.Entity.SportWantTo;

public interface SportWantToService {
	//����Ƿ��ѵ���
	  List<SportWantTo> checkIsWant(Integer sport_id,Integer user_id);
	//ɾ�����޾������Ϣ
	  void deleteWant(Integer sport_id,Integer user_id);
	//�����û��ĵ��޶Ծ���
	  void saveWant(SportWantTo sportWantTo);
	//�õ�һ��sport�����еĵ��ޣ�Ϊɾ��ɾ��sport��ʱ��
	  List<SportWantTo> getOneSportAll(Integer sportId);
	 //ɾ��һ����Ϣ��Ϊ��ɾ�������ʱ��
	  void deleteSportWantTo(Integer id);
}

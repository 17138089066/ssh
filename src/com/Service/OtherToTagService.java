package com.Service;

import java.util.List;
 
import com.Entity.OtherToTag;
import com.Entity.Tag;
 

public interface OtherToTagService {
	
	//�õ�tagͨ��ID
	  Tag getTagById(Integer id);
	 //��DAO���е�tag
	  List<Tag> getTagAllDesc(); 
	//������tag
	  void saveTag (Tag tag);
	  //�õ�һ�����»���һ����������еı�ǩ��ϵ
	  List<OtherToTag> getOtherToTagByOtherAll(Integer otherId, String flag);
	//��������or question ��tag
	  void saveOtherToTag(OtherToTag otherToTag);

	//ͨ��tag �ӹ������в�����Ӧ������
	/* List<OtherToTag> getOtherFromOtherToTagByTag_id1(String other, Integer tag_id);*/
	  
	  
	  //ͨ��tag��name�õ�tagID
	 List<Tag> getTagIdByName(String tag_name);
	//ɾ��һ����ϵ(�û��ݸ���޸ĵ���othertotag��revise)
	void deleteOtherToTag(OtherToTag otherToTag);
	//ɾ��һ�����µ�ʱ��ɾ����ϵ
	void delteArticleToTag(Integer otherId, String flag);
	
	 //����Ѱ���û��ĸ���Ȥ��sport��article
	 List<OtherToTag> getOtherForRecommend(String flag, Integer tagId);
	 
	 //�������е�tagid �õ����е�other��������
	 List<OtherToTag> getOtherAllByTagId(Integer tag_id);
}

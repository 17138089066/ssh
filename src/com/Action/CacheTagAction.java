package com.Action;

 

import java.util.HashMap;
import java.util.List;
import java.util.Map;
 

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.Entity.CacheTag;
import com.Entity.User;
import com.Service.CacheTagService;
import com.Service.UserService;
 
@Controller("cacheTagAction")
@Scope("prototype")
public class CacheTagAction  {
 
	@Resource
	private UserService userService;
	@Resource
	private CacheTagService cacheTagService;
	
	//�����û�cache����
	void saveCacheTag(String tagId){
		//tagId ÿһƪ���µ�id ��  3/4/12
		String []tempTagId = tagId.split("/");
		
		String name = (String)ServletActionContext.getRequest().getSession().getAttribute("name");
		if(name == null) return;
		User user = userService.getUserByName(name);
		//�õ�ԭ�е�������ߵ�cacheTag
		List<CacheTag> cacheTagList = cacheTagService.getUserCacheTagList(user.getId());
		
		/*new 5��  old 5��
		 * ���������������1.newû���� oldҲ�ض�û����  ֱ�Ӳ���  2.new���� oldû��  ��Ҫ��һ��new ���old �ٲ�������
		 * 			  3.new����oldҲ����  ��һ��old��Ϣ   ȥ������update�����µ�����
		 * �������ԭ������Ѿ�����������Ϣ  1.��new�� --->count��һ   2.��old��--->flag���new count����һ �鿴�ǲ�����Ҫȥ��old����һ��new�ȵ�
		 */
		
		//�Ƿ��Ѿ�����map<tagid,new/old>
		Map<Integer, CacheTag> mapNew = new HashMap<Integer, CacheTag>();
		Map<Integer, CacheTag> mapOld = new HashMap<Integer, CacheTag>();
		
		for(CacheTag var : cacheTagList) {
			if(var.getFlag().equals("new")) {
				mapNew.put(var.getTagId(), var);
			}else {
				mapOld.put(var.getTagId(), var);
			}
		}
		
		for(int i = 0; i < tempTagId.length; i++) {
			
			Integer tag_id = Integer.parseInt(tempTagId[i]);
			
			if(mapNew.containsKey(tag_id)) {
				CacheTag tempCacheTag = mapNew.get(tag_id);
				tempCacheTag.setCount(tempCacheTag.getCount() + 1);
				cacheTagService.save(tempCacheTag);
			}else if(mapOld.containsKey(Integer.parseInt(tempTagId[i]))) {
				//��������¼ ������old ��Ҫ���new
				CacheTag tempCacheTag = mapOld.get(tag_id);
				tempCacheTag.setFlag("new");
				
				if(mapNew.size() < 5){ //ֱ�Ӵ�old ���  new������max-5�� 
					mapNew.put(tag_id, tempCacheTag);
					mapOld.remove(tag_id);
					cacheTagService.save(tempCacheTag);
				} else {//��Ҫ��new���ҵ�һ������Ϊold
					int count = Integer.MAX_VALUE;
					CacheTag minCacheTag = new CacheTag();
					for(Integer var : mapNew.keySet()) {
						if(mapNew.get(var).getCount() <= count) {
							minCacheTag = mapNew.get(var);  //����������new
						}
					}
					if(mapOld.size() < 5){ //��ֱ�ӽ���bian old
						minCacheTag.setFlag("old");
						cacheTagService.save(minCacheTag);//���潵����new
						cacheTagService.save(tempCacheTag);//����������old
						mapOld.put(minCacheTag.getTagId(), minCacheTag);
						mapNew.remove(minCacheTag.getId());
						mapNew.put(tag_id, tempCacheTag);
						mapOld.remove(tag_id);
					}else {
						//������old�ͽ�����new���໻�Ϳ��ԣ�old ���ˣ�
					 
						mapNew.remove(minCacheTag.getTagId());
						mapOld.remove(tag_id);//�Ƴ�������old ��ID
						
						minCacheTag.setFlag("old");
						cacheTagService.save(minCacheTag);//���潵����new
						
						cacheTagService.save(tempCacheTag);//����������old
 
						mapOld.put(minCacheTag.getTagId(), minCacheTag);
						mapNew.put(tag_id, tempCacheTag);
					}
				}
				
			}else {
				//���֮ǰ���������tag
				//new �Ƿ�����
				if(mapNew.size() < 5) {
					CacheTag tempCacheTag = new CacheTag(tag_id, user.getId(), 0, "new");
					mapNew.put(tag_id, tempCacheTag);
					cacheTagService.save(tempCacheTag);
				}else {
					//��new�е�һ��ת���old
					//�ҵ��������С��new���old
					int count = Integer.MAX_VALUE;
					CacheTag minCacheTag = new CacheTag();
					for(Integer var : mapNew.keySet()) {
						if(mapNew.get(var).getCount() <= count) {
							minCacheTag = mapNew.get(var);//��Ҫ������new
						}
					}
 
					 if((mapOld.size()) < 5){
						//�����µ����new  ���old û��
						 CacheTag tempCacheTag = new CacheTag(tag_id, user.getId(), 0, "new");
						 
						 mapNew.put(tag_id, tempCacheTag);
						 mapNew.remove(minCacheTag.getTagId());
						 mapOld.put(minCacheTag.getTagId(), minCacheTag);
						 minCacheTag.setFlag("old");

						 cacheTagService.save(minCacheTag);//���潵����new

						 cacheTagService.save(tempCacheTag);//�����µ�new
					 }else {
						 //�ҵ���Ҫȥ�������old (�����滻�� �µ����new)
						 	count = Integer.MAX_VALUE;
						 	CacheTag minCacheTag2 = new CacheTag();
							for(Integer var : mapOld.keySet()) {
								if(mapOld.get(var).getCount() <= count) {
									minCacheTag2 = mapOld.get(var);//�ҵ���Ҫ��̭��old
								}
							}
							
							mapOld.remove(minCacheTag2.getTagId());
 
							minCacheTag2.setFlag("new");
							minCacheTag2.setCount(0);
							minCacheTag2.setTagId(tag_id);

							cacheTagService.save(minCacheTag2);//�����µ����new
							minCacheTag.setFlag("old");

							cacheTagService.save(minCacheTag);//��Ҫ������new
							
							mapNew.remove(minCacheTag.getTagId());
							mapNew.put(tag_id, minCacheTag2);
							mapOld.put(minCacheTag.getTagId(), minCacheTag);
					 }
					
				}

			}
		}

	}
}

package com.Entity;
/*
 * ��Ϊ������¼�û�ƽʱ���ϰ�ߵı�
 * ÿ���û��̶�30����¼(15����ΪNew 15����Ϊold)
 * ������޳������������tag�ı�ǩ
 */
public class CacheTag {
	private Integer id;
	private Integer tagId;
	private Integer userId;
	private Integer count;//Ƶ��
	private String flag;//old or new
	
	public CacheTag(){
		
	}
  
 
	public CacheTag(Integer tagId, Integer userId, Integer count, String flag) {
		 
		this.tagId = tagId;
		this.userId = userId;
		this.count = count;
		this.flag = flag;
	}


	public Integer getTagId() {
		return tagId;
	}


	public void setTagId(Integer tagId) {
		this.tagId = tagId;
	}


	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	 
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	
	
}

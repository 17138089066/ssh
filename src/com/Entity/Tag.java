package com.Entity;
/*
 * ���б�ǩ��
 */
public class Tag {
	
	private Integer id;
	private String tag;
	//�����ж��Ƿ����ű�ǩ
	private Integer tagCount;
	
	
	 
	public Tag(String tag, Integer tagCount) {
		//super();
		this.tag = tag;
		this.tagCount = tagCount;
	}


	public Tag(){
	  
	}
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public Integer getTagCount() {
		return tagCount;
	}
	public void setTagCount(Integer tagCount) {
		this.tagCount = tagCount;
	}
	
	
}

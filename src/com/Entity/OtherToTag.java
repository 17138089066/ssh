package com.Entity;
/*
 * ����to��ǩ��
 * ����to��ǩ
 */
public class OtherToTag {
	
	private Integer id;
	private Integer otherId;//����������µ�id
	private Tag tag;
	private String flag;//�������������»������� article question

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getOtherId() {
		return otherId;
	}
	public void setOtherId(Integer otherId) {
		this.otherId = otherId;
	}
	public Tag getTag() {
		return tag;
	}
	public void setTag(Tag tag) {
		this.tag = tag;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public OtherToTag(){
		
	}

	public OtherToTag(Integer otherId, Tag tag, String flag) {
		//super();
		this.otherId = otherId;
		this.tag = tag;
		this.flag = flag;
	}
	
	
	 
}

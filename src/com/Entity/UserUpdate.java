package com.Entity;

import java.util.Date;

/*
 *  ���˶�̬��
 */
public class UserUpdate {
	private Integer id;
	private String flag;// �������μ�    | �������μ�    �����˾���   �����˻ش� ���������  ��ע������  �ش�������  
	private Integer other_id;//�����flag����Ͳ��ID��  ���������µ�id�ţ�
	private String title;//�κ����඼�ж�̬����
	private String content;//��ע��ĳ������û�����ݵ�ֻ�б���
	private User author;//�ش���ĳ��������Լ�   �����Ķ�������user   ��ע������û��user  
	private Integer count;//���Ա�ʾ��26����ͬ�˸����£�
	private User owner;//������˭�Ķ�̬
	private Date time;
	
	public UserUpdate(){
		
	}
 
	public UserUpdate(String flag, Integer other_id, String title,
			String content, User author, Integer count, User owner,Date time) {
		//super();
		this.flag = flag;
		this.time = time;
		this.other_id = other_id;
		this.title = title;
		this.content = content;
		this.author = author;
		this.count = count;
		this.owner = owner;
	}



	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public Integer getOther_id() {
		return other_id;
	}
	public void setOther_id(Integer other_id) {
		this.other_id = other_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	 
	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	
}

package com.Entity;

import java.util.Date;

/*
 * ����һ��һ
 */
public class SportReply {

	private Integer id;
	private Sport sport;
	private Integer	targetCRId;//�ظ����ۻ��߻ظ��ظ���Ŀ��ID
	private String type;//����  ��  comment ����  reply��ǰ���Ƕ�comment�Ļظ������ǶԻظ��Ļظ���
	private User user;
	private User targetUser;//Ŀ����
	private Date time;
	private String content;
	
	public SportReply(){
		
	}
 
	public SportReply( Sport sport, Integer targetCRId, String type,
			User user, User targetUser, Date time, String content) {
		//super();
		//this.id = id;
		this.sport = sport;
		this.targetCRId = targetCRId;
		this.type = type;
		this.user = user;
		this.targetUser = targetUser;
		this.time = time;
		this.content = content;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}



	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Sport getSport() {
		return sport;
	}

	public void setSport(Sport sport) {
		this.sport = sport;
	}

	public Integer getTargetCRId() {
		return targetCRId;
	}

	public void setTargetCRId(Integer targetCRId) {
		this.targetCRId = targetCRId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getTargetUser() {
		return targetUser;
	}

	public void setTargetUser(User targetUser) {
		this.targetUser = targetUser;
	}
	
	
	
	
}

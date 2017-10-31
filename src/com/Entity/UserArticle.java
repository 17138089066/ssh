package com.Entity;

import java.util.Date;

public class UserArticle {
	private Integer id;
	private Date time;
	private String title;
	private String content;
	private User user;
	private String tag;
	private Integer commentCount;//������
	private Integer wantCount;//�����
	//����
	private String cover;
	//������
	private Integer greatCount;
	//�ۺϵ����� �����*0.5+����*0.2+����*0.3
	private Double score;
	private String isDraft; //�Ƿ��ǲݸ�
	private String isExamination;//admin ���  �Ƿ�ͨ�����
	
	
	public UserArticle(){
		
	}
	
	
	public String getIsExamination() {
		return isExamination;
	}
	public void setIsExamination(String isExamination) {
		this.isExamination = isExamination;
	}
	public String getIsDraft() {
		return isDraft;
	}
	public void setIsDraft(String isDraft) {
		this.isDraft = isDraft;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	public Integer getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}
	public Integer getWantCount() {
		return wantCount;
	}
	public void setWantCount(Integer wantCount) {
		this.wantCount = wantCount;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public Integer getGreatCount() {
		return greatCount;
	}
	public void setGreatCount(Integer greatCount) {
		this.greatCount = greatCount;
	}
 
	public Double getScore() {
		return score;
	}
	public void setScore(Double score) {
		this.score = score;
	}
	
	public UserArticle(Integer id){
		this.id = id;
	}
	 
 
	
}

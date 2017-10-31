package com.Entity;

import java.util.Date;

public class User {

	private int id;
	private String username;
	private String password;
	private String avatar;
	private String background;//�û�����
	private Date registerDate;//ע�������
	private String managerOrNot;//�����Ƿ��ǹ���Ա
	 
	private String individualSignature;//����ǩ��
	private Integer greatCount;//��õĵ�����
	private Integer answerCount;//�ش��������
	private Integer blogCount;//�μ���
	private Integer followerCount;//��ע��
	private Integer followingCount;//��ע��
	private Integer questionCount;//��ע������ĸ���
	private String interestTag;//�û�����Ȥģ��
	private String isForbidden;
	
	
	public User(){
		
	}
	public User(Integer id){
		this.id = id;
	}

 
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public Date getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(Date registerDate) {
		this.registerDate = registerDate;
	}
	public String getManagerOrNot() {
		return managerOrNot;
	}
	public void setManagerOrNot(String managerOrNot) {
		this.managerOrNot = managerOrNot;
	}
	public String getIndividualSignature() {
		return individualSignature;
	}
	public void setIndividualSignature(String individualSignature) {
		this.individualSignature = individualSignature;
	}
	public Integer getGreatCount() {
		return greatCount;
	}
	public void setGreatCount(Integer greatCount) {
		this.greatCount = greatCount;
	}
	public Integer getAnswerCount() {
		return answerCount;
	}
	public void setAnswerCount(Integer answerCount) {
		this.answerCount = answerCount;
	}
	public Integer getBlogCount() {
		return blogCount;
	}
	public void setBlogCount(Integer blogCount) {
		this.blogCount = blogCount;
	}
	public Integer getFollowerCount() {
		return followerCount;
	}
	public void setFollowerCount(Integer followerCount) {
		this.followerCount = followerCount;
	}
	public Integer getFollowingCount() {
		return followingCount;
	}
	public void setFollowingCount(Integer followingCount) {
		this.followingCount = followingCount;
	}
	public String getBackground() {
		return background;
	}
	public void setBackground(String background) {
		this.background = background;
	}
	public String getInterestTag() {
		return interestTag;
	}
	public void setInterestTag(String interestTag) {
		this.interestTag = interestTag;
	}
 
	public Integer getQuestionCount() {
		return questionCount;
	}
	public void setQuestionCount(Integer questionCount) {
		this.questionCount = questionCount;
	}
	public String getIsForbidden() {
		return isForbidden;
	}
	public void setIsForbidden(String isForbidden) {
		this.isForbidden = isForbidden;
	}
	
	
}

package com.Entity;

import java.util.Date;

/*
 * ��¼ÿ��ķ�����  ����� ...
 */
public class PageView {
	private Integer id;
	private Date time;
	private Integer loginCount;//�û���������
	private Integer pageViewCount;//�����
	private Integer articleCount;//��������
	private Integer commentCount;//��������
	private Integer questionCount;//��������
	private Integer answerCount;//�����ش���
	private Integer registerCount;//����ע����
	
	public PageView() {
		
	}
 
	public PageView(Date time, Integer loginCount, Integer pageViewCount,
			Integer articleCount, Integer commentCount, Integer questionCount,
			Integer answerCount, Integer registerCount) {
 
		this.time = time;
		this.loginCount = loginCount;
		this.pageViewCount = pageViewCount;
		this.articleCount = articleCount;
		this.commentCount = commentCount;
		this.questionCount = questionCount;
		this.answerCount = answerCount;
		this.registerCount = registerCount;
	}
 
	public Integer getRegisterCount() {
		return registerCount;
	}
	public void setRegisterCount(Integer registerCount) {
		this.registerCount = registerCount;
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
	public Integer getLoginCount() {
		return loginCount;
	}
	public void setLoginCount(Integer loginCount) {
		this.loginCount = loginCount;
	}
	public Integer getPageViewCount() {
		return pageViewCount;
	}
	public void setPageViewCount(Integer pageViewCount) {
		this.pageViewCount = pageViewCount;
	}
	public Integer getArticleCount() {
		return articleCount;
	}
	public void setArticleCount(Integer articleCount) {
		this.articleCount = articleCount;
	}
	public Integer getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}
	public Integer getQuestionCount() {
		return questionCount;
	}
	public void setQuestionCount(Integer questionCount) {
		this.questionCount = questionCount;
	}

	public Integer getAnswerCount() {
		return answerCount;
	}
	public void setAnswerCount(Integer answerCount) {
		this.answerCount = answerCount;
	}
	 
	 

}

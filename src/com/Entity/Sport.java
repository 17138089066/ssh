package com.Entity;

import java.util.Date;

public class Sport {
	
	private Integer id;
	private String name;
	//��Ҫ
	private String summary;
	private String cover;
 
	private String content;
	//��ȥ������
	private Integer wantCount;
	//������γ
	private String x;
	private String y;
	//�㽭  ��԰
	private Category1 category1;//�ȽϹ̶��Ĵ����ǩ
	private Category2 category2;
	private String customTag;//�Զ����ǩ
	//������
	private Integer commentCount;
	//�����
    private Integer pageViewCount;
    //�ۺ�����
    private Double score;
    private Date time;//����ʱ��

	public Sport(){
		
	}
	
	
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getCustomTag() {
		return customTag;
	}

	public void setCustomTag(String customTag) {
		this.customTag = customTag;
	}
	
	public Integer getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(Integer commentCount) {
		this.commentCount = commentCount;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}
	 
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getWantCount() {
		return wantCount;
	}
	public void setWantCount(Integer wantCount) {
		this.wantCount = wantCount;
	}
	public String getX() {
		return x;
	}
	public void setX(String x) {
		this.x = x;
	}
	public String getY() {
		return y;
	}
	public void setY(String y) {
		this.y = y;
	}

	public Category1 getCategory1() {
		return category1;
	}

	public void setCategory1(Category1 category1) {
		this.category1 = category1;
	}

	public Category2 getCategory2() {
		return category2;
	}

	public void setCategory2(Category2 category2) {
		this.category2 = category2;
	}

	public Integer getPageViewCount() {
		return pageViewCount;
	}

	public void setPageViewCount(Integer pageViewCount) {
		this.pageViewCount = pageViewCount;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}
	 
 
}

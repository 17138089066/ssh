package com.Action;

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.Algorithm.EliminateHtml;
import com.Entity.ArticleGreat;
import com.Entity.User;
import com.Entity.UserArticle;
import com.Entity.UserUpdate;
import com.Service.ArticleGreatService;
import com.Service.ArticleService;
import com.Service.UserService;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@Controller("articleGreatAction")
@Scope("prototype")
public class ArticleGreatAction extends ActionSupport {
	
	@Resource
	private ArticleGreatService articleGreatService;
	@Resource
	private UserService userService;
	@Resource
	private ArticleService articleService;
	
	private String json;//���ܴ���
	
	
	//���ӻ���ɾ��������Ϣ
	public void addOrSub(){
		JSONObject jsonObject = new JSONObject(json);
		String userArticle_id = jsonObject.getString("userArticle_id");
		String method = jsonObject.getString("method");
			
		//User user = userService.getUserByName(ServletActionContext.getRequest().getSession().getAttribute("name").toString());
		Integer user_id1 = (Integer) ServletActionContext.getRequest().getSession().getAttribute("user_id");
		if(user_id1 != null ) {
 
	    Integer user_id = user_id1; 
		User user = new User(user_id);
		
	    UserArticle userArticle = new UserArticle();
	    
		userArticle = articleService.getArticleDetail(Integer.parseInt(userArticle_id));
		
		if(method.equals("��")){
 
			ArticleGreat articleGreat = new ArticleGreat(userArticle, user);
  
			articleGreatService.saveGreat(articleGreat);
			
			//���µĵ�������һ
			userArticle.setGreatCount(userArticle.getGreatCount() + 1);
			
			userService.saveUserArticle(userArticle);
			
			//�����û��Ķ�̬
			String content = userArticle.getContent();
			/*if(content.length() > 550){
				content = content.substring(0,550);
			}*/
			UserUpdate userUpdate = new UserUpdate("�������μ�", userArticle.getId(), userArticle.getTitle(),
					//�õ����µ�����
					content,//new EliminateHtml().RemoveHtmlTag(content),
					//�μǵ�����   ���µ� ������
					userArticle.getUser(), userArticle.getGreatCount(), user, new Date());
				
			userService.saveUserUpdate(userUpdate);
			
		}
		else {
			articleGreatService.deleteGreat(Integer.parseInt(userArticle_id), user.getId());
			
			//���µĵ�������һ
			if(userArticle.getGreatCount() > 0){
				userArticle.setGreatCount(userArticle.getGreatCount() - 1);
				userService.saveUserArticle(userArticle);
			}
			
			//ɾ�����û��Ķ�̬��Ϣ
			userService.deleteUserUpdate("�������μ�", userArticle.getId(), user.getId());
		}
		
		//�����µĵ�����
		String greatCount = userArticle.getGreatCount().toString();
		String json2 = "{\"greatCount\":\"" + greatCount + "\"}";
		try {
			sendMsg(json2);
		} catch (IOException e) {
			 
			e.printStackTrace();
		}
	  }
	}
	public void sendMsg(String content) throws IOException{
	     HttpServletResponse response = ServletActionContext.getResponse();//��ȡresponse
	     response.setContentType("text/html;charset=UTF-8");//����
	     response.getWriter().write(content);
	}
	
	
	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
	
	
	
}

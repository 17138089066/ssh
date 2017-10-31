package com.Action;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.Entity.Question;
import com.Entity.QuestionFocus;
import com.Entity.User;
import com.Entity.UserUpdate;
import com.Service.QuestionFocusService;
import com.Service.TalkService;
import com.Service.UserService;


@Controller("questionFocusAction")
@Scope("prototype")
public class QuestionFocusAction {
	@Resource
	private QuestionFocusService quesionFocusService;
	@Resource 
	private  TalkService talkService ;
	@Resource
	private UserService userService;
	
	private String json;
	//��ע����ȡ����עtalk�е�ĳһ��---question
	public void focusOrNo() {
		JSONObject jsonObject = new JSONObject(json);
		String question_id = jsonObject.getString("question_id");
		String method = jsonObject.getString("method");//��add����sub
		Integer user_id1 = (Integer)ServletActionContext.getRequest().getSession().getAttribute("user_id");
		if(user_id1 != null){
 
	    Integer user_id = user_id1;
		User user = new User(user_id);
	    
	    Question q = talkService.getQuestionDetailById(Integer.parseInt(question_id));
	 
	    if(method.equals("add")){
	    	 
		   quesionFocusService.savaQuestionFocus(new QuestionFocus(user, q));
		   q.setFocusCount(q.getFocusCount() + 1);
		   
		   //ִ���û��Ķ�̬��Ϣ����
		   
		   UserUpdate userUpdate = new UserUpdate("��ע������", q.getId(), q.getTitle(), 
				   //content Ϊnull ����Ϊ�� 
				   null , null, q.getFocusCount(), user,new Date()
				   );
		   userService.saveUserUpdate(userUpdate);
		   
		  //�����û��Ĺ�ע��������
		   user = userService.getUserById(user_id);
		   user.setQuestionCount(user.getQuestionCount() + 1);
		   userService.saveUser(user);
		   
		} else {
			if (q.getFocusCount() >= 1 ){
				q.setFocusCount(q.getFocusCount() -1);
			} else {
				q.setFocusCount(0);
			}
			quesionFocusService.deleteQuestionFocus(user_id, q.getId());
			
			//ɾ���û��Ķ�̬��Ϣ
			userService.deleteUserUpdate("��ע������", q.getId(), user_id);
			
			//�����û��Ĺ�ע��������
			user = userService.getUserById(user_id);
			user.setQuestionCount(((user.getQuestionCount() - 1) >= 0) ? (user.getQuestionCount() - 1) : 0);
			userService.saveUser(user);
			
		}
	       	//��������Ĺ�ע����
	       talkService.saveQuestion(q);
		} 
	}
 
	
	
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}
	
	
	
	
	
}

package com.Action;
/*
 * 
 * �����û��������Ĳ���
 */

import java.io.IOException;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

 
import com.Algorithm.EliminateHtml;
import com.Entity.Sport;
import com.Entity.SportWantTo;
import com.Entity.User;
import com.Entity.UserUpdate;
 
import com.Service.SportService;
import com.Service.SportWantToService;
import com.Service.UserService;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@Controller("sportWantToAction")
@Scope("prototype")

public class SportWantToAction extends ActionSupport {
	@Resource
	private SportWantToService sportWantToService;
	@Resource
	private UserService userService;
	@Resource
	private SportService sportService;
	
	private String json;//���ܴ���
	

	//���ӻ���ɾ��������Ϣ
	public void addOrSub(){
		JSONObject jsonObject = new JSONObject(json);
		String sport_id = jsonObject.getString("sport_id");
		String method = jsonObject.getString("method");
		
		String name = (String) ServletActionContext.getRequest().getSession().getAttribute("name");
		if(name != null ){
 
		User user = userService.getUserByName(name);
		
		Sport sport = new Sport();
		sport = sportService.getSportDetail(Integer.parseInt(sport_id));
		
		if(method.equals("��")){
 
			SportWantTo sportWantTo = new SportWantTo();
			sportWantTo.setSport(sport);
			sportWantTo.setUser(user);
			
			sportWantToService.saveWant(sportWantTo);
			
			//sport����ȥ����һ   ͬʱ���ۺ�����---���0.2  ��ȥ0.3 ����0.5
			sport.setWantCount(sport.getWantCount() + 1);
			sport.setScore(sport.getScore() + 0.3);
			sportService.saveSport(sport);
			
			//ͬʱ�����û��Ķ�̬��Ϣ
			//����洢��ȥ��content ͬʱ  ��ȡ
			String content = sport.getContent();
			/*if(content.length() > 550){
				content = content.substring(0,540);
			}*/
			UserUpdate userUpdate = new UserUpdate("�����˾���", sport.getId(), sport.getName(),
				
				content,//new EliminateHtml().RemoveHtmlTag(content),
				//authorΪ��
				null, sport.getWantCount(), user, new Date());
			
			userService.saveUserUpdate(userUpdate);
			
			
		}
		else {
			sportWantToService.deleteWant(Integer.parseInt(sport_id), user.getId());
			
			//sport����ȥ����һ  �÷ֲ���
			if(sport.getWantCount() > 0){
				sport.setWantCount(sport.getWantCount() - 1);
				sportService.saveSport(sport);
			}
			
			//ɾ���û��Ķ�̬
			userService.deleteUserUpdate("�����˾���", Integer.parseInt(sport_id), user.getId());
			
		}
		
		//�����µĵ�����
		String wantCount = sport.getWantCount().toString();
		String json2 = "{\"wantCount\":\"" + wantCount + "\"}";
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

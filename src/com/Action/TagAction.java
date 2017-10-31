package com.Action;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;


import com.Algorithm.EliminateHtml;
import com.Entity.User;
import com.Entity.UserArticle;
import com.Service.ArticleService;
import com.Service.OtherToTagService;
import com.Service.UserService;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@Controller("tagAction")
@Scope("prototype")

public class TagAction extends ActionSupport {
	
	@Resource
	private OtherToTagService otherToTagService;
	@Resource
	private ArticleService articleService;
	@Resource
	private UserService userService;
	
	//����json
	private String json;
	 
	/*
	public void getArticleByTag(){
		//����Ҫ����
		JSONObject ObjecJson = new JSONObject();
		ObjecJson = JSONObject.fromObject(json);
		String tagName = ObjecJson.getString("tagName");
		Integer count = ObjecJson.getInt("count");
		Integer pageSize = ObjecJson.getInt("pageSize");
		//����tagName��id;
		Integer tag_id = articleToTagService.searchTag(tagName).get(0).getId();
		//������Ӧ������Tag��ϵ
		List<ArticleToTag> temp = articleToTagService.getArticleToTagByTag_id(tag_id);
		List<ArticleToTag> temp2 = new LinkedList<ArticleToTag>();
		//��Ҫ�µļ��صĶ���
		for(int i = count * pageSize, j = 0; i < temp.size() && j < pageSize; i++, j++){
			temp2.add(temp.get(i));
		}
		//����json2����(��Ҫjson�����а�,��Ҫ���)
		JsonConfig config = new JsonConfig();
		
		//config.setExcludes(new String[]{"handler","hibernateLazyInitializer"});  
		//config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		
		//����  (�����˲��ü����ķ�ʽ)
		config.setExcludes(new String[] { "userArticle","tag"});
		
		//��ֹutil��ʱ���ʽ����json (1.�����Ƿ����˼������������  2.���ܱ����Ķ������java.util.data  ��֮û����)
		//config.registerJsonValueProcessor(java.util.Date.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
		
		
		//ȡ�����µ�user he article��ȡ������ʱû�������أ�
		//ͨ����jsonarray�з�jsonobject  jsonobject�зŻ�����Ϣ
		JSONArray ja = new JSONArray();
		for(ArticleToTag articleToTag:temp2){
			JSONObject jsonObject = new JSONObject();
			UserArticle userArticle = articleService.getArticleDetail(articleToTag.getUserArticle().getId()); 
			jsonObject.put("time", userArticle.getTime().toString().substring(0, 16));
			
			jsonObject.put("title", userArticle.getTitle());
			
			jsonObject.put("greatCount", userArticle.getGreatCount());
			jsonObject.put("content", new EliminateHtml().RemoveHtmlTag(userArticle.getContent()));
			
			Map<String, String> map = new HashMap<String, String>();
			User user = userArticle.getUser();
			map.put("username", user.getUsername());
			map.put("img", user.getAvatar());
			jsonObject.put("user", map);
			
			//��Ϊ��֪Ϊ��  list  key ��Ϊtag �ͽ��ղ���
			List<String> list = new LinkedList<String>();
			String tag[] = userArticle.getTag().split("/");
			for(int i = 0; i < tag.length; i++){
				list.add(articleToTagService.getTagById(Integer.parseInt(tag[i])).getTag());
			}
			jsonObject.put("tagName", list);
			
			//������  ���߾ȹ�
			Map<String, String> tagMap = new LinkedHashMap<String, String>();//Ϊ�˰�˳����Linked..
			String tag[] = userArticle.getTag().split("/");
			tagMap.put("length",  tag.length + "");//����jsp �ж����� �ж��ٸ���ǩ
			for(int i = 0; i < tag.length; i++){
				tagMap.put(i + "", articleToTagService.getTagById(Integer.parseInt(tag[i])).getTag());
			}
			jsonObject.put("tagName", tagMap);
			
			
			//System.out.println(jsonObject);
			
			ja.add(jsonObject);
		}
		//System.out.println(ja);
		
		
		JSONArray jsonArray = JSONArray.fromObject(ja, config);
		
		String json2 = jsonArray.toString();
		
		try {
			sendMsg(json2);
		} catch (IOException e) {
			 
			e.printStackTrace();
		}
 
	}
 

	public void sendMsg(String content) throws IOException{
	     HttpServletResponse response = ServletActionContext.getResponse();//��ȡresponse
	     response.setContentType("text/html;charset=UTF-8");//����
	     response.getWriter().write(content);
	}
*/

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	 
	
	
}

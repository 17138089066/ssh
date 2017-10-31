package com.Action;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
 
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;



import com.Algorithm.EliminateHtml;
import com.Entity.News;

 
import com.Entity.CacheTag;
import com.Entity.OtherToTag;

import com.Entity.Sport;

import com.Entity.User;
import com.Entity.UserArticle;
import com.Entity.UserFocus;
import com.Entity.UserUpdate;

import com.Service.ArticleService;
import com.Service.CacheTagService;
import com.Service.NewsService;
import com.Service.OtherToTagService;
import com.Service.SportService;
import com.Service.UserService;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@Controller("userAction")
@Scope("prototype")
public class UserAction extends ActionSupport{

	private int id;
	private String username;
	private String password;
	private String address;
	private String phone;
	private String avatar;
	private String registerDate;
	private String managerOrNot;
	private String tagName;//ע��ʱѡ��ϲ��������
	
	//ͼƬ�ϴ�
	private File photo[];
	private String photoFileName[];
	private String photoContentType[];
	//�����޸�	 
	private String oldpwd;
	private String newpwd;
	private String cfmnewpwd;
 
	//������֤�� 
	private String checkcode;  
	//����ʱ��ǰһ����ַ
	private String backUrl;
	
	private User user;
	private String account;
	
	private List<Object> userFocusOrNot;
 
	@Resource
	public UserService userService;
	@Resource
	private NewsService newsService;
	@Resource
	private OtherToTagService otherToTagService;
	@Resource
	private SportService sportService;
	@Resource
	private ArticleService articleService;
	@Resource
	private CacheTagService cacheTagService;
	@Resource
	private AdminStatisticsAction adminAtatisticsAction; 
 
	
	private List<News> list = new LinkedList<News>();
	private News news,news2;
	private String json;
	
	private String category;//��Ϊ��� activities blog  answer question follower  following  
	private List<UserUpdate> userUpdateList;
	private String userUpdateName;//�鿴��˭����ҳ �Լ� or ������ 
	private String isOwner;//�������û���ҳ   �Ƿ����Լ�����ҳ����ʾ��ע  ����  ��ʾ�༭������Ϣ��
	private String isFocus;//�û��鿴�û���ҳ��ʱ��    ������  ��  ��ҳӵ���ߵ�  ��ϵ  �Ƿ��Ѿ���ע
	
	private String number;//�༭������Ϣ��ʱ��  ������ֻ�ϴ���ͷ���Ǳ���  ����ϴ���
	
	private Integer startRow = 0;//���ظ��˶�̬��ʱ��   ���ö���ʼֵ
	private Integer size = 4;//ÿ�μ�������
	boolean isEnd = false;
	
	private Integer startPage;//�û��鿴following��follower
	private Integer endPage;
	private Integer totalPage;//��ҳ��
	private Integer pageSize;//��ʼ����
	private Integer currentPage = 1;
	
	private List<Sport> recommendSport;//Ϊ���Ƽ�չʾ����
	private List<UserArticle> recommendArticle;
	private List<User> recommendUser;
	
 
	//���Ϊ���Ƽ� �Ƽ� �û��Ĺ�ע
	public void recommendUserMore() {
		JSONObject jo = new JSONObject();
		jo = JSONObject.fromObject(json);
		int startRow = jo.getInt("startRow");
		int size = jo.getInt("size");
		
		List<User> tempUser= new LinkedList<User>();
		
		tempUser = userService.getUserOrderByFollower();
		
		startRow %=  tempUser.size();
		recommendUser = new LinkedList<User>();
		while(recommendUser.size() != size) {
			startRow %=  tempUser.size();
			recommendUser.add(tempUser.get(startRow));
			startRow ++;
		}
		
		JSONArray ja = new JSONArray();
		ja = JSONArray.fromObject(recommendUser);
			
		String json2 = ja.toString(); //ja.toString();
		try {
			sendMsg(json2);
		} catch (IOException e) {
			 
			e.printStackTrace();
		}
		
	}
	
	//recommend �õ�Ϊ���Ƽ�
	public String recommend(){
	 
		recommendSport = new LinkedList<Sport>();
		recommendArticle = new LinkedList<UserArticle>();
		recommendUser = new LinkedList<User>();
		List<Sport> tempSport= new LinkedList<Sport>();
		List<UserArticle> tempArticle = new LinkedList<UserArticle>();
		List<User> tempUser= new LinkedList<User>();
		
		String name = (String)ServletActionContext.getRequest().getSession().getAttribute("name");
		
		if(name == null) {
			//ѡȡ�÷���ߵ�
			tempSport = sportService.getSportAllOrderBySocre();
			tempArticle = articleService.getUserArticleOrderByScore();
		}else {
			//���ǵ���ע����û���û����ص�cacheTag��
			ArrayList<Integer> arraySport = new ArrayList<Integer>();  //�洢sport����userArticle�ģɣ�
			ArrayList<Integer> arrayArticle = new ArrayList<Integer>();
			tempSport = new LinkedList<Sport>();
			tempArticle = new LinkedList<UserArticle>();
			
			User user = userService.getUserByName(name);
			
			//�õ��û��ĳ��õı�ǩ cachetag
			List<CacheTag> cacheTag = cacheTagService.getUserCacheTagList(user.getId());
			for (CacheTag var : cacheTag) {
				
				//�õ���Ӧ��sport��article
				List<OtherToTag> sportToTag = otherToTagService.getOtherForRecommend("sport", var.getTagId());
				List<OtherToTag> articleToTag = otherToTagService.getOtherForRecommend("article", var.getTagId());
 
				for (OtherToTag var2 : sportToTag) {
					if(!arraySport.contains(var2.getOtherId())) { //��ֹһƬ���� ������tag ����������
						tempSport.add(sportService.getSportDetail(var2.getOtherId()));
						arraySport.add(var2.getOtherId());
					}
					
				}
			    for(OtherToTag var3 : articleToTag) {
					if(!arrayArticle.contains(var3.getOtherId())) {
						tempArticle.add(articleService.getArticleDetail(var3.getOtherId()));
						arrayArticle.add(var3.getOtherId());
					}
				} 
			}
			//���ǵ���ע����û���û����ص�cacheTag��
			List<Sport> tempSport2 = sportService.getSportAllOrderBySocre();
			List<UserArticle> tempArticle2 = articleService.getUserArticleOrderByScore();
			for (Sport var4 : tempSport2) {
				if(!arraySport.contains(var4.getId())) {
					tempSport.add(var4);
				}
			}
			for (UserArticle var5 : tempArticle2) {
				if(!arrayArticle.contains(var5.getId())) {
					tempArticle.add(var5);
				}
			}

		}
		//�趨ÿҳ����(sport --> 6 �� article--->3 
		//����һ���˵�follower�����ɸ�DAO�͵����� �Ƽ� ���û������ܹ�ע��
		tempUser = userService.getUserOrderByFollower();
		int startRow1 = (currentPage - 1) * 6;//currrentPage ��ʼֵ Ϊ1
		int startRow2 = (currentPage - 1) * 3;
		int z = startRow1;
		for(int j = 0; z < tempSport.size() && j < 6; j++, z++) { 
			recommendSport.add(tempSport.get(z));
		}
		for(int i = startRow2, j = 0; i < tempArticle.size() && j < 3; j++, i++) { 
			tempArticle.get(i).setContent(new EliminateHtml().RemoveHtmlTag(tempArticle.get(i).getContent()));
			recommendArticle.add(tempArticle.get(i));
		}
		//����Ƽ���articleû��  Ϊ�˲���  ����sport
		while(recommendSport.size() + recommendArticle.size() != 9) {
			if (z == tempSport.size()) break;
			recommendSport.add(tempSport.get(z));
			z++;
		}
		 
		//7 Ϊ �Ƽ�����  ��ֹ tempUser.size()��û�дﵽ7	
		for(int i = 0; i < 7 && i < tempUser.size(); i++) {
			recommendUser.add(tempUser.get(i));
		}
		
		int tempTotal = tempSport.size() + tempArticle.size();
		//9 = 6 + 3
		totalPage = ((tempTotal % 9) == 0)?(tempTotal / 9):((tempTotal / 9) + 1);
	
		
		return SUCCESS;
	}
	
	
	//follower��ҳ
	public void getFollowPagination(){
		JSONObject jo = new JSONObject();
		jo = JSONObject.fromObject(json);
		this.currentPage = jo.getInt("currentPage");
		
		getUserUpdateMethod();
		
		JSONArray ja = new JSONArray();
		for(Object var : userFocusOrNot){
			JSONObject jo2 = new JSONObject();
			jo2.put("userFocusOrNot", var);
			ja.add(jo2);
		}
		String json2 = ja.toString();
//		System.out.println(json2);
		try {
			sendMsg(json2);
		} catch (IOException e) {
			 
			e.printStackTrace();
		}
		
	}
	
	//����ʵ�ּ��ظ���
	public void getUserUpdateMore(){
		JSONObject jo = new JSONObject();
		jo = JSONObject.fromObject(json);
		this.startRow = jo.getInt("startRow");
		this.size = jo.getInt("size");
		
		getUserUpdateMethod();
		
		JSONArray ja = new JSONArray();
		if(userUpdateList != null){
			for(UserUpdate var : userUpdateList){
				JSONObject jo2 = new JSONObject();
				jo2.put("isEnd", this.isEnd);
				jo2.put("userUpdate", var);
				ja.add(jo2);
			}
		}
		String json2 = ja.toString();
	//	System.out.println("-----" + jo.getInt("startRow")+"-------" + jo.getInt("size"));
		try {
			sendMsg(json2);
		} catch (IOException e) {
			 
			e.printStackTrace();
		}
		
	}
	
	
	//��עĳ�� ����ȡ����ע
	public void focusUserOrNot(){
		JSONObject jo = new JSONObject();
		jo = JSONObject.fromObject(json);
		String targetUsername = jo.getString("targetUsername");
		String method = jo.getString("method");
		//User targetUser = new User(1);
		User targetUser = userService.getUserByName(targetUsername);
		String name = (String) ServletActionContext.getRequest().getSession().getAttribute("name");
		if(name != null){
		User loginUser = userService.getUserByName(name);
		if(method.equals("focus")){//��ע���˵�ʱ��
			//���滥���ע�Ĺ�ϵ��
			UserFocus userFocus = new UserFocus(loginUser, targetUser);
			userService.saveUserFocus(userFocus);
			//update�û��Ĺ�ע���ͱ���ע��(�ҵ�following +1)
			loginUser.setFollowingCount(loginUser.getFollowingCount() + 1);
			//���˵ı���ע����һ
			targetUser.setFollowerCount(targetUser.getFollowerCount() + 1);
		}else{
			userService.deletUserFocus(loginUser.getId(), targetUser.getId());
			loginUser.setFollowingCount(loginUser.getFollowingCount() - 1);
			targetUser.setFollowerCount(targetUser.getFollowerCount() - 1);
		}
		userService.saveUser(loginUser);
		userService.saveUser(targetUser);
	}
		
	}
	
	//���û�����Ϣ���� --->�õ��û�������Ϣ
	public String getUserDetail(){
		String username = (String) ServletActionContext.getRequest().getSession().getAttribute("name");
		if(username == null){
			return INPUT;
		}else{
			user = userService.getUserByName(username);
			String []tag = new String[3];
			tag = user.getInterestTag().split("/");
			StringBuffer tempTagName = new StringBuffer();
			for(int i = 0; i < tag.length; i++){
				tempTagName.append("/" + otherToTagService.getTagById(Integer.parseInt(tag[i])).getTag()); 
			}
			tagName = tempTagName.substring(1);
			//tagName = new StringBuffer(tagName.substring(1));
		}
		return SUCCESS;
	}
	
	//��ҳ����(��Ҫ��ʾ�û���̬)
	void getPage(List<UserUpdate> tempList,Integer startRow, Integer size){
		int i = startRow ;
		userUpdateList = new LinkedList<UserUpdate>();
		for(int j = 0; i < tempList.size() && j < size; j++, i++){
			userUpdateList.add(tempList.get(i));
		}
		if(i >= tempList.size()) this.isEnd = true;
	}
	//�û���ע�ͱ���ע��ҳ
	List<UserFocus> getFollowPage(List<UserFocus> templist){
		List<UserFocus> list = new LinkedList<UserFocus>();
		this.totalPage = ((templist.size() % pageSize) == 0)?(templist.size() / pageSize):((templist.size() / pageSize) + 1);
		Integer startRow = (currentPage - 1) * pageSize;//�±�
		for(int j = 0, i = startRow; i < templist.size() && j < pageSize; j++, i++){
			list.add(templist.get(i));
		}
		return list;
	}
	
	//�õ��û���activities   ���ָ�����ҳ
	public String getUserUpdate(){
			getUserUpdateMethod();
		return SUCCESS;
	}
	
	public void getUserUpdateMethod(){
		 user = userService.getUserByName(userUpdateName);//�������Լ�����ҳ���Ǳ��˵���ҳ   ������userupadateName ���ݹ���   ��userΪ��ҳ����
		 User loginUser = new User();
		//�ж����Լ����� ���������� ����ҳ
		String  loginName= (String) ServletActionContext.getRequest().getSession().getAttribute("name");  
		if (loginName != null ) { 
			loginUser = userService.getUserByName(loginName); 
			if (loginName.equals(userUpdateName))isOwner = "true";
			else {//�������ߺ��û���ҳ�Ĺ�ϵ
				if (userService.checkUserFocus(loginUser.getId(), user.getId()).size() != 0) {
					isFocus = "true";
				}
			}
		}
 	    
		List<UserUpdate> tempList = new LinkedList<UserUpdate>();
		if (category == null || category.equals("activities")) {
			category = "activities";
			tempList = userService.getUserUpdate(user.getId());
			//�ָ�ҳ
			getPage(tempList, startRow, size);
		}else if(category.equals("answer")){
			tempList = userService.getUserUpdateAnswer(user.getId());
			getPage(tempList, startRow, size);
		}else if(category.equals("blog")){
			tempList = userService.getUserUpdateBlog(user.getId());
			getPage(tempList, startRow, size);
		}else if(category.equals("question")){
			tempList = userService.getUserUpdateQuestion(user.getId());
			getPage(tempList, startRow, size);
		}else if(category.equals("focusQuestion")){
			tempList = userService.getUserUpdateFocusQuestion(user.getId());
			getPage(tempList, startRow, size);
		}else if(category.equals("following")){
			
			userFocusOrNot = new LinkedList<Object>();
			List<UserFocus> userFocusFollowing = userService.getUserFocusFollowing(user.getId());
			
			//�����ķ�ҳ
			this.pageSize = 6;
			
			userFocusFollowing = getFollowPage(userFocusFollowing);
			//�����û�е����ߵ�  ��  ֱ����ʾ  ����ҳ���˵�  ��ע���
			if(loginName == null ){
				for(UserFocus var:userFocusFollowing){
					Map<String,Object> userMap = new HashMap<String,Object>();
					userMap.put("isFocus", "true");
					userMap.put("user", var.getTargetUser());
					userFocusOrNot.add(userMap);
				}
			} else {//�е���  ��Ҫ���  ������  �͸���ҳ�������Ĺ�ע���   ��  ����  �Ƿ��Ѿ��������߹�ע				 
				for(UserFocus var:userFocusFollowing){
					Map<String,Object> userMap = new HashMap<String,Object>();
					List<UserFocus> checkUserFocus = userService.checkUserFocus(loginUser.getId(), var.getTargetUser().getId());
					if(checkUserFocus.size() == 0){
						userMap.put("isFocus", "false"); //û�б������߹�ע
					}else userMap.put("isFocus", "true");
					userMap.put("user", var.getTargetUser());
					userFocusOrNot.add(userMap);
				}
			}
		} else if(category.equals("follower")){  
			userFocusOrNot = new LinkedList<Object>();
			List<UserFocus> userFocusFollower = userService.getUserFocusFollower(user.getId());
			
			this.pageSize = 6;
			
			userFocusFollower = getFollowPage(userFocusFollower);
			if(loginName == null){  //�鿴��ҳ  ͬʱ��Ϊû��û�е���  ����  ��ʾ����ҳ�����˺���follower�Ĺ�ϵ�����ܻ����ע�ģ�
				for(UserFocus var:userFocusFollower){
					Map<String,Object> userMap = new HashMap<String,Object>();
					List<UserFocus> checkUserFocus = userService.checkUserFocus(user.getId(), var.getId());
					if(checkUserFocus.size() == 0){
						userMap.put("isFocus", "false");
					}else userMap.put("isFocus", "true");
					userMap.put("user", var.getUser());
					userFocusOrNot.add(userMap);
				}
			} else {
			 
				for(UserFocus var:userFocusFollower){
					Map<String,Object> userMap = new HashMap<String,Object>();
					//�������Ƿ� ��ҳ �� ��ע���������ע��
					List<UserFocus> checkUserFocus = userService.checkUserFocus(loginUser.getId(), var.getUser().getId());
					if(checkUserFocus.size() == 0){
						userMap.put("isFocus", "false");
					}else userMap.put("isFocus", "true");
					userMap.put("user", var.getUser());
					userFocusOrNot.add(userMap);
				}
			}
		}
		
	}
	
	//ajax----��֤��ʱ����ȷ(���õڶ��ַ���)
	public void isCheckcodeTrue(){
		//�õ���ȷ����֤��
		String checkcode1 =(String)ServletActionContext.getRequest().getSession().getAttribute("checkcode");  
		if(checkcode1 != null){
		String json2 = "";
		if(!checkcode1.toLowerCase().equals(checkcode.toLowerCase()) || !checkcode1.toUpperCase().endsWith(checkcode.toUpperCase()) ){  
	           json2 = "{\"sta\":0,\"msg\":\"����\"}";
	    } 
		else {
			  json2 = "{\"sta\":1,\"msg\":\"��ȷ\"}";
		}
		try {
			sendMsg(json2);
		} catch (IOException e) {
			 
			e.printStackTrace();
		}
		}
	}
	//ajax����У��----�û����Ƿ����
	public void isExist(){
		JSONObject jsonObj = new JSONObject();
		jsonObj = JSONObject.fromObject(json);
		String username = jsonObj.getString("username");
		if(userService.registValid(username).size() > 0){
			json = "{\"sta\":0,\"msg\":\"�Ѵ���\"}";
		}
		else json = "{\"sta\":1,\"msg\":\"��ȷ\"}";
		try {
			sendMsg(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	 public void sendMsg(String content) throws IOException{
	     HttpServletResponse response = ServletActionContext.getResponse();//��ȡresponse
	     response.setContentType("text/html;charset=UTF-8");//����
	     response.getWriter().write(content);
	   }

 
 
	//ȫ�����δ�Ѷ�
	public String changeNewsAllToRead(){
		User user = userService.getUserByName(ServletActionContext.getRequest().getSession().getAttribute("name").toString());
		list = newsService.getNewsAll(user.getId());
		for(int i = 0; i<list.size(); i++){
			News news2 = list.get(i);
			news2.setFlag(1);
			newsService.saveNews(news2); 
		}
		return SUCCESS;
	}
	
	//�鿴һ����Ϣ������
	public String getNewsDetail(){
		//���Ϊ�Ѷ�
		news2 = newsService.getNewsDetail(news.getId());
		news2.setFlag(1);
		newsService.saveNews(news2);
		//�ж���ʲô�ٷֱ�ת��
		if(news2.getType2() == 0){
			return "sportDetail";
		}
		else return "articleDetail";
	}

 
 
	//ע����ͨ�û�
	public String cancel(){
		ServletActionContext.getRequest().getSession().removeAttribute("name");
		return SUCCESS;
	}

    //ajax  ��¼
	public void loginValid(){
		
		JSONObject jsonObj = new JSONObject();
		jsonObj = JSONObject.fromObject(json);
		String username = jsonObj.getString("username");
		String password = jsonObj.getString("password");
		List<User> userList = userService.loginValid(username, password);
		if(userList.size() > 0){
		   if(userList.get(0).getManagerOrNot().equals("true")) {  //����Ա����
			   //��¼����Ա����εĵ���ʱ��
			   User admin = userList.get(0);
			   admin.setRegisterDate(new Date());
			   admin.setGreatCount(admin.getGreatCount() + 1);//������� +1
			   userService.saveUser(admin);
			   
			   ServletActionContext.getRequest().getSession().removeAttribute("admin");
			   ServletActionContext.getRequest().getSession().setAttribute("admin", username);
			   json = "{\"sta\":2}";
		   } else {
			   ServletActionContext.getRequest().getSession().removeAttribute("name");
			   ServletActionContext.getRequest().getSession().setAttribute("name", username);
			   ServletActionContext.getRequest().getSession().setAttribute("user_id",userList.get(0).getId());
			   ServletActionContext.getRequest().getSession().setAttribute("img",userList.get(0).getAvatar());
			   json = "{\"sta\":1}";	
			   
			  //���е�������ͳ��
			   adminAtatisticsAction.countAdd("login");
		   } 

		}else{
			json = "{\"sta\":0}";
			
		}
		
		try {
			sendMsg(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	 
	}

	//ע��
	public String regist() throws ParseException{
		 user.setRegisterDate(new Date());
		 user.setManagerOrNot("false");
		 user.setBackground("image/background.jpg");
		 user.setAnswerCount(0); user.setBlogCount(0);
		 user.setFollowerCount(0);user.setFollowingCount(0);
		 user.setQuestionCount(0);
		 user.setGreatCount(0); user.setIndividualSignature("����˺�����ʲô��û����");
		 user.setAvatar("image/1.jpg");
		 user.setIsForbidden("false");
		 //�༭�û�ϲ���ı�ǩ
		 String tag[] = tagName.split("/");
		 StringBuffer tagId = new StringBuffer();
		 for(int i = 0; i < tag.length; i++) {
			 tagId.append("/" + otherToTagService.getTagIdByName(tag[i]).get(0).getId());
		 }
		 user.setInterestTag(tagId.toString().substring(1));
		 
		 //String path=ServletActionContext.getServletContext().getRealPath("image");
		 //System.out.println("--------->"+path);
		 userService.saveUser(user);
		
		 // ���ڷǷ��˳�ʱ���session			
		ServletActionContext.getContext().getSession().remove("name");
		//����½�ɹ����û���Ϣд��session			
		//ServletActionContext.getRequest().getSession().setAttribute("name",user.getUsername());
		
		//����ע������ͳ��
		adminAtatisticsAction.countAdd("register");
		
		return SUCCESS;
	}
	
	//������Ϣ
	public String userDetail(){
		String name =(String) ServletActionContext.getRequest().getSession().getAttribute("name");
		if(name==null) return "unlogin";
		else{	
			user=new User();
			user=userService.getUserByName(name);
			return "success";			
		}				
		
	}
 
	//���¸�����Ϣ   //�ϴ�ͷxiang�ȵ�
	public String updateUser() throws IOException{
		 account = (String) ServletActionContext.getRequest().getSession().getAttribute("name");
		if(account == null || !account.equals(user.getUsername()))		//�ж��û��Ƿ��ѵ�¼��session�Ƿ��ѹ��ڻ��߷Ǳ��˲���
			return "fail";
		else {			
			if (photo != null   ) {
				String path=ServletActionContext.getServletContext().getRealPath("image/user");
				File file=new File(path);
				if (!file.exists()) {
					file.mkdir();
				}
				SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			    for (int i = 0; i < photo.length; i++) {
			    	//��Ҫ���� �������� ͼƬ���ϴ�
					 photoFileName[i] = df.format(new Date()) + i + photoFileName[i].substring(photoFileName[i].indexOf('.'));
					 if (i == 0 ) {
						 if (number.equals("one") || number.equals("all")) {
							 FileUtils.copyFile(photo[i], new File(file, photoFileName[i]));
							 user.setAvatar("image/user/" +photoFileName[i]);
							 ServletActionContext.getRequest().getSession().setAttribute("img", user.getAvatar());
						 } else {
							 FileUtils.copyFile(photo[i], new File(file, photoFileName[i]));
							 user.setBackground("image/user/"  +photoFileName[i]);
						 }
					 } else {
						 FileUtils.copyFile(photo[i], new File(file, photoFileName[i]));
						 user.setBackground("image/user/"  +photoFileName[i]);
					 }
				 }
			}
			//�����û���ϲ���ı�ǩ
			 //�༭�û�ϲ���ı�ǩ
			 String tag[] = tagName.split("/");
			 StringBuffer tagId = new StringBuffer();
			 for(int i = 0; i < tag.length; i++) {
				 tagId.append("/" + otherToTagService.getTagIdByName(tag[i]).get(0).getId());
			 }
			 user.setInterestTag(tagId.toString().substring(1));
			
			userService.saveUser(user);
			return "success";
		}	
	}
 
	//��֤�����
	public String checkImg() throws IOException{
		int width = 120;  
        int height = 30;  
  
        // ����һ ����һ���ڴ���ͼƬ  
        BufferedImage bufferedImage = new BufferedImage(width, height,  
                BufferedImage.TYPE_INT_RGB);  
  
        // ����� ͼƬ���Ʊ�����ɫ ---ͨ����ͼ����  
        Graphics graphics = bufferedImage.getGraphics();// �õ���ͼ���� --- ����  
        // �����κ�ͼ��֮ǰ ������ָ��һ����ɫ  
        graphics.setColor(getRandColor(200, 250));  
        graphics.fillRect(0, 0, width, height);  
  
        // ������ ���Ʊ߿�  
        graphics.setColor(Color.WHITE);  
        graphics.drawRect(0, 0, width - 1, height - 1);  
  
        // ������ �ĸ��������  
        Graphics2D graphics2d = (Graphics2D) graphics;  
        // �����������  
        graphics2d.setFont(new Font("����", Font.BOLD, 18));  
          
        String words ="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";  
       // String words ="1111";
        //String words = "\u7684\u4e00\u4e86\u662f\u6211\u4e0d\u5728\u4eba\u4eec\u6709\u6765\u4ed6\u8fd9\u4e0a\u7740\u4e2a\u5730\u5230\u5927\u91cc\u8bf4\u5c31\u53bb\u5b50\u5f97\u4e5f\u548c\u90a3\u8981\u4e0b\u770b\u5929\u65f6\u8fc7\u51fa\u5c0f\u4e48\u8d77\u4f60\u90fd\u628a\u597d\u8fd8\u591a\u6ca1\u4e3a\u53c8\u53ef\u5bb6\u5b66\u53ea\u4ee5\u4e3b\u4f1a\u6837\u5e74\u60f3\u751f\u540c\u8001\u4e2d\u5341\u4ece\u81ea\u9762\u524d\u5934\u9053\u5b83\u540e\u7136\u8d70\u5f88\u50cf\u89c1\u4e24\u7528\u5979\u56fd\u52a8\u8fdb\u6210\u56de\u4ec0\u8fb9\u4f5c\u5bf9\u5f00\u800c\u5df1\u4e9b\u73b0\u5c71\u6c11\u5019\u7ecf\u53d1\u5de5\u5411\u4e8b\u547d\u7ed9\u957f\u6c34\u51e0\u4e49\u4e09\u58f0\u4e8e\u9ad8\u624b\u77e5\u7406\u773c\u5fd7\u70b9\u5fc3\u6218\u4e8c\u95ee\u4f46\u8eab\u65b9\u5b9e\u5403\u505a\u53eb\u5f53\u4f4f\u542c\u9769\u6253\u5462\u771f\u5168\u624d\u56db\u5df2\u6240\u654c\u4e4b\u6700\u5149\u4ea7\u60c5\u8def\u5206\u603b\u6761\u767d\u8bdd\u4e1c\u5e2d\u6b21\u4eb2\u5982\u88ab\u82b1\u53e3\u653e\u513f\u5e38\u6c14\u4e94\u7b2c\u4f7f\u5199\u519b\u5427\u6587\u8fd0\u518d\u679c\u600e\u5b9a\u8bb8\u5feb\u660e\u884c\u56e0\u522b\u98de\u5916\u6811\u7269\u6d3b\u90e8\u95e8\u65e0\u5f80\u8239\u671b\u65b0\u5e26\u961f\u5148\u529b\u5b8c\u5374\u7ad9\u4ee3\u5458\u673a\u66f4\u4e5d\u60a8\u6bcf\u98ce\u7ea7\u8ddf\u7b11\u554a\u5b69\u4e07\u5c11\u76f4\u610f\u591c\u6bd4\u9636\u8fde\u8f66\u91cd\u4fbf\u6597\u9a6c\u54ea\u5316\u592a\u6307\u53d8\u793e\u4f3c\u58eb\u8005\u5e72\u77f3\u6ee1\u65e5\u51b3\u767e\u539f\u62ff\u7fa4\u7a76\u5404\u516d\u672c\u601d\u89e3\u7acb\u6cb3\u6751\u516b\u96be\u65e9\u8bba\u5417\u6839\u5171\u8ba9\u76f8\u7814\u4eca\u5176\u4e66\u5750\u63a5\u5e94\u5173\u4fe1\u89c9\u6b65\u53cd\u5904\u8bb0\u5c06\u5343\u627e\u4e89\u9886\u6216\u5e08\u7ed3\u5757\u8dd1\u8c01\u8349\u8d8a\u5b57\u52a0\u811a\u7d27\u7231\u7b49\u4e60\u9635\u6015\u6708\u9752\u534a\u706b\u6cd5\u9898\u5efa\u8d76\u4f4d\u5531\u6d77\u4e03\u5973\u4efb\u4ef6\u611f\u51c6\u5f20\u56e2\u5c4b\u79bb\u8272\u8138\u7247\u79d1\u5012\u775b\u5229\u4e16\u521a\u4e14\u7531\u9001\u5207\u661f\u5bfc\u665a\u8868\u591f\u6574\u8ba4\u54cd\u96ea\u6d41\u672a\u573a\u8be5\u5e76\u5e95\u6df1\u523b\u5e73\u4f1f\u5fd9\u63d0\u786e\u8fd1\u4eae\u8f7b\u8bb2\u519c\u53e4\u9ed1\u544a\u754c\u62c9\u540d\u5440\u571f\u6e05\u9633\u7167\u529e\u53f2\u6539\u5386\u8f6c\u753b\u9020\u5634\u6b64\u6cbb\u5317\u5fc5\u670d\u96e8\u7a7f\u5185\u8bc6\u9a8c\u4f20\u4e1a\u83dc\u722c\u7761\u5174\u5f62\u91cf\u54b1\u89c2\u82e6\u4f53\u4f17\u901a\u51b2\u5408\u7834\u53cb\u5ea6\u672f\u996d\u516c\u65c1\u623f\u6781\u5357\u67aa\u8bfb\u6c99\u5c81\u7ebf\u91ce\u575a\u7a7a\u6536\u7b97\u81f3\u653f\u57ce\u52b3\u843d\u94b1\u7279\u56f4\u5f1f\u80dc\u6559\u70ed\u5c55\u5305\u6b4c\u7c7b\u6e10\u5f3a\u6570\u4e61\u547c\u6027\u97f3\u7b54\u54e5\u9645\u65e7\u795e\u5ea7\u7ae0\u5e2e\u5566\u53d7\u7cfb\u4ee4\u8df3\u975e\u4f55\u725b\u53d6\u5165\u5cb8\u6562\u6389\u5ffd\u79cd\u88c5\u9876\u6025\u6797\u505c\u606f\u53e5\u533a\u8863\u822c\u62a5\u53f6\u538b\u6162\u53d4\u80cc\u7ec6";  
        Random random = new Random();// ���������  
        // ����StringBuffer   
        StringBuffer sb = new StringBuffer();  
        // ����x����  
        int x = 10;  
        for (int i = 0; i < 4; i++) {  
            // �����ɫ  
            graphics2d.setColor(new Color(20 + random.nextInt(110), 20 + random  
                    .nextInt(110), 20 + random.nextInt(110)));  
            // ��ת -30 --- 30��  
            int jiaodu = random.nextInt(60) - 30;  
            // ���㻡��  
            double theta = jiaodu * Math.PI / 180;  
  
            // ����һ���������  
            int index = random.nextInt(words.length()); // ��������� 0 �� length - 1  
            // �����ĸ����  
            char c = words.charAt(index);  
            sb.append(c);  
            // ��c �����ͼƬ  
            graphics2d.rotate(theta, x, 20);  
            graphics2d.drawString(String.valueOf(c), x, 20);  
            graphics2d.rotate(-theta, x, 20);  
            x += 30;  
        }  
  
        // �����ɵ���ĸ���뵽session��  
        ServletActionContext.getRequest().getSession().setAttribute("checkcode", sb.toString());  
  
        // ������ ���Ƹ�����  
        graphics.setColor(getRandColor(160, 200));  
        int x1;  
        int x2;  
        int y1;  
        int y2;  
        for (int i = 0; i < 30; i++) {  
            x1 = random.nextInt(width);  
            x2 = random.nextInt(12);  
            y1 = random.nextInt(height);  
            y2 = random.nextInt(12);  
            graphics.drawLine(x1, y1, x1 + x2, x2 + y2);  
        }  
  
        // ������ͼƬ���������� ImageIO  
        graphics.dispose();// �ͷ���Դ  
        ImageIO.write(bufferedImage, "jpg", ServletActionContext.getResponse().getOutputStream());  
        return NONE;  
    }  
	
	/** 
     * ȡ��ĳһ��Χ��color 
     */  
    private Color getRandColor(int fc, int bc) {  
        // ȡ�������ɫ  
        Random random = new Random();  
        if (fc > 255) {  
            fc = 255;  
        }  
        if (bc > 255) {  
            bc = 255;  
        }  
        int r = fc + random.nextInt(bc - fc);  
        int g = fc + random.nextInt(bc - fc);  
        int b = fc + random.nextInt(bc - fc);  
        return new Color(r, g, b);  
    }  
 
	
	public String getCheckcode() {
		return checkcode;
	}
	public void setCheckcode(String checkcode) {
		this.checkcode = checkcode;
	}
	public String getOldpwd() {
		return oldpwd;
	}


	public void setOldpwd(String oldpwd) {
		this.oldpwd = oldpwd;
	}


	public String getNewpwd() {
		return newpwd;
	}


	public void setNewpwd(String newpwd) {
		this.newpwd = newpwd;
	}


	public String getCfmnewpwd() {
		return cfmnewpwd;
	}


	public void setCfmnewpwd(String cfmnewpwd) {
		this.cfmnewpwd = cfmnewpwd;
	}
 
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public File[] getPhoto() {
		return photo;
	}

	public void setPhoto(File[] photo) {
		this.photo = photo;
	}

	public String[] getPhotoFileName() {
		return photoFileName;
	}

	public void setPhotoFileName(String[] photoFileName) {
		this.photoFileName = photoFileName;
	}

	public String[] getPhotoContentType() {
		return photoContentType;
	}

	public void setPhotoContentType(String[] photoContentType) {
		this.photoContentType = photoContentType;
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


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getPhone() {
		return phone;
	}


	public void setPhone(String phone) {
		this.phone = phone;
	}


	public String getAvatar() {
		return avatar;
	}


	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public String getRegisterDate() {
		return registerDate;
	}


	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}


	public String getManagerOrNot() {
		return managerOrNot;
	}


	public void setManagerOrNot(String managerOrNot) {
		this.managerOrNot = managerOrNot;
	}
	
	public UserService getUserService() {
		return userService;
	}


	public void setUserService(UserService userService) {
		this.userService = userService;
	}


	public User getUser() {
		return user;
	}


	public void setUser(User user) {
		this.user = user;
	}

	public String getBackUrl() {
		return backUrl;
	}

	public void setBackUrl(String backUrl) {
		this.backUrl = backUrl;
	}


	public List<News> getList() {
		return list;
	}


	public void setList(List<News> list) {
		this.list = list;
	}

	public News getNews() {
		return news;
	}

	public void setNews(News news) {
		this.news = news;
	}

	public News getNews2() {
		return news2;
	}

	public void setNews2(News news2) {
		this.news2 = news2;
	}
	public String getJson() {
		return json;
	}
	public void setJson(String json) {
		this.json = json;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<UserUpdate> getUserUpdateList() {
		return userUpdateList;
	}

	public void setUserUpdateList(List<UserUpdate> userUpdateList) {
		this.userUpdateList = userUpdateList;
	}

	public String getUserUpdateName() {
		return userUpdateName;
	}

	public void setUserUpdateName(String userUpdateName) {
		this.userUpdateName = userUpdateName;
	}

	public String getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(String isOwner) {
		this.isOwner = isOwner;
	}

	public List<Object> getUserFocusOrNot() {
		return userFocusOrNot;
	}

	public void setUserFocusOrNot(List<Object> userFocusOrNot) {
		this.userFocusOrNot = userFocusOrNot;
	}

	public String getIsFocus() {
		return isFocus;
	}

	public void setIsFocus(String isFocus) {
		this.isFocus = isFocus;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}


	public Integer getStartRow() {
		return startRow;
	}


	public void setStartRow(Integer startRow) {
		this.startRow = startRow;
	}


	public Integer getSize() {
		return size;
	}


	public void setSize(Integer size) {
		this.size = size;
	}


	public boolean isEnd() {
		return isEnd;
	}

	public void setEnd(boolean isEnd) {
		this.isEnd = isEnd;
	}


	public Integer getStartPage() {
		return startPage;
	}


	public void setStartPage(Integer startPage) {
		this.startPage = startPage;
	}


	public Integer getEndPage() {
		return endPage;
	}


	public void setEndPage(Integer endPage) {
		this.endPage = endPage;
	}


	public Integer getTotalPage() {
		return totalPage;
	}


	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}


	public Integer getPageSize() {
		return pageSize;
	}


	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}


	public Integer getCurrentPage() {
		return currentPage;
	}


	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}


	public List<Sport> getRecommendSport() {
		return recommendSport;
	}


	public void setRecommendSport(List<Sport> recommendSport) {
		this.recommendSport = recommendSport;
	}


	public List<UserArticle> getRecommendArticle() {
		return recommendArticle;
	}


	public void setRecommendArticle(List<UserArticle> recommendArticle) {
		this.recommendArticle = recommendArticle;
	}


	public List<User> getRecommendUser() {
		return recommendUser;
	}


	public void setRecommendUser(List<User> recommendUser) {
		this.recommendUser = recommendUser;
	}
 
 
 

	 
	
	
	
   
	
}

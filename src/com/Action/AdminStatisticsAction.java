package com.Action;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.Algorithm.CRList;
import com.Entity.Answer;
import com.Entity.Category1;
import com.Entity.Category2;
import com.Entity.OtherToTag;
import com.Entity.PageView;
import com.Entity.Question;
import com.Entity.QuestionFocus;
import com.Entity.SpecialScheme;
import com.Entity.SpecialSchemeUser;
import com.Entity.Sport;
import com.Entity.SportComment;
import com.Entity.SportReply;
import com.Entity.SportWantTo;
import com.Entity.Tag;
import com.Entity.User;
import com.Entity.UserArticle;
import com.Entity.UserArticleComment;
import com.Entity.UserArticleReply;
import com.Entity.UserUpdate;
import com.Service.AdminStatisticsService;
import com.Service.ArticleGreatService;
import com.Service.ArticleService;
import com.Service.OtherToTagService;
import com.Service.QuestionFocusService;
import com.Service.SportService;
import com.Service.SportWantToService;
import com.Service.TalkService;
import com.Service.UserService;
import com.opensymphony.xwork2.ActionSupport;
 


@SuppressWarnings("serial")
@Controller("adminStatisticsAction")
@Scope("prototype")
public class AdminStatisticsAction extends ActionSupport {
	@Resource
	private AdminStatisticsService adminStatisticsService;
	@Resource
	private SportService sportService;
	@Resource
	public UserService userService;
	@Resource
	private OtherToTagService otherToTagService;
	@Resource
	private SportWantToService sportWantToService; 
	@Resource
	private ArticleService articleService;
	@Resource
	private ArticleGreatService  articleGreatService;
	@Resource
	private TalkService talkService;
	@Resource
	private QuestionFocusService quesionFocusService;
	
	
	private String json;//index ҳ���޸� ����Ϣ���ݹ���
	private User user;
	
	private List<Sport> sportList;//admin  �鿴���¼���
	private Integer totalPage;//���¼��ͷ�ҳ
	private Integer currentPage;
	private Integer pageSize;
	
	private Integer totalPage2;//��������jsp �д���˵��б��ҳ
	private Integer currentPage2; 
	
	private Integer modalPageSize;//����ģ̬�� �ķ�ҳ
	private Integer modalCurrentPage;
	private Integer modalTotalPage;
	private  List<CRList> crlist;
	
	private Sport sport;
	private String sportCategory1;//�㽭
	private String sportCategory2;//ɽ��ˮ��
	
	private String sportTime;//����sport���޸ĵ�ʱ�� ���� ʱ��
	private String sportCustomTag;//admin�Զ���ı�ǩ�ǵĴ��ݣ�tagid-->tagname  1/2/3 --> ����/����/��ã�
	
	private List<UserArticle> examinationLoading;//����˵�
	private List<UserArticle> examinationAdopt;//ͨ����˵�����
	
	private List<Question> questionList;
	private List<Object> answerList;//admin �鿴question��answer �б�
	
	private List<User> userList;//admin manage-User  
	
	private SpecialScheme specialScheme;//����ر�߻�
	private List<SpecialScheme> specialSchemeList;
	private SpecialSchemeUser specialSchemeUser;//�û������
	
	
	
	
	//�û�����
	public String saveSpecialSchemeUser() {
		specialScheme = adminStatisticsService.getSpecialSchemeDetail(specialScheme.getId());
		specialScheme.setUserCount(specialScheme.getUserCount() + 1);
		adminStatisticsService.saveSpecialScheme(specialScheme);
		
		specialSchemeUser.setSchemeId(specialScheme);
		adminStatisticsService.saveSpecialSchemeUser(specialSchemeUser);
		
		return SUCCESS;
	}
	
	//�õ������
	public String getSpecialSchemeDetail() {
		specialScheme = adminStatisticsService.getSpecialSchemeDetail(specialScheme.getId());
		return SUCCESS;
	}
	
	//admin �½�����   ����  �޸ı���   �ر�߻�
	public String saveScheme() {
 
		if(specialScheme.getUserCount() == null) {
			specialScheme.setUserCount(0);
			specialScheme.setTime(new Date());
		}
			
		adminStatisticsService.saveSpecialScheme(specialScheme);
		return SUCCESS;
	}
	
	// �õ����еĻ
	public String getSpecialSchemeAll() {
		List<SpecialScheme> tempList = adminStatisticsService.getSchenmeListAll();
		pageSize = 6;
		if(currentPage == null) currentPage = 1;
		totalPage = (tempList.size() % pageSize == 0) ? (tempList.size() / pageSize) : ((tempList.size() / pageSize) + 1);
		if(totalPage == 0) totalPage = 1;
		int startRow = (currentPage - 1) * pageSize;
		
		specialSchemeList = new  LinkedList<SpecialScheme>(); 
		for	(int i = startRow, j = 0; i < tempList.size() && j < pageSize; j++, i++) {
			specialSchemeList.add(tempList.get(i));
		}
		
		return SUCCESS;
	}
	
	//admin �˳�����
	public String adminLoginOut() {
		ServletActionContext.getRequest().getSession().removeAttribute("admin");
		return SUCCESS;
	}
	
	//admin Forbidden usser
	public void adminForbiddenUser() {
		JSONObject jo =new JSONObject();
		jo = JSONObject.fromObject(json);
		String method = jo.getString("method");
		JSONArray ja = jo.getJSONArray("userId");
		 for(int i = 0 ; i < ja.size(); i++) {
			 User user = userService.getUserById(ja.getInt(i));
			 if(method.equals("disable"))
			 	user.setIsForbidden("true");
			 else user.setIsForbidden("false");
			 userService.saveUser(user);
		 }
	}
	
	//admin �����û�
	public String adminGetUserAll() {
		List<User>  tempList = adminStatisticsService.adminGetUserAll();
		
		 pageSize = 10;
		 if(currentPage == null) currentPage = 1; 
		 int startRow = (currentPage - 1) * pageSize;
		 totalPage = (tempList.size() % pageSize == 0) ? (tempList.size() / pageSize) : ((tempList.size() / pageSize) + 1);
		 
		 userList = new LinkedList<User>();
		 for(int i = startRow, j = 0; i < tempList.size() && j < pageSize; i++, j++) {
			 userList.add(tempList.get(i));
		 }
		 
		 return SUCCESS;
	}
	
	
	//admin ɾ��һ��question �µ�����
	public void adminDeleteQuestionAnswer() {
		JSONArray ja = new JSONArray();
		ja = JSONArray.fromObject(json);
		for(int i = 0; i < ja.size(); i++) {
			JSONObject jo = new JSONObject();
			jo = ja.getJSONObject(i);
			Integer id =  jo.getInt("id");   //admin ����ɾ������һ��  Ҳ�����Ƕ���  ͨ��rank �ж�
			Integer flag = jo.getInt("flag");
			if(flag == 0) {
				//��Ϊ�п����ж�������
				Answer answer = talkService.getOneAnswerDetailById(id);
				answer.setContent("�ûش��ѱ��۵�");
				talkService.saveAnswer(answer);
			}else {
				//����id ɾ����������
				talkService.deleteAnswerById(id);
			}
		}
	}
	
	//admin �鿴һ��question�µ����еĻش�
	public void getAnswerAll() {
		JSONObject jo = new JSONObject();
		jo = JSONObject.fromObject(json);
		Integer questionId = jo.getInt("questionId");
		
		answerList = new LinkedList<Object>();
		  
		List<Answer> templateAll = talkService.getAnswerAllToOneQuestion(questionId);//�õ���һ���Ͷ������лظ�
		List<Answer> fatherList = new LinkedList<Answer>();
		List<Answer> sonList = new LinkedList<Answer>();
		
		for(Answer var:templateAll) {
			if(var.getFlag() == 0) fatherList.add(var);
			else sonList.add(var);
		}
		
		//ģ̬��  ��ҳ
		modalPageSize = 10; //ÿҳչʾ������������
		modalCurrentPage = jo.getInt("modalCurrentPage");
		//����ķ�ҳ�����ж�����������Ϊ������
		modalTotalPage = (fatherList.size() % modalPageSize == 0) ? (fatherList.size() / modalPageSize) : ((fatherList.size() / modalPageSize) + 1);
		int startRow = (modalCurrentPage - 1) * modalPageSize;
		
		List<Answer> templateAll2 = new LinkedList<Answer>();
		for(int i = startRow, j =0; j < modalPageSize && i < fatherList.size(); i++, j++) {
			templateAll2.add(fatherList.get(i));
		 }
 
		for(Answer var:templateAll2){	// ����һҳ��Ҫսʿ��fatherAnswer����tempalteAll2)  �ҵ���sonReply
			//���ҳ�һ������--�ṹ-->(list(map(father,son(list))))
			
				Map<String, Object> map = new LinkedHashMap<String, Object>();
				map.put("father", var);
				//��Ŷ�������
				List<Answer> sonAnswer = new LinkedList<Answer>();   
				for(Answer var2 : sonList){      // templateAll2  �����е���������һ�������۵ģ� ����
					if(var2.getFlag() == 1 && var2.getFather_id() == var.getId()){
						sonAnswer.add(var2);
					}
				}
				map.put("son", sonAnswer);
				
				answerList.add(map);
			 
		}
 
		JSONArray ja = new JSONArray();
		ja = JSONArray.fromObject(answerList);
		
		JSONObject jo2  = new JSONObject();
		jo2.put("modalCurrentPage", modalCurrentPage);
		jo2.put( "modalTotalPage", modalTotalPage);
		jo2.put("answerlist", ja);
		
		try {
			sendMsg(jo2.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//admin ɾ�� ���µ�����
	public void adminDeleteArticleComment() {
		JSONArray ja = new JSONArray();
		ja = JSONArray.fromObject(json);
		for(int i = 0; i < ja.size(); i++) {
			JSONObject jo = new JSONObject();
			jo = ja.getJSONObject(i);
			Integer id =  jo.getInt("id");   //admin ����ɾ������һ��  Ҳ�����Ƕ���  ͨ��rank �ж�
			Integer rank = jo.getInt("rank");
			if(rank == 0) {
				//userService.deleteArticleCommentById(id);
				UserArticleComment ac = articleService.getUserArticleCommentById(id);
				ac.setContent("�������ѱ��۵�");
				articleService.saveUserArticleComment(ac);
			}else {
				//�� reply ����ɾ��
				userService.deleteArticleReplyById(id);
			}
		}
	}
	
	//admin  �õ����µ�����
	public void getArticleCommentAll() {
		JSONObject jo = new JSONObject();
		jo = JSONObject.fromObject(json);
		Integer articleId = jo.getInt("articleId");
		
		//�õ�������µ�����
		List<UserArticleComment> comment = articleService.getUserArticleComment(articleId);
		List<UserArticleReply> reply = articleService.getUserArticleReply(articleId);
		//�������ݽṹ����
		crlist=new CRList().getCRArticleList(comment, reply);
		//ģ̬��ķ�ҳ
		modalPagination(jo);
		
	}
	
	public void modalPagination(JSONObject jo) {
		//ģ̬��ķ�ҳ
		 modalPageSize = 6;
		 modalCurrentPage = jo.getInt("modalCurrentPage");
		 modalTotalPage = (crlist.size() % modalPageSize == 0) ? (crlist.size() / modalPageSize) : ((crlist.size() / modalPageSize) + 1);
		 int startRow = (modalCurrentPage - 1) * modalPageSize;
		 List<CRList> tempCRList = new LinkedList<CRList>();
		 for(int i = startRow, j =0; j < modalPageSize && i < crlist.size(); i++, j++) {
			 tempCRList.add(crlist.get(i));
		 }
		 
		 JSONArray ja = new JSONArray();
		 ja = JSONArray.fromObject(tempCRList);
		 
		 JSONObject jo2 = new JSONObject();
		 jo2.put("crList", ja);
		 jo2.put("modalCurrentPage", modalCurrentPage);
		 jo2.put("modalTotalPage", modalTotalPage);
		 
		 try {
			sendMsg(jo2.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//admin ɾ��sport ��ѡ�е�����
	public void adminDeleteComment() {
		JSONArray ja = new JSONArray();
		ja = JSONArray.fromObject(json);
		for(int i = 0; i < ja.size(); i++) {
			JSONObject jo = new JSONObject();
			jo = ja.getJSONObject(i);
			Integer id =  jo.getInt("id");   //admin ����ɾ������һ��  Ҳ�����Ƕ���  ͨ��rank �ж�
			Integer rank = jo.getInt("rank");
			if(rank == 0) {
				SportComment sc = sportService.getSportCommentById(id);
				sc.setContent("�������ѱ��۵�");
				sportService.saveSportComment(sc);
			}else {
				//�� reply ����ɾ��
				sportService.deleteSportReply(id);
			}
		}
	}
	
	//admin �õ�sport�����е�����
	public void getSportCommentAll() {
		JSONObject jo = new JSONObject();
		jo = JSONObject.fromObject(json);
		Integer sportId = jo.getInt("sportId");
		 
		//�õ���ؾ�������ۺͻظ�
		 List<SportComment> comment = sportService.getSportComment(sportId);
		 List<SportReply> reply = sportService.getSportReply(sportId);
		//�������ݽṹ��������
		 crlist = new CRList().getCRList(comment, reply);
		 
		 modalPagination(jo);
	}
	
	// admin ɾ��һ������(ɾ�� -�û��Ĺ�ע--�û��Ļش�--�û����ʵĶ�̬--���ɾ���������)
	public void adminDeleteQuestion() {
		JSONArray ja = new JSONArray();
		ja = JSONArray.fromObject(json);
		for(int i = 0; i < ja.size(); i++) {
			//�õ����е�����--�û�  ��ע��ϵ   delete
			List<QuestionFocus> questionFocusList = quesionFocusService.getOneQuestionFocusAll(ja.getInt(i));
			for(QuestionFocus var : questionFocusList) {
				quesionFocusService.deleteQueestionFocusById(var.getId());
			}
			//�õ��������µ����еĻش�  delete
			List<Answer> answerList = talkService.getAnswerAllToOneQuestion(ja.getInt(i));
			for(Answer var : answerList) {
				talkService.deleteAnswerById(var.getId());
			}
			//ɾ������
			talkService.deleteQuestion(ja.getInt(i));
			//ɾ���û���̬���ش�������,��ע������,�ȵȣ�
			userService.adminDeleteUpdate("���������", ja.getInt(i));
			userService.adminDeleteUpdate("��ע������", ja.getInt(i));
			userService.adminDeleteUpdate("�ش�������", ja.getInt(i));
			userService.adminDeleteUpdate("�����˻ش�", ja.getInt(i));
			//ɾ��otherTotag
			otherToTagService.delteArticleToTag(ja.getInt(i), "question");
		}
	}
	
	// admin  �鿴���������
	public void adminGetQuestionDetail() {
		JSONObject jo = new JSONObject();
		jo = JSONObject.fromObject(json);
		Integer questionId = jo.getInt("questionId");
		Question question = talkService.getQuestionDetailById(questionId);
		jo.put("question", question);
		try {
			sendMsg(jo.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//admin �õ�talk �Ĳ�ͬ����
	public String adminGetQuestionAll() {
	    pageSize = 10;
	    if(currentPage == null) currentPage = 1; 
		int startRow = (currentPage - 1) * pageSize;
		
		List<Question> tempList = talkService.getQuestionAllByTime();
		totalPage = (tempList.size() % pageSize == 0)?(tempList.size() / pageSize):((tempList.size() / pageSize) + 1);
		
		questionList = new LinkedList<Question>();
		for(int i = startRow, j = 0; j < pageSize && i < tempList.size(); j++, i++) {
			questionList.add(tempList.get(i));
		}
 
		return SUCCESS;	
	}
	
	
	//������õ�article-detail
	public void adminGetArticleDetail() {
		JSONObject jo = new JSONObject();
		jo = JSONObject.fromObject(json);
		Integer articleId = jo.getInt("articleId");
		UserArticle ua = articleService.getArticleDetail(articleId);
		JSONObject json2 = new JSONObject();
		json2.put("article", ua);
		try {
			sendMsg(json2.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	//admin ͨ�����
	public void adminAdoptArticle() {
		JSONArray ja = new JSONArray();
		ja = JSONArray.fromObject(json);
		for (int i = 0; i < ja.size(); i++) {
			UserArticle userArticle = articleService.getArticleDetail(ja.getInt(i));
			userArticle.setIsExamination("true");
			userService.saveUserArticle(userArticle);
			
			//����û��Ķ�̬��Ϣ
			/*String content = userArticle.getContent();
			if(content.length() > 550){
				content = content.substring(0,550);
			}*/
			//δ����  ���޸� ��̬   ---��ʱͨ��������ж��Ƿ��ǵ�һ�η��� �����޸� ������˶�̬��-----------------------------------------------------------
			if (userArticle.getGreatCount() == 1) {
				user = userService.getUserById(userArticle.getUser().getId());
				/*userService.saveUserUpdate(new UserUpdate("�������μ�", userArticle.getId(),userArticle.getTitle(), 
						new EliminateHtml().RemoveHtmlTag(content), user, null, user, userArticle.getTime()
				 ));*/
				userService.saveUserUpdate(new UserUpdate("�������μ�", userArticle.getId(),userArticle.getTitle(), 
						userArticle.getContent(), user, null, user, userArticle.getTime()
				 ));
			}
			
		}
	}
	
	//admin ɾ������
	public void adminDeleteArticle() {
		JSONArray ja = new JSONArray();
		ja = JSONArray.fromObject(json);
		 for (int i = 0; i < ja.size(); i++) {
			//ɾ�����µ����е�����
			userService.deleteArticleComment(ja.getInt(i));
			userService.deleteArticleReply(ja.getInt(i));
	 
			//ɾ����̬(����������  ������ �������μ� ��ֹһ���˵Ķ�̬)
			//UserArticle userArticle = articleService.getArticleDetail(ja.getInt(i));
			userService.adminDeleteUpdate("�������μ�", ja.getInt(i));
			userService.adminDeleteUpdate("�������μ�", ja.getInt(i));
			 
			//ɾ��OtherToTag
			otherToTagService.delteArticleToTag(ja.getInt(i), "article");
			//ɾ��������Ϣ
			articleGreatService.deleteArticleToGreat(ja.getInt(i));
			//ɾ������
			userService.deletUserArticle(ja.getInt(i));
		} 
	}
	
	//admin����   article �õ����¼���  (�Ѿ���˵ĺ�û����˵�)
	public String adminGetArticleAll() {
		pageSize = 10;
		
		List<UserArticle> examinationLoadingTemp = articleService.getExaminationLoading();
		if(currentPage2 == null) currentPage2 = 1; 
		int startRow2 = (currentPage2 - 1) * pageSize;
		totalPage2 = (examinationLoadingTemp.size() % pageSize == 0)?(examinationLoadingTemp.size() / pageSize):((examinationLoadingTemp.size() / pageSize) + 1);
		examinationLoading = new LinkedList<UserArticle>();
		for(int i = startRow2, j = 0; j < pageSize && i < examinationLoadingTemp.size(); j++, i++) {
			examinationLoading.add(examinationLoadingTemp.get(i));
		}
		
		List<UserArticle> examinationAdoptTemp = articleService.getExaminationAdopt();
		if(currentPage == null) currentPage = 1;
		int startRow = (currentPage - 1) * pageSize;
		totalPage = (examinationAdoptTemp.size() % pageSize == 0)?(examinationAdoptTemp.size() / pageSize):((examinationAdoptTemp.size() / pageSize) + 1);
		examinationAdopt = new LinkedList<UserArticle>();
		for(int i = startRow, j = 0; j < pageSize && i < examinationAdoptTemp.size(); j++, i++) {
			examinationAdopt.add(examinationAdoptTemp.get(i));
		}

		return SUCCESS;
	}
 
	
	//admin ��sport��ɾ��
	public void adminDeleteSport() {
		JSONArray ja = new JSONArray();
		ja = JSONArray.fromObject(json);
		//ɾ��comment  reply  sportWantTo otherToTag
		 for(int i = 0 ; i < ja.size(); i++) {
			//ɾ����������
			List<SportComment> sportCList = sportService.getSportComment(ja.getInt(i));
			List<SportReply> sportRList = sportService.getSportReply(ja.getInt(i));
			for(SportComment var : sportCList) {
				sportService.deleteSportComment(var.getId());
			}
			for(SportReply var : sportRList) {
				sportService.deleteSportReply(var.getId());
			}
			//ɾ������
			List<SportWantTo> sportWList = sportWantToService.getOneSportAll(ja.getInt(i));
			for(SportWantTo var : sportWList) {
				sportWantToService.deleteSportWantTo(var.getId());
			}
			//ɾ��tag��sport�Ĺ�ϵ
			List<OtherToTag> sportToTagList = otherToTagService.getOtherToTagByOtherAll(ja.getInt(i), "sport");
			for(OtherToTag var : sportToTagList) {
				otherToTagService.deleteOtherToTag(var);
			}
			//ɾ������
			sportService.deleteSport(ja.getInt(i));
			//ɾ���û���̬
			userService.adminDeleteUpdate("�����˾���", ja.getInt(i));
		} 
		
	}
	
	//admin  ��sport �����޸�
	public String adminGetSportDetail() {
		sport = sportService.getSportDetail(sport.getId());
		
		SimpleDateFormat myfrt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		sportTime = myfrt.format(sport.getTime());
		
		StringBuffer tagName = new StringBuffer("");
		String []str = sport.getCustomTag().split("/");
		for(int i = 0; i < str.length; i++) {
			Tag tag = otherToTagService.getTagById(Integer.parseInt(str[i]));
			tagName.append("/" + tag.getTag());
		}
		sportCustomTag = tagName.substring(1);
		
		return SUCCESS;
	}
	
	//admin����µ� sport
	public String saveUpdateSport() throws ParseException {
		Category1  category1 = new Category1(sportService.getCategory1Id(sportCategory1).getId());
		Category2  category2 = new Category2(sportService.getCategory2Id(sportCategory2).getId());
		sport.setCategory1(category1);
		sport.setCategory2(category2);
 	
		if(sport.getId() == null) {
			sport.setTime(new Date());
			sport.setCommentCount(0);
			sport.setWantCount(0);
			sport.setScore(0.0);
			sport.setPageViewCount(0);
		}else {  //˵�����޸� 
			SimpleDateFormat myFrt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			 sport.setTime(myFrt.parse(sportTime));
		}
		
		
		//�����Զ���ı�ǩ
		
		String []str = sport.getCustomTag().trim().replaceAll("\\s+", "").split("/");
		List<Tag> taglist = new LinkedList<Tag>(); //���sport��tag ���ں�����otherToTag�Ļ����ϵ�ı���
		StringBuffer tag2 = new StringBuffer();
		
		Set<String> tempTagName = new HashSet<String>();
		
		for(int i = 0; i < str.length; i++) {
			if(str[i].length() == 0) continue;
			if(tempTagName.contains(str[i])) continue;
			tempTagName.add(str[i]);
			List<Tag> tempTag = new LinkedList<Tag>(); 
			tempTag = otherToTagService.getTagIdByName(str[i]); //���ñ�ǩ�Ƿ����
			if (tempTag.size() == 0) {
				Tag tag = new Tag(str[i], 0);
				otherToTagService.saveTag(tag);//����һ������û���µ�tag
				tempTag.add(tag);//������뵽taglist�У�taglist���ڱ����ϵ��
			} else {
				//�Ա�ǩ�����ų̶�+1�����ţ�
				tempTag.get(0).setTagCount(tempTag.get(0).getTagCount() + 1);
				otherToTagService.saveTag(tempTag.get(0));
			}
			taglist.add(tempTag.get(0));
			tag2.append("/" + tempTag.get(0).getId());
		}
		sport.setCustomTag(tag2.substring(1));
		
		//�ж��ǲ����޸�  ���ǵ�һ�α���
		boolean isUpdate = (sport.getId() == null) ? false : true;
		
		sportService.saveSport(sport);
		
		//����tag he sport de ����   ����Ҫ���ù�ϵ�Ƿ������  ��Ϊ�� �޸��������  �����¿������޸ģ�
		//������޸ĵĻ�  ��Ҫ�鿴sport-tag ��ϵ�ǲ�����Ҫɾ��  ��������
		if(isUpdate) {
			List<OtherToTag> tagRelationAll ;
			//�õ��������еĹ�ϵ
			tagRelationAll = otherToTagService.getOtherToTagByOtherAll(sport.getId(), "sport");
			for(OtherToTag var1 : tagRelationAll){
				int flag = 0;
				for(Tag  var2 : taglist){//taglist  �����ڵ�sport�ı�ǩ
					if(var1.getTag().getTag().equals(var2.getTag())){ //˵����ϵ���豣��
						flag = 1; break;
					}
				}
				if(flag == 0){//�����ϵ��Ҫɾ��(�����У����¹�����)
					otherToTagService.deleteOtherToTag(var1);
				}
			}
			for(Tag var2 : taglist){
				int flag = 0;
				for(OtherToTag var1 : tagRelationAll){
					if(var1.getTag().getTag().equals(var2.getTag())){ //˵����ϵ�Ѿ�����
						flag = 1; break;
					}
				}
				if(flag == 0){ //��Ҫ�¼������ϵ(������ ��������Ҫ����)
					otherToTagService.saveOtherToTag(new OtherToTag(sport.getId(), var2, "sport"));
				}
			}
		}else { //ֱ�ӱ����ϵ�Ϳ���
			Set<String> temp = new HashSet<String>();
			for (Tag var2 : taglist) {
				if (temp.contains(var2.getTag())) continue;
				temp.add(var2.getTag());
				otherToTagService.saveOtherToTag(new OtherToTag(sport.getId(), var2, "sport"));
			}
		}
	  
		return SUCCESS;
	}
	
	
	//admin �õ����е���� ��Ϣչʾ�б�
	public String adminGetSportAll() {
		pageSize = 10;
		if(currentPage == null) currentPage = 1; 
		int startRow = (currentPage - 1) * pageSize;
		
		List<Sport> tempList = sportService.getSportAllByTime();
		totalPage = (tempList.size() % pageSize == 0)?(tempList.size() / pageSize):((tempList.size() / pageSize) + 1);
		
		sportList = new LinkedList<Sport>();
		for(int i = startRow, j = 0; j < pageSize && i < tempList.size(); j++, i++) {
			sportList.add(tempList.get(i));
		}
 
		return SUCCESS;
	}
	
	
	//�õ�������ռ�ȱ���
	public void getPageViewPie() {
		Double countAll = 0.0;
		List<Sport> sportList = sportService.getSportAllOrderBySocre();
		for(Sport var : sportList) {
			countAll +=var.getScore();
		}

		Double category[] = {0.0,0.0,0.0,0.0,0.0};//�������ռ����    
		for(Sport var : sportList) {
			if(var.getCategory2().getName().equals("�������")) {
				category[0] += var.getScore();
			}else if(var.getCategory2().getName().equals("������ʷ")) {
				category[1] += var.getScore();
			}else if(var.getCategory2().getName().equals("��԰����")) {
				category[2] += var.getScore();
			}else if(var.getCategory2().getName().equals("������̬")) {
				category[3] += var.getScore();
			}else if(var.getCategory2().getName().equals("ɽ��ˮ��")) {
				//category[4] += var.getScore();
				category[4] = 100.0;
			}
		}
		JSONArray ja = new JSONArray();
		//������ �洢ajax
		for(int i = 0; i < 5; i++) {
			JSONObject jo = new JSONObject();
			if(i == 0) {
				jo.put("name", "�������");
			}else if(i == 1) {
				jo.put("name", "������ʷ");
			}else if(i == 2) {
				jo.put("name", "��԰����");
			}else if(i == 3) {
				jo.put("name", "������̬");
			}else if(i == 4) {
			}
			jo.put("value", category[i]);
			ja.add(jo);
		}
		
		//ѡ��ǰ30������  �����ķ������ٵ�  ����������
		Double temp = 0.0;
		int i;
		for(i = 0; i < 30 && i < sportList.size(); i++) {
			temp += sportList.get(i).getScore();
			JSONObject jo = new JSONObject();
			jo.put("name", sportList.get(i).getName());
			jo.put("value", sportList.get(i).getScore());
			ja.add(jo);
		}
		if(i != sportList.size()) {
			JSONObject jo = new JSONObject();
			jo.put("name",  "����");
			jo.put("value", countAll-temp);
			ja.add(ja);
		}
		try {
			sendMsg(ja.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	//�õ��ַ���
	public void getTagCloud() {
		List<Tag> tagList = otherToTagService.getTagAllDesc();
		JSONArray ja = new JSONArray();
		//�õ���ߵ�50���ؼ���������
		for(int i = 0; i < 50 && i <tagList.size(); i++) {
			JSONObject jo = new JSONObject();
			jo.put("tagName", tagList.get(i).getTag());
			jo.put("value", tagList.get(i).getTagCount());
			ja.add(jo);
		}
		try {
			sendMsg(ja.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// �õ���û� �������������û�������ͳǷֲ������
	public void getSchemeUser() {
		List<SpecialSchemeUser> schemeUser = adminStatisticsService.getSchemeUserAll();//�õ��û��ı�����
		JSONArray ja = new JSONArray();//���һ���û������
		JSONArray ja2 = new JSONArray();//������е��û�
		ArrayList<String> provinceArray = new ArrayList<String>();
		for(SpecialSchemeUser var : schemeUser) {
			//���˵õ�������
			String province = "";
			if	(var.getAddress().contains("ʡ")) {
				province = var.getAddress().substring(0, var.getAddress().indexOf('ʡ'));
			}else {
				province = var.getAddress().substring(0, var.getAddress().indexOf('��'));
			}
			
			String sex = var.getSex();
			Integer age = var.getAge();
			JSONObject jo = new JSONObject(); 
			jo.put("sex", sex);
			jo.put("age", age);
			jo.put("province", province);
			if (!provinceArray.contains(province)) {
				provinceArray.add(province);
			}
			ja2.add(jo);
		}
		
		//������Ǽ��������
		String []provinceName = {"����","���","�Ϻ�","����","�ӱ�","ɽ��","���ɹ�","����","����","������","����","�㽭","����","����","����","ɽ��","�ຣ","����","�½�","���","����","̨��"};

		for(int i = 0; i < 200; i++) {
			JSONObject jo = new JSONObject();
			String province = "";
			if (i < 10) {
				jo.put("province", "�㽭");
				province = "�㽭";
			}else if(i > 11 && i < 20) {
				jo.put("province", "�Ϻ�");
				province = "�Ϻ�";
			}else if(i > 21 && i < 30) {
				jo.put("province", "�㶫");
				province = "�㶫";
			}else if(i > 31 && i < 40) {
				jo.put("province", "����");
				province = "����";
			} {
				int random1 = new Random().nextInt(provinceName.length);
				province = provinceName[random1];
				jo.put("province", province);
			}
			
			
			if(i < 60) {
				jo.put("sex", "��");
				jo.put("age", new Random().nextInt(32) + 18);
			}
			else {
				jo.put("sex", "Ů");
				jo.put("age", new Random().nextInt(32) + 25);
			}
			
			
			if (!provinceArray.contains(province)) {
				provinceArray.add(province);
			}
			ja2.add(jo);
		}
		//����Ϊ���������
		
		JSONArray ja3 = new JSONArray();
		ja3 = JSONArray.fromObject(provinceArray);
		ja.add(ja2);
		ja.add(ja3);
		
		try {
			sendMsg(ja.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	//�õ�ÿ�շ�����
	public void getPageViewAll() {
		List<PageView> pageViewList = adminStatisticsService.getAllOrderByTime();
		JSONArray ja = new JSONArray();
		//����Ϊ���������
		for(int i = 1; i <= 30; i++) {
			JSONObject jo = new JSONObject();
			jo.put("n", 2017);
			jo.put("y", 7);   //ǰ����Ҫ��һ  �·�
			jo.put("r", i);
			jo.put("pageViewCount", (Math.random()*30) - 0);
			ja.add(jo);
		}
		for(int i = 1; i <= 17; i++) {
			JSONObject jo = new JSONObject();
			jo.put("n", 2017);
			jo.put("y", 8);
			jo.put("r", i);
			jo.put("pageViewCount", (Math.random()*30) - 0);
			ja.add(jo);
		}
		//����Ϊ���������
		
		for(int i = pageViewList.size() - 1; i >= 0; i--) {
			JSONObject jo = new JSONObject();
			SimpleDateFormat myDate = new SimpleDateFormat("yyyyMMddhhmm");
			jo.put("n", myDate.format(pageViewList.get(i).getTime()).substring(0, 4));
			jo.put("y", myDate.format(pageViewList.get(i).getTime()).substring(4, 6));
			jo.put("r", myDate.format(pageViewList.get(i).getTime()).substring(6, 8));
			jo.put("pageViewCount", pageViewList.get(i).getPageViewCount());
			ja.add(jo);
		}
		try {
			sendMsg(ja.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//admin login-out
	public void cancealAdminLogin() {
		ServletActionContext.getRequest().getSession().removeAttribute("admin");
	}
	
	//amdin detail update
	public void adminDetailUpdate() throws ParseException {
		JSONObject jo = new JSONObject();
		jo = JSONObject.fromObject(json);
		String loginTime = jo.getString("loginTime");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		user.setRegisterDate(sdf.parse(loginTime));
		user.setBackground(jo.getString("trueName"));
		userService.saveUser(user);
		//ͬʱ��session�е����ݸ���
		//ServletActionContext.getRequest().getSession().removeAttribute("admin");
		ServletActionContext.getRequest().getSession().setAttribute("admin", user.getUsername());
	}
	
	
	//admin�ĵõ�������Ϣ��    �ͽ��� ��Ϣ���ܱ�
	public void getAdminDetail() {
		String args;
		JSONObject jo = new JSONObject();
		String name = (String)ServletActionContext.getRequest().getSession().getAttribute("admin");
		if(name == null) {
			args = "{'sta':'unLoginValid'}";
			jo = JSONObject.fromObject(args);
		}else {
			User admin = userService.getUserByName(name);
			//�õ����ջ�����Ϣ
			List<PageView> pageViewList = adminStatisticsService.getAllOrderByTime();
			
			SimpleDateFormat myFmt=new SimpleDateFormat("yyyyMMdd");
			PageView pageView = pageViewList.get(0);
			Date date = pageView.getTime();
			String time = myFmt.format(date);
			String now = myFmt.format(new Date());
			
			SimpleDateFormat myFmt2=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String loginTime = myFmt2.format(admin.getRegisterDate());
			//��û�н��յĻ���
			if(time.equals(now)) {
				jo.put("statistics", pageView);
				jo.put("admin", admin);
				jo.put("loginTime", loginTime);
			}else {
				pageView = new PageView(new Date(), 0, 0, 0, 0, 0, 0, 0);
				jo.put("statistics", pageView);
				jo.put("admin", admin);
				jo.put("loginTime", loginTime);
			}
			
		}
		
		try {
			sendMsg(jo.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//ͳ��ÿ���������Ϣ
	public void countAdd(String str) {
		List<PageView> list = adminStatisticsService.getAllOrderByTime();
		if(list.size() != 0) {
			SimpleDateFormat myFmt=new SimpleDateFormat("yyyyMMdd");
			PageView pageView = list.get(0);
			Date date = pageView.getTime();
			String time = myFmt.format(date);
			String now = myFmt.format(new Date());
			if(time.equals(now)) {
				if(str.equals("login")) {
					pageView.setLoginCount(pageView.getLoginCount() + 1);
				}else if(str.equals("article")) {
					pageView.setArticleCount(pageView.getArticleCount() + 1);
				}else if(str.equals("pageView")) {
					pageView.setPageViewCount(pageView.getPageViewCount() + 1);
				}else if(str.equals("comment")) {
					pageView.setCommentCount(pageView.getCommentCount() + 1);
				}else if(str.equals("question")) {
					pageView.setQuestionCount(pageView.getQuestionCount() + 1);
				}else if(str.equals("answer")) {
					pageView.setAnswerCount(pageView.getAnswerCount() + 1);
				}else if(str.equals("register")) {
					pageView.setRegisterCount(pageView.getRegisterCount() + 1);
				}
				
				adminStatisticsService.savePageView(pageView);
				
			}else {
				//�����µ�һ��
				PageView pageView2 = new PageView();
				if(str.equals("login")) {
					pageView2 = new PageView(new Date(), 1, 0, 0, 0, 0, 0, 0);
				}else if(str.equals("article")) {
					pageView2 = new PageView(new Date(), 0, 0, 1, 0, 0, 0, 0);
				}else if(str.equals("pageView")) {
					pageView2 = new PageView(new Date(), 0, 1, 0, 0, 0, 0, 0);
				}else if(str.equals("comment")) {
					pageView2 = new PageView(new Date(), 0, 0, 0, 1, 0, 0, 0);
				}else if(str.equals("question")) {
					pageView2 = new PageView(new Date(), 0, 0, 0, 0, 1, 0, 0);
				}else if(str.equals("answer")) {
					pageView2 = new PageView(new Date(), 0, 0, 0, 0, 0, 1, 0);
				}else if(str.equals("register")) {
					pageView2 = new PageView(new Date(), 0, 0, 0, 0, 0, 0, 1);
				}
				adminStatisticsService.savePageView(pageView2);
			}
			 
			
		}else { //��û�����ݵ�ʱ��
			PageView pageView2 = new PageView();
			if(str.equals("login")) {
				pageView2 = new PageView(new Date(), 1, 0, 0, 0, 0, 0, 0);
			}else if(str.equals("article")) {
				pageView2 = new PageView(new Date(), 0, 0, 1, 0, 0, 0, 0);
			}else if(str.equals("pageView")) {
				pageView2 = new PageView(new Date(), 0, 1, 0, 0, 0, 0, 0);
			}else if(str.equals("comment")) {
				pageView2 = new PageView(new Date(), 0, 0, 0, 1, 0, 0, 0);
			}else if(str.equals("question")) {
				pageView2 = new PageView(new Date(), 0, 0, 0, 0, 1, 0, 0);
			}else if(str.equals("answer")) {
				pageView2 = new PageView(new Date(), 0, 0, 0, 0, 0, 1, 0);
			}else if(str.equals("register")) {
				pageView2 = new PageView(new Date(), 0, 0, 0, 0, 0, 0, 1);
			}
			adminStatisticsService.savePageView(pageView2);
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
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<Sport> getSportList() {
		return sportList;
	}
	public void setSportList(List<Sport> sportList) {
		this.sportList = sportList;
	}
	public Integer getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Sport getSport() {
		return sport;
	}
	public void setSport(Sport sport) {
		this.sport = sport;
	}
	public String getSportCategory1() {
		return sportCategory1;
	}
	public void setSportCategory1(String sportCategory1) {
		this.sportCategory1 = sportCategory1;
	}
	public String getSportCategory2() {
		return sportCategory2;
	}
	public void setSportCategory2(String sportCategory2) {
		this.sportCategory2 = sportCategory2;
	}
	public String getSportTime() {
		return sportTime;
	}
	public void setSportTime(String sportTime) {
		this.sportTime = sportTime;
	}
	public String getSportCustomTag() {
		return sportCustomTag;
	}
	public void setSportCustomTag(String sportCustomTag) {
		this.sportCustomTag = sportCustomTag;
	}

	public List<UserArticle> getExaminationLoading() {
		return examinationLoading;
	}

	public void setExaminationLoading(List<UserArticle> examinationLoading) {
		this.examinationLoading = examinationLoading;
	}

	public List<UserArticle> getExaminationAdopt() {
		return examinationAdopt;
	}

	public void setExaminationAdopt(List<UserArticle> examinationAdopt) {
		this.examinationAdopt = examinationAdopt;
	}
	public Integer getTotalPage2() {
		return totalPage2;
	}

	public void setTotalPage2(Integer totalPage2) {
		this.totalPage2 = totalPage2;
	}

	public Integer getCurrentPage2() {
		return currentPage2;
	}

	public void setCurrentPage2(Integer currentPage2) {
		this.currentPage2 = currentPage2;
	}


	public List<Question> getQuestionList() {
		return questionList;
	}


	public void setQuestionList(List<Question> questionList) {
		this.questionList = questionList;
	}

	public Integer getModalPageSize() {
		return modalPageSize;
	}

	public void setModalPageSize(Integer modalPageSize) {
		this.modalPageSize = modalPageSize;
	}

	public Integer getModalCurrentPage() {
		return modalCurrentPage;
	}

	public void setModalCurrentPage(Integer modalCurrentPage) {
		this.modalCurrentPage = modalCurrentPage;
	}

	public Integer getModalTotalPage() {
		return modalTotalPage;
	}

	public void setModalTotalPage(Integer modalTotalPage) {
		this.modalTotalPage = modalTotalPage;
	}

	public List<CRList> getCrlist() {
		return crlist;
	}

	public void setCrlist(List<CRList> crlist) {
		this.crlist = crlist;
	}


	public List<User> getUserList() {
		return userList;
	}
 
	public void setUserList(List<User> userList) {
		this.userList = userList;
	}


	public SpecialScheme getSpecialScheme() {
		return specialScheme;
	}


	public void setSpecialScheme(SpecialScheme specialScheme) {
		this.specialScheme = specialScheme;
	}

	public List<SpecialScheme> getSpecialSchemeList() {
		return specialSchemeList;
	}

	public void setSpecialSchemeList(List<SpecialScheme> specialSchemeList) {
		this.specialSchemeList = specialSchemeList;
	}

	public SpecialSchemeUser getSpecialSchemeUser() {
		return specialSchemeUser;
	}

	public void setSpecialSchemeUser(SpecialSchemeUser specialSchemeUser) {
		this.specialSchemeUser = specialSchemeUser;
	}
 
}

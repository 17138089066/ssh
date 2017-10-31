package com.Action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.struts2.ServletActionContext;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.Algorithm.EliminateHtml;
import com.Entity.Answer;
import com.Entity.AnswerGreat;
import com.Entity.CacheTag;
import com.Entity.OtherToTag;
import com.Entity.Question;
import com.Entity.QuestionFocus;
import com.Entity.Tag;
import com.Entity.User;
import com.Entity.UserUpdate;
import com.Service.CacheTagService;
import com.Service.OtherToTagService;
import com.Service.QuestionFocusService;
import com.Service.TalkService;
import com.Service.UserService;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@Controller("talkAction")
@Scope("prototype")

public class TalkAction extends ActionSupport {
	
	@Resource
	private TalkService talkService;
	@Resource
	private UserService userService;
	@Resource
	private OtherToTagService otherToTagService;
	@Resource
	private QuestionFocusService questionFocusService;
	@Resource
	private CacheTagAction cacheTagAction;
	@Resource
	private CacheTagService cacheTagService;
	@Resource
	private AdminStatisticsAction adminAtatisticsAction; 
	
	private Question question;
	private String flag;//�������
	private List<Object> list;//��ҳ�����б�չʾ
	private List<String> tagList;//һ�������еı�ǩ�б�
	private Answer answer;
	private List<Object> answerList;//�ش��б����һ�����ۣ��������ۣ�
	private List<Answer> secondeAnswerList;
	private String json;//���ܵ�����Ϣ
	private Integer category;//�ֱ�������� 1 ����Ϊ���Ƽ���2���ǽ������ȣ�3�����������⣩
	private List<Tag> hotTag;//�������ű�ǩչʾ 
	private List<Answer> bestAnswerUser;//����ش���
	private List<Question> questionList;//ÿ�˻ش������
	
	private List<Question> relateQuestion;//һ���������ص����������Ƽ�
	
	private Integer pageSize;//���ڷ�ҳ
	private Integer totalPage;
	private Integer currentPage;
	
	private boolean isShowAll;
	
	
	private String questionFocusImg;//���ڹ�ע������
	private String questionFocusWord;
 
	@SuppressWarnings("rawtypes")
	static private Set QuestionId;//���һ��session֮�ڵ�����������������
	
	private String isShowAnswerAll;//��Ҫ�������û������ʱ��  ����㿪  ��ֻչʾ�û�����һ���ش�  
	private String userUpdateName;//����ȷ��չ������˭�Ļش�
	
	private String questionCategory[];//�������޸ĵ�ʱ�򴫻ر�ǩ
	
	//����Ҫ�޸�һ�������ʱ��
	public String reviseQuestion(){
		question = talkService.getQuestionDetailById(question.getId());
		//���ҳ�  ���question ѡ������Щ�ı�ǩ
		question.setTag(question.getTag().trim().replaceAll("\\s+", ""));
		String []tempTag  = question.getTag().split("/");
		questionCategory = new String[3];
		StringBuffer tagCustom = new StringBuffer();
		for(int i = 0; i < tempTag.length; i++){
			if(tempTag[i].length() == 0) continue;
			Tag tag = otherToTagService.getTagById(Integer.parseInt(tempTag[i]));	
			if (i >= 2 ){
				tagCustom.append(tag.getTag() + "/");
			} else {
				questionCategory[i] = tag.getTag();
			}
		}
		if (tempTag.length > 2) {
			questionCategory[2] = tagCustom.substring(0, tagCustom.length()-1); 
		}
		return SUCCESS;
	}
	
	//����ajax����
	public void addOrSub(){
		JSONObject jsonObject = new JSONObject();
		jsonObject = JSONObject.fromObject(json);
		String answer_id = jsonObject.getInt("answer_id")+"";
		String method = jsonObject.getString("method");
 		
		String name = (String) ServletActionContext.getRequest().getSession().getAttribute("name");
		if(name != null){
 
		User user = userService.getUserByName(name);
		//User user = userService.getUserByName("aa");
		
		Answer answer = new Answer();
		answer = talkService.getOneFatherAnswer(Integer.parseInt(answer_id));
		
		if(method.equals("��")){
 
			AnswerGreat answerGreat = new AnswerGreat();
			answerGreat.setAnswer(answer);
			answerGreat.setUser(user);
			
			talkService.saveAnswerGreat(answerGreat);
			
			//����ش�ĵ�������һ
			answer.setGreatCount(answer.getGreatCount() + 1);
			talkService.saveAnswer(answer);
			
			//��¼�û��Ķ�̬��Ϣ
			//�õ�����ش������ı��� �������ID���ǻش���е�ID�Ŷ�����������ID�ţ�
			Question q = talkService.getQuestionDetailById(answer.getQuestion_id());
			//��content�ǻش��content
			String content = answer.getContent();
			/*if(content.length() > 550){
				content = content.substring(0,550);
			}*/
			UserUpdate userUpdate = new UserUpdate("�����˻ش�", q.getId(), q.getTitle(), content, 
					//author �ҵ�����ش��� 
					answer.getUser(), answer.getGreatCount(), user, new Date());
			userService.saveUserUpdate(userUpdate);
			
		} else {
			talkService.deleteAnswerGreat(user.getId(),Integer.parseInt(answer_id));
			
			//���µĵ�������һ
			if(answer.getGreatCount() > 0){
				answer.setGreatCount(answer.getGreatCount() - 1);
				talkService.saveAnswer(answer);
			}
			//ɾ���û��Ķ�̬��Ϣ
			//�õ�����ش������ı��� �������ID���ǻش���е�ID�Ŷ�����������ID�ţ�
			Question q = talkService.getQuestionDetailById(answer.getQuestion_id());
			userService.deleteUserUpdate("�����˻ش�", q.getId(), user.getId());
		}
		
		//�����µĵ�����
		String greatCount = answer.getGreatCount().toString();
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
	
	//�õ�����һ��father���Ե����е�son�ظ�
	public String getSecondeAnswerAll(){
		//�õ�father
		answer = talkService.getOneFatherAnswer(answer.getId());
		//�õ����еĶ����ظ�
		secondeAnswerList = talkService.getSonAllOfFather(answer.getId());
		return SUCCESS;
	}
	
	//ajax ����һ�������ظ�
	public  void ajaxSaveReply() {
		JSONObject jo = new JSONObject();
		jo = JSONObject.fromObject(json);
		answer = new Answer();
		answer.setTime(new Date());
		answer.setGreatCount(0);
		User user = userService.getUserByName(ServletActionContext.getRequest().getSession().getAttribute("name").toString());
		answer.setUser(user);
		
		answer.setContent(jo.getString("content"));
		answer.setFather_id(jo.getInt("father_id"));
		answer.setQuestion_id(jo.getInt("question_id"));
		answer.setFlag(jo.getInt("flag"));
		
		talkService.saveAnswer(answer);
		
		if (answer.getFlag() == 0) {
			JSONObject jo2 = new JSONObject();
			jo2.put("father_id", answer.getId());
			try {
				sendMsg(jo2.toString());
			} catch (IOException e) { 
				e.printStackTrace();
			}
			
			//�����û��Ļش�Ķ�̬    //�õ�����ش��question  ��Ϊ��̬���е�otherID
			Question q = talkService.getQuestionDetailById(answer.getQuestion_id());
			String content = answer.getContent();
			/*if(content.length() > 550){
				content = content.substring(0,550);
			}*/
			UserUpdate userUpdate = new UserUpdate("�ش�������",
					//other_id Ϊ�������ID
					answer.getQuestion_id(), q.getTitle(), 
					//�õ��ش������  author Ϊ�Լ�
					content,
					//new EliminateHtml().RemoveHtmlTag(content),
					 user, answer.getGreatCount(),
					 user , new Date()
					);
			userService.saveUserUpdate(userUpdate);
			
			//�����һ���ش�  ����question��**���ش����
			q.setReplyCount(q.getReplyCount() + 1);
			talkService.saveQuestion(q);
			
			//ͳ��
			adminAtatisticsAction.countAdd("answer"); 
		}
  
		
	}
 
	//�õ�һ�����������
	public String getQuestionDetail(){
		
		question = talkService.getQuestionDetailById(question.getId());
		//�õ�tag
		String tag_id[] = question.getTag().split("/");
		tagList = new LinkedList<String>();
		for(int i = 0; i < tag_id.length; i++){
			tagList.add(otherToTagService.getTagById(Integer.parseInt(tag_id[i])).getTag());
		}
		
		//�õ�all�ش� ( ������Ҫɸѡ  ��ʱ  ֻ��Ҫһ��)
		 getAnswerAll();
 
		 //�õ�������(��ص�question) ÿ����ǩ�������
		 relateQuestion = new LinkedList<Question>();
		 ArrayList<Integer>  arrayQuestion = new ArrayList<Integer>();
		 arrayQuestion.add(question.getId());
		 for(int i = 0; i < tag_id.length; i++) {
			 //�õ����иñ�ǩ��other
			 List<OtherToTag> questionToTag = otherToTagService.getOtherForRecommend("question", Integer.parseInt(tag_id[i]));
			 for(int j = 0, z = 0; j < questionToTag.size() && z < 2; z++, j++) {
				 //ÿ���Ƽ�2��
				 int questionId = questionToTag.get(j).getOtherId();
				 if(!arrayQuestion.contains(questionId)) {
					 relateQuestion.add(talkService.getQuestionDetailById(questionId));
					 arrayQuestion.add(questionId);
				 }
				 
			 }
		 }
		 
		 
		 
		//�鿴ʱ���Ѿ���ע�˸�����
		String name = (String) ServletActionContext.getRequest().getSession().getAttribute("name");
		if(name != null){

		User user = userService.getUserByName(name);
		 
		List<QuestionFocus> questionFocusList = questionFocusService.search(user.getId(), question.getId());
		if(questionFocusList.size() == 0){
			questionFocusImg = "../image/focusNo.png";
			questionFocusWord = "��ע����";
		}else{
			questionFocusImg = "../image/focus.png";
			questionFocusWord = "�ѹ�ע";
		}
		
		}else {
			questionFocusImg = "../image/focusNo.png";
			questionFocusWord = "��ע����";
		}
		
		//����session�Ƿ�ʧЧ�ж�  �Ƿ����¼���
		 if(ServletActionContext.getRequest().getSession().getAttribute("QuestionId") != null){
			 //�����
			 if(QuestionId != null && !QuestionId.contains(question.getId())){
				 question.setPageView(question.getPageView() + 1);  
				 talkService.saveQuestion(question);
			 }
		 }
		 
		//����cacheTag
		if(name != null){
			cacheTagAction.saveCacheTag(question.getTag());
		}
		
		//ͳ��
	    adminAtatisticsAction.countAdd("pageView"); 
 
		return SUCCESS;
	}
	
	//ajax �õ�����Ļش�
	public  void ajaxGetMoreAnswer() {
		JSONObject jo = new JSONObject();
		jo = JSONObject.fromObject(json);
		int totalCount = jo.getInt("totalCount");//ǰ������ʾ������
		int addCount = jo.getInt("addCount");//ÿ����Ҫ����������
		int question_id = jo.getInt("question_id");
		
		List<Answer> templateAll  = talkService.getAnswerAllToOneQuestion(question_id);//�õ���һ���Ͷ������лظ�
		//�������ݲ�������������endRow��startRow��listAll
		sortFatherAndSon(addCount, totalCount + addCount - 1, totalCount, templateAll);
		
		JSONArray ja = new JSONArray();
		ja = JSONArray.fromObject(answerList);
		/*for (int i = 0; i < answerList.size(); i++) {
			System.out.println(((Answer)((Map)answerList.get(i)).get("father")).getContent());
		}*/
		JSONObject jo2 = new JSONObject();
		jo2.put("list", ja);
		jo2.put("isShowAll", isShowAll);
		try {
			sendMsg(jo2.toString());
		} catch (IOException e) { 
			e.printStackTrace();
		}
		
	}
 
	//��һ�εõ����еĻش�all 
	@SuppressWarnings("rawtypes")
	public void getAnswerAll(){
		List<Answer> templateAll ;
		
		pageSize = 3; //���ü��صĵ�һ�μ��ص�ҳ��
		currentPage = 1; 
		int startRow = (currentPage - 1) * pageSize;

		//��Ҫ���� �Ǵ�  �û���ҳ���DAO�������  ���Ǵ�   �����б�  �������������飨�費��Ҫչʾ���У�
		if(isShowAnswerAll != null && isShowAnswerAll.equals("false")){
			User u = userService.getUserByName(userUpdateName);//��userAction �е� ȥһ��**�û�����ҳ��ʱ��  ��������  ������
			templateAll = talkService.getOneAnswerToOneQuestion(u.getId(), question.getId());
		}else{
			templateAll = talkService.getAnswerAllToOneQuestion(question.getId());//�õ���һ���Ͷ������лظ�
		}
		
		//���жԻش������   add -> answerList
		sortFatherAndSon(pageSize, startRow + pageSize, startRow, templateAll);
 
		//��¼�������������
		HttpSession session = ServletActionContext.getRequest().getSession();
		if(session.getAttribute("QuestionId") == null){
			session.setAttribute("QuestionId", true);
			QuestionId = new HashSet();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void sortFatherAndSon(int addCount,int endRow ,int startRow, List<Answer> templateAll) {
		List<Answer> fatherAnswer = new LinkedList();
		List<Answer> sonAnswer = new LinkedList();
		answerList = new LinkedList<Object>(); 
		
		for (Answer var2 : templateAll) {
			if(var2.getFlag() == 0) fatherAnswer.add(var2);
			else sonAnswer.add(var2);
		}
		
		isShowAll = (endRow  >= fatherAnswer.size() ) ? true : false;
		
		for (int i = startRow, j = 0; i < fatherAnswer.size() && j < addCount; i++, j++ ) {
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("father", fatherAnswer.get(i));
			List<Answer> sonAnswerToOneFather = new LinkedList<Answer>();
			for (Answer var3 : sonAnswer) {
				 if(var3.getFather_id() == fatherAnswer.get(i).getId()){
					 sonAnswerToOneFather.add(var3);
					}
				 }
				 map.put("son", sonAnswerToOneFather);
					//��ÿһ���ش��ж�֮ǰ�Ƿ�����(ǰ����ʾ��ͬͼƬ)
				 map.put("img", checkIsGreat(fatherAnswer.get(i).getId()));
				 answerList.add(map);
		 }
	}
 
	//�ж��Ƿ�����(�û�������id)
	public String checkIsGreat(Integer answer_id){
		List<AnswerGreat> list = new LinkedList<AnswerGreat>();
		String name =  (String) ServletActionContext.getRequest().getSession().getAttribute("name");
		if(name != null && !name.equals("")){
			User user = userService.getUserByName(name);
		
			list = talkService.checkIsGreat(user.getId(), answer_id);
			if(list.size() == 0){
				return "../image/00.jpg";
			}
			else return "../image/11.jpg";
		}
		else return "../image/00.jpg";
	}

	//ִ����ʾ��������   �õ����е�����
	public String getQuestionAll(){
		
		list = new LinkedList<Object>();
		List<Question> temp = new LinkedList<Question>();
		//��ֹ�ظ��Ƽ�
		ArrayList<Integer> talkList = new ArrayList<Integer>();
		String name = (String) ServletActionContext.getRequest().getSession().getAttribute("name");
 
		if(category == 1) {
			if(name != null) {
				//Ϊ���Ƽ�   �õ��û���cacheTag
				User user = userService.getUserByName(name);	
				List<CacheTag> cacheTagList = cacheTagService.getUserCacheTagList(user.getId());
				for(CacheTag var : cacheTagList) {
					List<OtherToTag> questionToTag = otherToTagService.getOtherForRecommend("question", var.getTagId());
					for(OtherToTag  var2 : questionToTag) {
						if(!talkList.contains(var2.getOtherId())) {
							Question quesiton = talkService.getQuestionDetailById(var2.getOtherId());
							temp.add(quesiton);
							talkList.add(quesiton.getId());
						}
					}
				}
				List<Question> temp2 = talkService.getQuestionOrderByFocus();
				for(Question var3 : temp2) {
					if(!talkList.contains(var3.getId())) {
						temp.add(var3);
					}
				}		
			} else {
				temp = talkService.getQuestionOrderByFocus();
			}
 
		} else if(category == 2) {//��������
			temp = talkService.getQuestionElite();
		} else if(category == 3) {//��������
			//ʱ������  ����û�лش��һϵ������
		   temp = talkService.getQuestionAllByTime();		 
		}
		
		//�õ�ÿһ������ı�ǩ�������������Ʊ�ش�
	    getTag(temp);
		
 
		return 	SUCCESS;
	}
	
	
	
	//�õ��Ƽ�1,2,3��ش�������ҳ��   �õ�ÿһ���tag  �ṹ��(list(map("answer","question","tag")))
	public void getTag(List<Question> temp){
 	
		for(Question item:temp){
			List<String>  tag = new LinkedList<String>();//���ڴ��ÿһ�������е�tag��ǩ
			//�õ�����������Ʊ�Ļش�
			List<Answer> answer = new LinkedList<Answer>();
			answer = talkService.getAnswerExcludeSon(item.getId());
			
			String tag_id[] =  item.getTag().split("/");
			for(int i = 0; i < tag_id.length; i++){
				tag.add(otherToTagService.getTagById(Integer.parseInt(tag_id[i])).getTag());
			}
			Map<String, Object> map = new LinkedHashMap<String, Object>();	
			
			if(category != 3 && answer.size() != 0 ){
				Answer best = new Answer();
				best = answer.get(0);
				//ȥ���ش��е�html 
				best.setContent(new EliminateHtml().RemoveHtmlTag(best.getContent()));
				map.put("answer", best);
				map.put("question", item);
				//�õ������Ʊ�ش�Ļظ���
				List<Answer> l = new LinkedList<Answer>();
				l = talkService.getSonAllOfFather(best.getId());
				map.put("commentCount", l.size());
				map.put("tag", tag);
				list.add(map);
			} else if(category == 3) {//�õ�û�ش�� ���� ���������⣩   && answer.size() == 0
				item.setContent(new EliminateHtml().RemoveHtmlTag(item.getContent()));
				//Ϊ��ǰ̨��jsp ������ͬһ��  c:forearch
				map.put("answer", item);//Ϊ����ʾ��������summary
				map.put("question", item);//�����������
				map.put("commentCount", item.getPageView());//ʵΪ�����
				map.put("tag", tag);
				list.add(map);
			}	
		}

		
		List<Object> tempList = new LinkedList<Object>();
		tempList = list;
		list = new LinkedList<Object>(); 
		//���з�ҳ
		pageSize = 6;
		if(currentPage == null) currentPage = 1;
		totalPage = (tempList.size() % pageSize == 0) ? (tempList.size() / pageSize) : ((tempList.size() / pageSize) + 1);
		if(totalPage == 0) totalPage = 1;
		int startRow = (currentPage - 1) * pageSize;
		for(int i = startRow, j =0; j < pageSize && i < tempList.size(); i++, j++) {
			list.add(tempList.get(i));
		}
		
		
		 //�õ�15�����ű�ǩ
		 getHotTagTop();
		 //�õ�����ش��û�
		 getBestAnswerUserTop();
  
	}
	
	public void getBestAnswerUserTop(){
		List<Answer> temp ;
		//�õ��������ң���֤�û����ظ���
		Set<Integer> user_id = new HashSet<Integer>();
		temp = talkService.getBestAnswerTop();
		bestAnswerUser = new LinkedList<Answer>();
		for(int i = 0; i < 7 && i < temp.size(); i++){
			if(!user_id.contains(temp.get(i).getUser().getId())){
				//����HTML��ǩ�Ĵ���
				temp.get(i).setContent(new EliminateHtml().RemoveHtmlTag(temp.get(i).getContent()));
				bestAnswerUser.add(temp.get(i));
				user_id.add(temp.get(i).getUser().getId());
			}
			
		}
	}
	
	public void getHotTagTop(){
		List<Tag> temp = new LinkedList<Tag>();
		temp = otherToTagService.getTagAllDesc();
		hotTag = new LinkedList<Tag>();
		for(int i = 0; i < 25 && i < temp.size(); i++){
			hotTag.add(temp.get(i));
		}
	}
	
	//ִ�б���������
	public String saveQuestion() {
		
		String isUpdateQuestion = "";
		if(question.getId() == null) isUpdateQuestion = "NO";
		else isUpdateQuestion = "YES";
		
		String name = (String)ServletActionContext.getRequest().getSession().getAttribute("name");
		if(name == null) return INPUT;
				
		//String name = "aa";
		User user = userService.getUserByName(name);
		//��ʱ���tag Ϊ�� ��ϵtag��question
		List<Tag> tempTag = new LinkedList<Tag>();
		//�ұ�ǩ
		question.setTag(question.getTag().trim().replaceAll("\\s+", ""));
		String tag[] = question.getTag().split("/");
		String realTag = "";
		
		Set<String> tempTagName = new HashSet<String>();
		
		for(int i = 0; i < tag.length; i++){
			if(tag[i].length() == 0 || tempTagName.contains(tag[i])) continue;
			tempTagName.add(tag[i]);
			List<Tag> tempTagList = new LinkedList<Tag>();
			tempTagList	= otherToTagService.getTagIdByName(tag[i]);
			if(tempTagList.size() != 0){
				realTag = realTag + "/" + tempTagList.get(0).getId();
				tempTag.add(tempTagList.get(0));
			}else{
				//�����µı�ǩ
				otherToTagService.saveTag(new Tag(tag[i], 0));
				Tag tempTag2 ;
				tempTag2 = otherToTagService.getTagIdByName(tag[i]).get(0);
				realTag = realTag + "/" + tempTag2.getId();
				tempTag.add(tempTag2);
				
			}
			 
		}
		question.setFocusCount(1);//������
		question.setPageView(0);
		question.setReplyCount(0);
		question.setTag(realTag.substring(1));
		question.setUser(user);
		question.setTime(new Date());
		
		talkService.saveQuestion(question);
		
		//����questionFocus
		questionFocusService.savaQuestionFocus(new QuestionFocus(user, question));
		
		//��ע�������ͼƬ������
		questionFocusImg = "../image/focus.png";
		questionFocusWord = "�ѹ�ע";
		
		//Ϊ��jsp ��תDAO������
		question = talkService.getQuestionAllByTime().get(0);
		
		//����question��tag�Ĺ�ϵ
		
		tempTagName = new HashSet<String>();
		for(Tag var:tempTag){
			if(tempTagName.contains(var.getTag())) continue;
			tempTagName.add(var.getTag());
			otherToTagService.saveOtherToTag(new OtherToTag(question.getId(), var, "question"));
		}
		
		//ִ�б����û��Ķ�̬��������޸�����Ļ�   ��Ӧ�ñ��棩
		if(isUpdateQuestion.equals("NO")){
			String content = question.getContent();
			/*if(content.length() > 550){
				content = content.substring(0, 550);
			}*/
			UserUpdate userUpdate = new UserUpdate("���������", 
					//����id
					question.getId(), question.getTitle(), content, //new EliminateHtml().RemoveHtmlTag(content),
					//author Ϊ�� count Ϊ��
					null, null, user, new Date()
					);
			userService.saveUserUpdate(userUpdate);
		}
		
		//ͳ��
	    adminAtatisticsAction.countAdd("question"); 
 
		return SUCCESS;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public List<Object> getList() {
		return list;
	}

	public void setList(List<Object> list) {
		this.list = list;
	}

	public List<String> getTagList() {
		return tagList;
	}

	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}
 
	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}
 
	public List<Object> getAnswerList() {
		return answerList;
	}
 
	public void setAnswerList(List<Object> answerList) {
		this.answerList = answerList;
	}
 
	public List<Answer> getSecondeAnswerList() {
		return secondeAnswerList;
	}
 
	public void setSecondeAnswerList(List<Answer> secondeAnswerList) {
		this.secondeAnswerList = secondeAnswerList;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
	public Integer getCategory() {
		return category;
	}
	public void setCategory(Integer category) {
		this.category = category;
	}
	public List<Tag> getHotTag() {
		return hotTag;
	}
	public void setHotTag(List<Tag> hotTag) {
		this.hotTag = hotTag;
	}
	public List<Answer> getBestAnswerUser() {
		return bestAnswerUser;
	}
	public void setBestAnswerUser(List<Answer> bestAnswerUser) {
		this.bestAnswerUser = bestAnswerUser;
	}
	
	public List<Question> getQuestionList() {
		return questionList;
	}
	public void setQuestionList(List<Question> questionList) {
		this.questionList = questionList;
	}
	public String getQuestionFocusImg() {
		return questionFocusImg;
	}
	public void setQuestionFocusImg(String questionFocusImg) {
		this.questionFocusImg = questionFocusImg;
	}
	public String getQuestionFocusWord() {
		return questionFocusWord;
	}
	public void setQuestionFocusWord(String questionFocusWord) {
		this.questionFocusWord = questionFocusWord;
	}
	public String getIsShowAnswerAll() {
		return isShowAnswerAll;
	}
	public void setIsShowAnswerAll(String isShowAnswerAll) {
		this.isShowAnswerAll = isShowAnswerAll;
	}
	public String getUserUpdateName() {
		return userUpdateName;
	}
	public void setUserUpdateName(String userUpdateName) {
		this.userUpdateName = userUpdateName;
	}

	public String[] getQuestionCategory() {
		return questionCategory;
	}

	public void setQuestionCategory(String[] questionCategory) {
		this.questionCategory = questionCategory;
	}
	public List<Question> getRelateQuestion() {
		return relateQuestion;
	}

	public void setRelateQuestion(List<Question> relateQuestion) {
		this.relateQuestion = relateQuestion;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
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

	public boolean getIsShowAll() {
		return isShowAll;
	}

	public void setShowAll(boolean isShowAll) {
		this.isShowAll = isShowAll;
	}
 

	 
	
	
}

package com.Action;
 
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
 
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.apache.struts2.ServletActionContext;
 

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.Entity.Category1;
import com.Entity.Category2;
import com.Entity.News;
import com.Entity.OtherToTag;
import com.Entity.Question;
import com.Entity.Sport;
import com.Entity.SportComment;
import com.Entity.SportReply;
import com.Entity.Tag;

import com.Entity.User;
import com.Entity.UserArticle;
import com.Algorithm.Arrange;
import com.Algorithm.CRList;
import com.Algorithm.EliminateHtml;
import com.Algorithm.MnService;
import com.Algorithm.Pager;
import com.Algorithm.PagerService;
import com.Algorithm.KeywordSplit;

import com.Service.ArticleService;
import com.Service.NewsService;
import com.Service.OtherToTagService;
import com.Service.SportService;
import com.Service.SportWantToService;
import com.Service.TalkService;
import com.Service.UserService;
import com.opensymphony.xwork2.ActionSupport;

@SuppressWarnings("serial")
@Controller("sportAction")
@Scope("prototype")
public class SportAction extends ActionSupport {
	@Resource
	private SportService sportService;
	@Resource 
	private UserService userService;
	@Resource
	private NewsService newsService;
	@Resource
	private SportWantToService sportWantToService;
	@Resource
	private CacheTagAction cacheTagAction;
	@Resource
	private AdminStatisticsAction adminAtatisticsAction; 
	@Resource
	private OtherToTagService otherToTagService;
	
	private List<Sport> list = new LinkedList<Sport>();//���sport
	private List<UserArticle> list2 = new LinkedList<UserArticle>();
	private List<CRList> crlist ;//��������������
	
	
	private Sport sport;//ʵ����
	private Category1 category1;//����
	private Category2 category2;//����
	private String category3;//����ʽ
	private SportReply sportReply;
 
	private PagerService pagerService;//��ҳ����
	private Pager pager;
	protected String currentPage;//��jsp�д��ݹ����ĵ�ǰҳ
	protected int totalPages;
	protected String totalRows;
	protected String pagerMethod;
	protected String startPage;//Ϊ��ǰ̨չʾ
	protected String endPage;
	
	private String hql = "'%";
 
	private String CR;//���ۻ����ǻظ�
	
	private String wantImg;//����Ƿ���ȥͼ

	@SuppressWarnings("rawtypes")
	static private Set SporId;//���һ��session֮�ڵ���������µ�ID 
	
	private String isToEnd;
	private List<String> sportTag;//���ı�ǩչʾ
	private List<Sport>  sportSimilar;//���ƾ���
 
	//�����û���Ϣ����
	public void saveNews(User targetuser, User user, String title, Integer title_id, Date time, Integer type1, Integer type2, Integer flag){
		News news = new News(targetuser, user, title, title_id, time, type1, type2, flag);
		newsService.saveNews(news);
	}
	
 
	//��ҳ����
	public void Page(int totalRow, int PageRow){
		 
		pagerService = new PagerService();
		this.pager = pagerService.getPager(this.getCurrentPage(), this.getPagerMethod(), totalRow, PageRow);
		this.setCurrentPage(String.valueOf(this.pager.getCurrentPage()));
		this.setTotalRows(String.valueOf(totalRow));
		this.totalPages = pager.getTotalPages();
	}
 
	//������𣨵��������ͣ�����ʽ��  ��ȡ���еľ����ź�ͼƬ
	@SuppressWarnings("rawtypes")
	public String showSportAll() throws UnsupportedEncodingException{
		category1.setName( new String(category1.getName().getBytes("iso-8859-1"),"utf-8"));
		category2.setName( new String(category2.getName().getBytes("iso-8859-1"),"utf-8"));
		
        List<Sport> template = sportService.getSportAll(category1.getName(), category2.getName(), category3);
		list = new LinkedList<Sport>();
		if (template.size() > 0) {
			 Page(template.size(), 6);//ÿҳ��ʾ��
			 for(int i = this.pager.getStartRow() - 1,j = 0; i < this.pager.getTotalRows() && j < this.pager.getPageSize(); j++, i++){
				 list.add(template.get(i));
			 }	
			 setPage();
		 }
		//�õ�һ���Ự��ʼ��session
		HttpSession session = ServletActionContext.getRequest().getSession();
		if(session.getAttribute("SporId") == null){
			session.setAttribute("SporId", true);
			SporId = new HashSet();
		}
		return SUCCESS;
	}
	
	public void setPage(){
		if (currentPage == null) {
			currentPage = "1";
			startPage = "1";
			endPage =  totalPages + "";
		} else {
			if (Integer.parseInt(currentPage) - 7 <= 0) {
				 startPage = 1 + "";
			 } else {
				 startPage = (Integer.parseInt(currentPage) - 7) + ""; 
			 }
			 if (Integer.parseInt(currentPage) + 7 <= totalPages) {
				 endPage = (Integer.parseInt(currentPage) + 7) + "";
			 } else {
				 endPage = totalPages + "";
			 } 
		}
		
	}
	
	//�õ�һ��������ϸ��Ϣ
	@SuppressWarnings("unchecked")
	public String getSportDetail(){
		 
		sport = sportService.getSportDetail(sport.getId());
		
		//����session�Ƿ�ʧЧ�ж�  �Ƿ����¼���
		if(ServletActionContext.getRequest().getSession().getAttribute("SporId") != null){
			 //ȥ���в鿴�Ƿ��о���id��
			if(SporId != null && !SporId.contains(sport.getId())){
				sport.setPageViewCount(sport.getPageViewCount() + 1);
				//�ۺϵ÷ּ�0.2
				sport.setScore(sport.getScore() + 0.2);
				sportService.saveSport(sport);
				SporId.add(sport.getId());
			}
		}
		
		//�ж��Ƿ���Ե����ȥ
		String name = (String) ServletActionContext.getRequest().getSession().getAttribute("name");
		if(name != null){
			User user = userService.getUserByName(name);
			if(sportWantToService.checkIsWant(sport.getId(), user.getId()).size() != 0){
					wantImg = "../image/33.png";
			}
			else wantImg = "../image/22.png";
			}
			else{
				wantImg = "../image/22.png";
		}
 
 
		//�õ���ؾ�������ۺͻظ�
		List<SportComment> comment = sportService.getSportComment(sport.getId());
		List<SportReply> reply = sportService.getSportReply(sport.getId());
		//�������ݽṹ��������
		crlist=new CRList().getCRList(comment, reply);
		//��ҳ����
		List<CRList> template = new LinkedList<CRList>();
		template = crlist;
		crlist = new LinkedList<CRList>();
		if (template.size() > 0) {
			 Page(template.size(), 8);
			 for(int i = this.pager.getStartRow() - 1,j = 0; i < this.pager.getTotalRows() && j < this.pager.getPageSize(); j++, i++){
				 crlist.add(template.get(i));
			 }
		 }
		//����start��end��page
		setPage();
		
		//���б����û�cache
		if(name != null)
			cacheTagAction.saveCacheTag(sport.getCustomTag());
		
		//ִ�����������
		adminAtatisticsAction.countAdd("pageView");
		
		//�õ����ı�ǩ  //�õ������Ƽ������
		getSportTagName();
 
		return SUCCESS;
	}

	
	public void getSportTagName() {
		//�õ��Զ���ı�ǩ(��tagId ���)
		String []str = sport.getCustomTag().split("/");
		//��ֹ��ǩ�ظ�
		Set<String> temp = new HashSet<String>();
		sportTag = new LinkedList<String>();
		sportSimilar = new LinkedList<Sport>();
		//��ֹ�ظ�
		ArrayList<Integer> array = new ArrayList<Integer>();
		array.add(sport.getId());
		
		for(int i = 0; i < str.length; i++) {
			String tempTagName = otherToTagService.getTagById(Integer.parseInt(str[i])).getTag();
			if (temp.contains(tempTagName)) continue;
			temp.add(tempTagName);
			sportTag.add(tempTagName);
			//�õ��ñ�ǩ�ľ���һ��
			List<OtherToTag> sportToTag = new LinkedList<OtherToTag>();
			sportToTag = otherToTagService.getOtherForRecommend("sport", Integer.parseInt(str[i]));
			for(int z = 0, j = 0; z < sportToTag.size() && j < 1; z++, j++) {
				Integer tempTag = sportToTag.get(z).getOtherId();
				if(!array.contains(tempTag)) {
					Sport sport = sportService.getSportDetail(tempTag);
					sportSimilar.add(sport);
					array.add(tempTag);
				}
			}
			
		}
		//��������ǩ
		if (!temp.contains(sport.getCategory2().getName()))
			sportTag.add(0, sport.getCategory2().getName());
		//�õ�(����ɽ��ˮ�̱�ǩ)�������Ƽ�
		Category2 category2 = sportService.getCategory2Id(sport.getCategory2().getName());
		for(Sport var : category2.getSport()) {
			if(!array.contains(var.getId())) {
				sportSimilar.add(var);
				array.add(var.getId());
				break;
			}
		}
		
		if (!temp.contains(sport.getCategory1().getName()))
			sportTag.add(0, sport.getCategory1().getName());
		
		Category1 category1 = sportService.getCategory1Id(sport.getCategory1().getName());
		for(Sport var : category1.getSport()) {
			if(!array.contains(var.getId())) {
				sportSimilar.add(var);
				array.add(var.getId());
				break;
			}
		}
 	}
	
	//���Ӷ����µ�����
	public String saveSportComment(){
		
		String name = (String) ServletActionContext.getRequest().getSession().getAttribute("name");
		
		if(name == null ) return INPUT;
		//ǰ��jsp ���صײ�
		isToEnd = "true";
		
		SportComment sportComment = new SportComment();
		String u="";
		u = CR;
		/*try {
			  u=new String(CR.getBytes("iso-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
		sportComment.setContent(u);
		sportComment.setUser(userService.getUserByName(name));
		sportComment.setTime(new Date());
		sportComment.setSport(sportService.getSportDetail(sport.getId()));
		sportService.saveSportComment(sportComment);
		
		//��Ҫ�����µ�������+1
		Sport temp = sportService.getSportDetail(sport.getId());
		//�ۺϵ÷ּ�0.5
		temp.setScore(temp.getScore() + 0.5);
		temp.setCommentCount(temp.getCommentCount() + 1);
		sportService.saveSport(temp);
		
		//ִ������������ͳ��
		adminAtatisticsAction.countAdd("comment");
		
		return SUCCESS;
	}
	
	//���ӶԻظ��Ļظ�
	public String saveSportReply(){
		
		String name = (String) ServletActionContext.getRequest().getSession().getAttribute("name");
		if(name == null ) return INPUT;
		
		//Ϊ����ǰ��ֱ����DAO�ײ�
		isToEnd = "true";
		
		SportReply sr = new SportReply();
		//new����
		/*String targetUser = "";
		char[] cr = CR.toCharArray();
		for(int j = 0, i= 0; i < CR.length(); i++){
			if(cr[i] == ']') break;
			if(j == 1) targetUser = targetUser + cr[i];
			if(cr[i] == ':') j++;

		}*/
		
		sr.setTime(new Date());
		User user = userService.getUserByName(name);
		sr.setUser(user);
		//User targetuser = userService.getUserByName(targetUser);
		//sr.setTargetUser(targetuser);
		sr.setTargetCRId(sportReply.getTargetCRId());
		Sport s = sportService.getSportDetail(sport.getId());
		sr.setSport(s);
		sr.setContent(CR);
		sr.setType(sportReply.getType());
		sportService.saveSportReply(sr);
		
		//��Ҫ�����µ�������+1
		Sport temp = sportService.getSportDetail(sport.getId());
		temp.setScore(temp.getScore() + 0.5);
		temp.setCommentCount(temp.getCommentCount() + 1);
		sportService.saveSport(temp);
		
		//���û�����Ϣ����
		//saveNews(targetuser, user, s.getName(), s.getId(), new Date(), 1, 0, 0);
		
		//ִ������������ͳ��
		adminAtatisticsAction.countAdd("comment");
				
		return SUCCESS;
	}
 
	//����Ϊ����
	private String method;//�ж��������ؼ��ʻ���tag
	private String value;//input �е����ݻ���tagName
	private List<Object> searchList;//(list(<flag>,<object(<cover,title,content,id>)>))
	private List<Sport> searchSportList;
	private List<UserArticle> searchArticleList;
	private List<Question> searchQuestionList;
	private Integer current_page;
	private Integer page_size;
	private Integer total_page;
	private Integer searchCount;//������������
	
	private ArrayList<String> array ;//�����ؼ��ַ�ֹ�ظ�
	
	private String json;
	
	private List<Tag> tagTopList;//�г����ű�ǩ
	
	@Resource
	private ArticleService articleService;
	@Resource
	private TalkService talkService;
	
	public void methodTag(String category) {
		//��tag�еõ�tagId;
		List<Tag> tagList = otherToTagService.getTagIdByName(value);
		Integer tagId  = tagList.get(0).getId();
		//�õ����е�otherTOtag ��other
		List<OtherToTag> otherTotagList = otherToTagService.getOtherAllByTagId(tagId);
		
		searchList = new LinkedList<Object>();
		searchSportList = new LinkedList<Sport>();
		searchQuestionList = new LinkedList<Question>();
		searchArticleList = new LinkedList<UserArticle>();
		for (OtherToTag var : otherTotagList) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, String> map2 = new HashMap<String, String>();
			
			if (var.getFlag().equals("sport") && (category.equals("ȫ��") || category.equals("���"))) {
				Sport sport = sportService.getSportDetail(var.getOtherId());
				searchSportList.add(sport);
				
				map.put("flag", "sport");
				map2.put("id", sport.getId().toString());
				map2.put("cover", sport.getCover());
				map2.put("title", sport.getName());
				map2.put("content", new EliminateHtml().RemoveHtmlTag(sport.getSummary()));
				map.put("object", map2);
				
				searchList.add(map);
				
			}else if (var.getFlag().equals("article") && (category.equals("ȫ��") || category.equals("����")) ) {
				UserArticle userArticle = articleService.getArticleDetail(var.getOtherId());
				userArticle.setContent(new EliminateHtml().RemoveHtmlTag(userArticle.getContent()));
				searchArticleList.add(userArticle);
				
				map.put("flag", "article");
				map2.put("id", userArticle.getId().toString());
				map2.put("title", userArticle.getTitle());
				map2.put("cover", userArticle.getCover());
				map2.put("content", new EliminateHtml().RemoveHtmlTag(userArticle.getContent()));
				map.put("object", map2);
				
				searchList.add(map);
			}else if (var.getFlag().equals("question") && (category.equals("ȫ��") || category.equals("����ҹ̸")) ) {
				Question question = talkService.getQuestionDetailById(var.getOtherId());
				question.setContent(new EliminateHtml().RemoveHtmlTag(question.getContent()));
				searchQuestionList.add(question);
				
				map.put("flag", "question");
				map2.put("id", question.getId().toString());
				map2.put("title", question.getTitle());
				map2.put("content", new EliminateHtml().RemoveHtmlTag(question.getContent()));
				map.put("object", map2);
				
				searchList.add(map);
			}
		}
	}
	
	public void methodInput(String category) {
		String word[] = new KeywordSplit().getKeyWord(value);//�ָ�õ��ؼ���
		
		searchList = new LinkedList<Object>();
		searchSportList = new LinkedList<Sport>();
		searchQuestionList = new LinkedList<Question>();
		searchArticleList = new LinkedList<UserArticle>();
		array = new ArrayList<String>();//��ֹ�ظ�
		
		for(int i = word.length; i >= 2; i--){
			List<String[]> temp = new MnService().getCombine(word, i);//M���ؼ�����ȡ��N�������   
			for(int z = 0; z < temp.size(); z++){
				String[] w = temp.get(z);
				Arrange arr = new Arrange();	//����ȫ����    N���ؼ��ֵ�ȫ����
				// ΪHQL����Ҫƥ����� ���� from table where  content like ***
				arr.arrange("content", w, 0, w.length);
				hql = arr.getHql();  //�����ݿ��в�ѯ�����
 
				tempSearch(word, category);
			}
		}
		hql = "'%";  //���ؼ��ʷָ��ֻ��һ����ʱ�����⴦��(  '1' or '2' desc)   �������� ( '1' desc) ��( '2' desc) 
		for(int j = 0; j < word.length; j++){
			hql = this.hql + word[j] + "%' or "+ "content" + " like '%";
		}
		tempSearch(word, category);
	}
	
	public void tempSearch(String []word, String category) {
		
		//��Ҫ�Ӿ��㣬��壬������зֱ���
		hql = hql.substring(0, hql.length() - 19);//ȥ����β��'% or like
		
		if((category.equals("ȫ��") || category.equals("���"))) {
			List<Sport> sportTemp = sportService.searchSportByDefault(hql); 
			for(Sport var : sportTemp) {
				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, String> map2 = new HashMap<String, String>();
				
				if(!array.contains("sport" + var.getId())) {  // ��ֹ �ظ�
					array.add("sport" + var.getId());
					searchSportList.add(var);
					var.setContent(new EliminateHtml().RemoveHtmlTag(var.getContent()));
					
					//��������
					for(int j = 0; j < word.length; j++)
						var.setContent(var.getContent().replaceAll(word[j], "<font color=\"#428bca;\" >" + word[j] + "</font>" ));
					
					map.put("flag", "sport");
					map2.put("id", var.getId().toString());
					map2.put("cover", var.getCover());
					map2.put("title", var.getName());
					map2.put("content", var.getContent());
					map.put("object", map2);
					
					//����  DAO  searchList ��
					searchList.add(map);
				}
			}
		}
		
		if((category.equals("ȫ��") || category.equals("����"))) {
			List<UserArticle> articleTemp = sportService.searchUserArticle(hql);
			for(UserArticle var : articleTemp) {
				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, String> map2 = new HashMap<String, String>();
	
				if(!array.contains("article" + var.getId())) {  // ��ֹ �ظ�
					array.add("article" + var.getId());
					searchArticleList.add(var);
					var.setContent(new EliminateHtml().RemoveHtmlTag(var.getContent()));
					
					for(int j = 0; j < word.length; j++)
						var.setContent(var.getContent().replaceAll(word[j], "<font color=\"#428bca;\" >" + word[j] + "</font>" ));
					
					map.put("flag", "article");
					map2.put("id", var.getId().toString());
					map2.put("cover", var.getCover());
					map2.put("title", var.getTitle());
					map2.put("content", var.getContent());
					map.put("object", map2);
					
					searchList.add(map);
				}
			}
		}
		
		
		if((category.equals("ȫ��") || category.equals("����ҹ̸"))) {
			List<Question> questionTemp = sportService.searchQuestion(hql);
			for(Question var : questionTemp) {
				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, String> map2 = new HashMap<String, String>();
				
				
				if(!array.contains("question" + var.getId())) {  // ��ֹ �ظ�
					array.add("question" + var.getId());
					searchQuestionList.add(var);
					var.setContent(new EliminateHtml().RemoveHtmlTag(var.getContent()));
					
					for(int j = 0; j < word.length; j++)
						var.setContent(var.getContent().replaceAll(word[j], "<font color=\"#428bca;\" >" + word[j] + "</font>" ));
	
					map.put("flag", "question");
					map2.put("id", var.getId().toString()); 
					map2.put("title", var.getTitle());
					map2.put("content", var.getContent());
					map.put("object", map2);
					
					searchList.add(map);
				}
			}
		}
	}

	
	//�����������ؼ��ֻ��� tag
	public String searchContent() throws UnsupportedEncodingException {
		//�õ����ű�ǩ
		tagTopList = otherToTagService.getTagAllDesc();
		
		if (String.valueOf(method).equals("tag")) {
			 	value = new String(value.getBytes("iso-8859-1"),"utf-8");
			 	methodTag("ȫ��");
				 page_size = 10;
				
		} else { //ͨ��input ����
			ServletActionContext.getRequest().setCharacterEncoding("iso-8859-1");
			methodInput("ȫ��");
			 page_size = 10;
		}
		if (current_page == null) current_page = 1;
		int startRow = page_size * (current_page - 1);
		total_page = (searchList.size() % page_size == 0) ? (searchList.size() / page_size) : ((searchList.size() / page_size) + 1);	
		if(total_page == 0) total_page = 1;	
		searchCount = searchList.size();
		List<Object> tempList = searchList;
		searchList = new LinkedList<Object>();
		for (int i = startRow, j = 0; j < page_size && i < tempList.size(); i++,j++) {
			searchList.add(tempList.get(i));
		} 
 
		return SUCCESS;
	}
	
	//ajax   ���������� ɸѡ���ѡ�� ȫ��   ����壬����....��
	@SuppressWarnings("static-access")
	public void getSearchCategory() throws UnsupportedEncodingException {
		JSONObject jo = new JSONObject();
		jo = JSONObject.fromObject(json);
		String category = jo.getString("category");
		String method = jo.getString("method");
		current_page = jo.getInt("current_page");
		
		
		if (method.equals("tag")) {
			value = new String(jo.getString("value").getBytes("iso-8859-1"), "utf-8");
			methodTag(category);
		} else {
			value = jo.getString("value");
			methodInput(category);
		}
		
		page_size = 6;
		Integer length = 0;
		JSONArray ja = new JSONArray();
		if (category.equals("���")) {	
			if (current_page == null) current_page = 1;
			total_page = (searchSportList.size() % page_size == 0) ? (searchSportList.size() / page_size) : ((searchSportList.size() / page_size) + 1);
			int startRow = (current_page - 1) * page_size;
			
			length = searchSportList.size();
			List<Sport> tempList = searchSportList;
			searchSportList = new LinkedList<Sport>();
			for (int i = startRow, j = 0; j < page_size && i < tempList.size(); i++,j++) {
				searchSportList.add(tempList.get(i));
			}
			
			
			JsonConfig config = new JsonConfig();
			 config.setExcludes(new String[]{"category1","category2"}); 
			 ja = JSONArray.fromObject(searchSportList, config);
		} else if (category.equals("����")) {
			if (current_page == null) current_page = 1;
			total_page = (searchArticleList.size() % page_size == 0) ? (searchArticleList.size() / page_size) : ((searchArticleList.size() / page_size) + 1);
			length = searchArticleList.size();	
			int startRow = (current_page - 1) * page_size;
			
			length = searchArticleList.size();
			List<UserArticle> tempList = searchArticleList;
			searchArticleList = new LinkedList<UserArticle>();
			for (int i = startRow, j = 0; j < page_size && i < tempList.size(); i++,j++) {
				searchArticleList.add(tempList.get(i));
			}
			
			
			ja = ja.fromObject(searchArticleList);
		} else if (category.equals("����ҹ̸")) {
			if (current_page == null) current_page = 1;
			total_page = (searchQuestionList.size() % page_size == 0) ? (searchQuestionList.size() / page_size) : ((searchQuestionList.size() / page_size) + 1);
			length = searchQuestionList.size();
			int startRow = (current_page - 1) * page_size;
			
			length = searchQuestionList.size();
			List<Question> tempList = searchQuestionList;
			searchQuestionList = new LinkedList<Question>();
			for (int i = startRow, j = 0; j < page_size && i < tempList.size(); i++,j++) {
				searchQuestionList.add(tempList.get(i));
			}
			
			ja = ja.fromObject(searchQuestionList);
		} else {
			if (current_page == null) current_page = 1;
			total_page = (searchList.size() % page_size == 0) ? (searchList.size() / page_size) : ((searchList.size() / page_size) + 1);
			int startRow = (current_page - 1) * page_size;
			
			length = searchList.size();
			List<Object> tempList = searchList;
			searchList = new LinkedList<Object>();
			for (int i = startRow, j = 0; j < page_size && i < tempList.size(); i++,j++) {
				searchList.add(tempList.get(i));
			}			
			
			ja = ja.fromObject(searchList);
		}
		
		JSONObject jo2 = new JSONObject();
		jo2.put("current_page", current_page);
		jo2.put("total_page", total_page);
		jo2.put("list", ja);
		jo2.put("length", length);
		try {
			sendMsg(jo2.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendMsg(String content) throws IOException{
	     HttpServletResponse response = ServletActionContext.getResponse();//��ȡresponse
	     response.setContentType("text/html;charset=UTF-8");//����
	     response.getWriter().write(content);
	}
	
 
	
	
 
	 
	public List<Tag> getTagTopList() {
		return tagTopList;
	}


	public void setTagTopList(List<Tag> tagTopList) {
		this.tagTopList = tagTopList;
	}


	public Integer getSearchCount() {
		return searchCount;
	}


	public void setSearchCount(Integer searchCount) {
		this.searchCount = searchCount;
	}


	public String getJson() {
		return json;
	}


	public void setJson(String json) {
		this.json = json;
	}


	public Integer getCurrent_page() {
		return current_page;
	}


	public void setCurrent_page(Integer current_page) {
		this.current_page = current_page;
	}


	public Integer getPage_size() {
		return page_size;
	}


	public void setPage_size(Integer page_size) {
		this.page_size = page_size;
	}


	public Integer getTotal_page() {
		return total_page;
	}


	public void setTotal_page(Integer total_page) {
		this.total_page = total_page;
	}


	public List<Sport> getSearchSportList() {
		return searchSportList;
	}
 
	public void setSearchSportList(List<Sport> searchSportList) {
		this.searchSportList = searchSportList;
	}
 
	public List<UserArticle> getSearchArticleList() {
		return searchArticleList;
	}


	public void setSearchArticleList(List<UserArticle> searchArticleList) {
		this.searchArticleList = searchArticleList;
	}


	public List<Question> getSearchQuestionList() {
		return searchQuestionList;
	}


	public void setSearchQuestionList(List<Question> searchQuestionList) {
		this.searchQuestionList = searchQuestionList;
	}


	public List<Object> getSearchList() {
		return searchList;
	}
    public void setSearchList(List<Object> searchList) {
		this.searchList = searchList;
	}

	public List<UserArticle> getList2() {
		return list2;
	}

	public void setList2(List<UserArticle> list2) {
		this.list2 = list2;
	}

	public SportReply getSportReply() {
		return sportReply;
	}

	public void setSportReply(SportReply sportReply) {
		this.sportReply = sportReply;
	}

	public List<CRList> getCrlist() {
		return crlist;
	}

	public void setCrlist(List<CRList> crlist) {
		this.crlist = crlist;
	}

	public String getCR() {
		return CR;
	}

	public void setCR(String cR) {
		CR = cR;
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


	public String getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(String totalRows) {
		this.totalRows = totalRows;
	}

	public String getPagerMethod() {
		return pagerMethod;
	}

	public void setPagerMethod(String pagerMethod) {
		this.pagerMethod = pagerMethod;
	}

	public PagerService getPagerService() {
		return pagerService;
	}

	public void setPagerService(PagerService pagerService) {
		this.pagerService = pagerService;
	}

	public Pager getPager() {
		return pager;
	}

	public void setPager(Pager pager) {
		this.pager = pager;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}

	public List<Sport> getList() {
		return list;
	}

	public void setList(List<Sport> list) {
		this.list = list;
	}

	public Sport getSport() {
		return sport;
	}

	public void setSport(Sport sport) {
		this.sport = sport;
	}
 
	public String getWantImg() {
		return wantImg;
	}
 
	public void setWantImg(String wantImg) {
		this.wantImg = wantImg;
	}
 
	public String getCategory3() {
		return category3;
	}

 	public void setCategory3(String category3) {
		this.category3 = category3;
	}
 
	public String getStartPage() {
		return startPage;
	}
 
	public void setStartPage(String startPage) {
		this.startPage = startPage;
	}
	public String getEndPage() {
		return endPage;
	}
	public void setEndPage(String endPage) {
		this.endPage = endPage;
	}
	public String getIsToEnd() {
		return isToEnd;
	}
	public void setIsToEnd(String isToEnd) {
		this.isToEnd = isToEnd;
	}
	public List<String> getSportTag() {
		return sportTag;
	}
	public void setSportTag(List<String> sportTag) {
		this.sportTag = sportTag;
	}
	public List<Sport> getSportSimilar() {
		return sportSimilar;
	}
	public void setSportSimilar(List<Sport> sportSimilar) {
		this.sportSimilar = sportSimilar;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
 
}

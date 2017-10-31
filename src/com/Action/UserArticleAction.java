package com.Action;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
 
 
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
 
 

import javax.annotation.Resource;

import org.apache.struts2.ServletActionContext;
import org.aspectj.util.FileUtil;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.Algorithm.Pager;
import com.Algorithm.PagerService;
import com.Entity.*;
import com.Service.ArticleGreatService;
import com.Service.ArticleService;
import com.Service.OtherToTagService;
import com.Service.UserService;
import com.opensymphony.xwork2.ActionSupport;
/*
 * ��ɾ�Ĳ�  �������µĹ���
 * ���˲���    ���漰����(��������ʲô��)
 */

@SuppressWarnings("serial")
@Controller("userArticleAction")
@Scope("prototype")
public class UserArticleAction extends ActionSupport {
	
	@Resource
	public UserService userService;
	@Resource
	private OtherToTagService otherToTagService;
	@Resource
	private ArticleService articleService;
	@Resource
	private ArticleGreatService  articleGreatService;
	@Resource
	private AdminStatisticsAction adminAtatisticsAction; 
	
	private UserArticle userArticle;
	private List<UserArticle> list;
	
	private File uploadImage;//����
	private String uploadImageContentType;
	private String uploadImageFileName;
	
	private PagerService pagerService;//��ҳ����
	private Pager pager;
	protected String currentPage; 
	protected int totalPages;
	protected String totalRows;
	protected String pagerMethod;
	
	private String articleCategory[];//��Ųݸ�����
	private String isRevise;
	private String json;//ɾ������(userarticle_id)
 
	
	//ɾ������  ɾ����˳��
	public void deleteUserArticle(){
		
		JSONObject jo = new JSONObject(json);
		Integer userArticle_id = jo.getInt("userArticle_id");
		
		//ɾ�����µ����е�����
		userService.deleteArticleComment(userArticle_id);
		userService.deleteArticleReply(userArticle_id);
 
		//ɾ����̬
		Integer userId = (Integer)ServletActionContext.getRequest().getSession().getAttribute("user_id");
		if (userId != null) {
			userService.deleteUserUpdate("�������μ�", userArticle_id, userId);
		}
		//ɾ��OtherToTag
		otherToTagService.delteArticleToTag(userArticle_id, "article");
		//ɾ��������Ϣ
		articleGreatService.deleteArticleToGreat(userArticle_id);
		//ɾ������
		userService.deletUserArticle(userArticle_id);
		
		
	}
	
	//�õ�����  ת���޸�ҳ��
	public String getArticleDetailUpdate(){
		list = new LinkedList<UserArticle>();
		list.add(articleService.getArticleDetail(userArticle.getId()));
		getUserArticleCategory(list); // ��ȡ���� ͬʱ �������һ  Ϊ�� ��ֹ ���� �û���̬
		return SUCCESS;
	}
	
	//�鿴�Լ��Ƿ��Ѿ����˲ݸ�
	public String getUserDraft(){
		String username = (String) ServletActionContext.getRequest().getSession().getAttribute("name");
		if(username == null) return INPUT;
		
		list = userService.getUserDraft(username);
		if(list.size() != 0){
			getUserArticleCategory(list);
		}
		return SUCCESS;
	}
	
	//��Ϊת��articleupdate��ҳ���  ��ȡ  ��ǩ�ķ���
	public void getUserArticleCategory(List<UserArticle> list){
		userArticle = list.get(0);
		userArticle.setGreatCount(userArticle.getGreatCount() + 1);//�� ��˵�ʱ���ж�  �����!=1  ��������  �û���̬ 
		String []tagName = userArticle.getTag().split("/");
		articleCategory = new String[3];
		StringBuffer tagCustom = new StringBuffer();
		for (int i = 0; i < tagName.length; i++) {
			Tag tag = otherToTagService.getTagById(Integer.parseInt(tagName[i]));	
			if (i >= 2 ) {
				tagCustom.append(tag.getTag() + "/");
			} else {
				articleCategory[i] = tag.getTag();
			}
		}
		if (tagName.length > 2) {
			articleCategory[2] = tagCustom.substring(0, tagCustom.length()-1); 
		}
	}

 
	//�������� (����ݸ壬�޸ĺ�����£��½�������)
	public String saveUserArticle() throws IOException{
		
		String name = (String) ServletActionContext.getRequest().getSession().getAttribute("name");
		if(name == null) return INPUT;
		if(uploadImage != null){
			String path = ServletActionContext.getServletContext().getRealPath("/image/article");
			File file = new File(path);
			if(!file.exists()){
				file.mkdir();
			}
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
			uploadImageFileName = format.format(new Date()) + uploadImageFileName.substring(uploadImageFileName.indexOf('.'));
			FileUtil.copyFile(uploadImage, new File(file, uploadImageFileName));
			userArticle.setCover("image/article/" + uploadImageFileName);
		}
		
		userArticle.setTime(new Date());
		userArticle.setIsExamination("false");
		if (String.valueOf(isRevise).equals("null") || String.valueOf(isRevise).equals("")) {  //������޸ľͲ���Ҫ
			userArticle.setGreatCount(1);//�����
			userArticle.setScore(0.0);
			userArticle.setCommentCount(0);
			userArticle.setWantCount(0);
		}
		
		userArticle.setUser(userService.getUserByName(name));
		//�������µ�tag���ǩ ���ұ�������  
		doArticleToTag(name);
			
		if(userArticle.getIsDraft().equals("false")){ 
			adminAtatisticsAction.countAdd("article"); 
			return SUCCESS;
		}	
		else return "index";//������ǲݸ�
	}
	
 
	
	//����һЩ��tag����   ע���ȱ���-����-�ٱ���-��ϵ-
	public void  doArticleToTag(String name){
		//ȡ���ؼ�tag
		String str[] = userArticle.getTag().trim().replaceAll("\\s+", "").split("/");
		 
		StringBuffer tag2 = new StringBuffer();

		//��ǩid��tagʵ��  ������/������ʷ  --->  23/34
		List<Tag> taglist = new LinkedList<Tag>(); //������µ�tag ���ں�����otherToTag�Ļ����ϵ�ı���
		//ȥ����ͬ��ǩ��
		Set<String> tempTagName = new HashSet<String>();
		
		for (int i = 0; i < str.length; i++) {
			if(tempTagName.contains(str[i])) continue;
			tempTagName.add(str[i]);
			if(str[i].length() == 0) continue;
			List<Tag> tempTag = new LinkedList<Tag>(); 
			tempTag = otherToTagService.getTagIdByName(str[i]); //���ñ�ǩ�Ƿ����
			if(tempTag.size() == 0){
				Tag  tag = new Tag(str[i], 0);
				otherToTagService.saveTag(tag);//����һ������û���µ�tag
				tempTag.add(tag);//�û�������뵽taglist�У�taglist���ڱ����ϵ��
				//otherToTagService.saveTag(tempTag.get(0));
			} else {
				//�Ա�ǩ�����ų̶�+1�����ţ�
				tempTag.get(0).setTagCount(tempTag.get(0).getTagCount() + 1);
				otherToTagService.saveTag(tempTag.get(0));
			}
			taglist.add(tempTag.get(0));
			tag2.append("/" + tempTag.get(0).getId());
		}
 
		userArticle.setTag(tag2.substring(1));
		//��������
		userService.saveUserArticle(userArticle);

		//User user = userService.getUserByName(name);
		
		//����ǲݸ岻�����ϵ   ���������»����޸������ٱ���tag-article  combination
		if (userArticle.getIsDraft().equals("false")) {
			List<OtherToTag> tagRelationAll ;
			if(String.valueOf(isRevise).equals("true")){  //������޸ģ��Ѿ�������ģ�
				//�õ�ԭ�е����еı�ǩ��ϵ
				tagRelationAll = otherToTagService.getOtherToTagByOtherAll(userArticle.getId(), "article");
				for(OtherToTag var1 : tagRelationAll){
					int flag = 0;
					for(Tag  var2 : taglist){//taglist  �����ڵ����µı�ǩ
						if(var1.getTag().getTag().equals(var2.getTag())){ //˵����ϵ���豣��
							flag = 1; break;
						}
					}
					if(flag == 0){//�����ϵ��Ҫɾ��
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
					if(flag == 0){ //��Ҫ�¼������ϵ
						otherToTagService.saveOtherToTag(new OtherToTag(userArticle.getId(), var2, "article"));
					}
				}		
			} else {//�ݸ���ߵ�һ�η���
				//�����ǩ���ظ� ��֤otherToTag �Ĳ��ظ�
				Set<String> temp = new HashSet<String>();
				for (Tag var2 : taglist) {
					if (temp.contains(var2.getTag())) continue;
					temp.add(var2.getTag());
					otherToTagService.saveOtherToTag(new OtherToTag(userArticle.getId(), var2, "article"));
				}
			}
	  }
		
		//����û��Ķ�̬  ֻ�з����µ����µ�ʱ��ű��涯̬   
		//�û�����Ա���֮���  ����û���̬
		/*if(String.valueOf(isRevise).equals("null") && userArticle.getIsDraft().equals("false")){
			String content = userArticle.getContent();
			if(content.length() > 550){
				content = content.substring(0,550);
			}
			userService.saveUserUpdate(new UserUpdate("�������μ�", userArticle.getId(),userArticle.getTitle(), 
					new EliminateHtml().RemoveHtmlTag(content), user, null, user, userArticle.getTime()
					));
		}*/
	}
	
	//�õ������û�����
	public String getUserArticleAll(){
		
		String name = (String) ServletActionContext.getRequest().getSession().getAttribute("name");
		if(name == null) return INPUT;
		list = new LinkedList<UserArticle>();
		list = userService.getUserArticleAll(name);
		
		//��ҳ
		List<UserArticle> template = list;
		list = new LinkedList<UserArticle>();
		if(template.size() > 0){
			 Page(template.size(), 2);
			 for(int i = this.pager.getStartRow() - 1, z = 0; i < this.pager.getTotalRows() && z < this.pager.getPageSize(); z++, i++){
				 list.add(template.get(i));
			 }
		 }
 
		return SUCCESS;
	}
	
	
	//��ҳ����
	public void Page(int totalRow, int PageRow){ 
		pagerService = new PagerService();
		this.pager = pagerService.getPager(this.getCurrentPage(), this.getPagerMethod(), totalRow, PageRow);
		this.setCurrentPage(String.valueOf(this.pager.getCurrentPage()));
		this.setTotalRows(String.valueOf(totalRow));
		this.totalPages = pager.getTotalPages();
	}

	
	public File getUploadImage() {
		return uploadImage;
	}

	public void setUploadImage(File uploadImage) {
		this.uploadImage = uploadImage;
	}

	public String getUploadImageContentType() {
		return uploadImageContentType;
	}

	public void setUploadImageContentType(String uploadImageContentType) {
		this.uploadImageContentType = uploadImageContentType;
	}

	public String getUploadImageFileName() {
		return uploadImageFileName;
	}

	public void setUploadImageFileName(String uploadImageFileName) {
		this.uploadImageFileName = uploadImageFileName;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
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

	public List<UserArticle> getList() {
		return list;
	}

	public void setList(List<UserArticle> list) {
		this.list = list;
	}

	public UserArticle getUserArticle() {
		return userArticle;
	}
 
	public void setUserArticle(UserArticle userArticle) {
		this.userArticle = userArticle;
	}

	public String[] getArticleCategory() {
		return articleCategory;
	}

	public void setArticleCategory(String[] articleCategory) {
		this.articleCategory = articleCategory;
	}

	public String getIsRevise() {
		return isRevise;
	}

	public void setIsRevise(String isRevise) {
		this.isRevise = isRevise;
	}
	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}
 
	
}

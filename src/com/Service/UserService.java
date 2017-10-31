package com.Service;

import java.util.List;

import com.Entity.User;
import com.Entity.UserArticle;
import com.Entity.UserFocus;
import com.Entity.UserUpdate;

 public interface UserService {

	  List<User> registValid(String username);
	  List<User> loginValid(String username, String password);
	 void saveUser(User user);
	 User getUserById(Integer id);
	 User getUserByName(String Username);

	
	//���²���
	  void saveUserArticle(UserArticle userArticle);
	  //�õ�user�Ĳ��ǲݸ�����е�����
	 List<UserArticle> getUserArticleAll(String username);
	 //�õ�user��draft
	 List<UserArticle> getUserDraft(String username);
	//ɾ������
	 void deletUserArticle (Integer userArticle_id);
	//ɾ��һ������(ͨ��userarticle id)
	void deleteArticleComment(Integer userArticle_id);
	//ɾ����������
	void deleteArticleReply(Integer userArticle_id);
	//ɾ��һ������(ͨ��id)
	 void deleteArticleCommentById(Integer id);
	 //ɾ����������
    void deleteArticleReplyById(Integer id);
	
	//������Ƕ����û���̬�ļ�¼
	void saveUserUpdate(UserUpdate userUpdate);
	//ɾ���û��Ķ�̬
	void deleteUserUpdate(String flag, Integer other_id, Integer owner_id);
	//ɾ�������й�һ��other�Ķ�̬������adminɾ����һ�����⣬��������µĻش� ��ע  ��̬����Ҫɾ����
	 void adminDeleteUpdate(String flag, Integer other_id);
	//�õ����еĶ�̬
	List<UserUpdate> getUserUpdate(Integer user_id);
	//�û���̬��ɸѡ��question�Ķ�̬
	List<UserUpdate> getUserUpdateQuestion(Integer user_id);
	List<UserUpdate> getUserUpdateBlog(Integer user_id);
	List<UserUpdate> getUserUpdateAnswer(Integer user_id);
	//�õ���ע������Ķ�̬
	 List<UserUpdate> getUserUpdateFocusQuestion(Integer user_id);
	//�õ�following
	List<UserFocus>  getUserFocusFollowing(Integer user_id);
	//�õ�follower
	List<UserFocus> getUserFocusFollower(Integer targetUser_id);
	
	//�����û�1���û�2�Ĺ�ע
	 void saveUserFocus(UserFocus userFocus);
	 //ȡ���û�1���û�2�Ĺ�ע
	 void deletUserFocus(Integer user_id, Integer targetUser_id);
	//�鿴�Ƿ�����û�1 ���û�2�Ĺ�ע
	 List<UserFocus> checkUserFocus(Integer user_id, Integer targetUser_id);
	 
	//�õ��Ƚ϶���û���ע����
	 List<User> getUserOrderByFollower();
}

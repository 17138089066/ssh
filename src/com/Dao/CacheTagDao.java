package com.Dao;

import java.util.List;

import com.Entity.CacheTag;

public interface CacheTagDao {
	//����cache
	void save(CacheTag cacheTag);
	//�õ�user���Ѿ��е�cachetag
	List<CacheTag> getUserCacheTagList(Integer userId);

	
}

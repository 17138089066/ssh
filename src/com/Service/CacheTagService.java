package com.Service;

import java.util.List;

import com.Entity.CacheTag;

public interface CacheTagService {
	//����cache
	 void save(CacheTag cacheTag);
	//�õ�user���Ѿ��е�cachetag
	 List<CacheTag> getUserCacheTagList(Integer userId);
}

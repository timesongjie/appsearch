package com.bbkmobile.iqoo.cache.redis;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

import com.bbkmobile.iqoo.bean.AppView;
import com.bbkmobile.iqoo.cache.CacheService;
import com.bbkmobile.iqoo.service.SearchServiceResult;
import com.bbkmobile.iqoo.service.elastic.ElasticSearchService;
import com.bbkmobile.iqoo.util.JsonObjectUtil;
import com.bbkmobile.iqoo.util.SearchUtil;

/**
 * 
 * @Title:
 * @Description: redis cache service
 * @Author:yangzt
 * @Since:2014年9月26日
 * @Modified By:
 * @Modified Date:
 * @Why & What is modified:
 * @Version:1.0
 */
@Service("redisCacheSvc")
public class RedisCacheService implements CacheService {

	@Resource
	private RedisManager redisManager;

	@Resource
	private RedisWriteManager redisWriteManager;

	@Resource
	private ElasticSearchService elasticSearchSvc;

	public RedisManager getRedisManager() {
		return redisManager;
	}

	public void setRedisManager(RedisManager redisManager) {
		this.redisManager = redisManager;
	}

	public RedisWriteManager getRedisWriteManager() {
		return redisWriteManager;
	}

	public void setRedisWriteManager(RedisWriteManager redisWriteManager) {
		this.redisWriteManager = redisWriteManager;
	}

	@Override
	public boolean isCacheReadOpen() {
		return getRedisManager().isCacheSwitch();
	}

	@Override
	public boolean isCacheWriteOpen() {
		return getRedisWriteManager().isCacheSwitch();
	}

	// utility

	public String get(String key) {
		String value = null;
		ShardedJedis jedis = null;
		try {
			jedis = redisManager.getShardedJedis();
			value = jedis.get(key);
		} catch (Exception e) {
			e.printStackTrace();
			redisManager.returnBrokenShardedJedis(jedis);
		} finally {
			redisManager.returnShardedJedis(jedis);
		}
		jedis = null;
		return value;
	}

	@Override
	public String set(String key, String value) {
		String newKey = null;
		Jedis jedis = null;
		try {
			jedis = redisWriteManager.getJedis();
			newKey = jedis.set(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			redisWriteManager.returnBrokenJedis(jedis);
		} finally {
			redisWriteManager.returnJedis(jedis);
		}
		jedis = null;
		return newKey;
	}

	@Override
	public Long delete(String key) {
		Long d = 0l;
		Jedis jedis = null;
		try {
			jedis = redisWriteManager.getJedis();
			d = jedis.del(key);
		} catch (Exception e) {
			e.printStackTrace();
			redisWriteManager.returnBrokenJedis(jedis);
		} finally {
			redisWriteManager.returnJedis(jedis);
		}
		jedis = null;
		return d;
	}

	// search result
	@Override
	public SearchServiceResult getRes(String key) {
		ShardedJedis jedis = null;
		String value = null;
		try{
			jedis = redisManager.getShardedJedis();
			value = jedis.get(key);
		}catch(Exception e){
			redisManager.returnBrokenShardedJedis(jedis);
		}finally{
			redisManager.returnShardedJedis(jedis);
		}
		jedis = null;
		if (value != null) {
			SearchServiceResult r = JsonObjectUtil.toObject(value,
					SearchServiceResult.class);
			return r;
		}
		return null;
	}

	public List<AppView> getNew(String key) {
		ShardedJedis jedis = null;
		String ids = null;
		List<AppView> apps = null;
		try {
			jedis = redisManager.getShardedJedis();
			ids = jedis.get(key);

			apps = new ArrayList<AppView>();
			String[] d = ids.split(",");
			AppView app = null;
			for (String s : d) {
				app = JsonObjectUtil.toObject(jedis.get(s), AppView.class);
				if (app != null) {
					if (SearchUtil.FROM_BAIDU.equals(app.getFrom())) {
						if (app.isCheckedApkUrl() && !app.isCheckedApkOK()) {
							// TODO log error
							// dead link
						} else {
							apps.add(app);
						}
					} else {
						apps.add(app);
					}
				} else {
					// not in cache
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			redisManager.returnBrokenShardedJedis(jedis);
		} finally {
			redisManager.returnShardedJedis(jedis);
		}

		return apps;
	}

	@Override
	public String setRes(String key, SearchServiceResult res) {
		Jedis jedis = redisWriteManager.getJedis();
		String ok = jedis.set(key, JsonObjectUtil.toJson(res));
		jedis.expire(key, redisWriteManager.getSearchKeyTTL());
		redisWriteManager.returnJedis(jedis);
		jedis = null;
		return ok;
	}


	// app (for baidu)
	@Override
	public AppView getApp(String key) {
		ShardedJedis jedis = null;
		String value = null;
		try {
			jedis = redisManager.getShardedJedis();
			value = jedis.get(key);

		} catch (Exception e) {
			e.printStackTrace();
			redisManager.returnBrokenShardedJedis(jedis);
		} finally {
			redisManager.returnShardedJedis(jedis);
		}
		jedis = null;
		if (value != null) {
			AppView app = JsonObjectUtil.toObject(value, AppView.class);
			return app;
		}
		return null;
	}

	@Override
	public List<AppView> getAppList(String... key) {
		if (key == null || key.length < 1) {
			return null;
		}
		Jedis jedis = null;
		List<String> value = null;
		try {
			jedis = redisWriteManager.getJedis();
			value = jedis.mget(key);

		} catch (Exception e) {
			e.printStackTrace();
			redisWriteManager.returnBrokenJedis(jedis);
		} finally {
			redisWriteManager.returnJedis(jedis);
		}
		jedis = null;
		List<AppView> appList = new ArrayList<AppView>();
		if (value != null) {
			AppView app = null;
			for (String s : value) {
				if (s != null) {
					app = JsonObjectUtil.toObject(s, AppView.class);
					appList.add(app);
				}
			}
			return appList;
		}
		return null;
	}

	@Override
	public String setApp(String key, AppView app) {
		String ok = null;
		Jedis jedis = null;
		try {
			jedis = redisWriteManager.getJedis();
			app.setCheckedApkUrl(false);
			app.setUpdateApkUrl(null);
			ok = jedis.set(key, JsonObjectUtil.toJson(app));
			jedis.expire(key, redisWriteManager.getAppKeyTTL());
		} catch (Exception e) {
			e.printStackTrace();
			redisWriteManager.returnBrokenJedis(jedis);
		} finally {
			redisWriteManager.returnJedis(jedis);
		}
		jedis = null;
		return ok;
	}

	@Override
	public String setAppList(String keyPrefix, List<AppView> apps) {
		String ok = null;
		Jedis jedis = null;
		try {
			jedis = redisWriteManager.getJedis();
			for (AppView app : apps) {
				app.setCheckedApkUrl(false);
				app.setUpdateApkUrl(null);
				ok = jedis.mset(keyPrefix + app.getId(),
						JsonObjectUtil.toJson(app));
				jedis.expire(keyPrefix + app.getId(),
						redisWriteManager.getAppKeyTTL());
			}
		} catch (Exception e) {
			e.printStackTrace();
			redisWriteManager.returnBrokenJedis(jedis);
		} finally {
			redisWriteManager.returnJedis(jedis);
		}
		jedis = null;
		return ok;
	}

}

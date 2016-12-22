/*
* 2015-1-27 下午4:26:47
* 吴健 HQ01U8435
*/

package com.mbgo.search.core.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mbgo.search.util.PropertieUtil;

/**
 * 共享锁
 * @author 吴健 HQ01U8435
 *
 */
public class JobLockHandler {
	private static Logger log = LoggerFactory.getLogger(JobLockHandler.class);
	private static String LOG_PREV = "[JobLockHandler]";
	//实例
	private static final JobLockHandler INSTANCE = new JobLockHandler();
	//zookeeper连接实例
	private CuratorFramework CLIENT = null;
	//存放不同名称的锁
	private Map<String, InterProcessLock> LOCK_MAP = null;
	//针对不同的锁，存放对应的完整节点路径，节点上存储的是锁的最后归还时间
	private Map<String, String> LOCK_DATA_PATH_MAP = null;
	//zookeeper配置
	private String ZK_HOST = PropertieUtil.getStrProp("resource", "search.zkHost");
	//zookeeper连接实例的命名空间
	private String NAME_SPACE = "MB_solrcloud";
	//共享锁基础路径
	private String LOCK_PATH = "/searchQuartzPath/locker";
	//锁对应数据的路径
	private String LOCK_DATA_PATH = "/searchQuartzPath/data";
	
	private JobLockHandler() {
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
		CLIENT = CuratorFrameworkFactory.builder().connectString(ZK_HOST).retryPolicy(retryPolicy).namespace(NAME_SPACE).build();
		CLIENT.start();
		CLIENT.getConnectionStateListenable().addListener(new ConnectionStateListener() {
			
			@Override
			public void stateChanged(CuratorFramework client, ConnectionState state) {
				log.debug("{} stateChanged {}", LOG_PREV, state);
			}
		});
		
		log.debug("{} init curator client", LOG_PREV);
		LOCK_MAP = new HashMap<String, InterProcessLock>();
		LOCK_DATA_PATH_MAP = new HashMap<String, String>();
		createNewLock(LOCK_PATH, LOCK_PATH, LOCK_DATA_PATH);
	}
	//根据锁的名称，获得锁的对象
	private InterProcessLock getInterProcessMutex(String name) {
		InterProcessLock lock = LOCK_MAP.get(name);
		
		if(lock != null) {
			return lock;
		}
		return createNewLock(name, LOCK_PATH + "/" + name, LOCK_DATA_PATH + "/" + name);
	}
	//根据锁的名称，路径，以及数据路径，创建新的锁节点和数据节点
	private InterProcessLock createNewLock(String name, String lockPath, String dataPath) {
		InterProcessLock lock = new InterProcessSemaphoreMutex(CLIENT, lockPath);
		try {
			if(CLIENT.checkExists().forPath(dataPath) == null) {
				CLIENT.create().forPath(dataPath, "-1".getBytes());
			}
		} catch (Exception e) { }
		LOCK_MAP.put(name, lock);
		LOCK_DATA_PATH_MAP.put(name, dataPath);
		return lock;
	}
	//获得指定名字的锁的最后release时间
	private long lockLastReleaseTime(String name) {
		String path = getDataPath(name);
		try {
			byte[] data = CLIENT.getData().forPath(path);
			String dataStr = new String(data);
			return Long.parseLong(dataStr);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return -1;
	}
	//设置指定名字的锁的最后release时间
	private void setLockLastReleaseTime(String name) {
		String path = getDataPath(name);
		try {
			CLIENT.setData().forPath(path, String.valueOf(System.currentTimeMillis()).getBytes());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	//获得锁对应的数据路径
	private String getDataPath(String name) {
		String path = LOCK_DATA_PATH_MAP.get(name);
		if(StringUtils.isBlank(path)) {
			path = LOCK_DATA_PATH + "/" + name;
		}
		return path;
	}
	private String setNameIfBlank(String name) {
		if(StringUtils.isBlank(name)) {
			return LOCK_PATH;
		}
		return name;
	}
	public static InterProcessLock lock(String lockName) {
		return instance().getInterProcessMutex(lockName);
	}
	private static JobLockHandler instance() {
		return INSTANCE;
	}
	
	public static CuratorFramework client() {
		return instance().CLIENT;
	}
	
	public static boolean getLock(String name) {
		return getLock(name, -1);
	}
	
	/**
	 * 尝试获得指定名字的锁
	 * @param name			指定的锁
	 * @param afterSecond	时间，单位：秒；表示此次获得锁时，必须距离该锁最后一次归还的时间的间隔秒数；小于0，表示无时间限制
	 * @return
	 */
	public static synchronized boolean getLock(String name, int afterSecond) {
		name = instance().setNameIfBlank(name);
		try {
			boolean isGetLock = lock(name).acquire(1, TimeUnit.MILLISECONDS);
			
			if(afterSecond > 0 && isGetLock) {//获得锁，则判断时间
				long lastReleaseTime = instance().lockLastReleaseTime(name);
				if(System.currentTimeMillis() - lastReleaseTime >= afterSecond * 1000) {
					//当前获得锁的时间，距离上次锁的release时间，已经超过要求的时间间隔，则返回
					return isGetLock;
				} else {
					//否则直接释放锁
					lock(name).release();
					return false;
				}
			}
			
			return isGetLock;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}
	
	public static void closeLock(String name) {
		name = instance().setNameIfBlank(name);
		try {
			lock(name).release();
			log.debug("{} release lock[{}] success", new Object[]{LOG_PREV, name});
		} catch (Exception e) {
			log.error("{} release lock[{}] failed : {}", new Object[]{LOG_PREV, name, e.getMessage()});
		} finally {
			instance().setLockLastReleaseTime(name);
		}
	}
}

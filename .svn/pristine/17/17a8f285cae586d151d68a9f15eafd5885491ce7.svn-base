package com.mbgo.search.core.tools;

import java.util.Date;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.banggo.common.redis.RedisTemplate;

@Service("lastIntervalRegister")
public class LastIntervalRegister {

  @Resource(name = "redisTemplate")
  private RedisTemplate redisTemplate;
  private static final String LAST_UPDATE_TIMESTAMP = "lastUpdateTimestamp";
  private static final String REBUILD_AND_UPDATE_SWITCH_STATUS = "rebuildAndUpdateSwitchStatus";
  private static Logger log = LoggerFactory.getLogger(LastIntervalRegister.class);

  /*
   * private static SharedCount counter = null; private static int defaultIntervalInMs = 600000;
   * private static boolean maybeCorrupted; private static Object lock = new Object();
   * 
   * static { RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3); CuratorFramework
   * client = CuratorFrameworkFactory.builder() .connectString(PropertieUtil.getStrProp("resource",
   * "dubbo.registry")) .retryPolicy(retryPolicy).namespace("MB_solrcloud").build(); client.start();
   * counter = new SharedCount(client, "/searchQuartzPath/counter", defaultIntervalInMs);
   * counter.addListener(new SharedCountListener() {
   * 
   * @Override public void stateChanged(CuratorFramework client, ConnectionState state) { if
   * (state.toString().equalsIgnoreCase(ConnectionState.SUSPENDED.toString()) ||
   * state.toString().equalsIgnoreCase(ConnectionState.LOST.toString())) { synchronized (lock) {
   * maybeCorrupted = true; } log.debug("connection error, use default interval [" +
   * defaultIntervalInMs + "]"); } else if
   * (state.toString().equalsIgnoreCase(ConnectionState.SUSPENDED.toString()) ||
   * state.toString().equalsIgnoreCase(ConnectionState.LOST.toString())) { synchronized (lock) {
   * maybeCorrupted = false; } log.debug("connected or reconnected"); } }
   * 
   * @Override public void countHasChanged(SharedCountReader sharedCount, int newCount) throws
   * Exception {
   * 
   * } }); try { counter.start(); } catch (Exception e) { log.error(e.getMessage(), e); } }
   * 
   * public static void setInterval(long intervalInMs) { try { counter.setCount((int) intervalInMs);
   * log.debug("set last interval to " + (int) intervalInMs + "ms"); } catch (Exception e) {
   * synchronized (lock) { maybeCorrupted = true; } log.error(e.getMessage(), e);
   * 
   * } }
   * 
   * public static int getInterval() { synchronized (lock) { if (!maybeCorrupted) { // 将时间窗口扩大500ms
   * if (counter.getCount() + 500 > defaultIntervalInMs) return (((counter.getCount() + 500) /
   * defaultIntervalInMs) + 1) * defaultIntervalInMs; }
   * log.debug("interval is corrupted or less than default interval, return 600000"); return
   * defaultIntervalInMs; } }
   */

  /*
   * public static void setUpdateTimestamp(String time) { updateTimestamp = time;
   * log.debug("set update timestamp to " + time); }
   * 
   * public static String getUpdateTimestamp() { if (StringUtils.isBlank(updateTimestamp)) { Date
   * date = new Date(); int min = date.getMinutes(); int m = (min / 10) * 10; String time =
   * TimeUtil.getStrDateByFormate(date, "yyyy-MM-dd HH:"); if (m == 0) { time += "00:00"; } else {
   * 
   * time += m + ":00"; } return time;
   * 
   * } return updateTimestamp; }
   */
  /**
   * 设置重建更新商品开关状态到redis
   * 
   * @param status
   */
  public void saveRebuildAndUpdateSwitchStatusToRedis(int status) {
    try {
      redisTemplate.set(REBUILD_AND_UPDATE_SWITCH_STATUS, String.valueOf(status));
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  /**
   * 设置重建更新商品开关状态到redis
   * 
   */
  public int getRebuildAndUpdateSwitchStatusFromRedis() {
    // 默认开启
    int status = 1;
    try {
      String value = redisTemplate.get(REBUILD_AND_UPDATE_SWITCH_STATUS);
      if (StringUtils.isNotBlank(value)) {
        status = Integer.parseInt(value);
      }
    } catch (Exception e) {
      log.error("redis error : {}", e.getMessage());
    }
    return status;
  }

  /**
   * 设置更新重建商品数据时间戳到redis
   * 
   * @param updateTimestamp
   */
  public void saveUpdateTimestampToRedis(String updateTimestamp) {
    try {
      redisTemplate.set(LAST_UPDATE_TIMESTAMP, updateTimestamp);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  /**
   * 获取更新重建商品数据时间戳
   * 
   * @return
   */
  public String getUpdateTimestampFromRedis() {
    String updateTimestamp = null;
    try {
      updateTimestamp = redisTemplate.get(LAST_UPDATE_TIMESTAMP);
    } catch (Exception e) {
      log.error("redis error : {}", e.getMessage());
    }
    if (StringUtils.isBlank(updateTimestamp)) {
      Date date = new Date();
      int min = date.getMinutes();
      int m = (min / 10) * 10;
      String time = TimeUtil.getStrDateByFormate(date, "yyyy-MM-dd HH:");
      if (m == 0) {
        time += "00:00";
      } else {

        time += m + ":00";
      }
      log.debug("value not exist or redis error,default return current time: " + time);
      return time;
    }
    return updateTimestamp;
  }

}

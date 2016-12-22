package com.mbgo.search.core.tools;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mbgo.mybatis.mbstore.mapper.MallThemeGoodsMapper;
import com.mbgo.search.util.PropertieUtil;

@Service("clearVarnishCacheHandler")
public class ClearVarnishCacheHandler {

  private static Logger log = LoggerFactory.getLogger(ClearVarnishCacheHandler.class);
  // varnish ip配置
  private String VARNISH_IP_LIST = PropertieUtil.getStrProp("resource", "varnish.ip.list");
  // varnish port配置
  private String VARNISH_PORT = PropertieUtil.getStrProp("resource", "varnish.port");
  @Autowired
  private MallThemeGoodsMapper mallThemeGoodsMapper;

  public void clearCache(String updateTimeStamp) {
    try {
      // updateTimeStamp = "2016-05-24 00:00:00";
      List<String> themeCodes = mallThemeGoodsMapper
          .getUpdatedThemeCodes(updateTimeStamp);
      log.debug("themeCodesSize is:{}", themeCodes);
      if (themeCodes != null && themeCodes.size() > 0 && StringUtils.isNotBlank(VARNISH_IP_LIST)) {
        String[] ipArray = VARNISH_IP_LIST.split(",");
        if (ipArray != null && ipArray.length > 0) {
          for (String ip : ipArray) {
            if (StringUtils.isNotBlank(ip)) {
              log.debug("sendCommandToServer method execute,ip is:{}.......", ip);
              sendCommandToServer(ip, themeCodes);
            }
          }
        }
      }
    } catch (Exception e) {
      log.error("clearCache occuors error");
      log.error(e.getMessage(), e);
      // add for debug
      log.error(e.getCause().toString());
    }
  }

  private void sendCommandToServer(String ip, List<String> themeCodes) {
    Socket client = null;
    try {
      // 与服务端建立连接
      client = new Socket(ip, Integer.parseInt(VARNISH_PORT));
      // /theme/20160526-2.shtml
      // 建立连接后就可以往服务端写数据了
      Writer writer = new OutputStreamWriter(client.getOutputStream());
      for (String themeCode : themeCodes) {
        if (StringUtils.isNotBlank(themeCode)) {
          log.debug("themeCode:{}", themeCode);
          writer.write("ban req.url ~ /theme/" + themeCode + ".shtml " + "\r\n");
          writer.flush();
        }
      }
      writer.close();
    } catch (Exception e) {
      log.error("sendCommandToServer occuors error");
      log.error(e.getMessage(), e);
      // add for debug
      log.error(e.getCause().toString());
    } finally {
      try {
        client.close();
      } catch (IOException e) {
        log.error("close socket: {} occuors error", ip);
        log.error(e.getMessage(), e);
        // add for debug
        log.error(e.getCause().toString());
      }
    }
  }
}

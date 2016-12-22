package com.mbgo.search.core.tools.alsolike;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.mbgo.mybatis.mbsearch.bean.MgrDicKeyword;
import com.mbgo.search.core.tools.alsolike.device.IWeightingDevice;
import com.mbgo.search.core.tools.alsolike.device.sttic.DeviceHandler;

/**
 * 用户输入的原始关键字分析器
 * 
 * @author HQ01U8435
 *
 */
public class WordAnalyzer {

  /**
   * 关键字
   */
  private String _inputWord;

  private List<IWeightingDevice> devices;

  public List<IWeightingDevice> getDevices() {
    return devices;
  }

  public void setDevices(List<IWeightingDevice> devices) {
    this.devices = devices;
  }

  /**
   * 是否输出信息
   */
  private boolean _isDebug = false;

  /**
   * 初始化分析器
   * 
   * @param word
   *          用户输入的原始关键字
   */
  public WordAnalyzer(String word) {
    _inputWord = word;
  }

  /**
   * 初始化加权器 分析用户原始的关键字，确定人群加权器和季节加权器
   */
  public void initDevices() {
    devices = new ArrayList<IWeightingDevice>(0);
    // 初始化加权器
    initDevice();
  }

  /**
   * 获得加权器默认的查询关键字
   * 
   * @return
   */
  public List<String> queryWords() {
    List<String> rs = new ArrayList<String>(0);
    String query = "";
    for (IWeightingDevice d : devices) {
      query += "," + d.getQueryWord();
    }
    String[] ps = query.split(",");
    for (int i = 0; i < ps.length; i++) {
      if (ps[i].isEmpty())
        continue;
      rs.add(ps[i]);
    }
    return rs;
  }

  public void setDebug(boolean isDebug) {
    _isDebug = isDebug;
  }

  /**
   * 分析目标关键字集合， 确定各个关键字包含的加权器种类以及权重值，并计算综合权重值，排序
   * 
   * @param words
   */
  public void calculate(List<MgrDicKeyword> words) {
    int index = words.size() * 2;
    for (MgrDicKeyword word : words) {
      index--;
      setNewWeigh(word, index);
    }
    Collections.sort(words, new Comparator<MgrDicKeyword>() {

      @Override
      public int compare(MgrDicKeyword o1, MgrDicKeyword o2) {
        if (o1.getWeight() < o2.getWeight()) {
          return 1;
        } else if (o1.getWeight() > o2.getWeight()) {
          return -1;
        } else {
          return 0;
        }
      }

    });
  }

  /**
   * 计算各个关键字的总和权重值
   * 
   * @param word
   */
  private void setNewWeigh(MgrDicKeyword word, int index) {
    int old = word.getWeight();
    // float people = _peopleDevice.contain(word.getWord());
    // float season = _seasonDevice.contain(word.getWord());
    // people = people > 0 ? 1 : 0;
    // season = season > 0 ? 1 : 0;
    // float rs = 0f;
    // // rs = index + (float) (old / Math.pow(old + 0.1, Math.sqrt(people + season)) + 20 * (people
    // / (people + 1)) + season / Math.pow(season + 1, people));
    // rs = (float)(index + people * 1.1 + season * 1.8);
    word.setNewWeigh(old);
    // if(_isDebug)
    // System.out.println("新权重：w =" + rs + "\t旧权重n：" + old + "\t\t人群k：" + people + "\t\t季节q：" +
    // season + "\t" + word.getWord());
  }

  /**
   * 初始化人群加权器
   * 
   * 分析用户输入的初始关键字， 推算其可能对应的人群；童装，男装，女装
   */
  private void initDevice() {
    devices.clear();
    int currentNum = 0;
    IWeightingDevice peopleDevice = null;

    int num = DeviceHandler.manDevice.contain(_inputWord);
    if (num > currentNum) {
      peopleDevice = DeviceHandler.manDevice;
      currentNum = num;
    }

    num = DeviceHandler.womanDevice.contain(_inputWord);
    if (num > currentNum) {
      peopleDevice = DeviceHandler.womanDevice;
      currentNum = num;
    }
    num = DeviceHandler.childDevice.contain(_inputWord);
    if (num > currentNum) {
      peopleDevice = DeviceHandler.childDevice;
      currentNum = num;
    }

    if (peopleDevice != null) {
      devices.add(peopleDevice);
    }
    devices.add(DeviceHandler.cateNameDevice);
  }

  public String show() {
    String info = "新权重计算公式：\n" +
        "\t自身权重：n\n" +
        "\t人群：k\n" +
        "\t季节：q\n" +
        "\tweight = n / Math.pow(n, Math.sqrt(k + q)) + q * Math.pow(k, 2) + q / Math.pow(q + 1, k)";
    return info;
  }

  public String get_inputWord() {
    return _inputWord;
  }

  public void set_inputWord(String word) {
    _inputWord = word;
  }

  public boolean is_isDebug() {
    return _isDebug;
  }

  public void set_isDebug(boolean _isDebug) {
    this._isDebug = _isDebug;
  }

  public static void main(String[] args) {
    WordAnalyzer wa = new WordAnalyzer("吊带衫");

    wa.initDevices();

    System.out.println(wa.queryWords());
  }
}

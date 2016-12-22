package com.mbgo.search.core.tools.alsolike.device;

/**
 * 权重加分器接口
 * @author HQ01U8435
 *
 */
public interface IWeightingDevice {
	
	/**
	 * 分析关键词，找出属于对应分类器词典的词汇数量
	 * @param word 关键词
	 * @return
	 */
	public int contain(String word);
	
	/**
	 * 获得该分类器默认的常用查询关键字
	 * 例如：小孩子、小朋友、小孩，都属于童装
	 * 			那默认的查询关键字就是：童装,男童,女童
	 * @return
	 */
	public String getQueryWord();
	
	/**
	 * 获取lucene查询时，关键词的权重
	 * @return
	 */
	public int getQueryWeight();
	
	/**
	 * 增加对应词库的基础分词
	 * @param w
	 */
	public void loadWord(String w);
	
	/**
	 * 获取关键字排序权重
	 * @return
	 */
	public int getKeyWeight();
}

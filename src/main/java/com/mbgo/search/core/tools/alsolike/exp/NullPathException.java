package com.mbgo.search.core.tools.alsolike.exp;

/**
 * 错误信息
 * 未指定分类加权器的词典路径
 * @author HQ01U8435
 *
 */
public class NullPathException extends Exception {
	
	public NullPathException() {
		super("the dic path is null");
	}
	
	public NullPathException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 628375128425584018L;
}

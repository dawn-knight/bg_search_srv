package com.mbgo.search.core.tools.alsolike.wordstock;

import java.util.HashMap;
import java.util.Map;

/**
 * 词典树的节点
 * @author HQ01U8435
 *
 */
public class Node {

	/**
	 * 自身节点的汉字信息
	 */
	private Character _selfChar;
	
	/**
	 * 是否可以是词尾
	 */
	private boolean _allowEnd = false;
	
	/**
	 * 后续词匹配节点集
	 */
	private Map<Character, Node> _subNodes = new HashMap<Character, Node>(16, 0.95f);

	public Node() {
	}
	public Node(Character ch) {
		this._selfChar = ch;
	}

	public Character get_selfChar() {
		return _selfChar;
	}
	public void set_selfChar(Character char1) {
		_selfChar = char1;
	}
	public Map<Character, Node> get_subNodes() {
		return _subNodes;
	}
	public void set_subNodes(Map<Character, Node> nodes) {
		_subNodes = nodes;
	}
	public Node push(Character ch) {
		if(_subNodes.get(ch) == null) {
			_subNodes.put(ch, new Node(ch));
		}
		return _subNodes.get(ch);
	}
	
	public Node get(Character ch) {
		return _subNodes.get(ch);
	}
	public boolean is_allowEnd() {
		return _allowEnd;
	}
	public void set_allowEnd(boolean end) {
		_allowEnd = end;
	}
}

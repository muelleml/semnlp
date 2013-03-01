/**
 * 
 */
package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author muelleml
 * 
 */
public class Node {

	public interface ChildSelector {
		public boolean selectChild(Node child);
	}
	
	public String pos = "";
	public Node mother;
	public List<Node> daughters = new LinkedList<Node>();
	public Word word;

	public Node() {
	}

	/**
	 * @param pos
	 * @param mother
	 * @param word
	 */
	public Node(String pos, Node mother, Word word) {
		this.pos = pos;
		this.mother = mother;
		this.word = word;
	}

	/**
	 * @returns Mother-Node with a PoS-Tag in searchPos (or null) 
	 **/
	public Node findMother(Set<String> searchPos){
		if (searchPos.contains(pos)){
			return this;
		}
		else if (mother != null) {
			return mother.findMother(searchPos);
		}
		else return null;
	}

	public Node findChild(ChildSelector childSelector) {
		if(childSelector.selectChild(this)) {
			return this;
		}
		else {
			Node node = null;
			for(Node child : daughters) {
				node = child.findChild(childSelector);
				if(node != null) return node;
			}
			return null;
		}
	}

	public Node findRoot()
	{
		if (mother != null) {
			return mother.findRoot();
		}
		else return this;
	}

}

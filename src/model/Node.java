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
	
	public Node findMother(Set<String> m){
		Node r = null;
		
		
		if (m.contains(pos)){
			r = this;
		}
		else if (mother != null) {
			r = mother.findMother(m);
		}
		
		
		
		return r;
	}

}

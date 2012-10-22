/**
 * 
 */
package model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author muelleml
 * 
 */
public class Sentence {

	private String del = System.lineSeparator();

	String origin;

	public List<Word> words;

	public Sentence() {
		words = new LinkedList<Word>();
	}

	public String toString() {

		StringBuilder sb = new StringBuilder(100);

		for (Word w : words) {
			sb.append(w.toString() + del);
		}

		return sb.toString();
	}

}

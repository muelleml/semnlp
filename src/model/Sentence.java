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

	// System.lineSeparator() requires jre 7
	private String del = System.getProperty("line.separator");

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

	public void finalize() {
		int max = 0;

		for (Word w : words) {
			if (w.cues.size() > max) {
				max = w.cues.size();
				//System.out.println(w.toString());
			}
		}
		
		for (Word w : words) {
			while (w.cues.size() < max) {
				w.cues.add(new Cue("_", "_", "_"));
				//System.out.println(w.toString());
				
			}
		}
	}

}

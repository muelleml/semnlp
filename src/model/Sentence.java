/**
 * 
 */
package model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author muelleml
 */
public class Sentence {
	String origin;

	public List<Word> words;
	
	public List<List<Cue>> verticalCues = new LinkedList<List<Cue>>();

	public Sentence() {
		words = new LinkedList<Word>();
	}

	public String toString() {

		StringBuilder sb = new StringBuilder(100);

		for (Word w : words) {
			sb.append(w.toString() + "\n");
		}

		return sb.toString();
	}

	

	/*
	 * NEVER RENAME THIS TO finalize()!
	 * Method ensures that the cuedepth is uniform.
	 * The verticalCues list is filled.
	 * Call after inserting the last Word or Cue.
	 */
	public void finalizeSent() {
		int max = 0;

		for (Word w : words) {
			if (w.cues.size() > max) {
				max = w.cues.size();
			}
		}
		
		int i = 0;
		while (i < max) {
			verticalCues.add(new LinkedList<Cue>());
			i++;
		}
		
		
		for (Word w : words) {
			i = 0;
			while (w.cues.size() < max) {
				w.cues.add(new Cue("_", "_", "_"));
				
			}
			for (Cue c: w.cues){
				verticalCues.get(i).add(c);
				i++;
			}
		}
	
	}

}

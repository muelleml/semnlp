/**
 * 
 */
package model;

import java.util.LinkedList;

/**
 * @author muelleml
 * 
 */
public class Word {

	final static String del = "\t";

	public String origin;
	public String sentenceID;
	public String tokenID;
	public String word;
	public String lemma;
	public String pos;
	public String parseTree;
	public LinkedList<Cue> cues = new LinkedList<Cue>();
	public Node node;

	public Word() {
	}

	public Word(String origin, String sentenceID, String tokenID, String word,
			String lemma, String pos, String parseTree) {
		this.origin = origin;
		this.sentenceID = sentenceID;
		this.tokenID = tokenID;
		this.word = word;
		this.lemma = lemma;
		this.pos = pos;
		this.parseTree = parseTree;
	}

	public Word(Word w) {
		this.origin = new String(w.origin);
		this.sentenceID = new String(w.sentenceID);
		this.tokenID = new String(w.tokenID);
		this.word = new String(w.word);
		this.lemma = new String(w.lemma);
		this.pos = new String(w.pos);
		this.parseTree = new String(w.parseTree);
		for(Cue c : w.cues)
		{
			this.cues.add(new Cue(c));
		}
	}

	@Override
	public String toString() {
		String appendix;

		if (cues.isEmpty()) {
			appendix = "***";
		} 
		else {
			StringBuilder sb = new StringBuilder(100);
			for (Cue c : cues) {
				sb.append(c.toString());
			}

			appendix = sb.toString();
		}

		String r = origin + del + sentenceID + del + tokenID + del + word + del
				+ lemma + del + pos + del + parseTree + del + appendix;

		return r.trim();
	}

}

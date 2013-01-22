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
public class Word {

	final static String del = "\t";

	public String origin;
	public String sentenceID;
	public String tokenID;
	public String word;
	public String lemma;
	public String pos;
	public String parseTree;
	public List<Cue> cues = new LinkedList<Cue>();
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

	public Word(Word base) {
		// Deep Copy
		this.origin = base.origin;
		this.sentenceID = base.sentenceID;
		this.tokenID = base.tokenID;
		this.word = base.word;
		this.lemma = base.lemma;
		this.pos = base.pos;
		this.parseTree = base.parseTree;
		if(base.cues != null) {
			this.cues = new LinkedList<Cue>();
			for(Cue c : base.cues) this.cues.add(new Cue(c));
		}
		this.node = new Node(base.node);
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

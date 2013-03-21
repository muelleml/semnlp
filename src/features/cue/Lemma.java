/**
 * 
 */
package features.cue;

import java.util.LinkedList;
import java.util.List;

import model.Sentence;
import model.Word;

/**
 * @author muelleml
 * 
 */
public class Lemma implements CueFeature {

	int pos;

	public Lemma() {
		pos = 0;
	}

	public Lemma(int pos) {
		this.pos = pos;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see features.Feature#extract(model.Word, model.Sentence)
	 */
	@Override
	public List<String> extract(Word w, Sentence s) {

		Word tWord = new Word();
		tWord.lemma = "";
		
		int rel = s.words.indexOf(w);
		if (rel != -1) {
			if (rel + pos >= 0 && rel + pos <= s.words.size() - 1) {
				tWord = s.words.get(rel+pos);
			}

		}
		

		List<String> r = new LinkedList<String>();
		r.add("Lemma"+Integer.toString(pos)+":" + tWord.lemma);
		return r;
	}
}

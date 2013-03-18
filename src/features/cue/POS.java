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
public class POS implements CueFeature {

	int position;

	public POS() {
		position = 0;
	}

	/**
	 * Part Of Speech Tag
	 * 
	 * @param position
	 *            The position relative to the given Word. 0 is the Word itself
	 */
	public POS(int position) {
		this.position = position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see features.Feature#extract(model.Word, model.Sentence)
	 */
	@Override
	public List<String> extract(Word w, Sentence s) {
		List<String> r = new LinkedList<String>();

		String prefix = "POS" + Integer.toString(position)+":";

		int rel = getPosition(w, s);

		if (rel < 0) {
			System.out.println("PANIC!");
		}

		if (rel + position >= 0 && rel + position <= s.words.size() - 1) {

			r.add(prefix + s.words.get(rel + position).pos);
		} else {
			r.add(prefix + "NULL");
		}

		return r;
	}

	private int getPosition(Word w, Sentence s) {
		int i = 0;

		for (Word tW : s.words) {

			if (tW.tokenID.equals(w.tokenID)) {
				i = s.words.indexOf(tW);
			}

		}

		return i;
	}

}

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
/**
 * @author muelleml
 * 
 */
public class NGram implements CueFeature {

	private int min;
	private int max;
	private int length;
	private boolean direction;

	/**
	 * Extracts N-Grams from a Word. Missing Characters will be filled with
	 * blankspaces.
	 * 
	 * @param min
	 *            The minimal length of the N-Grams
	 * @param max
	 *            The maximal length of the N-Grams
	 * @param length
	 *            The length of the Substring that will be processed. Shortening
	 *            will happen after reversal and filling.
	 * @param direction
	 *            The direction for the N-Gram Generation. False means from back
	 *            to front. e.g. instead of processing "hello", the String will
	 *            be taken as "olleH". The String will first be reversed an then
	 *            filled
	 */
	public NGram(int min, int max, int length, boolean direction) {

		this.min = min;
		this.max = max;
		this.length = length;
		this.direction = direction;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see features.Feature#extract(model.Word, model.Sentence)
	 */
	@Override
	public List<String> extract(Word w, Sentence s) {

		StringBuilder sb = new StringBuilder(w.word);

		// reversal
		if (!direction) {
			sb.reverse();
		}

		// filling
		while (sb.length() <= length) {
			sb.append(" ");
		}

		// shortening
		sb = new StringBuilder(sb.substring(0, length));
		
		//System.out.println(sb);

		return gram2(sb.toString(), min, max);
	}

	private List<String> gram(String l, int mn, int mx) {

		// System.out.println(l);

		List<String> r = new LinkedList<String>();

		// check if maximum depth is reached
		if (mx - mn > 0) {

			int i = 0;

			while (mx + i <= l.length()) {
				String t = l.substring(i, mx + i);

				r.add("NGRAM" + Integer.toString(mn) + Integer.toString(mx) + t);
				r.addAll(gram(t, mn, mx - 1));

				i++;
			}

		}

		return r;

	}

	private List<String> gram2(String l, int mn, int mx) {
		List<String> r = new LinkedList<String>();

		for (int i = 0; i < l.length()-1; i++) {

			for (int j = i; j < l.length(); j++) {
				r.add("NGRAM" + Integer.toString(i) + Integer.toString(j)
						+ l.substring(i, j));
			}
		}

		return r;

	}
}

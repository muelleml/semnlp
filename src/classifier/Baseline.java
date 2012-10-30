/**
 * 
 */
package classifier;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import model.Corpus;
import model.Cue;
import model.Sentence;
import model.Word;

/**
 * @author muelleml
 * 
 */
public class Baseline implements Classifier {

	private Set<String> lu = new TreeSet<String>();

	private Set<String> singleCue = new TreeSet<String>();

	private Corpus training, classif;

	/*
	 * (non-Javadoc)
	 * 
	 * @see classifier.Classifier#train(model.Corpus)
	 */
	@Override
	public void train(Corpus c) {

		training = c;

		for (Sentence s : c.sentences) {

			List<Integer> cueList = new LinkedList<Integer>();

			int max = s.words.get(0).cues.size();

			int cueIndex = 0;

			while (max > cueIndex) {

				int cueLength = 0;

				Cue cue;

				for (Word w : s.words) {

					cue = w.cues.get(cueIndex);

					if (!cue.cue.equals("_")) {
						cueLength++;
					}
				}

				if (cueLength == 1) {
					cueList.add(cueIndex);
				}

				cueIndex++;
			}

			for (Integer i : cueList) {
				for (Word w : s.words) {
					Cue cue = w.cues.get(i);
					if (cue.cue.equals(w.word)) {
						lu.add(cue.cue.toLowerCase());
					}

				}

			}
		}

		/*
		 * for (Word w : s.words) { for (Cue cue : w.cues) {
		 * 
		 * // if (cue.cue.equals(w.word)) { if (!cue.cue.equals("_") &
		 * cue.scope.equals("_")) { w.c = cue.cue; if (cue.cue.equals("world"))
		 * { System.out.println(w.toString()); } // System.out.println(cue.cue +
		 * w.word); lu.add(w.word.toLowerCase()); for (Cue cu : w.cues) { //
		 * System.out.println(cu.toString()); } } } } }
		 * 
		 * for (Sentence s : c.sentences) { for (Word w : s.words) { for (Cue
		 * cue : w.cues) {
		 * 
		 * // if (cue.cue.equals(w.word)) { if (!cue.cue.equals("_") &
		 * cue.scope.equals("_")) { w.c = cue.cue; if (cue.cue.equals("world"))
		 * { System.out.println(w.toString()); } // System.out.println(cue.cue +
		 * w.word); lu.add(w.word.toLowerCase()); for (Cue cu : w.cues) { //
		 * System.out.println(cu.toString()); } } } } }
		 * System.out.println(lu.size()); for (String s : lu) {
		 * System.out.println(s); }
		 */

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see classifier.Classifier#classify(model.Corpus)
	 */
	@Override
	public Corpus classify(Corpus c) {
		classif = new Corpus();

		for (Sentence s : c.sentences) {
			classif.sentences.add(classify(s));

		}

		for (String s : lu) {
			// System.out.println(s);
		}

		return classif;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see classifier.Classifier#classify(model.Sentence)
	 */
	@Override
	public Sentence classify(Sentence s) {
		Sentence r = new Sentence();

		Word t;

		int max = 0;

		for (Word w : s.words) {
			t = classify(w, max);
			if (max < t.cues.size()) {
				max = t.cues.size();
			}
			r.words.add(t);
			r.finalize();
			if (r.words.get(0).sentenceID.equals("43")) {
				System.out.println(r.words.get(0).cues.size());
			}
		}

		// System.out.println(max);

		return r;
	}

	private Word classify(Word w, int depth) {
		Word r = classify(w);

		if (r.cues.size() > 0) {
			depth += 1;
		}

		while (r.cues.size() < depth) {

			r.cues.add(0, new Cue("_", "_", "_"));
			// System.out.println(w.toString());

		}

		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see classifier.Classifier#classify(model.Word)
	 */
	@Override
	public Word classify(Word w) {
		Word r = new Word(w.origin, w.sentenceID, w.tokenID, w.word, w.lemma,
				w.pos, w.parseTree);

		if (lu.contains(w.word.toLowerCase())) {
			Cue cu = new Cue(w.word, "_", "_");
			r.cues.add(cu);
			r.c = w.c;
		}

		lu.remove("none");

		return r;
	}

	public Metric metrics(Corpus check) {

		float tp = 0f;
		float tn = 0f;
		float fp = 0f;
		float fn = 0f;

		String c1;
		String c2;

		int nrSent = check.sentences.size();

		if (check.sentences.size() > classif.sentences.size()) {
			nrSent = classif.sentences.size();
		}

		for (int i = 0; i < nrSent; i++) {

			for (int j = 0; j < check.sentences.get(i).words.size(); j++) {
				// System.out.println(check.sentences.get(i).toString());
				// System.out.println(check.sentences.get(i).toString());

				c1 = classif.sentences.get(i).words.get(j).c;
				c2 = check.sentences.get(i).words.get(j).c;

				if (c1.equals("_") & c2.equals("_")) {
					tn++;
				} else if (!c1.equals("_") & !c2.equals("_")) {
					tp++;
				} else if (c1.equals("_") & !c2.equals("_")) {
					fn++;
				} else if (!c1.equals("_") & c2.equals("_")) {
					fp++;
				}
			}

		}

		return new Metric(tp, tn, fp, fn);
	}
}

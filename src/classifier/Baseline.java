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
import model.Node;
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

		System.out.println(lu.size());

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

		}

		r.finalizeSent();
		r.generateTree();

		int cue = 0;

		Set<String> targetNodePos = new TreeSet<String>();
		targetNodePos.add("S");
		targetNodePos.add("SBAR");

		for (List<Cue> cl : r.verticalCues) {
			int word = 0;
			for (Cue c : cl) {

				if (!c.cue.equals("_")) {

					Word tword = r.words.get(word);
					Node n = tword.node.findMother(targetNodePos);
					if (n != null) {
						addScope(n, cue);
					}
				}

				word++;
			}

			cue++;
		}

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

	public Word classify(Word w) {
		Word r = new Word(w.origin, w.sentenceID, w.tokenID, w.word, w.lemma,
				w.pos, w.parseTree);

		if (lu.contains(w.word.toLowerCase())) {
			Cue cu = new Cue(w.word, "_", "_");
			r.cues.add(cu);
		}

		lu.remove("none");

		return r;
	}

	private void addScope(Node n, int cueLevel) {

		for (Node t : n.daughters) {

			addScope(t, cueLevel);

		}

		if (n.daughters.isEmpty()) {

			n.word.cues.get(cueLevel).scope = n.word.word;

		}

	}

}

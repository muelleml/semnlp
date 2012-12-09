/**
 * Based on Baseline classifier
 */
package classifier;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import features.Extractor;
import features.Feature;
import features.Lemma;
import features.NGram;
import features.POS;

import malletwrap.ClassifyMalletMaxEnt;
import malletwrap.TrainMalletMaxEnt;
import model.Corpus;
import model.Cue;
import model.Node;
import model.Sentence;
import model.Word;

/**
 * @author muelleml
 * 
 */
public class DemClassifier implements Classifier {

	// mallet trainers and classifiers
	TrainMalletMaxEnt train;
	// initialize after training
	ClassifyMalletMaxEnt classifier;

	Extractor ex;
	List<Feature> featureList;

	private Set<String> lu;

	private Set<String> affixCues;

	Corpus classif, training;

	String nonAffixCue = "nonAffixCue";

	public DemClassifier() {

		// mallet trainers and classifiers
		train = new TrainMalletMaxEnt();

		// initialize after training

		lu = new TreeSet<String>();

		affixCues = new TreeSet<String>();

		// configure the featureExtractor
		ex = new Extractor();
		featureList = new LinkedList<Feature>();
		featureList.add(new POS());
		featureList.add(new POS(-1));
		featureList.add(new POS(-2));
		featureList.add(new POS(1));
		featureList.add(new Lemma());
		ex.addFeatures(featureList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see classifier.Classifier#train(model.Corpus)
	 */
	@Override
	public void train(Corpus c) {

		// remove after testing:
		// ----begin----
		// mallet trainers and classifiers
		train = new TrainMalletMaxEnt();

		// initialize after training

		lu = new TreeSet<String>();

		affixCues = new TreeSet<String>();

		// configure the featureExtractor
		ex = new Extractor();
		featureList = new LinkedList<Feature>();
		featureList.add(new POS(0));
		featureList.add(new POS(-1));
		featureList.add(new POS(-2));
		featureList.add(new POS(1));
		featureList.add(new Lemma());
		featureList.add(new NGram(1, 3, 4, true));
		featureList.add(new NGram(1, 3, 5, false));
		ex.addFeatures(featureList);
		// ----end----

		training = c;

		for (Sentence s : c.sentences) {

			List<Integer> cueList = new LinkedList<Integer>();

			int max = s.words.get(0).cues.size();

			int cueIndex = 0;

			// build vertical cue list to exclude multiword cues

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

					// experiment! also in classifyWord
					// train.addTrainingInstance(cue.cue, ex.extract(w, s));

					if (cue.cue.equals(w.word)) {
						lu.add(cue.cue.toLowerCase());
						// train.addTrainingInstance(nonAffixCue, ex.extract(w,
						// s));
					} else if (!cue.cue.equals("_")) {
						affixCues.add(cue.cue);
						train.addTrainingInstance(cue.cue, ex.extract(w, s));

					}

					else {
						train.addTrainingInstance(nonAffixCue, ex.extract(w, s));
					}

				}

			}
		}

		classifier = new ClassifyMalletMaxEnt(train.train());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see classifier.Classifier#classify(model.Corpus)
	 */
	@Override
	public Corpus classify(Corpus c) {
		classif = new Corpus();

		int i = 0;
		for (Sentence s : c.sentences) {
			// System.out.println("classifying sentence #: " +
			// Integer.toString(i));
			i++;

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
			t = classify(w, max, s);
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

	private Word classify(Word w, int depth, Sentence s) {
		Word r = classify(w, s);

		if (r.cues.size() > 0) {
			depth += 1;
		}

		while (r.cues.size() < depth) {

			r.cues.add(0, new Cue("_", "_", "_"));
			// System.out.println(w.toString());

		}

		return r;
	}

	public Word classify(Word w, Sentence s) {

		Word r = new Word(w.origin, w.sentenceID, w.tokenID, w.word, w.lemma,
				w.pos, w.parseTree);

		// experiment! also in train
		/*
		 * String c = classifier.classifyInstance(ex.extract(r, s)); if
		 * (!c.equals("_")) { Cue cu = new Cue(c, "_", "_"); r.cues.add(cu); }
		 */

		Cue cu;

		if (lu.contains(w.word.toLowerCase())) {
			cu = new Cue(w.word, "_", "_");
			r.cues.add(cu);
		} else {

			String c = classifier.classifyInstance(ex.extract(r, s));
			if (!c.equals(nonAffixCue)) {
				cu = new Cue(c, "_", "_");
				r.cues.add(cu);
			}

		}

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

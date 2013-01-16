/**
 * Based on Baseline classifier
 */
package classifier;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import main.Application;
import malletwrap.ClassifyMalletCRF;
import malletwrap.ClassifyMalletMaxEnt;
import malletwrap.TrainMalletCRF;
import malletwrap.TrainMalletMaxEnt;
import model.Corpus;
import model.Cue;
import model.Sentence;
import model.Word;
import features.cue.CueFeature;
import features.cue.CueFeatureExtractor;
import features.cue.Lemma;
import features.cue.NGram;
import features.cue.POS;
import features.scope.POSSequence;
import features.scope.ScopeFeatureExtractor;
import features.scope.ScopeFeatureValue;

/**
 * @author muelleml
 * 
 */
public class DemClassifier implements Classifier {

	// mallet trainers and classifiers
	TrainMalletMaxEnt cueDetector;
	// initialize after training
	ClassifyMalletMaxEnt cueClassifier;
	
	TrainMalletCRF scopeDetector;
	ClassifyMalletCRF scopeClassifier;
	
	CueFeatureExtractor cueFeatureExtractor;
	List<CueFeature> cueFeatureList;
	
	ScopeFeatureExtractor scopeFeatureExtractor;

	private Set<String> lu;

	private Set<String> affixCues;

	Corpus classif;

	String nonAffixCue = "nonAffixCue";

	public DemClassifier() {

		// configure the featureExtractor
		cueFeatureExtractor = new CueFeatureExtractor();
		cueFeatureExtractor.addFeature(new POS(0));
		cueFeatureExtractor.addFeature(new POS(-1));
		cueFeatureExtractor.addFeature(new POS(-2));
		cueFeatureExtractor.addFeature(new POS(1));
		cueFeatureExtractor.addFeature(new Lemma());
		cueFeatureExtractor.addFeature(new NGram(1, 3, 4, true));
		cueFeatureExtractor.addFeature(new NGram(1, 3, 5, false));

		scopeFeatureExtractor = new ScopeFeatureExtractor();
		scopeFeatureExtractor.addFeature(new POSSequence(2));
//		scopeFeatureExtractor.addFeature(new POSTreePath());
		
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see classifier.Classifier#train(model.Corpus)
	 */
	@Override
	public void train(Corpus c) {
		
		//unique with every training run
		cueDetector = new TrainMalletMaxEnt();
		lu = new TreeSet<String>();
		affixCues = new TreeSet<String>();
		
		scopeDetector = new TrainMalletCRF(20);
		
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
					if (cue.cue.equals(w.word)) {
						lu.add(cue.cue.toLowerCase());
					} 
					else if (!cue.cue.equals("_")) {
						affixCues.add(cue.cue);
						cueDetector.addTrainingInstance(cue.cue, cueFeatureExtractor.extract(w, s));
					}

					else {
						cueDetector.addTrainingInstance(nonAffixCue, cueFeatureExtractor.extract(w, s));
					}

				}
			}
			List<ScopeFeatureValue> sfvList = scopeFeatureExtractor.extractTraing(s);
			for(ScopeFeatureValue sfv : sfvList) {
				scopeDetector.addSequenceInstance(sfv.labels, sfv.features);
			}
		}

		cueClassifier = new ClassifyMalletMaxEnt(cueDetector.train());
		scopeClassifier = new ClassifyMalletCRF(scopeDetector.train());
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

		return classif;

	}

	/*
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

		List<String> labels = scopeClassifier.predictSequence(scopeFeatureExtractor.extractClassif(r));
		int cueIndex = 0;
		Iterator<String> labelIt = labels.iterator();
		String recentLabel = "";
		for(Word w : r.words) {
			if(cueIndex < w.cues.size()) {
				
					if(labelIt.hasNext())  {
						String label = labelIt.next();
						if(w.cues.get(cueIndex).cue == "_") {		
						if(label == "B" || label == "I") {
							w.cues.get(cueIndex).scope = w.lemma;
							Application.out.println(w);
						}
						else if(recentLabel == "B" || recentLabel == "I") {
							cueIndex += 1;
						}
					}
					else {
						Application.out.println(w);
					}
				}
				else break;
				
				
			}
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

			String c = cueClassifier.classifyInstance(cueFeatureExtractor.extract(r, s));
			if (!c.equals(nonAffixCue)) {
				cu = new Cue(c, "_", "_");
				r.cues.add(cu);
			}

		}

		return r;
	}
}

/**
 * Based on Baseline classifier
 */
package classifier;

import model.Corpus;
import model.Sentence;
import classifier.cues.CueClassifier;
import classifier.cues.HybridCueDetector;
import classifier.scopes.CRFScopeDetector;
import classifier.scopes.ScopeClassifier;

/**
 * @author muelleml,eckebrpk
 * 
 */
public class Classifier {
	Corpus classif;
	
	CueClassifier cueClassif;
	ScopeClassifier scopeClassif;
	
	public Classifier() {
		cueClassif = new HybridCueDetector();
		scopeClassif = new CRFScopeDetector();
	}

	public void train(Corpus c) {
		cueClassif.train(c);
		scopeClassif.train(c);
	}

	public Corpus classify(Corpus c) throws InterruptedException {
		classif = new Corpus();

		for (Sentence s : c.sentences) {
			classif.sentences.add(classify(s));
		}

		return classif;

	}

	public Sentence classify(Sentence sentence) throws InterruptedException {
		cueClassif.classify(sentence);
		
		sentence.finalizeSent();
		sentence.generateTree();
		
		scopeClassif.classify(sentence);

		return sentence;
	}
}

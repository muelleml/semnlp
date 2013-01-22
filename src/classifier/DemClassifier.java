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
 * @author muelleml
 * 
 */
public class DemClassifier implements Classifier {
	Corpus classif;
	
	CueClassifier cueClassif;
	ScopeClassifier scopeClassif;
	
	public DemClassifier() {
		cueClassif = new HybridCueDetector();
		scopeClassif = new CRFScopeDetector();
	}

	@Override
	public void train(Corpus c) {
		cueClassif.train(c);
		scopeClassif.train(c);
	}

	@Override
	public Corpus classify(Corpus c) {
		classif = new Corpus();

		for (Sentence s : c.sentences) {
			classif.sentences.add(classify(s));
		}

		return classif;

	}

	@Override
	public Sentence classify(Sentence sentence) {
		Sentence response = new Sentence(sentence);
		cueClassif.classify(response);
		
		response.finalizeSent();
		response.generateTree();
		
		scopeClassif.classify(response);

		return response;
	}
}

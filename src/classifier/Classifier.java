/**
 * Based on Baseline classifier
 */
package classifier;

import model.Corpus;
import model.Sentence;
import classifier.cues.CueClassifier;
import classifier.cues.GoldCueDetector;
import classifier.cues.HybridCueDetector;
import classifier.scopes.BaselineScopeDetector;
import classifier.scopes.CRFScopeDetector;
import classifier.scopes.GoldScopeDetector;
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

	public Classifier(String cue)
	{
		switch (cue.toLowerCase())
		{
		case "gold":
			cueClassif = new GoldCueDetector();
			break;
		case "hybrid":
			cueClassif = new HybridCueDetector();
			break;
		default:
			throw new RuntimeException("Invalid Cue-Detector provided: '" + cue + "'. Valid are: 'Hybrid', 'Gold'");
		}
	}

	public Classifier(String cue, String scope)
	{
		switch (cue.toLowerCase())
		{
		case "gold":
			cueClassif = new GoldCueDetector();
			break;
		case "hybrid":
			cueClassif = new HybridCueDetector();
			break;
		default:
			throw new RuntimeException("Invalid Cue-Detector provided: '" + cue + "'. Valid are: 'Hybrid', 'Gold'");
		}

		switch (scope.toLowerCase())
		{
		case "gold":
			scopeClassif = new GoldScopeDetector();
			break;
		case "crf":
			scopeClassif = new CRFScopeDetector();
			break;
		case "baseline":
			scopeClassif = new BaselineScopeDetector();
			break;
		default:
			throw new RuntimeException("Invalid Scope-Detector provided: '" + scope + "'. Valid are: 'CRF', 'Gold', 'Baseline'");
		}
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
		
		sentence.ensureFinalized();
		sentence.generateTree();
		
		scopeClassif.classify(sentence);

		return sentence;
	}
}

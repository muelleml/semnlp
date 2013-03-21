/**
 * Based on Baseline classifier
 */
package classifier;

import model.Corpus;
import model.Sentence;
import util.Sysout;
import classifier.cues.CueClassifier;
import classifier.cues.GoldCueDetector;
import classifier.cues.HybridCueDetector;
import classifier.scopes.BaselineScopeDetector;
import classifier.scopes.CRFScopeDetector;
import classifier.scopes.ScopeClassifier;
import classifier.scopes.StackedScopeDetector;

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
		scopeClassif = new CRFScopeDetector("poshead(1,2,3,4,5)+baseline+posseq(10)", 150, false);
		
		//scopeClassif = new BaselineScopeDetector();
	}

	public Classifier(CueClassifier cueClassif, ScopeClassifier scopeClassif) {
		this.cueClassif = cueClassif;
		this.scopeClassif = scopeClassif;
	}

	public Classifier(String cue) {
		switch (cue.toLowerCase()) {
		case "gold":
			cueClassif = new GoldCueDetector();
			break;
		case "hybrid":
			cueClassif = new HybridCueDetector();
			break;
		default:
			throw new RuntimeException("Invalid Cue-Detector provided: '" + cue
					+ "'. Valid are: 'Hybrid', 'Gold'");
		}
	}

	public Classifier(String cue, String scope) {
		this(cue);

		String scopeParam;
		String[] p;

		switch (scope.toLowerCase().substring(0, 3)) {
		case "crf":

			scopeParam = scope.substring(scope.indexOf("[") + 1,
					scope.indexOf("]"));
			p = scopeParam.split(",");
			scopeClassif = new CRFScopeDetector(scope.substring(
					scope.indexOf("(") + 1, scope.lastIndexOf(")")),
					Integer.parseInt(p[0]), Integer.parseInt(p[1]) == 1);

			break;
		case "bas":
			scopeClassif = new BaselineScopeDetector();
			break;
		case "sta":

			scopeParam = scope.substring(scope.indexOf("[") + 1,
					scope.indexOf("]"));
			p = scopeParam.split(",");
			scopeClassif = new StackedScopeDetector(scope.substring(
					scope.indexOf("(") + 1, scope.lastIndexOf(")")),
					Integer.parseInt(p[0]), Integer.parseInt(p[1]) == 1);
			break;
		default:
			throw new RuntimeException("Invalid Scope-Detector provided: '"
					+ scope + "'. Valid are: 'CRF', 'Gold', 'Baseline'");
		}
	}

	public Classifier(String cue, String scope, String stackParams) {
		this(cue);

		String scopeParam;
		String[] p;

		switch (scope.toLowerCase().substring(0, 3)) {
		case "crf":
			scopeParam = scope.substring(scope.indexOf("[") + 1,
					scope.indexOf("]"));
			p = scopeParam.split(",");
			scopeClassif = new CRFScopeDetector(scope.substring(
					scope.indexOf("(") + 1, scope.lastIndexOf(")")),
					Integer.parseInt(p[0]), Integer.parseInt(p[1]) == 1);
			break;
		case "baseline":
			scopeClassif = new BaselineScopeDetector();
			break;
		case "stacked":
			scopeParam = scope.substring(scope.indexOf("[") + 1,
					scope.indexOf("]"));
			p = scopeParam.split(",");
			scopeClassif = new StackedScopeDetector(stackParams,
					Integer.parseInt(p[0]), Integer.parseInt(p[1]) == 1);
			break;
		default:
			throw new RuntimeException("Invalid Scope-Detector provided: '"
					+ scope + "'. Valid are: 'CRF', 'Gold', 'Baseline'");
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

	@Override
	public String toString() {
		return "Classifier Using:\nCue: " + cueClassif + "\nScope:\n"
				+ scopeClassif;

	}
}

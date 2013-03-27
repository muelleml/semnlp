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
public class JKCueClassifier implements Runnable, Classifier {

	Corpus classif;

	// for threading
	Corpus classify;
	Corpus train;
	Corpus result;

	/* (non-Javadoc)
	 * @see classifier.Classifier#getResult()
	 */
	@Override
	public Corpus getResult() {
		return result;
	}

	CueClassifier cueClassif;
	ScopeClassifier scopeClassif;

	public JKCueClassifier() {
		cueClassif = new HybridCueDetector();
		scopeClassif = new CRFScopeDetector(
				"poshead(1,2,3,4,5)+baseline+posseq(10)", 150, false);

		// scopeClassif = new BaselineScopeDetector();
	}

	/**
	 * Use this constructor for Multithreading
	 * 
	 * @param train
	 * @param classify
	 */
	public JKCueClassifier(Corpus train, Corpus classify) {

		this.train = train;
		this.classify = classify;

		cueClassif = new HybridCueDetector();
		scopeClassif = new CRFScopeDetector(
				"poshead(1,2,3,4,5)+baseline+posseq(10)", 150, false);

	}

	public JKCueClassifier(CueClassifier cueClassif, ScopeClassifier scopeClassif) {
		this.cueClassif = cueClassif;
		this.scopeClassif = scopeClassif;
	}

	public JKCueClassifier(String cue) {
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

	public JKCueClassifier(String cue, String scope) {
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

	public JKCueClassifier(String cue, String scope, String stackParams) {
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

	/* (non-Javadoc)
	 * @see classifier.Classifier#train(model.Corpus)
	 */
	@Override
	public void train(Corpus c) {
		cueClassif.train(c);
	}

	/* (non-Javadoc)
	 * @see classifier.Classifier#classify(model.Corpus)
	 */
	@Override
	public Corpus classify(Corpus c) {
		classif = new Corpus();

		for (Sentence s : c.sentences) {
			classif.sentences.add(classify(s));
		}

		result = classif;

		return classif;

	}

	/* (non-Javadoc)
	 * @see classifier.Classifier#classify(model.Sentence)
	 */
	@Override
	public Sentence classify(Sentence sentence) {
		cueClassif.classify(sentence);

		try {
			sentence.ensureFinalized();
			sentence.generateTree();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sentence;
	}

	/* (non-Javadoc)
	 * @see classifier.Classifier#toString()
	 */
	@Override
	public String toString() {
		return "Classifier Using:\nCue: " + cueClassif + "\nScope:\n"
				+ scopeClassif;

	}

	@Override
	public void run() {

		train(train);
		result = classify(classify);

	}
}

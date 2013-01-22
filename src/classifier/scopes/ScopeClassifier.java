package classifier.scopes;

import model.Corpus;
import model.Sentence;

public interface ScopeClassifier {
	public void train(Corpus c);
	public void classify(Sentence sentence);
}

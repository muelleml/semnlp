package classifier.scopes;

import java.util.List;

import model.Corpus;
import model.Sentence;

public interface ScopeClassifier {
	public void train(Corpus c);
	public void classify(Sentence sentence);
	public List<String> getPredictedLabels(Sentence s, int cueIndex);
}

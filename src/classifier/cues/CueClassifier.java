package classifier.cues;

import model.Corpus;
import model.Sentence;

public interface CueClassifier {
	public void train(Corpus c);
	public void classify(Sentence sentence);
}

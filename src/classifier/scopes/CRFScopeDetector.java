package classifier.scopes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import malletwrap.ClassifyMalletCRF;
import malletwrap.TrainMalletCRF;
import model.Corpus;
import model.Sentence;
import model.Word;
import features.scope.POSSequence;
import features.scope.ScopeFeatureExtractor;
import features.scope.ScopeFeatureValue;

public class CRFScopeDetector implements ScopeClassifier {

	TrainMalletCRF scopeDetector;
	ClassifyMalletCRF scopeClassifier;


	ScopeFeatureExtractor scopeFeatureExtractor;

	public CRFScopeDetector() {
		scopeFeatureExtractor = new ScopeFeatureExtractor();
		scopeFeatureExtractor.addFeature(new POSSequence(2));
	}

	@Override
	public void train(Corpus c) {
		scopeDetector = new TrainMalletCRF(200); // 5 for tests, 200 for real 

		for (Sentence s : c.sentences) {
			List<ScopeFeatureValue> sfvList = scopeFeatureExtractor.extractTraing(s);
			for(ScopeFeatureValue sfv : sfvList) {
				scopeDetector.addSequenceInstance(sfv.labels, sfv.features);
			}
		}
		scopeClassifier = new ClassifyMalletCRF(scopeDetector.train());
	}

	@Override
	public void classify(Sentence sentence) {
		ArrayList<List<List<String>>> values = scopeFeatureExtractor.extractClassif(sentence);
		for(int cueIndex = 0; cueIndex < values.size(); cueIndex++) {
			List<List<String>> valueItem = values.get(cueIndex);
			List<String> labels = scopeClassifier.predictSequence(valueItem);
			
			Iterator<String> labelIt = labels.iterator();
			for(Word w : sentence.words) {
				if(labelIt.hasNext())  {
					String label = labelIt.next();
					
					if(label == "B" || label == "I") {
						w.cues.get(cueIndex).scope = w.lemma;
					}
					
				}
				else break;
			}
		}
	}
}

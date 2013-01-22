package classifier.scopes;

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
		scopeDetector = new TrainMalletCRF(5); // 5 for tests, 200 for real 

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
		List<String> labels = scopeClassifier.predictSequence(scopeFeatureExtractor.extractClassif(sentence));
		int cueIndex = 0;
		Iterator<String> labelIt = labels.iterator();
		String recentLabel = "";
		for(Word w : sentence.words) {
			if(cueIndex >= w.cues.size()) {
				if(labelIt.hasNext())  {
					String label = labelIt.next();
					
					if(label == "B" || label == "I") {
						w.cues.get(cueIndex).scope = w.lemma;
					}
					else if(recentLabel == "B" || recentLabel == "I") {
						cueIndex += 1;
					}
					
				}
				else break;
				
			}
		}
	}
}

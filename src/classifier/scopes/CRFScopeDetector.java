package classifier.scopes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import malletwrap.ClassifyMalletCRF;
import malletwrap.TrainMalletCRF;
import model.Corpus;
import model.Cue;
import model.Sentence;
import model.Word;
import features.scope.POSHead;
import features.scope.POSSequence;
import features.scope.ScopeFeatureExtractor;

public class CRFScopeDetector implements ScopeClassifier {
final static int CRFIterationCount = 200;
final static int POSRange = 4;
	
	
	TrainMalletCRF scopeDetector;
	ClassifyMalletCRF scopeClassifier;


	ScopeFeatureExtractor scopeFeatureExtractor;

	public CRFScopeDetector() {
		scopeFeatureExtractor = new ScopeFeatureExtractor();
		scopeFeatureExtractor.addFeature(new POSSequence(POSRange));
		scopeFeatureExtractor.addFeature(new POSHead(0, 1, 2, 3, 4));
		scopeFeatureExtractor.addFeature(new Baseline());
	}

	@Override
	public void train(Corpus c) {
		scopeDetector = new TrainMalletCRF(CRFIterationCount); 


		for (Sentence s : c.sentences) {

			String[] recentScope = null;
			
			//ArrayList<List<List<String>>> features = extractClassif(s);
			List<List<String>> labels = new LinkedList<List<String>>();
			
			for(Word w : s.words) {

				if(recentScope == null) {
					recentScope = new String[w.cues.size()];
					for(int i=0; i<recentScope.length; i++){
						recentScope[i] = "_";
					}
				}

				int i = 0;
				for(Cue cue : w.cues) {
					if(labels.size() <= i) labels.add(i, new LinkedList<String>());
					if(!cue.scope.equals("_")) 
					{
						if(recentScope[i].equals("_")) {
							labels.get(i).add("B");
						}
						else {
							labels.get(i).add("I");
						}
					}
					else labels.get(i).add("O");

					recentScope[i] = cue.scope;

					i++;
				}
			}
			
			
			List<List<List<String>>> sfvList = scopeFeatureExtractor.extractTraing(s);
			int index = 0;
			for(List<List<String>> sfv : sfvList) {
				scopeDetector.addSequenceInstance(labels.get(index), sfv);
				index++;
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
					
					if(label.equals("B") || label.equals("I")) {
						w.cues.get(cueIndex).scope = w.lemma;
					}
					
				}
				else break;
			}
		}
	}
}

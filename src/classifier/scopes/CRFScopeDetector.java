package classifier.scopes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import malletwrap.ClassifyMalletCRF;
import malletwrap.TrainMalletCRF;
import model.Corpus;
import model.Cue;
import model.Sentence;
import model.Word;
import features.scope.Baseline;
import features.scope.POSHead;
import features.scope.POSSequence;
import features.scope.ScopeFeatureExtractor;

/**
 * @author: Patrik Eckebrecht
 **/
public class CRFScopeDetector implements ScopeClassifier {
	int CRFIterationCount;
	boolean reverted;
	ClassifyMalletCRF scopeClassifier;
	ScopeFeatureExtractor scopeFeatureExtractor;

	public CRFScopeDetector(String featureSpec, int crfIterations, boolean reverted)
	{
		scopeFeatureExtractor = new ScopeFeatureExtractor();
		String[] specs = featureSpec.split("\\+");
		for(String spec : specs) {
			switch(spec.substring(0,3)) {
			case "seq": 
				scopeFeatureExtractor.addFeature(new POSSequence(Integer.parseInt(spec.substring(spec.indexOf('(')+1,spec.lastIndexOf(')')))));
				break;
			case "hea":
				scopeFeatureExtractor.addFeature(new POSHead(spec.substring(spec.indexOf('(')+1,spec.lastIndexOf(')'))));
				break;
			case "bas":
				scopeFeatureExtractor.addFeature(new Baseline());
				break;
			default:
				throw new RuntimeException("Feature Spec invalid: " + spec);
			}
		}

		this.reverted = reverted;
		CRFIterationCount = crfIterations;
	}

	@Override
	public void train(Corpus c) {
		TrainMalletCRF scopeDetector = new TrainMalletCRF(CRFIterationCount); 
		for (Sentence s : c.sentences) {

			String[] recentScope = null;
			recentScope = new String[s.words.get(0).cues.size()];
			for(int i=0; i<recentScope.length; i++){
				recentScope[i] = "_";
			}
			//ArrayList<List<List<String>>> features = extractClassif(s);
			List<List<String>> labels = new LinkedList<List<String>>();
			
			for(int cueIndex = 0; cueIndex<s.words.get(0).cues.size(); cueIndex++) {
				List<String> cueLabels = new LinkedList<>();
				labels.add(cueLabels);
				if(reverted) {
					ListIterator<Word> wordIt = s.words.listIterator(s.words.size());
					while(wordIt.hasPrevious()) {
						Word w = wordIt.previous();
						Cue cue = w.cues.get(cueIndex);
						if(cue.scope.equals("_"))
							cueLabels.add("O");
						else if(recentScope[cueIndex].equals("_"))
								cueLabels.add("B");
						else cueLabels.add("I");
						 
						recentScope[cueIndex] = cue.scope;
					}
				}
				else {
					ListIterator<Word> wordIt = s.words.listIterator(0);
					while(wordIt.hasNext()) {
						Word w = wordIt.next();
						Cue cue = w.cues.get(cueIndex);
						if(cue.scope.equals("_"))
							cueLabels.add("O");
						else if(recentScope[cueIndex].equals("_"))
								cueLabels.add("B");
						else cueLabels.add("I");
						 
						recentScope[cueIndex] = cue.scope;
					}
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

	ArrayList<List<List<String>>> values;
	Sentence recentPredictedSentence;
	
	@Override
	public void classify(Sentence sentence) {
		
		for(int cueIndex = 0; cueIndex < sentence.words.get(0).cues.size(); cueIndex++) {

			List<String> labels = getPredictedLabels(sentence, cueIndex);
			ListIterator<String> labelIt = labels.listIterator();
			if(reverted) {
				ListIterator<Word> wordIt = sentence.words.listIterator(sentence.words.size());
				while(wordIt.hasPrevious()) {
					Word w = wordIt.previous();
					if(labelIt.hasPrevious())  {
						String label = labelIt.previous();
						if(label.equals("B") || label.equals("I")) {
							w.cues.get(cueIndex).scope = w.word;
						}
					}
					else break;
				}
			}
			else {
				ListIterator<Word> wordIt = sentence.words.listIterator(0);
				while(wordIt.hasNext()) {
					Word w = wordIt.next();
					if(labelIt.hasNext())  {
						String label = labelIt.next();
						if(label.equals("B") || label.equals("I")) {
							w.cues.get(cueIndex).scope = w.word;
						}
					}
					else break;
				}
			}
		}
	}
	
	@Override
	public List<String> getPredictedLabels(Sentence s, int cueIndex)
	{
		if(values == null || recentPredictedSentence == null || recentPredictedSentence!=s) {
			values = scopeFeatureExtractor.extractClassif(s);
			recentPredictedSentence = s;
		}

		return scopeClassifier.predictSequence(values.get(cueIndex));
	}
	

	@Override
	public String toString()
	{
		return "CRF@"+CRFIterationCount+" " +(reverted?"Reverse":"Forward") +", Features: \n" + scopeFeatureExtractor;
	}
}

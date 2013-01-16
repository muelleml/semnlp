package features.scope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import model.Cue;
import model.Sentence;
import model.Word;

public class POSSequence implements ScopeFeature {

	int range;
	
	public POSSequence(int range) {
		this.range = range;
	}
	
	@Override
	public ScopeFeatureValue[] extractTrain(Sentence s) {
		ScopeFeatureValue[] value = null;
		String[] recentScope = null;
		
		for(Word w : s.words) {
			List<List<String>> features = extractClassif(s);
			if(recentScope == null) {
				recentScope = new String[w.cues.size()];
				value = new ScopeFeatureValue[w.cues.size()];
				for(int i=0; i<recentScope.length; i++){
					recentScope[i] = "_";
					value[i] = new ScopeFeatureValue();
					value[i].features = features;
				}
			}

			int i = 0;
			for(Cue cue : w.cues) {
				if(cue.scope != "_") 
				{
					if(recentScope[i] == "_") {
						value[i].labels.add("B");
					}
					else {
						value[i].labels.add("I");
					}
					break;
				}
				else value[i].labels.add("O");

				recentScope[i] = cue.scope;
				
				i++;
			}
		}
		return value;
	}

	@Override
	public ArrayList<List<List<String>>> extractClassif(Sentence s) {
		ArrayList<List<List<String>>> value =  null;
		int[] cueIndices = null;
		// Feature Liste eines Satzes
		List<List<String>> sentenceList = new LinkedList<List<String>>();
		
		int wordIndex = 0;
		for(Word w : s.words) {
			if(value == null) {
				value = new ArrayList<List<List<String>>>(w.cues.size());
				cueIndices = new int[w.cues.size()];
			}
			// Feature Liste eines Wortes
			List<String> wordList = new LinkedList<String>();
			
			// Index of Cue in Sentence speichern
			for(int i=0; i<w.cues.size(); i++) if(w.cues.get(i).cue != "_") {
				cueIndices[i] = wordIndex;
			}
			
			for(int i=-range; i<range; i++) {
				if(wordIndex+i > 0 && wordIndex+i < s.words.size()) {
					wordList.add("pos" + i + ":" + s.words.get(wordIndex+i).pos);
				}
			}
			sentenceList.add(wordList);
			wordIndex++;
		}
		
		// Cue Index einbauen
		Iterator<List<String>> wlIt = sentenceList.iterator();
		int sIndex = 0;
		for(Word w :s.words) {
			List<List<String>> temp = new LinkedList<List<String>>();
			List<String> wordList = wlIt.next();
			int i = 0;
			for(Cue c : w.cues) {
				int cueIndex = cueIndices[i];
				List<String> cueWordList = new LinkedList<String>(wordList);
				cueWordList.add(0, "index:" + (sIndex-cueIndex));
				temp.add(cueWordList);
				i++;
			}
			sIndex++;
		}
		return value;
	}
}

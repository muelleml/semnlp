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
	public ArrayList<List<List<String>>> extractClassif(Sentence s) {
		try {
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
			for(int i=0; i<w.cues.size(); i++)
			{
				if(!w.cues.get(i).cue.equals("_")) {
					cueIndices[i] = wordIndex;
				}
			}
			
			for(int i=-range; i<range; i++) {
				if(wordIndex+i > 0 && wordIndex+i < s.words.size()) {
					wordList.add("pos" + i + ":" + s.words.get(wordIndex+i).pos);
				}
				else {
					wordList.add("pos" + i + ":NULL");
				}
			}
			sentenceList.add(wordList);
			wordIndex++;
		}
		ArrayList<List<List<String>>> list = new ArrayList<List<List<String>>>(s.words.getFirst().cues.size());
		
		// Cue Index einbauen
		Iterator<List<String>> wlIt = sentenceList.iterator();
		int sIndex = 0;
		for(Word w :s.words) {
			List<String> wordList = wlIt.next();
			for(int i = 0; i<w.cues.size(); i++) {
				
				
				if(list.size() <= i) list.add(new ArrayList<List<String>>(s.words.size()));
				List<List<String>> sentence = list.get(i);
				
				
				if(sentence.size()<=sIndex) sentence.add(new LinkedList<String>());
				List<String> word = sentence.get(sIndex);
				
				int cueIndex = cueIndices[i];
				// Hier stehen wieder die POS tags
				List<String> cueWordList = new LinkedList<String>(wordList);
				// Cue Diff Index reinschreiben
				word.add("index:" + (sIndex-cueIndex));
				word.addAll(wordList);
				// Liste für dieses Wort in die Liste für diese Cue packen
//				while(value.size() <= i) value.add(new LinkedList<List<String>>());
//				value.get(i).add(cueWordList);
			}
			sIndex++;
		}
		return list;
		}
		catch(Exception e) {
			System.err.println(e);
			return new ArrayList<List<List<String>>>();
		}
	}
}

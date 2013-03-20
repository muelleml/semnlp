package features.scope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import model.Sentence;
import model.Word;

public class POSSequence implements ScopeFeature {

	int range;

	public POSSequence(int range) {
		this.range = range;
	}

	@Override
	public ArrayList<List<List<String>>> extractClassif(Sentence s) {

		int[] cueIndices = null;
		// Feature Liste eines Satzes
		List<List<String>> sentenceList = new LinkedList<List<String>>();
		int wordIndex = 0;
		cueIndices = new int[s.words.get(0).cues.size()];
		for(Word w : s.words) {
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
				// Cue Diff Index reinschreiben
				word.add("index:" + bucket(sIndex-cueIndex));
				word.addAll(wordList);
				// Liste für dieses Wort in die Liste für diese Cue packen
				//				while(value.size() <= i) value.add(new LinkedList<List<String>>());
				//				value.get(i).add(cueWordList);
			}
			sIndex++;
		}
		return list;
	}

	private int bucket(int i)
	{
		switch(i) {
		case 0: return 0;
		case 1: return 1;
		case 2: return 2;
		case 3:
		case 4: return 4;
		case 5:
		case 6:
		case 7:return 7;
		case 8:
		case 9:
		case 10:
		case 11:return 11;
		default:return 15;
		}
	}

	@Override
	public String toString()
	{
		return "POS Sequence Range: " + range;
	}

	@Override
	public List<List<String>> extractClassif(Sentence s, int cueIndex) {
		try {
			int cueLocation = -1;
			// Feature Liste eines Satzes
			List<List<String>> sentenceList = new LinkedList<List<String>>();

			int wordIndex = 0;
			for(Word w : s.words) {

				// Index of Cue in Sentence speichern
				if(!w.cues.get(cueIndex).cue.equals("_")) {
					cueLocation = wordIndex;
				}
				wordIndex++;
			}

			// Cue Index einbauen
			for(int wi=0; wi < s.words.size(); wi++) {
				// Feature Liste eines Wortes
				List<String> wordList = new LinkedList<String>();
				for(int i=-range; i<range; i++) {
					if(wi+i > 0 && wi+i < s.words.size()) {
						wordList.add("pos" + i + ":" + s.words.get(wi+i).pos);
					}
					else {
						wordList.add("pos" + i + ":NULL");
					}
				}
				wordList.add("index:" + (wi-cueLocation));

				sentenceList.add(wordList);
			}
			return sentenceList;
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}

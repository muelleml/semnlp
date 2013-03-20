package features.scope;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import model.Cue;
import model.Node;
import model.Sentence;
import model.Word;
import util.ArraySet;
import util.CueChildSelector;

public class Baseline implements ScopeFeature {

	public static int FooTrue = 0;
	public static int FooFalse = 0;

	static final String trueString = "Baseline:True";
	static final String falseString = "Baseline:False";

	static Set<String> sNodes;
	static Set<String> sBarNodes;

	static {
		sNodes = new TreeSet<String>();
		sNodes.add("S");
		sNodes.add("SINV");
		sNodes.add("SBARQ");
		sNodes.add("SQ");
		sBarNodes = new TreeSet<String>();
		sBarNodes.add("SBar");
	}

	@Override
	public ArrayList<List<List<String>>> extractClassif(Sentence s) {
		try { s.ensureFinalized();s.generateTree(); } catch(Exception e) { }

		ArrayList<List<List<String>>> value = new ArrayList<List<List<String>>>();

		if(s.words.get(0).cues.size() >0) {

			List<List<String>> sentence = new ArrayList<List<String>>();
			value.add(sentence);
			int cueIndex = 0;
			for(Word w : s.words) {
				List<String> wordList = new LinkedList<>();
				wordList.add(falseString);
				sentence.add(wordList);

			}
			int wIndex = 0;
			for(Word w : s.words) {
				if(!w.cues.get(cueIndex).cue.equals("_")) {


					Node S = w.node.findMother(sBarNodes);
					if(S==null)
						S = w.node.findMother(sNodes);
					if(S==null)
						S=w.node.findRoot();

					for(Word scopeWord : S.getAllWords()) {
						//scopeWord.cues.get(cueIndex).scope = scopeWord.lemma;
						sentence.get(wIndex).set(0, trueString);
					}

					cueIndex++;
					if(w.cues.size() == cueIndex) break;
					else {
						sentence = new LinkedList<List<String>>();
						value.add(sentence);
						for(Word x : s.words) {
							List<String> wordList = new LinkedList<>();
							wordList.add(falseString);
							sentence.add(wordList);

						}
					}

				}
				wIndex++;
			}
		}

		//		for(int i=0; i<s.words.getFirst().cues.size(); i++) {
		//			List<List<String>> sentence = new LinkedList<List<String>>();
		//			value.add(sentence);
		//
		//			for(Word w : s.words) {
		//				List<String> word = new LinkedList<String>();
		//				sentence.add(word);
		//
		//				Node mother = w.node.findMother(sBarNodes);
		//				if(mother==null)
		//					mother = w.node.findMother(sNodes);
		//				if(mother==null)
		//					mother=w.node.findRoot();
		//
		//				if(mother == null) mother = w.node.findRoot();
		//
		//				Node cueNode = mother.findChild(new CueChildSelector(i));
		//				if(cueNode==null){ FooFalse+=1; word.add(falseString); }
		//				else {
		//					FooTrue += 1; word.add(trueString); }
		//			}
		//		}
		return value;
	}
	@Override
	public String toString()
	{
		return "Baseline";
	}
	@Override
	public List<List<String>> extractClassif(Sentence s, int cueIndex)
	{
		try { s.ensureFinalized();s.generateTree(); } catch(Exception e) { }

		List<List<String>> sentence = new LinkedList<List<String>>();

		for(Word w : s.words) {
			List<String> word = new LinkedList<String>();
			sentence.add(word);

			Node mother = w.node.findMother(sBarNodes);
			if(mother==null)
				mother = w.node.findMother(sNodes);
			if(mother==null)
				mother=w.node.findRoot();

			if(mother == null) mother = w.node.findRoot();

			Node cueNode = mother.findChild(new CueChildSelector(cueIndex));
			if(cueNode==null){ FooFalse+=1; word.add(falseString); }
			else {
				FooTrue += 1; word.add(trueString); }
		}
		return sentence;
	}
}

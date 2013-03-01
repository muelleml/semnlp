package classifier.scopes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Cue;
import model.Node;
import model.Sentence;
import model.Word;
import util.ArraySet;
import features.scope.ScopeFeature;
import features.scope.ScopeFeatureValue;

public class Baseline implements ScopeFeature {

	public static int FooTrue = 0;
	public static int FooFalse = 0;
	
	static final String trueString = "Baseline:True";
	static final String falseString = "Baseline:False";
	@Override
	public ArrayList<List<List<String>>> extractClassif(Sentence s) {
		try { s.ensureFinalized(); } catch(Exception e) { }
		
		ArrayList<List<List<String>>> value = new ArrayList<List<List<String>>>();
		
		for(int i=0; i<s.words.getFirst().cues.size(); i++) {
			final int citemp = i;
			List<List<String>> sentence = new LinkedList<List<String>>();
			value.add(sentence);
			
			for(Word w : s.words) {
				List<String> word = new LinkedList<String>();
				sentence.add(word);
				Node mother = w.node.findMother(new ArraySet<String>(new String[] { "S", "SBar" }));
				if(mother == null) mother = w.node.findRoot();
				
					Node cueNode = mother.findChild(new Node.ChildSelector() {
					
						@Override
						public boolean selectChild(Node child) {
							if(child.word == null) return false;
							else return child.word.cues.get(citemp).cue.equals("_");
						}
					});
					
					if(cueNode==null){ FooFalse+=1; word.add(falseString); }
					else {
						FooTrue += 1; word.add(trueString); }
			}
		}
		return value;
	}

}

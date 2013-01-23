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

	static final String trueString = "Baseline:True";
	static final String falseString = "Baseline:False";
	@Override
	public ScopeFeatureValue[] extractTrain(Sentence s) {
		List<List<List<String>>> featureList = extractClassif(s);
		ScopeFeatureValue[] sfv = new ScopeFeatureValue[featureList.size()];
		for(int i=0; i<sfv.length; i++) {
			String recentScope = "_";
			List<String> labels = new ArrayList<String>(s.words.size());
			int sIndex = 0;
			for(Word w : s.words) {
				if(w.cues.get(i).scope.equals("_")) {
					if(recentScope.equals("_")) {
						labels.add("B");
					}
					else labels.add("I");
				}
				else labels.add("O");
				
				sIndex+=1;
			}
			sfv[i] = new ScopeFeatureValue(featureList.get(i), labels);
		}
		
		return sfv;
	}

	@Override
	public ArrayList<List<List<String>>> extractClassif(Sentence s) {
		try { s.ensureFinalized(); } catch(Exception e) { }
		
		ArrayList<List<List<String>>> value = new ArrayList<List<List<String>>>();
		
		for(@SuppressWarnings("unused") Cue c : s.words.get(0).cues) {
			value.add(new LinkedList<List<String>>());
		}
		
		for(Word w : s.words) {
			List<String> item = new LinkedList<String>();
			
			Node mother = w.node.findMother(new ArraySet<String>(new String[] { "S", "SBar" }));
			
			
			for(int ci = 0; ci < w.cues.size(); ci++) {
				final int citemp = ci;
				Node cueNode = mother.findChild(new Node.ChildSelector() {
				
					@Override
					public boolean selectChild(Node child) {
						if(child.word == null) return false;
						else if(!child.word.cues.get(citemp).equals("_")) return true;
						else return false;
					}
				});
				if(cueNode==null) item.add(falseString);
				else item.add(trueString);
				
				value.get(ci).add(item);
			}
			
		}

		
		return value;
	}

}

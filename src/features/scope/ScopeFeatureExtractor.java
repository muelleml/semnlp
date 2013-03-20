/**
 * 
 */
package features.scope;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Sentence;

/**
 * @author eckebrpk
 * 
 */
public class ScopeFeatureExtractor {
	private List<ScopeFeature> features;

	public ScopeFeatureExtractor() {
		features = new LinkedList<ScopeFeature>();
	}

	public ScopeFeatureExtractor(List<ScopeFeature> f) {
		features = f;
	}

	public void addFeatures(List<ScopeFeature> f) {
		features.addAll(f);
	}
	
	public void addFeature(ScopeFeature f) {
		features.add(f);
	}

	public List<List<List<String>>> extractTraing(Sentence s){
		List<List<List<String>>> r = new LinkedList<List<List<String>>>();

//		List<String> labels = new LinkedList<String>();
//		String[] recentScope = null;
//		
//		for(Word w : s.words) {
//			
//			if(recentScope == null) {
//				recentScope = new String[w.cues.size()];
//				for(int i=0; i<recentScope.length; i++){
//					recentScope[i] = "_";
//				}
//			}
//
//			int i = 0;
//			for(Cue cue : w.cues) {
//				if(!cue.scope.equals("_")) 
//				{
//					if(recentScope[i].equals("_")) {
//						labels.add("B");
//					}
//					else {
//						labels.add("I");
//					}
//				}
//				else labels.add("O");
//
//				recentScope[i] = cue.scope;
//				
//				i++;
//			}
//		}
//		

		for (ScopeFeature f : features){
			int i=0;
			for(List<List<String>> cueLists : f.extractClassif(s)) {
					if(r.size() <= i) r.add(i, new ArrayList<List<String>>(s.words.size()));
					int j=0; 
					for(List<String> word : cueLists) {
						if(r.get(i).size() <= j) r.get(i).add(j, new LinkedList<String>());
						r.get(i).get(j).addAll(word);
						j++;
					}
				i++;
			}
		}
		
		return r;
	}
	public List<List<String>> extractTraing(Sentence s, int cueIndex){
		List<List<String>> r = new ArrayList<List<String>>(s.words.size());


		for (ScopeFeature f : features){
			List<List<String>> cueLists = f.extractClassif(s, cueIndex);
					int j=0; 
					for(List<String> word : cueLists) {
						if(r.size() <= j) r.add(j, new LinkedList<String>());
						r.get(j).addAll(word);
						j++;
					}
			}
		
		return r;
	}
	public ArrayList<List<List<String>>> extractClassif(Sentence s){
		ArrayList<List<List<String>>> r = new ArrayList<List<List<String>>>();

		for (ScopeFeature f : features){
			int i=0;
			for(List<List<String>> cueLists : f.extractClassif(s)) {
					if(r.size() <= i) r.add(i, new ArrayList<List<String>>(s.words.size()));
					int j=0; 
					for(List<String> word : cueLists) {
						if(r.get(i).size() <= j) r.get(i).add(j, new LinkedList<String>());
						r.get(i).get(j).addAll(word);
						j++;
					}
				i++;
			}
		}
		
		return r;
	}
	
	@Override
	public String toString()
	{
		String s = "";
		for(ScopeFeature f : features)
			s += "  " + f+"\n";
		return s;
	}
}

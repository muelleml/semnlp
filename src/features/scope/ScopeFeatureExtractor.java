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

	public List<ScopeFeatureValue> extractTraing(Sentence s){
		List<ScopeFeatureValue> r = new LinkedList<ScopeFeatureValue>();
		
		for (ScopeFeature f : features){
			for(ScopeFeatureValue sfv : f.extractTrain(s)) {
				r.add(sfv);
			}
		}
		return r;
	}
	public ArrayList<List<List<String>>> extractClassif(Sentence s){
		ArrayList<List<List<String>>> r = new ArrayList<List<List<String>>>();
		
		for (ScopeFeature f : features){
			for(List<List<String>> item : f.extractClassif(s)) {
				r.add(item);
			}
		}
		
		return r;
	}
}

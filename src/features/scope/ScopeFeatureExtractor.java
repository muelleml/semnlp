/**
 * 
 */
package features.scope;

import java.util.LinkedList;
import java.util.List;

import model.Sentence;
import model.Word;

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
			r.add(f.extractTrain(s));
		}
		
		return r;
	}
	public List<List<String>> extractClassif(Sentence s){
		List<List<String>> r = new LinkedList<List<String>>();
		
		for (ScopeFeature f : features){
			r.addAll(f.extractClassif(s));
		}
		
		return r;
	}
}

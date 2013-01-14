/**
 * 
 */
package features.cue;

import java.util.LinkedList;
import java.util.List;

import model.Sentence;
import model.Word;

/**
 * @author muelleml
 * 
 */
public class CueFeatureExtractor {
	private List<CueFeature> features;

	public CueFeatureExtractor() {
		features = new LinkedList<CueFeature>();
	}

	public CueFeatureExtractor(List<CueFeature> f) {
		features = f;
	}

	public void addFeatures(List<CueFeature> f) {
		features.addAll(f);
	}
	
	public void addFeature(CueFeature f) {
		features.add(f);
	}
	
	public List<String> extract(Word w, Sentence s){
		List<String> r = new LinkedList<String>();
		
		for (CueFeature f : features){
			r.addAll(f.extract(w, s));
		}
		
		return r;
	}
}

/**
 * 
 */
package features;

import java.util.LinkedList;
import java.util.List;

import model.Sentence;
import model.Word;

/**
 * @author muelleml
 * 
 */
public class Extractor {
	private List<Feature> features;

	public Extractor() {
		features = new LinkedList<Feature>();
	}

	public Extractor(List<Feature> f) {
		features = f;
	}

	public void addFeatures(List<Feature> f) {
		features.addAll(f);
	}
	
	public void addFeature(Feature f) {
		features.add(f);
	}
	
	public List<String> extract(Word w, Sentence s){
		List<String> r = new LinkedList<String>();
		
		for (Feature f : features){
			r.addAll(f.extract(w, s));
		}
		
		return r;
	}
}

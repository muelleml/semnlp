package features.scope;

import java.util.LinkedList;
import java.util.List;

public class ScopeFeatureValue {
	public List<List<String>> features;
	public List<String> labels;
	
	public ScopeFeatureValue(List<List<String>> features, List<String> labels) {
		this.features = features;
		this.labels = labels;
	}

	public ScopeFeatureValue() {
		this.features = new LinkedList<List<String>>();
		this.labels = new LinkedList<String>();
	}
}

package features.scope;

import java.util.ArrayList;
import java.util.List;

import model.Sentence;

public interface ScopeFeature {

	public ScopeFeatureValue[] extractTrain(Sentence s);
	public List<List<String>> extractClassif(Sentence s);
}

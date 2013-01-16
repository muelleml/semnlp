package features.scope;

import java.util.ArrayList;
import java.util.List;

import model.Sentence;

public interface ScopeFeature {

	public ScopeFeatureValue[] extractTrain(Sentence s);
	public ArrayList<List<List<String>>> extractClassif(Sentence s);
}

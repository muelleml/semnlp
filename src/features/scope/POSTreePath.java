package features.scope;

import java.util.LinkedList;
import java.util.List;

import model.Cue;
import model.Node;
import model.Sentence;
import model.Word;

public class POSTreePath implements ScopeFeature {

	@Override
	public ScopeFeatureValue extractTrain(Sentence s) {
		ScopeFeatureValue value = new ScopeFeatureValue();
		String recentScope = "_";
		for(Word w : s.words) {
			String label = null;
			for(Cue cue : w.cues) {
				if(cue.scope != "_") 
				{
					if(recentScope == "_") {
						label = "B";
					}
					else label = "I";
					break;
				}

				recentScope = cue.scope;
			}
			if(label == null)label = "O";
			value.labels.add(label);

			LinkedList<String> list = new LinkedList<String>();
			for(Node node = w.node; node != null; node = node.mother) {
				list.add(node.pos);
			}
			
			value.features.add(list);
		}
		return value;
	}

	@Override
	public List<List<String>> extractClassif(Sentence s) {
		List<List<String>> value = new LinkedList<List<String>>();
		for(Word w : s.words) {
			LinkedList<String> list = new LinkedList<String>();
			for(Node node = w.node; node != null; node = node.mother) {
				list.add(node.pos);
			}
			value.add(list);
		}
		return value;
	}

}

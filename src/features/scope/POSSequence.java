package features.scope;

import java.util.LinkedList;
import java.util.List;

import model.Cue;
import model.Node;
import model.Sentence;
import model.Word;

public class POSSequence implements ScopeFeature {

	@Override
	public ScopeFeatureValue extractTrain(Sentence s) {
		ScopeFeatureValue value = new ScopeFeatureValue();
		String recentEvent = "_";
		for(Word w : s.words) {
			String label = null;
			for(Cue cue : w.cues) {
				if(cue.event != "_") 
				{
					if(recentEvent == "_") {
						label = "B";
					}
					else label = "I";
					break;
				}

				recentEvent = cue.event;
			}
			if(label == null)label = "O";
			value.labels.add(label);

			LinkedList<String> list = new LinkedList<String>();
			Node mother = w.node.mother;
			for(Node daughter : mother.daughters) {
				list.add(daughter.pos);
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
			Node mother = w.node.mother;
			for(Node daughter : mother.daughters) {
				list.add(daughter.pos);
			}
			value.add(list);
		}
		return value;
	}
}

package classifier.scopes;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import model.Corpus;
import model.Node;
import model.Sentence;
import model.Word;
import util.CueChildSelector;

/**
 * @author: Patrik Eckebrecht
 **/

public class BaselineScopeDetector implements ScopeClassifier
{
	static Set<String> sNodes;
	static Set<String> sBarNodes;

	static {
		sNodes = new TreeSet<String>();
				sNodes.add("S");
				sNodes.add("SINV");
				sNodes.add("SQ");
		sBarNodes = new TreeSet<String>();
		sBarNodes.add("SBar");
		sBarNodes.add("SBARQ");
	}

	@Override
	public void train(Corpus c)
	{
	}

	@Override
	public void classify(Sentence sentence)
	{
		if(sentence.words.get(0).cues.size() >0) {
			int cueIndex = 0;
			for(Word w : sentence.words) {
				if(!w.cues.get(cueIndex).cue.equals("_")) {


					Node S = w.node.findMother(sBarNodes);
					if(S==null)
						S = w.node.findMother(sNodes);
					if(S==null)
						S=w.node.findRoot();

					for(Word scopeWord : S.getAllWords()) {
						if(scopeWord.cues.get(cueIndex).cue.equals("_"))
							scopeWord.cues.get(cueIndex).scope = scopeWord.word;
					}

					cueIndex++;
					if(w.cues.size() == cueIndex) break;

				}
			}
		}

		//		for(Word w : sentence.words) {
		//			Node S = w.node.findMother(sBarNodes);
		//			if(S==null)
		//				S = w.node.findMother(sNodes);
		//			if(S==null)
		//				S=w.node.findRoot();
		//			int cueIndex = 0;
		//			for(Cue c : w.cues){
		//				Node currentCue = S.findChild(new CueChildSelector(cueIndex));
		//				if(currentCue != null && currentCue.word != w)
		//				{
		//					c.scope = w.lemma;
		//				}
		//				cueIndex++;
		//			}
		//		}
	}
	@Override
	public List<String> getPredictedLabels(Sentence sentence, int cueIndex)
	{
		String recentLabel = "O";
		List<String> labels = new LinkedList<>();
		for(Word w : sentence.words) {
			Node S = w.node.findMother(sBarNodes);
			if(S==null)
				S = w.node.findMother(sNodes);
			if(S==null)
				S=w.node.findRoot();
			Node currentCue = S.findChild(new CueChildSelector(cueIndex));
			String label;
			if(currentCue != null && currentCue.word != w)
			{
				if(recentLabel.equals("O"))
					label = ("B");
				else label = ("I");
			}
			else label = ("O");
			labels.add(label);
			recentLabel = label;
		}
		return labels;
	}

	@Override
	public String toString()
	{
		return "Baseline";
	}
}

package classifier.scopes;

import java.util.Set;
import java.util.TreeSet;

import model.Corpus;
import model.Cue;
import model.Node;
import model.Node.ChildSelector;
import model.Sentence;
import model.Word;

public class BaselineScopeDetector implements ScopeClassifier
{
	private class CueChildSelector implements ChildSelector {
		private int cueIndex;
		public CueChildSelector(int cueIndex) {
			this.cueIndex = cueIndex;
		}
		@Override
		public boolean selectChild(Node child)
		{
			try {
				return !child.word.cues.get(cueIndex).cue.equals("_");
			}
			catch(IndexOutOfBoundsException e) {
				return false;
			}
			catch(NullPointerException ne) {
				return false;
			}
		}
	}
	static Set<String> sNodes;
	static Set<String> sBarNodes;
	
	static {
		sNodes = new TreeSet<String>();
		sNodes.add("S");
		sNodes.add("SINV");
		sNodes.add("SBARQ");
		sNodes.add("SQ");
		sBarNodes = new TreeSet<String>();
		sBarNodes.add("SBar");
	}

	@Override
	public void train(Corpus c)
	{
	}

	@Override
	public void classify(Sentence sentence)
	{
		for(Word w : sentence.words) {
			Node S = w.node.findMother(sBarNodes);
			if(S==null)
				S = w.node.findMother(sNodes);
			if(S==null)
				S=w.node.findRoot();
			int cueIndex = 0;
			for(Cue c : w.cues){
				Node currentCue = S.findChild(new CueChildSelector(cueIndex));
				if(currentCue != null && currentCue.word != w)
				{
					c.scope = w.lemma;
				}
				cueIndex++;
			}
		}
	}

}

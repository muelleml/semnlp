package util;

import model.Node;
import model.Node.ChildSelector;

	public class CueChildSelector implements ChildSelector {
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
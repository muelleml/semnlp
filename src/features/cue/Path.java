/**
 * 
 */
package features.cue;

import java.util.LinkedList;
import java.util.List;

import model.Node;
import model.Sentence;
import model.Word;

/**
 * @author muelleml
 * 
 */
public class Path implements CueFeature {

	int up;

	public Path(int up) {

		this.up = up;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see features.cue.CueFeature#extract(model.Word, model.Sentence)
	 */
	@Override
	public List<String> extract(Word w, Sentence s) {

		String prefix = "PATH ";

		List<String> r = new LinkedList<String>();

		Node tNode = w.node;

		int i = 0;
		while (i <= up) {

			if (tNode != null) {
				r.add(prefix + Integer.toString(i) + tNode.pos);
				tNode = tNode.mother;
			} else {
				r.add(prefix + Integer.toString(i) + "NULL");
			}

			i++;

		}

		return r;
	}
}

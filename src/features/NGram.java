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
public class NGram implements Feature {

	/*
	 * (non-Javadoc)
	 * 
	 * @see features.Feature#extract(model.Word, model.Sentence)
	 */
	@Override
	public List<String> extract(Word w, Sentence s) {
		List<String> r = new LinkedList<String>();
		
		return r;
	}

}

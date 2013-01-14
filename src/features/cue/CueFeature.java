/**
 * 
 */
package features.cue;

import java.util.List;

import model.Sentence;
import model.Word;

/**
 * @author muelleml
 * 
 */
public interface CueFeature {

	public List<String> extract(Word w, Sentence s);

}

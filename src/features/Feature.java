/**
 * 
 */
package features;

import java.util.List;

import model.Sentence;
import model.Word;

/**
 * @author muelleml
 * 
 */
public interface Feature {

	public List<String> extract(Word w, Sentence s);

}

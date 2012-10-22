/**
 * 
 */
package classifier;

import model.Corpus;
import model.Sentence;
import model.Word;

/**
 * @author muelleml
 * 
 */
public interface Classifier {

	public void train(Corpus c);

	public Corpus classify(Corpus c);

	public Sentence classify(Sentence s);

	public Word classify(Word w);
	
	
		
	public Metric metrics(Corpus check);

}

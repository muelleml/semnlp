package classifier;

import model.Corpus;
import model.Sentence;

public interface Classifier {

	public abstract Corpus getResult();

	public abstract void train(Corpus c);

	public abstract Corpus classify(Corpus c);

	public abstract Sentence classify(Sentence sentence);

	public abstract String toString();

}
package main;

import io.ConllWriter;
import io.PartitionReader;

import java.util.concurrent.Semaphore;

import model.Corpus;
import classifier.Classifier;
import classifier.StandardClassifier;

public class CrossValidator
{

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(final String[] args) throws InterruptedException
	{
		if(args.length != 2) {
			throw new RuntimeException("Usage: semnlp <input-dir> <output-dir>");
		}


		final Corpus[] corpi = PartitionReader.readPartitionFolder(args[0]);
		final Corpus[] result = new Corpus[corpi.length+1];
		result[0] = new Corpus();
		final Semaphore mutex = new Semaphore(0);

		for(int r = 0; r < corpi.length; r++) {
			final int run = r; 
			new Thread(new Runnable() {

				@Override
				public void run() {
					Classifier classifier = new StandardClassifier();
					Corpus train = new Corpus();
					for(int i=0; i<corpi.length; i++) {
						if(i!=run)
							train.addCorpus(corpi[i]);
					}
					classifier.train(train);
					result[run+1] = classifier.classify(corpi[run]);

					mutex.release();
				}
			}).start();
		}
		mutex.acquire(corpi.length);
		for(int i=1; i<result.length; i++) {
			result[0].addCorpus(result[i]);
		}
		for(int i=0; i<result.length; i++) {
			final int run = i;
			new Thread(new Runnable() {
				@Override
				public void run() {
					ConllWriter.write(result[run], args[1] + "/p" + run + ".txt");
					mutex.release();
				}
			}).start();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				Corpus gold = new Corpus();
				for(Corpus c : corpi) gold.addCorpus(c);
				ConllWriter.write(gold, "result/gold.txt");
				mutex.release();
			}
		}).start();

		mutex.acquire(corpi.length+1);
	}

}

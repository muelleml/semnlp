package main;
import io.ConllWriter;
import io.PartitionReader;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import model.Corpus;
import classifier.Classifier;
import classifier.DemClassifier;

public class Application {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InterruptedException, IOException {
		final Corpus[] corpi = PartitionReader.readPartitionFolder("partitions");
		final Corpus[] result = new Corpus[corpi.length+1];
		result[0] = new Corpus();
		final Semaphore mutex = new Semaphore(0);
		
		for(int r = 0; r < corpi.length; r++) {
			final int run = r; 
			new Thread(new Runnable() {
				
				@Override
				public void run() {

					Classifier dem = new DemClassifier();
					Corpus train = new Corpus();
					for(int i=0; i<corpi.length; i++) {
						if(i!=run) train.addCorpus(corpi[i]);
					}
					dem.train(train);
					result[run+1] = dem.classify(corpi[run]);
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
					ConllWriter.write(result[run], "result/p" + run + ".txt");
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

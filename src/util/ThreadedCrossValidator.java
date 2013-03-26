package util;

import io.EvaluationReader;
import io.PartitionReader;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import model.Corpus;
import model.Cue;
import model.Metric;
import model.Metrics;
import model.Sentence;
import model.Word;
import classifier.Classifier;
import classifier.StandardClassifier;

public class ThreadedCrossValidator {

	/**
	 * Does CrossValidation.
	 * 
	 * @param partitionFolder
	 *            Folder containing the Partitions
	 * @param classifier
	 *            The Classifier Object to use
	 * @return A Metrics Object containing both Micro and Macro averages
	 * @throws InterruptedException
	 */

	public static Metrics CrossValidate(String partitionFolder,
			Classifier classifier) throws InterruptedException {
		Metrics r = new Metrics();
		
		List<StandardClassifier> clList = new LinkedList<StandardClassifier>();

		// including virtual processors eg on Intel
		int nrCores = Runtime.getRuntime().availableProcessors();

		// leave at least one core for other stuff if possible
		ExecutorService executor = Executors.newFixedThreadPool(Math.max(
				nrCores - 1, 1));

		// classifier.getClass().ge

		Corpus[] testPartitions = PartitionReader
				.readPartitionFolder(partitionFolder);

		Corpus[] trainPartitions;

		int size = testPartitions.length;

		Corpus microAverage = new Corpus();

		Corpus test;

		Corpus train;

		for (int i = 0; i < size; i++) {

			train = new Corpus();

			trainPartitions = PartitionReader
					.readPartitionFolder(partitionFolder);
			test = trainPartitions[i];

			for (Corpus c : trainPartitions) {
				if (c != test) {
					for (Sentence s : c.sentences) {
						train.sentences.add(s);
					}
				}
			}

			Runnable myCl = new StandardClassifier(train, test);
			
			clList.add((StandardClassifier) myCl);

			System.out.println("Starting a Thread with: "
					+ classifier.getClass().getName());
			executor.execute(myCl);

		}

		
		executor.shutdown();
		
		while (!executor.isTerminated()) {
		}

		Corpus[] tGold = PartitionReader.readPartitionFolder(partitionFolder);
		
		int i = 0;
		for (Classifier c : clList){
			test = tGold[i];
			Corpus sys = c.getResult();
			
			for (Sentence s : sys.sentences) {
				microAverage.sentences.add(s);
			}
			
			Metric m = EvaluationReader.readScope(test, sys);
			r.addMetric(m);
			i++;
		}
		
		

		Corpus gold = new Corpus();

		for (Corpus c : tGold) {

			for (Sentence s : c.sentences) {
				gold.sentences.add(s);
			}
		}

		Metric m = EvaluationReader.readScope(gold, microAverage);
		r.addMicroAverage(m);

		return r;
	}
}
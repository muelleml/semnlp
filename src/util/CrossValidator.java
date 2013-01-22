package util;

import io.EvaluationReader;
import io.PartitionReader;

import java.util.LinkedList;

import model.Corpus;
import model.Cue;
import model.Metric;
import model.Metrics;
import model.Sentence;
import model.Word;
import classifier.Classifier;

public class CrossValidator {

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

		Corpus[] testPartitions = PartitionReader.readPartitionFolder(partitionFolder);

		Corpus[] trainPartitions;

		int size = testPartitions.length;

		Corpus microAverage = new Corpus();

		Corpus test;

		Corpus train;

		Corpus classify;

		for (int i = 0; i < size; i++) {

			train = new Corpus();

			classify = testPartitions[i];

			trainPartitions = PartitionReader
					.readPartitionFolder(partitionFolder);
			test = trainPartitions[i];
			

			for (Corpus c : trainPartitions) {
				if(c != test) {
					for (Sentence s : c.sentences) {
						train.sentences.add(s);
					}
				}
			}

			for (Sentence s : classify.sentences) {
				for (Word w : s.words) {
					w.cues = new LinkedList<Cue>();
				}
			}

			System.out.println("Training with: "
					+ classifier.getClass().getName());
			classifier.train(train);

			System.out.println("Classifying with: "
					+ classifier.getClass().getName());
			Corpus sys = classifier.classify(test);
			Metric m = EvaluationReader.readScope(test, sys);
			r.addMetric(m);

			for (Sentence s : sys.sentences) {
				microAverage.sentences.add(s);
			}

			System.out.println(m.toString());

		}

		Corpus[] tGold = PartitionReader.readPartitionFolder(partitionFolder);

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
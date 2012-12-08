package util;

import io.PartitionReader;
import io.EvaluationReader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import classifier.Classifier;

import model.Corpus;
import model.Cue;
import model.Metric;
import model.Metrics;
import model.Sentence;
import model.Word;

public class CrossValidator {

	/**
	 * Does CrossValidation.
	 * 
	 * @param partitionFolder
	 *            Folder containing the Partitions
	 * @param classifier
	 *            The Classifier Object to use
	 * @return A Metrics Object containing both Micro and Macro averages
	 */

	public static Metrics CrossValidate(String partitionFolder,
			Classifier classifier) {
		Metrics r = new Metrics();

		List<Corpus> testPartitions = PartitionReader
				.readPartitionFolder(partitionFolder);

		List<Corpus> trainPartitions;

		int size = testPartitions.size();

		Corpus microAverage = new Corpus();

		Corpus test;

		Corpus train;

		Corpus classify;

		for (int i = 0; i < size; i++) {

			train = new Corpus();

			classify = testPartitions.get(i);

			trainPartitions = PartitionReader
					.readPartitionFolder(partitionFolder);

			test = trainPartitions.get(i);

			trainPartitions.remove(test);

			for (Corpus c : trainPartitions) {

				for (Sentence s : c.sentences) {
					train.sentences.add(s);
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

		List<Corpus> tGold = PartitionReader
				.readPartitionFolder(partitionFolder);

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
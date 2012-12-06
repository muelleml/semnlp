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
	 * Does CrossValidation on multiple Partitions
	 * 
	 * @param partitionFolder
	 *            Folder where the test Partitions are
	 * @param testPartitions
	 *            # of Partitions to test on
	 * @param classifier
	 *            The Classifier Object to use
	 * @return the average Metric
	 */
	public static Metric CrossValidate(String partitionFolder,
			int testPartitions, Classifier classifier) {
		List<Corpus> cList = PartitionReader
				.readPartitionFolder(partitionFolder);

		List<Corpus> cGold = PartitionReader
				.readPartitionFolder(partitionFolder);

		return CrossValidate(cList, cGold, testPartitions, classifier);
	}

	/**
	 * Does CrossValidation on multiple Partitions
	 * 
	 * @param partitions
	 *            The Partitions to use
	 * @param cGold
	 *            The Gold Partitions. This has to be the same as the test
	 *            Partitions
	 * @param testPartitions
	 *            # of Partitions to test on
	 * @param classifier
	 *            The Classifier Object to use
	 * @return the average Metric
	 */
	public static Metric CrossValidate(List<Corpus> partitions,
			List<Corpus> cGold, int testPartitions, Classifier classifier) {
		Metrics r = new Metrics();

		if (1 > partitions.size() - testPartitions) {
			System.out.println("not enough partitions for crossvalidation!");
		} else {

			List<Corpus> tBeg;
			List<Corpus> tEnd;
			List<Corpus> tTest;
			List<Corpus> tGold;

			for (int i = testPartitions; i <= partitions.size()
					- testPartitions; i++) {

				System.out.println("Crossvalidation run #"
						+ Integer.toString(i));

				// corpus thats used for training
				Corpus train = new Corpus();
				// corpus to classify
				Corpus test = new Corpus();
				// gold Corpus
				Corpus gold = new Corpus();

				tBeg = cGold.subList(0, i - 1);
				tTest = partitions.subList(i - testPartitions, i);
				tGold = cGold.subList(i - testPartitions, i);
				tEnd = cGold.subList(i + 1, partitions.size());

				List<Corpus> tTrain = new LinkedList<Corpus>();
				tTrain.addAll(tBeg);
				tTrain.addAll(tEnd);

				// add cues and build training Corpus
				for (Corpus c : tTrain) {
					for (Sentence s : c.sentences) {
						train.sentences.add(s);
					}
				}

				// build Corpus to classify
				for (Corpus c : tTest) {
					for (Sentence s : c.sentences) {
						for (Word w : s.words) {
							w.cues = new LinkedList<Cue>();
						}
						test.sentences.add(s);
					}
				}

				// build Gold Corpus
				for (Corpus c : tGold) {
					for (Sentence s : c.sentences) {
						gold.sentences.add(s);
					}
				}

				classifier.train(train);

				Corpus sys = classifier.classify(test);
				Metric m = EvaluationReader.readScope(gold, sys);
				r.addMetric(m);

			}

		}

		return r.averages();
	}
}

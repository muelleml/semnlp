package util;

import model.Metrics;
import classifier.Classifier;

public class ThreadedDemo {

	public static void DoDemo(Classifier classifier) throws InterruptedException {
		String partitions = "partitions";
		String partitionsOnlyAffixes = "partitions/onlyAffixes";

		Metrics cross;
		Metrics crossOnlyAffixes;

		cross = ThreadedCrossValidator.CrossValidate(partitions, classifier);
		crossOnlyAffixes = ThreadedCrossValidator.CrossValidate(partitionsOnlyAffixes,
				classifier);
		
		System.out.println();
		System.out.println("Metrics for CrossValidation on devSet with: " + classifier.getClass().getSimpleName());
		System.out.println(cross.toString());
		System.out.println();
		
		System.out.println("Metrics for CrossValidation on OnlyAffixes with: " + classifier.getClass().getSimpleName());
		System.out.println(crossOnlyAffixes.toString());
		System.out.println();
		
		
		/*
		System.out.println("Metrics for normal run on devSet with: " + classifier.getClass().getSimpleName());
		System.out.println(dev.toString());
		System.out.println();
		
		System.out.println("Metrics for normal run  on OnlyAffixes with: " + classifier.getClass().getSimpleName());
		System.out.println(devOnlyAffixes.toString());
		*/
	}
}

package util;

import io.ConllReader;
import io.EvaluationReader;
import model.Corpus;
import model.Metric;
import model.Metrics;
import classifier.Classifier;

public class Demo {

	public static void Demo(Classifier classifier) {
		String partitions = "partitions";
		String partitionsOnlyAffixes = "partitions/onlyAffixes";

		Metrics cross;
		Metrics crossOnlyAffixes;

		Metric dev;
		Metric devOnlyAffixes;

		String devTrainSource = "starsem-st-2012-data/cd-sco/corpus/training/SEM-2012-SharedTask-CD-SCO-training-09032012.txt";
		String devTestSource = "starsem-st-2012-data/cd-sco/corpus/dev/SEM-2012-SharedTask-CD-SCO-dev-17102012-NO-GOLD.txt";
		String devGoldSource = "starsem-st-2012-data/cd-sco/corpus/dev/SEM-2012-SharedTask-CD-SCO-dev-09032012.txt";

		Corpus devTrain = ConllReader.read(devTrainSource);
		Corpus devTest = ConllReader.read(devTestSource);
		Corpus devGold = ConllReader.read(devGoldSource);
		classifier.train(devTrain);
		Corpus devSys = classifier.classify(devTest);

		dev = EvaluationReader.readScope(devGold, devSys);

		String devTrainSourceOnlyAffixes = "starsem-st-2012-data/extra/only_affix_cues/training-set-only-affix-cues.txt";
		String devTestSourceOnlyAffixes = "starsem-st-2012-data/cd-sco/corpus/dev/SEM-2012-SharedTask-CD-SCO-dev-17102012-NO-GOLD.txt";
		String devGoldSourceOnlyAffixes = "starsem-st-2012-data/extra/only_affix_cues/dev-set-only-affix-cues.txt";

		Corpus devTrainOnlyAffixes = ConllReader
				.read(devTrainSourceOnlyAffixes);
		Corpus devTestOnlyAffixes = ConllReader.read(devTestSourceOnlyAffixes);
		Corpus devGoldOnlyAffixes = ConllReader.read(devGoldSourceOnlyAffixes);
		classifier.train(devTrainOnlyAffixes);
		Corpus devSysOnlyAffixes = classifier.classify(devTestOnlyAffixes);

		devOnlyAffixes = EvaluationReader.readScope(devGoldOnlyAffixes,
				devSysOnlyAffixes);

		cross = CrossValidator.CrossValidate(partitions, classifier);
		crossOnlyAffixes = CrossValidator.CrossValidate(partitionsOnlyAffixes,
				classifier);
		
		System.out.println();
		System.out.println("Metrics for CrossValidation on devSet with: " + classifier.getClass().getSimpleName());
		System.out.println(cross.toString());
		System.out.println();
		
		System.out.println("Metrics for CrossValidation on OnlyAffixes with: " + classifier.getClass().getSimpleName());
		System.out.println(crossOnlyAffixes.toString());
		System.out.println();
		
		System.out.println("Metrics for normal run on devSet with: " + classifier.getClass().getSimpleName());
		System.out.println(dev.toString());
		System.out.println();
		
		System.out.println("Metrics for normal run  on OnlyAffixes with: " + classifier.getClass().getSimpleName());
		System.out.println(devOnlyAffixes.toString());

	}
}

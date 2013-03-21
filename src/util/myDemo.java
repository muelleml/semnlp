package util;

import model.Corpus;
import model.Metric;
import io.ConllReader;
import io.EvaluationReader;
import classifier.Classifier;

public class myDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Classifier cl = new Classifier();
			//model.Corpus c = ConllReader.read("starsem-st-2012-data/extra/only_affix_cues/training-set-only-affix-cues.txt");
			//cl.train(c);
			ThreadedDemo.DoDemo(cl);
			
			/*
			String devTrainSourceOnlyAffixes = "starsem-st-2012-data/extra/only_affix_cues/training-set-only-affix-cues.txt";
			String devTestSourceOnlyAffixes = "starsem-st-2012-data/cd-sco/corpus/dev/SEM-2012-SharedTask-CD-SCO-dev-17102012-NO-GOLD.txt";
			String devGoldSourceOnlyAffixes = "starsem-st-2012-data/extra/only_affix_cues/dev-set-only-affix-cues.txt";

			Corpus devTrainOnlyAffixes = ConllReader
					.read(devTrainSourceOnlyAffixes);
			Corpus devTestOnlyAffixes = ConllReader.read(devTestSourceOnlyAffixes);
			Corpus devGoldOnlyAffixes = ConllReader.read(devGoldSourceOnlyAffixes);
			Classifier classifier = new Classifier();
			classifier.train(devTrainOnlyAffixes);
			Corpus devSysOnlyAffixes = classifier.classify(devTestOnlyAffixes);

			Metric devOnlyAffixes = EvaluationReader.readScope(devGoldOnlyAffixes,
					devSysOnlyAffixes);
					System.out.println(devOnlyAffixes.toString());
					
			*/
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

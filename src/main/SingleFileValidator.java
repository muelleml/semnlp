package main;
import features.scope.Baseline;
import io.ConllReader;
import io.ConllWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Date;

import model.Corpus;
import util.Sysout;
import classifier.Classifier;

public class SingleFileValidator {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void main(final String[] args) throws InterruptedException, IOException {
		if(args.length >= 3) {
			Sysout.init();
			Date start = new Date();
			
			Corpus train = ConllReader.read(args[0]);
			Corpus test = ConllReader.read(args[1]);
			Classifier classif;
			if(args.length == 6)
				classif = new Classifier(args[3], args[4], args[5]);
			else if(args.length ==5)
				classif = new Classifier(args[3], args[4]);
			else if(args.length == 4)
				classif = new Classifier(args[3]);
			else 
				classif = new Classifier();
			
			Sysout.out.println("Training");
			classif.train(train);
			train = null;
			Sysout.out.println("Classification");
			Corpus result = classif.classify(test);
			Sysout.out.println("Done");
			ConllWriter.write(result, args[2]);
			Sysout.out.println("Finished. Took: " + new Date(start.getTime() - new Date().getTime()));
		}
		else {
			Sysout.out.println("Usage:   SingleFileValidator train test output [Cue-Detector:{hybrid|gold}] [Scope-Detector:{CRF[iterations,reverted=1](featureSpec)|Gold|Baseline|Stacked[iterations,reverted=1]featureSpec=({crf|stacked}[iterations,reverted=1](featureSpec);...)}");
			Sysout.out.println("FeatureSpec: crf: {baseline|posseq(range)|poshead(orders)}+...");
			Sysout.out.println("Example: SingleFileValidator train test output hybrid stacked crf(poshead(1,2)+baseline);crf_rev(posseq(4)+baseline)");
			Sysout.out.println("Using train-Data, running on test-Data and writing results to output-File, using a Hybrid-Cue-Detector and a Stacked Scope Detector, which uses two CRF-Scope-Detectors.)");
			Sysout.out.println("First one: Features: POSHead(Orders= 1 & 2) + Baseline, Second one reverted, Features: POSSequence(Range=4) + Baseline)");
			Sysout.out.println("Instead of a CRF-Detector, antother StackedDetector can be used. Unlimited nesting. Yo dawg ;-D");
					
		}
	}

}

package main;
import io.ConllReader;
import io.ConllWriter;

import java.io.IOException;
import java.io.PrintStream;

import model.Corpus;
import classifier.Classifier;

public class SingleFileValidator {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static PrintStream out;
	public static void main(final String[] args) throws InterruptedException, IOException {
		if(args.length >= 3) {
			Corpus train = ConllReader.read(args[0]);
			Corpus test = ConllReader.read(args[1]);
			Classifier classif;
			if(args.length ==5)
				classif = new Classifier(args[3], args[4]);
			else if(args.length ==4)
				classif = new Classifier(args[3]);
			else 
				classif = new Classifier();
			classif.train(train);
			Corpus result = classif.classify(test);
			ConllWriter.write(result, args[2]);
		}
		else {
			System.err.println("Usage: SingleFileValidator train test output [Cue-Detector:{hybrid|gold}] [Scope-Detector:{CRF|Gold|Baseline}]");
		}
	}

}

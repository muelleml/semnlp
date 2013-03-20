package main;

import io.ConllReader;
import io.ConllWriter;
import model.Corpus;
import util.Sysout;
import classifier.Classifier;

public class MultiConfigValidator
{
	public static void main(final String[] args) throws InterruptedException {
		Sysout.init();

		final String[] configs = new String[] {
				"stacked[200,0,0](crf[200,0](baseline+seq(4)+hea(1,2,3));baseline;seq(4);hea(1,2,3);crf[200,1,0](baseline+seq(4)+hea(1,2,3)))",
				"stacked[200,0,0](crf[200,0](baseline+seq(4)+hea(1,2,3));baseline;seq(4);hea(1,2,3))",
				"crf[200,0](baseline+seq(4)+hea(1,2,3))",
				"stacked[200,0,1](crf[200,0](baseline+seq(4)+hea(1,2,3));baseline;seq(4);hea(1,2,3);crf[200,1,1](baseline+seq(4)+hea(1,2,3)))",
				"stacked[200,0,1](crf[200,0](baseline+seq(4)+hea(1,2,3));baseline;seq(4);hea(1,2,3))",
				"crf[200,0](baseline+seq(4)+hea(1,2,3))"
		};

		for(int i=0; i<configs.length; i++) {
			final int index = i;
//			new Thread(new Runnable()
//			{
//
//				@Override
//				public void run()
//				{
					try {
						Corpus train = ConllReader.read(args[0]);
						Corpus test = ConllReader.read(args[1]);
						Classifier classif;
						classif = new Classifier(args[2], configs[index]);
						Sysout.out.println(index + ": Training");
						classif.train(train);
						Sysout.out.println(index + ": Classification");
						Corpus result = classif.classify(test);
						Sysout.out.println(index + ": Done");
						ConllWriter.write(result, "result" + index + ".txt");
					}
					catch(Throwable e) {
						e.printStackTrace(Sysout.err);
					}
//				}
//			}).start();
		}
	}
}

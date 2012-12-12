import java.util.List;

import util.Demo;

import classifier.Baseline;
import classifier.Classifier;
import classifier.DemClassifier;
import io.ConllReader;
import io.ConllWriter;
import io.PartitionReader;
import model.Corpus;
import model.Sentence;
import model.Word;

public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Demo.Demo(new DemClassifier());
		
/*		Corpus in = ConllReader.read("train.txt");
		
		
		Corpus test = ConllReader.read("test-NO_GOLD.txt");
		Corpus check = ConllReader.read("test.txt");
		
		//System.out.print(in.toString());
		
		Classifier b = new Baseline();
		Classifier dem = new DemClassifier();
		b.train(in);
		dem.train(in);
		dem.classify(test);
		Corpus res = b.classify(test);

		ConllWriter.write(res, "testout.txt");
		
		List<Corpus> c = PartitionReader.readPartitionFolder("partitions");
		System.out.println("#Partition: " + Integer.toString(c.size()));
		
		
		for (Sentence s : in.sentences){
			for (Word w : s.words){
				//System.out.println(w.cues.size());
				
			}
		}*/
	
	}

}

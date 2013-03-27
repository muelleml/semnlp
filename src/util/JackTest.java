/**
 * 
 */
package util;

import classifier.Classifier;
import classifier.JKCueClassifier;
import model.Corpus;
import model.Metrics;
import io.ConllReader;
import io.ConllWriter;

/**
 * @author muelleml
 *
 */
public class JackTest {

	public static void main(String[] args){
	
		Classifier jk1 = new JKCueClassifier();
		
		Metrics m = new Metrics();
		
		try {
			JackKnifeThreadedCrossValidatorStage1.CrossValidate("partitions",jk1);
			m = JackKnifeThreadedCrossValidatorStage2.CrossValidate("jackKnifePartionsS1",jk1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(m.toString());
		
		
	/*
	Corpus aligned;
	Corpus gold = ConllReader.read("testOutput/microaverage.txt");
	Corpus microaverage = ConllReader.read("testOutput/gold.txt");
	
	aligned = JackKnifeAligner.align(microaverage, gold);
	
	ConllWriter.write(aligned, "testOutput/aligned.txt");
	*/
	//ConllReader.read(aligned, "testOutput/aligned.txt");
	//ConllReader.read(microAverage, "testOutput/microaverage.txt");
	//ConllReader.  (gold, "testOutput/gold.txt");
	}
}

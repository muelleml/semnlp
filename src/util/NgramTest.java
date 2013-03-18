package util;

import java.util.List;

import io.ConllReader;
import features.cue.NGram;

public class NgramTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String devTrainSource = "starsem-st-2012-data/cd-sco/corpus/training/SEM-2012-SharedTask-CD-SCO-training-09032012.txt";
		String devTestSource = "starsem-st-2012-data/cd-sco/corpus/dev/SEM-2012-SharedTask-CD-SCO-dev-17102012-NO-GOLD.txt";
		String devGoldSource = "starsem-st-2012-data/cd-sco/corpus/dev/SEM-2012-SharedTask-CD-SCO-dev-09032012.txt";

		model.Corpus c = ConllReader.read(devTrainSource);
		model.Sentence s = c.sentences.get(3);
		
		NGram n = new NGram(1, 4, 6, true);
		List<String> r = n.extract(s.words.get(10), s);
		
		System.out.println(s.toString());
		
		for (String i : r){
			System.out.println(i);
		}
		
	}

}

package classifier.cues;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import malletwrap.ClassifyMalletMaxEnt;
import malletwrap.TrainMalletMaxEnt;
import model.Corpus;
import model.Cue;
import model.Sentence;
import model.Word;
import features.cue.CueFeature;
import features.cue.CueFeatureExtractor;
import features.cue.Lemma;
import features.cue.NGram;
import features.cue.POS;
/**
 * @author: Manuel Müller
 **/

public class HybridCueDetector implements CueClassifier {
	final static String nonAffixCue = "nonAffixCue";

	ClassifyMalletMaxEnt cueClassifier;
	TrainMalletMaxEnt cueDetector;
	CueFeatureExtractor cueFeatureExtractor;
	
	List<CueFeature> cueFeatureList;
	
	Set<String> affixCues;
    Set<String> lu;
	
	public HybridCueDetector() {
	
		// configure the featureExtractor
		cueFeatureExtractor = new CueFeatureExtractor();
		cueFeatureExtractor.addFeature(new POS(0));
		cueFeatureExtractor.addFeature(new POS(-1));
		cueFeatureExtractor.addFeature(new POS(-2));
		cueFeatureExtractor.addFeature(new POS(1));
		cueFeatureExtractor.addFeature(new Lemma());
		cueFeatureExtractor.addFeature(new NGram(1, 3, 4, true));
		cueFeatureExtractor.addFeature(new NGram(1, 3, 5, false));
	}

	@Override
	public void train(Corpus c) {
		cueDetector = new TrainMalletMaxEnt();
		lu = new TreeSet<String>();
		affixCues = new TreeSet<String>();

		for (Sentence s : c.sentences) {
			List<Integer> cueList = new LinkedList<Integer>();

			int max = s.words.get(0).cues.size();

			int cueIndex = 0;

			// build vertical cue list to exclude multiword cues

			while (max > cueIndex) {

				int cueLength = 0;
				Cue cue;
				for (Word w : s.words) {
					cue = w.cues.get(cueIndex);
					if (!cue.cue.equals("_")) {
						cueLength++;
					}
				}

				if (cueLength == 1) {
					cueList.add(cueIndex);
				}

				cueIndex++;
			}

			for (Integer i : cueList) {
				for (Word w : s.words) {
					Cue cue = w.cues.get(i);

					// experiment! also in classifyWord
					if (cue.cue.equals(w.word)) {
						lu.add(cue.cue.toLowerCase());
					} 
					else if (!cue.cue.equals("_")) {
						affixCues.add(cue.cue);
						cueDetector.addTrainingInstance(cue.cue, cueFeatureExtractor.extract(w, s));
					}

					else {
						cueDetector.addTrainingInstance(nonAffixCue, cueFeatureExtractor.extract(w, s));
					}

				}
			}
		}
		cueClassifier = new ClassifyMalletMaxEnt(cueDetector.train());
	}

	@Override
	public void classify(Sentence sentence) {
		int cueCount = 0;
		for (Word word : sentence.words) {
			Cue cu = null;

			word.cues.clear();
			
			for(int i=0; i<cueCount; i++)
			{
				word.cues.add(new Cue("_", "_", "_"));
			}
			
			if (lu.contains(word.word.toLowerCase())) {
				cu = new Cue(word.word, "_", "_");
			}
			else {
				String c = cueClassifier.classifyInstance(cueFeatureExtractor.extract(word, sentence));
				if (!c.equals(nonAffixCue)) {
					cu = new Cue(c, "_", "_");
				}
			}

			if (cu != null) {
				cueCount += 1;
				word.cues.add(cu);
			}
		}
	}
	
	@Override
	public String toString()
	{
		return "Hybrid Cue Detector";
	}
}

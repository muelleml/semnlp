package classifier.cues;

import java.util.Arrays;
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
import features.cue.Path;

/**
 * @author: Manuel M�ller
 **/

public class HybridCueDetector implements CueClassifier {
	final static String nonAffixCue = "nonAffixCue";

	// dis, un etc
	final static String shortAffixCue = "ShortAffixCue";

	// less
	final static String longAffixCue = "longAffixCue";

	ClassifyMalletMaxEnt cueClassifier;
	TrainMalletMaxEnt cueDetector;
	CueFeatureExtractor cueFeatureExtractor;

	List<CueFeature> cueFeatureList;

	Set<String> affixCues;
	Set<String> lu;

	Set<String> shortAffixes = new TreeSet<String>();
	Set<String> longAffixes = new TreeSet<String>();

	public HybridCueDetector() {

		// configure the featureExtractor
		cueFeatureExtractor = new CueFeatureExtractor();
		cueFeatureExtractor.addFeature(new POS(0));
		cueFeatureExtractor.addFeature(new POS(-1));
		//cueFeatureExtractor.addFeature(new POS(-2));
		//cueFeatureExtractor.addFeature(new POS(-3));
		cueFeatureExtractor.addFeature(new POS(1));
		//cueFeatureExtractor.addFeature(new POS(2));
		//cueFeatureExtractor.addFeature(new POS(3));
		//cueFeatureExtractor.addFeature(new Lemma(-3));
		//cueFeatureExtractor.addFeature(new Lemma(-2));
		//cueFeatureExtractor.addFeature(new Lemma(-1));
		cueFeatureExtractor.addFeature(new Lemma(0));
		cueFeatureExtractor.addFeature(new Lemma(1));
		// cueFeatureExtractor.addFeature(new Lemma(2));
		// cueFeatureExtractor.addFeature(new Lemma(3));

		//cueFeatureExtractor.addFeature(new NGram(1, 3, 10, true));
		//cueFeatureExtractor.addFeature(new NGram(1, 3, 10, false));
		//cueFeatureExtractor.addFeature(new Path(3));

		shortAffixes.addAll(Arrays.asList("dis", "im", "in", "ir", "un"));
		longAffixes.addAll(Arrays.asList("less"));

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
						cueDetector.addTrainingInstance(nonAffixCue,
								cueFeatureExtractor.extract(w, s));
					} else if (shortAffixes.contains(cue.cue)) {
						affixCues.add(cue.cue);
						cueDetector.addTrainingInstance(shortAffixCue,
								cueFeatureExtractor.extract(w, s));
					} else if (longAffixes.contains(cue.cue)) {
						affixCues.add(cue.cue);
						cueDetector.addTrainingInstance(longAffixCue,
								cueFeatureExtractor.extract(w, s));
					}

					else {
						cueDetector.addTrainingInstance(nonAffixCue,
								cueFeatureExtractor.extract(w, s));
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

			for (int i = 0; i < cueCount; i++) {
				word.cues.add(new Cue("_", "_", "_"));
			}

			if (lu.contains(word.word.toLowerCase())) {
				cu = new Cue(word.word, "_", "_");
			} else {
				String c = cueClassifier.classifyInstance(cueFeatureExtractor
						.extract(word, sentence));

				if (c.equals(nonAffixCue)) {
					// cu = new Cue("_", "_", "_");
					/*
					 * if (word.word.endsWith("less") |
					 * word.word.endsWith("lessly")) { cu = new Cue("less", "_",
					 * "_"); }
					 */
				} else if (c.equals(shortAffixCue)) {
					for (String tCue : shortAffixes) {
						if (word.word.startsWith(tCue)) {
							cu = new Cue(tCue, "_", "_");
							break;
						}

					}

				} else if (c.equals(longAffixCue)) {
					cu = new Cue("less", "_", "_");
				}
			}

			if (cu != null) {
				cueCount += 1;
				word.cues.add(cu);
			}
		}
	}

	@Override
	public String toString() {
		return "Hybrid Cue Detector";
	}
}

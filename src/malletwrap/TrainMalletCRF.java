package malletwrap;

import java.util.List;

import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFOptimizableByLabelLikelihood;
import cc.mallet.fst.CRFTrainerByValueGradients;
import cc.mallet.optimize.Optimizable;
import cc.mallet.types.Alphabet;
import cc.mallet.types.AugmentableFeatureVector;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import cc.mallet.types.LabelSequence;

public class TrainMalletCRF {

	private final Alphabet featureAlphabet;
	private final LabelAlphabet labelAlphabet;
	private final InstanceList isList;
	private final int iterations;

	public TrainMalletCRF() {
		this(200);
	}

	public TrainMalletCRF(int iterations) {
		featureAlphabet = new Alphabet();
		labelAlphabet = new LabelAlphabet();
		isList = new InstanceList(featureAlphabet, labelAlphabet);
		this.iterations = iterations;
	}

	public void addSequenceInstance(List<String> labels,
			List<List<String>> stateFeatures) {
		if (labels.size() != stateFeatures.size())
			throw new RuntimeException("not same number of labels and states");
		FeatureVectorSequence featureVectorSequence = stateFeatures2FVS(
				stateFeatures, featureAlphabet);
		LabelSequence labelSequence = new LabelSequence(this.labelAlphabet);
		for (int i = 0; i < labels.size(); i++)
			labelSequence.add(labels.get(i));

		Instance instance = new Instance(featureVectorSequence, labelSequence,
				null, null);
		isList.add(instance);
	}

	public CRF train() {
		CRF crf = train(isList, iterations);
		return crf;
	}

	static CRF train(InstanceList trainingData, int iterations) {
		CRF crf = new CRF(trainingData.getDataAlphabet(),
				trainingData.getTargetAlphabet());
		crf.addFullyConnectedStatesForLabels();
		crf.setWeightsDimensionAsIn(trainingData, false);
		CRFOptimizableByLabelLikelihood optLabel = new CRFOptimizableByLabelLikelihood(
				crf, trainingData);
		Optimizable.ByGradientValue[] opts = new Optimizable.ByGradientValue[] { optLabel };
		CRFTrainerByValueGradients crfTrainer = new CRFTrainerByValueGradients(
				crf, opts);

		for (int i = 1; i <= iterations; i++) {
			try {
				System.err.println("Starting iteration " + i);
				if (crfTrainer.train(trainingData, 1)) {
					System.err.println("Finished early after " + i
							+ " iterations");
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return crf;
	}

	static AugmentableFeatureVector features2AFV(List<String> features,
			Alphabet featureAlphabet) {
		AugmentableFeatureVector afv = new AugmentableFeatureVector(
				featureAlphabet, 100, true);
		for (String feature : features) {
			int idx = afv.getAlphabet().lookupIndex(feature);
			if (!afv.getAlphabet().growthStopped() || idx != -1) {
				afv.add(idx);
			}
		}
		return afv;
	}

	static FeatureVectorSequence stateFeatures2FVS(
			List<List<String>> stateFeatures, Alphabet featureAlphabet) {
		AugmentableFeatureVector[] augmentableFeatureVectorArray = new AugmentableFeatureVector[stateFeatures
				.size()];
		for (int i = 0; i < stateFeatures.size(); i++) {
			augmentableFeatureVectorArray[i] = features2AFV(
					stateFeatures.get(i), featureAlphabet);

		}
		FeatureVectorSequence featureVectorSequence = new FeatureVectorSequence(
				augmentableFeatureVectorArray);
		return featureVectorSequence;
	}
}

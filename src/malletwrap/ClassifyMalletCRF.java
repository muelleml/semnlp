package malletwrap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.mallet.fst.CRF;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.Sequence;

public class ClassifyMalletCRF implements Serializable {
	private static final long serialVersionUID = -2882213757689351587L;

	private final CRF crf;

	public ClassifyMalletCRF(CRF crf) {
		this.crf = crf;
	}

	public List<String> predictSequence(List<List<String>> stateFeatures) {
		FeatureVectorSequence fvs = TrainMalletCRF.stateFeatures2FVS(
				stateFeatures, crf.getInputAlphabet());
		Instance inst = new Instance(fvs, null, null, null);
		List<String> labelSeq = predict(inst, crf);
		return labelSeq;
	}

	public static List<String> predict(Instance instance, CRF crf) {
		List<String> ret = new ArrayList<String>();
		Sequence<?> output = crf.transduce((Sequence<?>) instance.getData());
		for (int j = 0; j < output.size(); j++)
			ret.add(output.get(j).toString());
		return ret;
	}
}

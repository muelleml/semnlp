package malletwrap;

import java.io.Serializable;
import java.util.List;

import cc.mallet.classify.RankMaxEnt;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.Labeling;

public class RankMaxEntRank implements Serializable {
	private static final long serialVersionUID = 1480446613762861072L;
	
	private final RankMaxEnt model;
	
	public RankMaxEntRank(RankMaxEnt model){
		this.model=model;
	}
	
	public int rank(List<List<String>> candidateFeatures){
		FeatureVector[] featureVectorArray=TrainMalletMaxEntRank.candidateFeatures2FV(candidateFeatures,model.getAlphabet());
		Instance instance=new Instance(featureVectorArray,null,null,null);
		Labeling lab=model.classify(instance).getLabeling();
		return Integer.parseInt(lab.getBestLabel().toString());
	}
}

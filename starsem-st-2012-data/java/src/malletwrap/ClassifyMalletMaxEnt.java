package malletwrap;

import java.io.Serializable;
import java.util.List;

import cc.mallet.classify.MaxEnt;
import cc.mallet.types.AugmentableFeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.Labeling;

public class ClassifyMalletMaxEnt implements Serializable{
	private static final long serialVersionUID = 8659728618752894612L;
	private final MaxEnt maxEnt;

	public ClassifyMalletMaxEnt(MaxEnt maxEnt){
		this.maxEnt=maxEnt;
	}

	public String classifyInstance(List<String> features){
		Instance inst=features2Instance(features);
		Labeling lab=maxEnt.classify(inst).getLabeling();
		return lab.getBestLabel().toString();
	}

	private Instance features2Instance(List<String> features) {
		AugmentableFeatureVector afv=new AugmentableFeatureVector(maxEnt.getAlphabet(),100,true);
		for(String s:features)
			addFeature(afv,s);
		Instance inst=new Instance(afv,null,null,null);
		return inst;
	}
	private static void addFeature(AugmentableFeatureVector afv, String feature){
		int idx = afv.getAlphabet().lookupIndex(feature);
		if (!afv.getAlphabet().growthStopped() || idx != -1){
			afv.add(idx);
		}
	}

}

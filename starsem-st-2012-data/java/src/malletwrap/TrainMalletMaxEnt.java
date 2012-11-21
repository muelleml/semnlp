package malletwrap;

import java.util.List;

import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.MaxEnt;
import cc.mallet.classify.MaxEntL1Trainer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.AugmentableFeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;

public class TrainMalletMaxEnt {
	
	private final InstanceList isList;
	private final ClassifierTrainer<MaxEnt> trainer;
	
	public TrainMalletMaxEnt(){
		this(new MaxEntL1Trainer(0.1d));
	}
	
	public TrainMalletMaxEnt(ClassifierTrainer<MaxEnt> trainer){
		Alphabet featureAlphabet=new Alphabet();
		LabelAlphabet labelAlphabet=new LabelAlphabet();
		featureAlphabet.startGrowth();
		isList=new InstanceList(featureAlphabet,labelAlphabet);
		this.trainer=trainer;
	}

	public void addTrainingInstance(String label,List<String> features){
		AugmentableFeatureVector afv=new AugmentableFeatureVector(isList.getDataAlphabet(),100,true);
		for(String s:features)
			addFeature(afv,s);
		Instance is=new Instance(afv,((LabelAlphabet)isList.getTargetAlphabet()).lookupLabel(label),null,null);
		isList.add(is);
	}
	
	private static void addFeature(AugmentableFeatureVector afv, String feature){
		int idx = afv.getAlphabet().lookupIndex(feature);
		if (!afv.getAlphabet().growthStopped() || idx != -1){
			afv.add(idx);
		}
	}
	
	public MaxEnt train(){
		MaxEnt model=trainer.train(isList);
		return model;
	}
}

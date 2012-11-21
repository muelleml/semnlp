package malletwrap;

import java.util.List;

import cc.mallet.classify.RankMaxEnt;
import cc.mallet.classify.RankMaxEntTrainer;
import cc.mallet.types.Alphabet;
import cc.mallet.types.AugmentableFeatureVector;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.LabelAlphabet;

public class TrainMalletMaxEntRank {

	private final Alphabet featureAlphabet=new Alphabet();
	private final LabelAlphabet labelAlphabet=new LabelAlphabet();
	private final InstanceList isList;
	private final RankMaxEntTrainer trainer;

	public TrainMalletMaxEntRank(RankMaxEntTrainer trainer){
		featureAlphabet.startGrowth();
		this.isList=new InstanceList(featureAlphabet,labelAlphabet);
		this.trainer=trainer;
	}
	
	public TrainMalletMaxEntRank(){
		this(new RankMaxEntTrainer());
	}
	
	public void addTraningInstance(int oracle,List<List<String>> candidateFeatures){
		if(oracle<0 || oracle>=candidateFeatures.size())
			throw new RuntimeException("oracle not in list");
		FeatureVector[] featureVectorArray=candidateFeatures2FV(candidateFeatures,isList.getDataAlphabet());
		Label label = labelAlphabet.lookupLabel(Integer.toString(oracle)); //String containing the index!!!
		Instance inst=new Instance(featureVectorArray,label,null,null);
		isList.add(inst);
	}
	
	private static void addFeature(AugmentableFeatureVector afv, String feature){
		int idx = afv.getAlphabet().lookupIndex(feature);
		if (!afv.getAlphabet().growthStopped() || idx != -1){
			afv.add(idx);
		}
	}
	
	public RankMaxEnt train(){
		RankMaxEnt model=(RankMaxEnt) trainer.train(isList);
		return model;
	}
	
	static FeatureVector[] candidateFeatures2FV(List<List<String>> candidateFeatures,Alphabet dataAlphabet){
		FeatureVector[] featureVectorArray = new FeatureVector[candidateFeatures.size()];
		int q=0;
		for(List<String> f:candidateFeatures){
			AugmentableFeatureVector afv=new AugmentableFeatureVector(dataAlphabet,100,true);
			for(String s:f)
				addFeature(afv,s);
			featureVectorArray[q++]=afv;
		}
		return featureVectorArray;
	}
}

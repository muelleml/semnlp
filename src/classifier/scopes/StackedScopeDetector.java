package classifier.scopes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.Semaphore;

import malletwrap.ClassifyMalletCRF;
import malletwrap.TrainMalletCRF;
import model.Corpus;
import model.Cue;
import model.Sentence;
import model.Word;
import util.Sysout;

/**
 * @author: Patrik Eckebrecht
 **/
public class StackedScopeDetector implements ScopeClassifier
{
	boolean reverted;
	List<ScopeClassifier> stage;
	List<ClassifyMalletCRF> stageClassifier;
	private int CRFIterationCount;
	ClassifyMalletCRF scopeClassifier;

	public StackedScopeDetector(int crfIterations, String stackParams) {
		this(stackParams, crfIterations, false);
	}

	public StackedScopeDetector(String stackParams, int crfIterations, boolean reverted)
	{
		this.reverted =reverted; 
		stage = new LinkedList<>();
		String[] specifications = stackParams.split(";");
		for(String spec : specifications) {

			String subFeatureSpec;
			String[] p;
			int subCrfIterations;
			boolean subReverted;

			switch(spec.substring(0,3)) {
			case "crf":
				subFeatureSpec = spec.substring(spec.indexOf('(')+1, spec.lastIndexOf(')'));
				p = spec.substring(spec.indexOf('[')+1, spec.indexOf(']')).split(",");
				subCrfIterations = Integer.parseInt(p[0]);
				subReverted = Integer.parseInt(p[1])==1;
				stage.add(new CRFScopeDetector(subFeatureSpec, subCrfIterations, subReverted));
				break;
			case "sta":
				subFeatureSpec = spec.substring(spec.indexOf('(')+1, spec.lastIndexOf(')'));
				p = spec.substring(spec.indexOf('[')+1, spec.indexOf(']')).split(",");
				subCrfIterations = Integer.parseInt(p[0]);
				subReverted = Integer.parseInt(p[1])==1;
				stage.add(new StackedScopeDetector(subFeatureSpec, subCrfIterations, subReverted));
				break;
			}
		}
		CRFIterationCount = crfIterations;
	}

	@Override
	public void train(final Corpus c)
	{
		stageClassifier = new LinkedList<>();
		final Semaphore mutex = new Semaphore(0);
		Iterator<ScopeClassifier> classifIt = stage.listIterator();
		while(classifIt.hasNext()) {
			final ScopeClassifier classif = classifIt.next();
			new Thread(new Runnable() {
				@Override
				public void run() {
					classif.train(c);
					mutex.release();
				}
			}).start();
		}
		try
		{
			mutex.acquire(stage.size());
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TrainMalletCRF trainer = new TrainMalletCRF(CRFIterationCount);

		for (Sentence s : c.sentences) {

			String[] recentScope = null;
			recentScope = new String[s.words.get(0).cues.size()];
			for(int i=0; i<recentScope.length; i++){
				recentScope[i] = "_";
			}

			for(int cueIndex = 0; cueIndex<s.words.get(0).cues.size(); cueIndex++) {
				List<String> cueLabels = new LinkedList<>();

				Iterator<Word> wordIt = s.words.listIterator(reverted?s.words.size():0);
				while(wordIt.hasNext()) {
					Word w = wordIt.next();
					Cue cue = w.cues.get(cueIndex);
					if(!cue.scope.equals("_")) 
					{
						//						if(recentScope[cueIndex].equals("_")) {
						//							cueLabels.add("B");
						//						}
						//						else 
						{
							cueLabels.add("I");
						}
					}
					else cueLabels.add("O");

					recentScope[cueIndex] = cue.scope;

				}

				//asd

				List<List<String>> featureLabels = extractFeatures(s, cueIndex, cueLabels);
				deleteWrongLabels(cueLabels, featureLabels);
				trainer.addSequenceInstance(cueLabels, featureLabels);
			}
		}
		scopeClassifier = new ClassifyMalletCRF(trainer.train());
	}

	private void deleteWrongLabels(List<String> cueLabels, List<List<String>> featureLabels)
	{
		Iterator<String> cue = cueLabels.iterator();
		Iterator<List<String>> fit = featureLabels.iterator();
		SortedSet<Integer> invalidIndices = new TreeSet<>(); 
		while(cue.hasNext() && fit.hasNext()) {
			List<String> feature = fit.next();
			for(int i=0; i<feature.size();i++)
				if(!feature.get(i).equals(cue.next()))
					invalidIndices.add(i);
		}
		if(invalidIndices.size() > 0) {
			List<Integer> indi = new LinkedList<>();
			indi.addAll(invalidIndices);

			for(List<String> feature : featureLabels) {
				Iterator<Integer> it = indi.listIterator(indi.size()-1);
				while(it.hasNext())
					feature.remove(it.next());
			}
		}
	}

	@Override
	public void classify(Sentence sentence)
	{
		for(int cueIndex = 0; cueIndex<sentence.words.get(0).cues.size(); cueIndex++)
		{
			int wordIndex = 0;
			for(String label : getPredictedLabels(sentence, cueIndex)) {
				Cue cue = sentence.words.get(wordIndex).cues.get(cueIndex);
				switch(label) {
				case "B": cue.scope = sentence.words.get(wordIndex).lemma; break;
				case "I": cue.scope = sentence.words.get(wordIndex).lemma; break;
				case "O": cue.scope = "_"; break;
				}
				wordIndex++;
			}
		}
	}

	@Override
	public List<String> getPredictedLabels(Sentence s, int cueIndex)
	{
		List<List<String>> f = extractFeatures(s, cueIndex, null);
		List<String> predicted = scopeClassifier.predictSequence(f);
		return predicted;
	}

	List<List<String>> extractFeatures(Sentence s, int cueIndex, List<String> cueLabels) {

		Semaphore mutex = new Semaphore(0);

		List<String[]> predicted = new ArrayList<>(s.words.size());
		for(int i=0;i<s.words.size();i++)
			predicted.add(new String[stage.size()]);
		Iterator<ScopeClassifier> classifIt = stage.listIterator();
		int classifIndex = 0;
		if(cueLabels!=null) {
			Sysout.logln("Dokument: " + s.words.get(0).origin+" "+s.words.get(0).sentenceID);
		}
		while(classifIt.hasNext()) {
			final int ci = classifIndex;
			final ScopeClassifier classif = classifIt.next();
			//			new Thread(new Runnable() {
			//				@Override
			//				public void run() {

			List<String> localLabels = classif.getPredictedLabels(s, cueIndex);
			Iterator<String> localLabelsIt = localLabels.listIterator();
			for(int i=0;i<localLabels.size();i++)
				predicted.get(i)[ci] =localLabelsIt.next();
			List<String> words = new LinkedList<>();
			if(cueLabels != null) {
				for(Word w : s.words)words.add(w.word);
				Sysout.logln(classif.toString().replace("\n"," ").replace("  "," ").replace("  "," ").replace("  "," ").replace("  "," ").replace("  "," ").replace("  "," "));
				//if(cueLabels!=null)
					Sysout.logAligned(words, ' ', localLabels, ',', cueLabels, ',');
				//else Sysout.logAligned(words, ' ', localLabels, ',');
				Sysout.logln();
			}
			mutex.release();
			//				}
			//			}).start();

			classifIndex++;
		}
		try
		{
			mutex.acquire(stage.size());
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}

		List<List<String>> value = new LinkedList<>();
		for(String[] fuckYou : predicted){
			ArrayList<String> fuckYouJava = new ArrayList<>(fuckYou.length);
			for(String fu : fuckYou) fuckYouJava.add(fu);
			value.add(fuckYouJava);
		}
		return value;
	}



	@Override
	public String toString()
	{
		String s = "Stacked@"+CRFIterationCount+" " +(reverted?"Reverse":"Forward") +", Classifiers: \n";
		for(ScopeClassifier c : stage) {
			String[] sc = c.toString().split("\n");
			for (String scs : sc)
				s += "  " + scs + "\n";
		}
		return s;
	}
}

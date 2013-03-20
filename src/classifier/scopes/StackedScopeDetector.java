package classifier.scopes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
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
import features.scope.Baseline;
import features.scope.POSHead;
import features.scope.POSSequence;
import features.scope.ScopeFeatureExtractor;

/**
 * @author: Patrik Eckebrecht
 **/
public class StackedScopeDetector implements ScopeClassifier
{
	boolean reverted;
	String[] specifications;
	List<ScopeClassifier[]> stage;
	private int CRFIterationCount;
	ClassifyMalletCRF scopeClassifier;
	int partCount;
	ScopeFeatureExtractor scopeFeatureExtractor;
	private boolean filter;

	public StackedScopeDetector(int crfIterations, String stackParams) {
		this(stackParams, crfIterations, false, false);
	}

	public StackedScopeDetector(String stackParams, int crfIterations, boolean reverted, boolean filter)
	{
		this.reverted =reverted;
		stage = new LinkedList<>();
		this.specifications = stackParams.split(";");
		this.CRFIterationCount = crfIterations;
		this.filter = filter;
	}

	@Override
	public void train(final Corpus c)
	{
		this.partCount = getPartCount(c);

		final Corpus[] partitions = getPartitions(partCount, c);
		final Corpus[] trainParts = new Corpus[partCount];
		scopeFeatureExtractor = new ScopeFeatureExtractor();
		for(String spec : specifications) {

			String subFeatureSpec;
			String[] p;
			int subCrfIterations;
			boolean subReverted;

			switch(spec.substring(0,3)) {
			case "crf":
			{
				subFeatureSpec = spec.substring(spec.indexOf('(')+1, spec.lastIndexOf(')'));
				p = spec.substring(spec.indexOf('[')+1, spec.indexOf(']')).split(",");
				subCrfIterations = Integer.parseInt(p[0]);
				subReverted = Integer.parseInt(p[1])==1;
				ScopeClassifier[] stagePart = new ScopeClassifier[partCount];
				for(int i=0; i<partCount; i++)
					stagePart[i] = new CRFScopeDetector(subFeatureSpec, subCrfIterations, subReverted);
				stage.add(stagePart);
				break;
			}
			case "sta":
			{
				subFeatureSpec = spec.substring(spec.indexOf('(')+1, spec.lastIndexOf(')'));
				p = spec.substring(spec.indexOf('[')+1, spec.indexOf(']')).split(",");
				subCrfIterations = Integer.parseInt(p[0]);
				subReverted = Integer.parseInt(p[1])==1;
				ScopeClassifier[] stagePart = new ScopeClassifier[partCount];
				for(int i=0; i<partCount; i++)
					stagePart[i]=new StackedScopeDetector(subFeatureSpec, subCrfIterations, subReverted, filter);
				stage.add(stagePart);
				break;
			}
			case "seq": 
				scopeFeatureExtractor.addFeature(new POSSequence(Integer.parseInt(spec.substring(spec.indexOf('(')+1,spec.lastIndexOf(')')))));
				break;
			case "hea":
				scopeFeatureExtractor.addFeature(new POSHead(spec.substring(spec.indexOf('(')+1,spec.lastIndexOf(')'))));
				break;
			case "bas":
				scopeFeatureExtractor.addFeature(new Baseline());
				break;
			}
		}
		Sysout.out.println("Training: ");
		Sysout.out.println(this);

		final Semaphore mutex = new Semaphore(0);
		Iterator<ScopeClassifier[]> classifIt = stage.listIterator();


		for(int i=0; i<partCount; i++){
			trainParts[i] = new Corpus();
			for(int j=0; j<partCount; j++) 
				if(!filter && i!=j)
					trainParts[i].addCorpus(partitions[j]);
		}
		while(classifIt.hasNext()) {
			final ScopeClassifier[] classif = classifIt.next();
			for(int i=0; i<partCount; i++) {
				final int partIndex = i;
				new Thread(new Runnable() {
					@Override
					public void run() {
						classif[partIndex].train(trainParts[partIndex]);
						mutex.release();
					}
				}).start();
			}
		}

		try
		{
			mutex.acquire(stage.size()*partCount);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace(Sysout.err);
		}
		TrainMalletCRF trainer = new TrainMalletCRF(CRFIterationCount);
		for(int pi = 0; pi < partCount; pi++) {
			for (Sentence s : partitions[pi].sentences) {

				String[] recentScope = null;
				recentScope = new String[s.words.get(0).cues.size()];
				for(int i=0; i<recentScope.length; i++){
					recentScope[i] = "_";
				}

				for(int cueIndex = 0; cueIndex<s.words.get(0).cues.size(); cueIndex++) {
					List<String> cueLabels = new LinkedList<>();

					if(reverted) {
						ListIterator<Word> wordIt = s.words.listIterator(s.words.size());
						while(wordIt.hasPrevious()) {
							Word w = wordIt.previous();
							Cue cue = w.cues.get(cueIndex);
							if(cue.scope.equals("_"))
								cueLabels.add("O");
							else if(recentScope[cueIndex].equals("_"))
								cueLabels.add("B");
							else cueLabels.add("I");

							recentScope[cueIndex] = cue.scope;
						}
					}
					else {
						ListIterator<Word> wordIt = s.words.listIterator(0);
						while(wordIt.hasNext()) {
							Word w = wordIt.next();
							Cue cue = w.cues.get(cueIndex);
							if(cue.scope.equals("_"))
								cueLabels.add("O");
							else if(recentScope[cueIndex].equals("_"))
								cueLabels.add("B");
							else cueLabels.add("I");

							recentScope[cueIndex] = cue.scope;
						}
					}

					List<List<String>> featureLabels = extractFeatures(s, cueIndex, pi, cueLabels);
					if(filter)
						deleteWrongLabels(cueLabels, featureLabels);
					trainer.addSequenceInstance(cueLabels, featureLabels);
				}
			}
		}
		scopeClassifier = new ClassifyMalletCRF(trainer.train());
	}

	private Corpus[] getPartitions(int partCount, Corpus c)
	{
		int partSize = c.sentences.size()/partCount;
		Corpus[] corpora = new Corpus[partCount];
		for(int i=0; i<partCount; i++) {
			Corpus temp = new Corpus();
			temp.sentences.addAll(c.sentences.subList(partSize*i, (i==partCount-1)?c.sentences.size()-1:partSize*(i+1)));
			corpora[i]= temp;
		}
		return corpora;
	}

	private int getPartCount(Corpus c)
	{
		if(filter) return 1;
		else if(c.sentences.size()<1000)
			return 2;
		else if(c.sentences.size()>10000)
			return 10;
		else return c.sentences.size()/500;
	}

	private void deleteWrongLabels(List<String> cueLabels, List<List<String>> featureLabels)
	{
		Iterator<String> cue = cueLabels.iterator();
		Iterator<List<String>> fit = featureLabels.iterator();
		SortedSet<Integer> invalidIndices = new TreeSet<>(); 
		while(cue.hasNext() && fit.hasNext()) {
			List<String> feature = fit.next();
			String c = cue.next();
			for(int i=0; i<feature.size();i++)
				if(!feature.get(i).equals(c))
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
				case "B": cue.scope = sentence.words.get(wordIndex).word; break;
				case "I": cue.scope = sentence.words.get(wordIndex).word; break;
				case "O": cue.scope = "_"; break;
				}
				wordIndex++;
			}
		}
	}

	@Override
	public List<String> getPredictedLabels(Sentence s, int cueIndex)
	{
		List<List<String>> f = extractFeatures(s, cueIndex, -1, null);
		List<String> predicted = scopeClassifier.predictSequence(f);
		return predicted;
	}

	private List<List<String>> extractFeatures(Sentence s, int cueIndex, int partIndex, List<String> cueLabels) {

		Semaphore mutex = new Semaphore(0);

		List<String[]> predicted = new ArrayList<>(s.words.size());
		for(int i=0;i<s.words.size();i++)
			predicted.add(new String[stage.size()*((partIndex!=-1)?1:partCount)]);
		Iterator<ScopeClassifier[]> classifIt = stage.listIterator();
		int classifIndex = 0;
		if(cueLabels!=null) {
			Sysout.logln("Dokument: " + s.words.get(0).origin+" "+s.words.get(0).sentenceID);
		}
		while(classifIt.hasNext()) {
			final int ci = classifIndex;
			final ScopeClassifier[] classif = classifIt.next();
			//			new Thread(new Runnable() {
			//				@Override
			//				public void run() {
			if(partIndex==-1) {
				for(int pi = 0; pi<partCount; pi++) {
					List<String> localLabels = classif[pi].getPredictedLabels(s, cueIndex);
					Iterator<String> localLabelsIt = localLabels.listIterator();
					for(int i=0;i<localLabels.size();i++)
						predicted.get(i)[ci*partCount + pi] = localLabelsIt.next();
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
				}
			}
			else {
				List<String> localLabels = classif[partIndex].getPredictedLabels(s, cueIndex);
				Iterator<String> localLabelsIt = localLabels.listIterator();
				for(int i=0;i<localLabels.size();i++)
					predicted.get(i)[ci] = localLabelsIt.next();
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
			}
			classifIndex++;
		}
		try
		{
			mutex.acquire(stage.size()*((partIndex==-1)?partCount:1));
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
		List<List<String>> value = new ArrayList<>(predicted.size());
		Iterator<List<String>> featureIt = scopeFeatureExtractor.extractTraing(s, cueIndex).listIterator();
		for(String[] item : predicted) {
			List<String> word = featureIt.next();
			word.addAll(Arrays.asList(item));
			value.add(word);

		}
		return value;
	}



	@Override
	public String toString()
	{
		String s = "Stacked@"+CRFIterationCount+" " +(reverted?"Reverse":"Forward") +", Classifiers: \n";
		for(ScopeClassifier[] c : stage) {
			String[] sc = c[0].toString().split("\n");
			for (String scs : sc)
				s += "  " + scs + "\n";
		}
		s += "Features: \n" + scopeFeatureExtractor;
		return s;
	}
}

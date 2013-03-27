/**
 * 
 */
package util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import model.Corpus;
import model.Cue;
import model.Sentence;
import model.Word;

/**
 * @author muelleml
 * 
 */
public class JackKnifeAligner {

	public static Corpus align(Corpus cue, Corpus scope) {
		Corpus r = new Corpus();

		List<String> shortAffixes = new LinkedList<String>();
		shortAffixes.addAll(Arrays.asList("dis", "im", "in", "ir", "un"));

		if (cue.sentences.get(0).words.get(0).word.equals(scope.sentences
				.get(0).words.get(0).word)) {

			int i = 0;
			for (Sentence cueS : cue.sentences) {

				if (cueS.words.get(0).word.equals("Today")) {
					int test = 0;
					test++;

				}

				Sentence tSent = new Sentence();

				Sentence scopeS = scope.sentences.get(i);
				try {
					scopeS.ensureFinalized();
					cueS.ensureFinalized();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				List<Integer> cueMarks = new LinkedList<Integer>();

				

				for (List<Cue> cL : cueS.verticalCues) {
					int k = 0;
					for (List<Cue> sL : scopeS.verticalCues) {
						int j = 0;
						for (Cue c : cL) {
							
							if (!c.cue.equals("_") & !sL.get(j).cue.equals("_")) {
								// mark for copy
								cueMarks.add(k);
							}
							if (c.cue.equals("un")) {
								int breakMe = 0;
								breakMe++;
							}
							j++;

						}
						k++;
					}
					
				}

				int wc = 0;

				for (Word w : cueS.words) {

					Word tWord = new Word(w.origin, w.sentenceID, w.tokenID,
							w.word, w.lemma, w.pos, w.parseTree);
					
					for (int tC : cueMarks){
						Cue tCue = scopeS.words.get(wc).cues.get(tC);
						tWord.cues.add(new Cue(tCue.cue, tCue.scope, tCue.event));
					}
					
					wc++;
					
					tSent.words.add(tWord);
				}

				// now copy from cue corpus

				r.sentences.add(tSent);
				i++;
			}

		}

		return r;
	}

}

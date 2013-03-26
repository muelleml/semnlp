/**
 * 
 */
package util;

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

		if (cue.sentences.get(0).words.get(0).word.equals(scope.sentences
				.get(0).words.get(0).word)) {

			int i = 0;
			for (Sentence cueS : cue.sentences) {

				Sentence tSent = new Sentence();

				Sentence scopeS = scope.sentences.get(i);
				try {
					scopeS.ensureFinalized();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				List<Integer> cueMarks = new LinkedList<Integer>();

				int j = 0;

				for (List<Cue> cL : cueS.verticalCues) {

					for (List<Cue> sL : scopeS.verticalCues) {

						int k = 0;
						for (Cue c : cL) {

							if (!c.cue.equals("_")
									& !sL.get(k).cue.equals("_")) {
								// mark for copy
								cueMarks.add(k);
							}

							
						}k++;
					}
					j++;
				}

				int wc = 0;
				// now copy from cue corpus
				for (Word w : cueS.words) {

					Word tWord = new Word(w.origin, w.sentenceID, w.tokenID,
							w.word, w.lemma, w.pos, w.parseTree);

					int cueCounter = 0;
					for (Cue c : w.cues) {

						if (cueMarks.contains(cueCounter)) {
							tWord.cues.add(new Cue(c.cue, c.scope, c.event));
						}

						cueCounter++;
					}
					tSent.words.add(tWord);
				}
				r.sentences.add(tSent);
				i++;
			}

		}

		return r;
	}

}

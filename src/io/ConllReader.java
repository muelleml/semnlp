/**
 * 
 */
package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import util.Sysout;

import model.Corpus;
import model.Cue;
import model.Sentence;
import model.Word;

/**
 * @author muelleml
 * 
 */
public class ConllReader {
	public static Corpus read(String fileName) {

		Corpus c = new Corpus();

		File f = new File(fileName);

		try {

			FileReader fr = new FileReader(f);
			BufferedReader br = new BufferedReader(fr);

			Sentence s = new Sentence();
			String line;
			while ((line = br.readLine())!=null) {

				String[] lineElements = line.trim().split("\\s");

				if (lineElements.length == 1) {
					if (s != null) {
						s.generateTree();
						c.sentences.add(s);
					}

					s = new Sentence();

				}
				else 
				{
					Word w = new Word(lineElements[0], lineElements[1], lineElements[2], lineElements[3], lineElements[4], lineElements[5],
							lineElements[6]);

					if (lineElements.length > 8) 
					{
						for (int i = 0; lineElements.length > i * 3 + 7; i++) 
						{
							int ti = 3 * i + 7;
							w.cues.add(new Cue(lineElements[ti], lineElements[ti + 1], lineElements[ti + 2]));
						}
					}

					s.words.add(w);
				}

				

			}
			if(s != null) {
				s.generateTree();
				c.sentences.add(s);
			}
			br.close();
			fr.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace(Sysout.err);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(Sysout.err);
		}

		// remove empty sentences

		List<Sentence> rSentences = new LinkedList<Sentence>();

		for (Sentence s : c.sentences) {
			if (s.words.size() == 0) {
				rSentences.add(s);
			}
		}

		for (Sentence s : rSentences) {
			c.sentences.remove(s);
		}

		return c;

	}
}
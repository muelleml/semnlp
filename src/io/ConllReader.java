/**
 * 
 */
package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

			String t = br.readLine();
			Sentence s = new Sentence();
			;

			while (t != null) {

				String[] wl = t.trim().split("\\s");

				// System.out.println(wl.length);

				if (wl.length == 1) {
					c.sentences.add(s);

					s = new Sentence();

				} else {

					for (String ack : wl) {
						// System.out.println(ack);
					}

					Word w = new Word(wl[0], wl[1], wl[2], wl[3], wl[4], wl[5],
							wl[6]);

					if (wl.length > 8) {
						Cue cue;
						for (int i = 0; wl.length > i * 3 + 7; i++) {
							int ti = 3 * i + 7;

							// System.out.println(Integer.toString(ti) +
							// wl.length);

							cue = new Cue(wl[ti], wl[ti + 1], wl[ti + 2]);

							w.cues.add(cue);

						}
					}

					s.words.add(w);
				}

				t = br.readLine();

			}

			c.sentences.add(s);

			br.close();
			fr.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return c;

	}
}
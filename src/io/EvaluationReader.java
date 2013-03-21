/**
 * 
 */
package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Corpus;
import model.Metric;

/**
 * @author muelleml
 * 
 */
public class EvaluationReader {

	/**
	 * Compares to Corpora and calculates the metrics
	 * 
	 * @param gold
	 *            The Gold Corpus
	 * @param sys
	 *            The System classified Corpus
	 * @return The Metric as determined by the Perl Script
	 */
	public static Metric readScope(Corpus gold, Corpus sys) {

		Metric r = new Metric();

		try {

			// create temp files
			File tGold = File.createTempFile("SemNLP_Gold", ".txt");
			File tSys = File.createTempFile("SemNLP_Sys", ".txt");

			// write corpus to tempfile
			ConllWriter.write(gold, tGold.getAbsolutePath());
			ConllWriter.write(sys, tSys.getAbsolutePath());

			// run eval script and parse metrics

			String p = "(.*?) \\s* (\\d+) .* (\\d+) .* (\\d+) .* (\\d+) .* (\\d+) .* (\\d+\\.\\d+) .*  (\\d+\\.\\d+) .*  (\\d+\\.\\d+)";
			Pattern pat = Pattern.compile(p);

			String cmd = "perl starsem-st-2012-data/cd-sco/src/eval.cd-sco.pl -g "
					+ tGold.getAbsolutePath() + " -s " + tSys.getAbsolutePath();

			Runtime run = Runtime.getRuntime();
			Process pr = run.exec(cmd);

			pr.waitFor();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					pr.getInputStream()));
			String line;
			while ((line = br.readLine()) != null) {
				Matcher m = pat.matcher(line.trim());
				if (m.matches() & m.groupCount() > 5) {
					if (m.group(1).equals("Cues:")) {
						r.addCueMetric(Integer.parseInt(m.group(2)),
								Integer.parseInt(m.group(3)),
								Integer.parseInt(m.group(4)),
								Integer.parseInt(m.group(5)),
								Integer.parseInt(m.group(6)),
								Float.parseFloat(m.group(7)),
								Float.parseFloat(m.group(8)),
								Float.parseFloat(m.group(9)));
					} else if (m.group(1).equals("Scope tokens(no cue match):")) {
						r.addScopeMetric(Integer.parseInt(m.group(2)),
								Integer.parseInt(m.group(3)),
								Integer.parseInt(m.group(4)),
								Integer.parseInt(m.group(5)),
								Integer.parseInt(m.group(6)),
								Float.parseFloat(m.group(7)),
								Float.parseFloat(m.group(8)),
								Float.parseFloat(m.group(9)));
					}
				}
			}

			tGold.delete();
			tSys.delete();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Failed to create temp File.");
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.out.println("Failed parsing eval data.");
		}

		return r;

	}

}

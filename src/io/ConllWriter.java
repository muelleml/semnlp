/**
 * 
 */
package io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import model.Corpus;

/**
 * @author muelleml
 * 
 */
public class ConllWriter {

	public static void write(Corpus c, String fileName) {

		File f = new File(fileName);

		try {
			FileWriter fw = new FileWriter(f);
			BufferedWriter bw = new BufferedWriter(fw);

			bw.write(c.toString());

			bw.close();

			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

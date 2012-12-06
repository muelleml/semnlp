/**
 * 
 */
package io;

import java.io.File;
import java.io.FileOutputStream;
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
			FileOutputStream fos = new FileOutputStream(f);

			fos.write(c.toString().trim().getBytes());

			fos.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}

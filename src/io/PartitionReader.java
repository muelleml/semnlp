/**
 * 
 */
package io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.List;

import util.SimpleFileNameFilter;

import model.Corpus;

/**
 * @author muelleml
 * 
 */
public class PartitionReader {

	public static Corpus[] readPartitionFolder(String partFolder) {
		File[] parts = new File(partFolder).listFiles(new SimpleFileNameFilter("txt"));
		Corpus[] corpi = new Corpus[parts.length]; 
		for (int i=0; i<parts.length; i++) {

			corpi[i] = ConllReader.read(parts[i].getAbsolutePath());

		}

		return corpi;
	}

}

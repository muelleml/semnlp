/**
 * 
 */
package io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.LinkedList;
import java.util.List;

import model.Corpus;

/**
 * @author muelleml
 *
 */
public class PartitionReader {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public static List<Corpus> readPartitionFolder(String partFolder){
		List<Corpus> r = new LinkedList<Corpus>();
		
		File folder = new File(partFolder);
		
		for (File f:folder.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".txt");
			}
		})){
			
			r.add(ConllReader.read(f.getAbsolutePath()));
			
		}
		
		
		return r;
	}

}

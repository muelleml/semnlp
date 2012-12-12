package util;

import java.io.File;
import java.io.FilenameFilter;

public class SimpleFileNameFilter implements FilenameFilter {

	final String ending;
	
	public SimpleFileNameFilter(String ending)
	{
		this.ending = ending;
	}
	@Override
	public boolean accept(File dir, String name) {
		return name.toLowerCase().endsWith("."+ending);
	}
}


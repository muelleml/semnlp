package util;

import java.io.File;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.List;

public class Sysout
{
	public static PrintStream out;
	static PrintStream log;
	static {
		try {
			log = new PrintStream(new File("log.txt"));
		}
		catch(Exception e) {
			out.println("WAAAHHH");
		}
	}
	public static void logln() {
		logln("", '\0');
	}
	public static void logln(Object f) {
		logln(f,'\0');
	}
	
	public static void logln(Object f, char sep)
	{
		log(f,sep);
		log("\n");
	}
	public static void log(Object f){
		log(f,'\0');
	}
	public static void log(Object f, char sep)
	{
		if(f instanceof Iterable<?>) {
			Iterator<?> it = ((Iterable<?>)f).iterator();
			while(it.hasNext()) {
				log(it.next() + (sep!='\0'&&it.hasNext()?sep+"":""));
			}
		}
		else if(f.getClass().isArray())
			for(int i=0; i<Array.getLength(f); i++)
				log(Array.get(f,i) + (sep!='\0'&&i<Array.getLength(f)-1?sep+"":""));
		else {
			log.print(f);
		}
	}
	public static void logAligned(Iterable<?> f1, char sep1, Iterable<?> f2, char sep2) 
	{
		StringBuilder s1 = new StringBuilder();
		StringBuilder s2 = new StringBuilder();
		
		Iterator<?> it1 = f1.iterator();
		Iterator<?> it2 = f2.iterator();
		
		while(it1.hasNext() || it2.hasNext()) {
			String o1 = "";
			String o2 = "";
			if(it1.hasNext()) o1 = it1.next().toString();
			if(it2.hasNext()) o2 = it2.next().toString();

			s1.append(extend(o1, o2.length() - o1.length(), ' ')+sep1);
			s2.append(extend(o2, o1.length() - o2.length(), ' ')+sep2);
		}
		
		logln(s1);
		logln(s2);
	}
	public static void logAligned(Iterable<?> f1, char sep1, Iterable<?> f2, char sep2,Iterable<?> f3, char sep3) 
	{
		StringBuilder s1 = new StringBuilder();
		StringBuilder s2 = new StringBuilder();
		StringBuilder s3 = new StringBuilder();
		
		Iterator<?> it1 = f1.iterator();
		Iterator<?> it2 = f2.iterator();
		Iterator<?> it3 = f3.iterator();
		
		while(it1.hasNext() || it2.hasNext() || it3.hasNext()) {
			String o1 = "";
			String o2 = "";
			String o3 = "";
			if(it1.hasNext()) o1 = it1.next().toString();
			if(it2.hasNext()) o2 = it2.next().toString();
			if(it3.hasNext()) o3 = it3.next().toString();

			int len;
			if(o1.length()>o2.length())
			{	if(o1.length()>o3.length())
					len = o1.length();
				else
					len = o3.length();
			}
			else if(o2.length() > o3.length())
				len = o2.length();
			else len = o3.length();
			
			s1.append(extend(o1, len-o1.length(), ' ')+sep1);
			s2.append(extend(o2, len - o2.length(), ' ')+sep2);
			s3.append(extend(o3, len - o3.length(), ' ')+sep3);
		}
		
		logln(s1);
		logln(s2);
		logln(s3);
	}
	
	static String extend(String s, int count, char c) {
		for(int i=0;i<count; i++) s+=c;
		return s;
	}
}

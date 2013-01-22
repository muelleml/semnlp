/**
 * 
 */
package model;

/**
 * @author muelleml
 *
 */
public class Cue {
	
	final static String del = "\t";

	public String cue;
	public String scope;
	public String event;
	
	public Cue(String cue, String scope, String event) {
		this.cue = cue;
		this.scope = scope;
		this.event = event;
	}

	public Cue(){
		
	}
	
	@Override
	public String toString(){
		return cue + del + scope + del + event + del;
	}
	
}

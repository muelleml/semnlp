/**
 * 
 */
package model;

/**
 * @author muelleml
 *
 */
public class Cue {
	
	private String del = "\t";

	public String cue;
	
	public String scope;
	
	public String event;
	
	public Cue(String cue, String scope, String event) {
		super();
		this.cue = cue;
		this.scope = scope;
		this.event = event;
	}

	public Cue(){
		
	}
	
	public String toString(){
		
		return cue + del + scope + del + event + del;
		
	}
	
}

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
	public Cue(Cue c) {
			this.cue = new String(c.cue);
			this.scope = new String(c.scope);
			this.event = new String(c.event);
	}

	@Override
	public String toString(){
		return cue + del + scope + del + event + del;
	}
	
}

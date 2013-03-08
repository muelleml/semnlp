package features.scope;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import model.Node;
import model.Sentence;
import model.Word;

public class POSHead implements ScopeFeature {

	int[] orders;
	public POSHead(int... orders) {
		this.orders = orders;
	}
	
	public POSHead(String spec)
	{
		String[] orders = spec.split(",");
		this.orders = new int[orders.length];
		for(int i=0; i<orders.length; i++)
			this.orders[i] = Integer.parseInt(orders[i].trim());
	}

	@Override
	public ArrayList<List<List<String>>> extractClassif(Sentence s) {
		try { s.ensureFinalized(); s.generateTree(); }catch(Exception e) { }
		ArrayList<List<List<String>>>  r = new ArrayList<List<List<String>>>();
		
		for(int i=0; i<s.words.getFirst().cues.size(); i++) {
			List<List<String>> sentence = new LinkedList<List<String>>();
			r.add(sentence);
			for(Word w : s.words) {
				ArrayList<String> heads = new ArrayList<String>();
				List<String> features = new LinkedList<String>();
				sentence.add(features);
				Node n = w.node;
				while(n.mother != null) {
					heads.add(n.mother.pos);
					n = n.mother;
				}
				
				for(int order : orders) {
					int o = order;
					if(order < 0) o = heads.size() - o;
					o = Math.min(o, heads.size()-1);
					features.add("order-" + order + "-head:" + heads.get(o));
				}
			}
		}
		return r;
	}
	@Override
	public String toString()
	{
		String s = "POS Head Orders: ";
		for(int i : orders) s += i+",";
		return s.substring(0, s.length()-1);
	}
}

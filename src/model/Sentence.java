/**
 * 
 */
package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author muelleml
 */
public class Sentence {
	String origin;

	public Node root;

	public List<Word> words;

	public List<List<Cue>> verticalCues = new LinkedList<List<Cue>>();

	public Sentence() {
		words = new LinkedList<Word>();
	}

	public Sentence(Sentence base) {
		// Deep Copy
		this.origin = base.origin;
		this.root = new Node(base.root);
		this.words = new LinkedList<Word>();
		for(Word w : base.words)this.words.add(new Word(w)); 
		if(base.verticalCues != null) {
			this.verticalCues = new LinkedList<List<Cue>>();
			for(List<Cue> list : base.verticalCues) {
				List<Cue> temp = new LinkedList<Cue>();
				for(Cue c : list) temp.add(new Cue(c));
				this.verticalCues.add(temp);
			}
		}
	}

	public String toString() {

		StringBuilder sb = new StringBuilder(100);

		for (Word w : words) {
			sb.append(w.toString() + "\n");
		}

		return sb.toString();
	}

	/*
	 * NEVER RENAME THIS TO finalize()! Method ensures that the cuedepth is
	 * uniform. The verticalCues list is filled. Call after inserting the last
	 * Word or Cue.
	 */
	public void finalizeSent() {
		int max = 0;

		for (Word w : words) {
			if (w.cues.size() > max) {
				max = w.cues.size();
			}
		}

		int i = 0;
		while (i < max) {
			verticalCues.add(new LinkedList<Cue>());
			i++;
		}

		for (Word w : words) {
			i = 0;
			while (w.cues.size() < max) {
				w.cues.add(new Cue("_", "_", "_"));

			}
			for (Cue c : w.cues) {
				verticalCues.get(i).add(c);
				i++;
			}
		}

	}

	public void generateTree() {
		Stack<Node> stack = new Stack<Node>();

		Pattern p = Pattern.compile("[\\w]+|[(]|[)]|[*]");

		String open = "(";
		String close = ")";
		String asterisk = "*";

		for (Word w : words) {

			Matcher m = p.matcher(w.parseTree);

			while (m.find()) {
				String match = m.group();

				if (open.equals(match)) {

				}

				else if (close.equals(match)) {
					stack.pop();
				} else if (asterisk.equals(match)) {

					Node t = new Node(w.pos, stack.lastElement(), w);
					stack.lastElement().daughters.add(t);
					w.node = t;

				} else {

					if (root == null) {
						root = new Node(match, null, null);
						stack.add(root);

					} else {
						Node t = new Node(match, stack.lastElement(), null);
						stack.lastElement().daughters.add(t);
						stack.add(t);
					}
				}
			}

		}

	}
}

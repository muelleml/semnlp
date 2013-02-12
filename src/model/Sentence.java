/**
 * 
 */
package model;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author muelleml
 */
public class Sentence {
	String origin;
	public Node root;
	public LinkedList<Word> words;
	public List<Cue>[] verticalCues;

	Semaphore mutex;
	boolean finalized;
	boolean generatedTree;

	public Sentence() {
		words = new LinkedList<Word>();
		finalized = false;
		generatedTree = false;
		mutex = new Semaphore(1);
	}

	public Sentence(Sentence s) {
		this.finalized = false;
		this.generatedTree = false;
		this.mutex = new Semaphore(1);
		this.words = new LinkedList<Word>();
		for(Word w : s.words) {
			this.words.add(new Word(w));
		}
	}

	@Override
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
	@SuppressWarnings("unchecked")
	public void ensureFinalized() throws InterruptedException {
		mutex.acquire();

		if(!finalized) {
			int cueCount = words.getLast().cues.size();
			verticalCues = new LinkedList[cueCount];
			for(Word w : words) {
				for(int i=cueCount-w.cues.size(); i> 0; i--) {
					w.cues.add(new Cue("_", "_", "_"));
				}
				int i = 0;
				for(Cue c : w.cues) {
					if(verticalCues[i] == null) verticalCues[i] = new LinkedList<Cue>();
					verticalCues[i].add(c);
					i++;
				}
			}
			finalized = true;
		}
		mutex.release();
	}

	public void generateTree() throws InterruptedException {
		mutex.acquire();

		if(!generatedTree) {

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
			generatedTree = true;
		}
		mutex.release();

	}
}

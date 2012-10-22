/**
 * 
 */
package classifier;

/**
 * @author muelleml
 * 
 */
public class Metric {

	public float f1 = 0f;
	public float prec = 0f;
	public float rec = 0f;

	public float tp = 0f;
	public float tn = 0f;
	public float fp = 0f;
	public float fn = 0f;

	public Metric() {

	}

	/**
	 * @param f1
	 * @param prec
	 * @param rec
	 */
	public Metric(float f1, float prec, float rec) {
		this.f1 = f1;
		this.prec = prec;
		this.rec = rec;
	}

	/**
	 * This is the prefered Constructor. f_1 score, precision and recall get
	 * calculated automaticly.
	 * 
	 * @param tp
	 * @param tn
	 * @param fp
	 * @param fn
	 */
	public Metric(float tp, float tn, float fp, float fn) {
		this.tp = tp;
		this.tn = tn;
		this.fp = fp;
		this.fn = fn;

		prec = tp / (tp + fp);
		rec = tp / (tp + fn);

		f1 = 2 * (prec * rec) / (prec + rec);
	}
	
	public void printMetrics(){
		System.out.print(f1);
		System.out.print(tp);
		System.out.print(tn);
		System.out.print(fp);
		System.out.print(fn);
	}

}

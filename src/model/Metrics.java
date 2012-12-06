/**
 * 
 */
package model;

import java.util.LinkedList;
import java.util.List;

/**
 * @author muelleml
 * 
 */
public class Metrics {

	List<Metric> mList;

	public Metrics() {
		mList = new LinkedList<Metric>();
	}

	/**
	 * Adding a Metric to this Metrics collection
	 * 
	 * @param m
	 *            the Metric to add.
	 */
	public void addMetric(Metric m) {
		mList.add(m);
	}

	/**
	 * Bulk add Metric Objects
	 * 
	 * @param m
	 *            List of Metrics to add
	 */
	public void addMetrics(List<Metric> m) {
		mList.addAll(m);
	}

	public Metric averages() {
		Metric r = new Metric();

		int size = mList.size();

		if (!mList.isEmpty()) {
			// Cue Metrics
			int cgold = 0;
			int csystem = 0;

			int ctp = 0;
			int cfp = 0;
			int cfn = 0;

			float cprec = 0;
			float crec = 0;
			float cf = 0;

			// scope Metrics
			int sgold = 0;
			int ssystem = 0;

			int stp = 0;
			int sfp = 0;
			int sfn = 0;

			float sprec = 0;
			float srec = 0;
			float sf = 0;

			for (Metric m : mList) {
				// Cue Metrics
				cgold += m.getCgold();
				csystem += m.getCsystem();

				ctp += m.getCtp();
				cfp += m.getCfp();
				cfn += m.getCfn();

				cprec += m.getCprec();
				crec += m.getCrec();
				cf += m.getCf();

				// scope Metrics
				sgold += m.getSgold();
				ssystem += m.getSsystem();

				stp += m.getStp();
				sfp += m.getSfp();
				sfn += m.getSfn();

				sprec += m.getSprec();
				srec += m.getSrec();
				sf += m.getSf();
			}

			r.addCueMetric(cgold / size, csystem / size, ctp / size,
					cfp / size, cfn / size, cprec / (float) size, crec
							/ (float) size, cf / (float) size);

			r.addScopeMetric(sgold / size, ssystem / size, stp / size, sfp
					/ size, sfn / size, sprec / (float) size, srec
					/ (float) size, sf / (float) size);
		}

		return r;
	}
}

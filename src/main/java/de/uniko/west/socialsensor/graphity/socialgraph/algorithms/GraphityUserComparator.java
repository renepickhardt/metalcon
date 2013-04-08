package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import java.util.Comparator;

/**
 * Graphity user comparator comparing status update time stamps
 * 
 * @author Sebastian Schlicht
 * 
 */
public class GraphityUserComparator implements Comparator<GraphityUser> {

	@Override
	public int compare(final GraphityUser o1, final GraphityUser o2) {
		if (o1.getStatusUpdateTimestamp() > o2.getStatusUpdateTimestamp()) {
			return 1;
		} else {
			return -1;
		}
	}

}
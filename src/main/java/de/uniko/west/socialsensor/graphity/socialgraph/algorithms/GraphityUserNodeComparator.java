package de.uniko.west.socialsensor.graphity.socialgraph.algorithms;

import java.util.Comparator;

/**
 * Graphity user node comparator comparing status update time stamps
 * 
 * @author Sebastian Schlicht
 * 
 */
public class GraphityUserNodeComparator implements Comparator<GraphityUserNode> {

	@Override
	public int compare(final GraphityUserNode o1, final GraphityUserNode o2) {
		if (o1.getStatusUpdateTimestamp() > o2.getStatusUpdateTimestamp()) {
			return 1;
		} else {
			return -1;
		}
	}

}
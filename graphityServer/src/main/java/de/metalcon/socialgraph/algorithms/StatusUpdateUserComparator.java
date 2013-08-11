package de.metalcon.socialgraph.algorithms;

import java.util.Comparator;

/**
 * user comparator comparing status update time stamps
 * 
 * @author Sebastian Schlicht
 * 
 */
public class StatusUpdateUserComparator implements Comparator<StatusUpdateUser> {

	@Override
	public int compare(final StatusUpdateUser o1, final StatusUpdateUser o2) {
		if (o1.getStatusUpdateTimestamp() > o2.getStatusUpdateTimestamp()) {
			return 1;
		} else {
			return -1;
		}
	}

}
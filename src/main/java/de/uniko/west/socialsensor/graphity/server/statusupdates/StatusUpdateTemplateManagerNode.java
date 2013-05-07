package de.uniko.west.socialsensor.graphity.server.statusupdates;

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.AbstractGraphDatabase;

import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;

/**
 * status update template manager node maintaining existing templates
 * 
 * @author Sebastian Schlicht
 * 
 */
public class StatusUpdateTemplateManagerNode {

	/**
	 * transaction for database changes
	 */
	private Transaction transaction;

	/**
	 * node referring to existing templates
	 */
	private Node node;

	/**
	 * create a new status update template manager node
	 * 
	 * @param graphDatabase
	 *            database targeted
	 */
	public StatusUpdateTemplateManagerNode(
			final AbstractGraphDatabase graphDatabase) {
		final Node root = graphDatabase.getNodeById(0);
		this.node = NeoUtils.getNextSingleNode(root,
				getStatusUpdateTemplatesNodeRT());

		// create status update templates node if not existing
		if (this.node == null) {
			this.transaction = graphDatabase.beginTx();
			try {
				this.node = graphDatabase.createNode();
				root.createRelationshipTo(this.node,
						getStatusUpdateTemplatesNodeRT());
				this.transaction.success();
			} finally {
				this.transaction.finish();
			}
		}
	}

	/**
	 * get the relationship type to find the status update template manager node
	 * 
	 * @return relationship type between root and manager nodes
	 */
	private static DynamicRelationshipType getStatusUpdateTemplatesNodeRT() {
		return DynamicRelationshipType
				.withName("statusUpdateTemplateManagerNode");
	}

}
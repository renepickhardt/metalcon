package de.uniko.west.socialsensor.graphity.server.statusupdates;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
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
	 * database targeted
	 */
	private final AbstractGraphDatabase graphDatabase;

	/**
	 * node referring to existing templates
	 */
	private Node managerNode;

	/**
	 * create a new status update template manager node
	 * 
	 * @param graphDatabase
	 *            database targeted
	 */
	public StatusUpdateTemplateManagerNode(
			final AbstractGraphDatabase graphDatabase) {
		this.graphDatabase = graphDatabase;
		final Node root = NeoUtils.getNodeByIdentifier(graphDatabase,
				NeoUtils.NodeIdentifiers.ROOT);

		final RelationshipType templateManager = DynamicRelationshipType
				.withName("statusUpdateTemplateManagerNode");
		this.managerNode = NeoUtils.getNextSingleNode(root, templateManager);

		// create status update template manager node if not existing
		if (this.managerNode == null) {
			final Transaction transaction = graphDatabase.beginTx();
			try {
				this.managerNode = graphDatabase.createNode();
				root.createRelationshipTo(this.managerNode, templateManager);
				transaction.success();
			} finally {
				transaction.finish();
			}
		}
	}

	/**
	 * load a certain status update template from the database
	 * 
	 * @param identifier
	 *            template name
	 * @return status update template if existing<br>
	 *         <b>null</b> otherwise
	 */
	public StatusUpdateTemplate getStatusUpdateTemplate(final String name) {
		final Node templateNode = NeoUtils.getNextSingleNode(this.managerNode,
				getStatusUpdateTemplateNodeRT(name));

		// load status update template if existing
		if (templateNode != null) {
			return new StatusUpdateTemplate(templateNode);
		} else {
			return null;
		}
	}

	/**
	 * store a status update template in the database
	 * 
	 * @param template
	 *            status update template to be stored
	 */
	public void storeStatusUpdateTemplate(final StatusUpdateTemplate template) {
		// check for previous versions
		final DynamicRelationshipType templateRelationshipType = getStatusUpdateTemplateNodeRT(template
				.getName());
		final Node previousTemplate = NeoUtils.getNextSingleNode(
				this.managerNode, templateRelationshipType);

		// create and connect status update template node
		final Transaction transaction = this.graphDatabase.beginTx();
		try {
			final Node templateNode = StatusUpdateTemplate.createTemplateNode(
					this.graphDatabase, template);

			if (previousTemplate != null) {
				// bridge previous template node
				this.managerNode.getSingleRelationship(
						templateRelationshipType, Direction.OUTGOING).delete();
				templateNode.createRelationshipTo(previousTemplate,
						templateRelationshipType);
			}
			this.managerNode.createRelationshipTo(templateNode,
					templateRelationshipType);

			transaction.success();
		} finally {
			transaction.finish();
		}
	}

	/**
	 * get the relationship type to find a certain status update template
	 * 
	 * @param identifier
	 *            status update template identifier
	 * @return relationship type between manager and template nodes
	 */
	private static DynamicRelationshipType getStatusUpdateTemplateNodeRT(
			final String identifier) {
		return DynamicRelationshipType.withName("tmpl:" + identifier);
	}

}
package de.uniko.west.socialsensor.graphity.server.statusupdates;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.AbstractGraphDatabase;

import de.uniko.west.socialsensor.graphity.socialgraph.NeoUtils;
import de.uniko.west.socialsensor.graphity.socialgraph.Properties;

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
		final Node root = graphDatabase.getNodeById(0);

		this.managerNode = NeoUtils.getNextSingleNode(root,
				getStatusUpdateTemplatesNodeRT());

		// create status update template manager node if not existing
		if (this.managerNode == null) {
			final Transaction transaction = graphDatabase.beginTx();
			try {
				this.managerNode = graphDatabase.createNode();
				root.createRelationshipTo(this.managerNode,
						getStatusUpdateTemplatesNodeRT());
				transaction.success();
			} finally {
				transaction.finish();
			}
		}
	}

	/**
	 * load a certain status update template node
	 * 
	 * @param identifier
	 *            template identifier
	 * @return status update template node if existing<br>
	 *         null otherwise
	 */
	public StatusUpdateTemplateNode getStatusUpdateTemplateNode(
			final String identifier) {
		final Node templateNode = NeoUtils.getNextSingleNode(this.managerNode,
				getStatusUpdateTemplateNodeRT(identifier));

		// load status update template node if existing
		if (templateNode != null) {
			return new StatusUpdateTemplateNode(templateNode);
		} else {
			return null;
		}
	}

	public StatusUpdateTemplateNode createStatusUpdateTemplateNode(
			final StatusUpdateTemplateFile templateFile) {
		// check for previous versions
		final DynamicRelationshipType templateRelationshipType = getStatusUpdateTemplateNodeRT(templateFile
				.getIdentifier());
		final Node previousTemplate = NeoUtils.getNextSingleNode(
				this.managerNode, templateRelationshipType);
		if (previousTemplate != null) {
			// remove relationship
			this.managerNode.getSingleRelationship(templateRelationshipType,
					Direction.OUTGOING).delete();
		}

		// create and fill status update template node
		final Transaction transaction = this.graphDatabase.beginTx();
		Node templateNode = null;
		try {
			templateNode = this.graphDatabase.createNode();
			templateNode.setProperty(Properties.Templates.IDENTIFIER,
					templateFile.getIdentifier());
			templateNode.setProperty(Properties.Templates.VERSION,
					templateFile.getVersion());
			templateNode.setProperty(Properties.Templates.CODE,
					templateFile.generateJavaCode());
			this.managerNode
					.createRelationshipTo(templateNode,
							getStatusUpdateTemplateNodeRT(templateFile
									.getIdentifier()));
			transaction.success();
		} finally {
			transaction.finish();
		}

		// append previous version line
		if (previousTemplate != null) {
			templateNode.createRelationshipTo(previousTemplate,
					templateRelationshipType);
		}

		// load status update template node
		return new StatusUpdateTemplateNode(templateNode);
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
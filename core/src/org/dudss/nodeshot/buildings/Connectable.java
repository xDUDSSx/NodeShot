package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.entities.nodes.Node;

/**A interface that identifies an {@link AbstractBuilding} that has a single {@link Node} that can be connected to the conveyor system.*/
public interface Connectable {
	/**@return A {@link Node} of the assigned {@link AbstractBuilding}*/
	public Node getNode();
}

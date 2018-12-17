package org.dudss.nodeshot.buildings;

import java.util.List;

import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.nodes.InputNode;
import org.dudss.nodeshot.items.StorableItem;

/**Skeletal construction of a building that can be alerted by I/O {@link Node}s.*/
public abstract class AlertableBuilding extends AbstractBuilding implements Alertable {

	List<ItemType> accepted;
	
	/**
	 * {@inheritDoc}
	 */
	public AlertableBuilding(float cx, float cy, float width, float height) {
		super(cx, cy, width, height);
	}

	/**Method used by {@link InputNode}s used to alert the building that a following package is trying to be transfered.
	 * @param p The {@link Package} that is being transfered.
	 * @return Returns if the {@linkplain Package} transfer was successful.
	 * */
	@Override
	public abstract boolean alert(StorableItem p);

	@Override
	public abstract boolean canStore(StorableItem p);

	@Override
	public void setAccepted(List<ItemType> accepted) {
		this.accepted = accepted;	
	}

	@Override
	public List<ItemType> getAccepted() {
		return accepted;
	}
}

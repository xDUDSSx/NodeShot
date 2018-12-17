package org.dudss.nodeshot.entities.nodes;

import org.dudss.nodeshot.buildings.AbstractStorage;
import org.dudss.nodeshot.buildings.Alertable;
import org.dudss.nodeshot.buildings.AlertableBuilding;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.misc.DefinitePathHandler;
import org.dudss.nodeshot.utils.SpriteLoader;

/**A node that can transfer nodes to an assigned {@link AbstractStorage}*/
public class InputNode extends Node {

	AlertableBuilding assignedBuilding;

	public InputNode(float cx, float cy, int radius, AlertableBuilding building) {
		super(cx, cy, radius);		
		assignedBuilding = building;
		this.set(SpriteLoader.nodeInputSprite);
		this.setPosition(x, y);
	}
	
	public void update() {
		if (this.getConnectors().size() > 0) {
			for (Connector c : this.getConnectors()) {
				Package p = c.recievePackage(this);		
				if (p != null) {					
					//Check if the package is an accepted type and if the storage is able to contain it
					if (this.assignedBuilding.canStore(p.getStorable())) {
						switch (p.getPathHandler().getType()) {
							//Check if this InputNode is the path destination of a definite path handler (if not, let it go through)
							case DefinitePathHandler: 
								DefinitePathHandler ph = (DefinitePathHandler) p.getPathHandler();
								if (ph.to == this) {
									c.remove(p);
									p.destroy();
									assignedBuilding.alert(p.getStorable());
								}
								break;
							case IndefinitePathHandler: 
								c.remove(p);
								p.destroy();
								assignedBuilding.alert(p.getStorable());
								break;
						}
					}
				}			
			}
		}
	}
	
	public Alertable getAssignedStorage() {
		return assignedBuilding;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.INPUTNODE;
	}
}

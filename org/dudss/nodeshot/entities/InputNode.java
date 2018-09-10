package org.dudss.nodeshot.entities;

import org.dudss.nodeshot.buildings.Building;
import org.dudss.nodeshot.misc.DefinitePathHandler;
import org.dudss.nodeshot.utils.SpriteLoader;

public class InputNode extends Node {

	Building assignedBuilding;
	
	public InputNode(float cx, float cy, int radius, Building building) {
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
					switch (p.getPathHandler().getType()) {
						case DefinitePathHandler: 
							DefinitePathHandler ph = (DefinitePathHandler) p.getPathHandler();
							if (ph.to == this) {
								c.remove(p);
								p.destroy();
								System.out.println("Alerting building PPH");
								assignedBuilding.alert(p);
							}
							break;
						case IndefinitePathHandler: 
							c.remove(p);
							p.destroy();
							System.out.println("Alerting building");
							assignedBuilding.alert(p);
							break;
					}
				}			
			}
		}
	}
	
	public Building getAssignedBuilding() {
		return assignedBuilding;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.INPUTNODE;
	}
}

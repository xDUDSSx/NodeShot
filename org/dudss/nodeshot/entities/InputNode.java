package org.dudss.nodeshot.entities;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.buildings.Building;
import org.dudss.nodeshot.buildings.Storage;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.misc.DefinitePathHandler;
import org.dudss.nodeshot.utils.SpriteLoader;

public class InputNode extends Node {

	Storage assignedBuilding;
	
	List<ItemType> accepted;
	
	public InputNode(float cx, float cy, int radius, Storage building) {
		super(cx, cy, radius);
		accepted = new ArrayList<ItemType>();
		assignedBuilding = building;
		this.set(SpriteLoader.nodeInputSprite);
		this.setPosition(x, y);
	}
	
	public void update() {
		if (this.getConnectors().size() > 0) {
			for (Connector c : this.getConnectors()) {
				Package p = c.recievePackage(this);		
				if (p != null) {
					if (accepted.contains(p.getItemType()) && this.assignedBuilding.canStore(p.getItemType())) {
						switch (p.getPathHandler().getType()) {
							case DefinitePathHandler: 
								DefinitePathHandler ph = (DefinitePathHandler) p.getPathHandler();
								if (ph.to == this) {
									c.remove(p);
									p.destroy();
									//System.out.println("Alerting building PPH");
									((Building) assignedBuilding).alert(p);
								}
								break;
							case IndefinitePathHandler: 
								c.remove(p);
								p.destroy();
								//System.out.println("Alerting building");
								((Building) assignedBuilding).alert(p);
								break;
						}
					}
				}			
			}
		}
	}
	
	public Storage getAssignedStorage() {
		return assignedBuilding;
	}
	
	public void setAccepted(List<ItemType> accepted) {
		this.accepted = accepted;
	}
	
	public List<ItemType> getAccepted() {
		return this.accepted;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.INPUTNODE;
	}
}

package org.dudss.nodeshot.entities.nodes;

import org.dudss.nodeshot.buildings.Building;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.utils.SpriteLoader;

public class OutputNode extends Node {
	
	Building assignedBuilding;
	
	public OutputNode(float cx, float cy, int radius, Building building) {
		super(cx, cy, radius);
		
		assignedBuilding = building;
		this.maxConnections = 1;
		this.setConnectable(false);
		
		this.set(SpriteLoader.nodeOutputSprite);
		this.setPosition(x, y);
		
	}

	public Building getAssignedBuilding() {
		return assignedBuilding;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.OUTPUTNODE;
	}
}

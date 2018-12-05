package org.dudss.nodeshot.entities.nodes;

import org.dudss.nodeshot.buildings.AbstractBuilding;

public class BuildingNode extends Node {

	AbstractBuilding assignedBuilding;
	
	public BuildingNode(float cx, float cy, int radius, AbstractBuilding building) {
		super(cx, cy, radius);
		assignedBuilding = building;		
	}

	public AbstractBuilding getAssignedBuilding() {
		return assignedBuilding;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.BUILDINGNODE;
	}
}

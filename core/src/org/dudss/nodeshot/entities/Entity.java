package org.dudss.nodeshot.entities;

public interface Entity {
	
	public static enum EntityType {
		NODE, CONNECTOR, PACKAGE, NONE, INPUTNODE, OUTPUTNODE, BUILDINGNODE, CONVEYOR, BULLET,
		HQ, MINE, STORAGE, FURNACE, GENERATOR, BUILDING
	}
	
	public int getID();
	public int getIndex();
	public EntityType getType();
	public float getX();
	public float getY();
}

package org.dudss.nodeshot.entities;

public interface Entity {
	
	public static enum EntityType {
		NODE, CONNECTOR, PACKAGE, NONE, IONODE, CONVEYOR, BULLET,
		HQ, MINE, STORAGE, FACTORY, POWER_GENERATOR, BUILDING, CREEPER_GENERATOR, EXPORTER, IMPORTER, NODE_BUILDING, SHIPDOCK, TURRET,
		VISUAL_EFFECT
	}
	
	public int getID();
	@Deprecated
	public int getIndex();
	public EntityType getType();
	public float getX();
	public float getY();
}

package org.dudss.nodeshot.entities;

/**A skeletal representation of any game object.*/
public interface Entity {	
	/**Enum containing all (currently just some) game object ({@link Entity}) types.*/
	public static enum EntityType {
		NODE, 
		CONNECTOR, 
		CONVEYOR,
		PACKAGE, 
		NONE, 
		IONODE, 
		BULLET,
		HQ, 
		MINE, 
		STORAGE, 
		FACTORY, 
		AMMO_PROCESSOR,
		ARTILLERY_CANNON,
		POWER_GENERATOR, 
		BUILDING, 
		CREEPER_GENERATOR, 
		EXPORTER, IMPORTER, 
		NODE_BUILDING, 
		SHIPDOCK, 
		TURRET,
		VISUAL_EFFECT
	}
	
	/**Returns a unique specifier.*/
	public int getID();
	@Deprecated
	public int getIndex();
	
	/**Returns the associated {@link EntityType}.*/
	public EntityType getType();
	
	/**Returns the X coordinate (bottom left corner).*/
	public float getX();
	
	/**Returns the Y coordinate (bottom left corner).*/
	public float getY();
}

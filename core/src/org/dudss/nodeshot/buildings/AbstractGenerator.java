package org.dudss.nodeshot.buildings;

/**Generator that spawns creeper*/
public abstract class AbstractGenerator extends AbstractBuilding {
	static float width = 16;
	static float height = 16;
	
	boolean active = false;
	
	public AbstractGenerator(float cx, float cy) {
		super(cx, cy, width, height);
	}

	public float spawnRate;
	
	@Override
	public void update() {
		generate();
	}
	
	protected abstract void generate();
	
	void setActive (boolean active) {
		this.active = active;
		
		if (active) {
			
		}
	}
	
	boolean isActive() {
		return active;
	}
}

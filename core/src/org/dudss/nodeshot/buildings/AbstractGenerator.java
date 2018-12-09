package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;

/**Generator that spawns creeper*/
public abstract class AbstractGenerator extends AbstractBuilding {
	static float width = Base.CHUNK_SIZE*3;
	static float height = Base.CHUNK_SIZE*3;
	
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

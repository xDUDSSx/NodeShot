package org.dudss.nodeshot.buildings;

/**Generator that spawns creeper*/
public abstract class Generator extends Building {
	static float width = 16;
	static float height = 16;
	
	public Generator(float cx, float cy) {
		super(cx, cy, width, height);
	}

	public float spawnRate;
	
	@Override
	public void update() {
		generate();
	}
	
	protected abstract void generate();
}

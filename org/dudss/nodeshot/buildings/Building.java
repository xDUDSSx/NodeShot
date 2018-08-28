package org.dudss.nodeshot.buildings;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Building {
	public void update();
	void draw(ShapeRenderer r);
	public void build();
	public void demolish();
}

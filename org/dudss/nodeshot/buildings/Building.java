package org.dudss.nodeshot.buildings;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.dudss.nodeshot.entities.Package;

public interface Building {
	public void update();	
	public void setLocation(float cx, float cy);
	void draw(ShapeRenderer r);
	void drawPrefab(ShapeRenderer r, float cx, float cy);	
	public void alert(Package p);
	public void alert();
	public void build();	
	public void demolish();
}

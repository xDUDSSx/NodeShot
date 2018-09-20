package org.dudss.nodeshot.buildings;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.dudss.nodeshot.entities.Package;

public interface Building {
	public void update();	
	public void setLocation(float cx, float cy, boolean snap);

	//Draw and prefab draw methods (prefab is the building representation following the cursor when in build mode)
	void draw(ShapeRenderer r);
	void drawPrefab(ShapeRenderer r, float cx, float cy, boolean snap);	
	
	//Method used by InputNodes used to alert the building that a following package had been accepted
	public void alert(Package p);
	
	public void build();	
	public void demolish();
}

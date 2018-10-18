package org.dudss.nodeshot.buildings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Package;

public abstract class Building {
	float x,y;
	float cx,cy;
	float width = 48;
	float height = 48;

	public void setLocation(float cx, float cy, boolean snap) {
		if (snap) {
			float nx = Math.round(cx - (cx % Base.CHUNK_SIZE));
			float ny = Math.round(cy - (cy % Base.CHUNK_SIZE));
			
			x = nx - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;
			y = ny - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;
			
			this.cx = nx + Base.CHUNK_SIZE/2;
			this.cy = ny + Base.CHUNK_SIZE/2;
		} else {
			this.cx = cx;
			this.cy = cy;
			
			x = cx - (width/2);
			y = cy - (height/2);
		}		
	}
	
	public abstract void update();	
	
	//Draw and prefab draw methods (prefab is the building representation following the cursor when in build mode)
	public abstract void draw(ShapeRenderer r, SpriteBatch batch);	
	public abstract void drawPrefab(ShapeRenderer r, float cx, float cy, boolean snap);	
	
	//Method used by InputNodes used to alert the building that a following package had been accepted
	public abstract void alert(Package p);
	
	public abstract void build();	
	public abstract void demolish();
}

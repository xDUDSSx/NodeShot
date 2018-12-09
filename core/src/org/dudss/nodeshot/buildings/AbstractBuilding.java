package org.dudss.nodeshot.buildings;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunks;

public abstract class AbstractBuilding implements Entity {
	float x,y;
	float cx,cy;
	
	float width;
	float height;
	
	boolean outlined = false;
	
	int id;
	
	public AbstractBuilding(float cx, float cy, float width, float height) {		
		this.id = java.lang.System.identityHashCode(this);
		
		this.cx = cx;
		this.cy = cy;
		
		this.width = width;
		this.height = height;
		
		x = cx - (width/2);
		y = cy - (height/2);
	}
	
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
	
	public float getPrefabX(float cx, boolean snap) {
		float prefX;
		if (snap) {
			float nx = Math.round(cx - (cx % Base.CHUNK_SIZE));
			prefX = nx - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;
		} else {
			prefX = cx - (width/2);
		}
		return prefX;
	}
	
	public float getPrefabY(float cy, boolean snap) {
		float prefY;
		if (snap) {;
			float ny = Math.round(cy - (cy % Base.CHUNK_SIZE));
			prefY = ny - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;	
		} else {
			prefY = cy - (height/2);
		}
		return prefY;
	}
	
	public abstract void update();	
	
	//Draw and prefab draw methods (prefab is the building representation following the cursor when in build mode)
	public abstract void draw(ShapeRenderer r, SpriteBatch batch);	
	public abstract void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap);	
	
	//Method used by InputNodes used to alert the building that a following package had been accepted
	public abstract void alert(Package p);
	
	public abstract void build();	
	public abstract void demolish();
	
	public void outline(boolean outline) {
		outlined = outline;
	}
	
	protected void updateFogOfWar(boolean show) {
		if (show) {
			GameScreen.chunks.setVisibility(this.cx, this.cy, Base.SECTION_SIZE, Chunks.Visibility.ACTIVE);
		} else {
			GameScreen.chunks.setVisibility(this.cx, this.cy, Base.SECTION_SIZE, Chunks.Visibility.SEMIACTIVE);
		}
		
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
	
	@Override
	public int getID() {
		return id;
	}

	@Override
	public int getIndex() {
		return GameScreen.buildingHandler.getAllBuildings().indexOf(this);
	}

	@Override
	public EntityType getType() {
		return EntityType.BUILDING;
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}

}

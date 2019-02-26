package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.buildings.AbstractBuilding.BuildingType;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.nodes.ConveyorNode;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class NodeBuilding extends AbstractBuilding implements Connectable {
	
	static float width = Base.CHUNK_SIZE*1, height = Base.CHUNK_SIZE*1;
	
	TextureRegion s = new TextureRegion(SpriteLoader.node);
	
	ConveyorNode n;
	
	public NodeBuilding(float cx, float cy) {
		super(cx, cy, width, height);
		this.buildingType = BuildingType.MISC;
		this.fogOfWarRadius = Base.SECTION_SIZE/4;
	}

	@Override
	public void update() {
		super.update();
	}
	
	@Override
	protected void setCoordinates(float cx, float cy, boolean snap) {
		float newX = 0;
		float newY = 0;
		
		if (snap) {
			if (GameScreen.expandingANode && GameScreen.expandedConveyorNode != null) {
				int sx = (int) (cx / Base.CHUNK_SIZE);
				int sy = (int) (cy / Base.CHUNK_SIZE);
				
				int ex = (int) (GameScreen.expandedConveyorNode.getX() / Base.CHUNK_SIZE);
				int ey = (int) (GameScreen.expandedConveyorNode.getY() / Base.CHUNK_SIZE);
				if (Math.abs(GameScreen.expandedConveyorNode.getCX()-cx) > Math.abs(GameScreen.expandedConveyorNode.getCY()-cy)*2) {
					newX = sx * Base.CHUNK_SIZE;
					newY = ey * Base.CHUNK_SIZE;
				} else 
				if (Math.abs(GameScreen.expandedConveyorNode.getCY()-cy) > Math.abs(GameScreen.expandedConveyorNode.getCX()-cx)*2){
					newX = ex * Base.CHUNK_SIZE;
					newY = sy * Base.CHUNK_SIZE;
				} else {
					int offset = (Math.abs(ex-sx) + Math.abs(ey-sy))/2;
					if (sx > ex) {
						if (sy > ey) {
							newX = (ex + offset) * Base.CHUNK_SIZE;
							newY = (ey + offset) * Base.CHUNK_SIZE;
						} else {
							newX = (ex + offset) * Base.CHUNK_SIZE;
							newY = (ey - offset) * Base.CHUNK_SIZE;
						}
					} else {
						if (sy > ey) {
							newX = (ex - offset) * Base.CHUNK_SIZE;
							newY = (ey + offset) * Base.CHUNK_SIZE;
						} else {
							newX = (ex - offset) * Base.CHUNK_SIZE;
							newY = (ey - offset) * Base.CHUNK_SIZE;
						}
					}
				}
						
				this.cx = newX + Base.CHUNK_SIZE/2;
				this.cy = newY + Base.CHUNK_SIZE/2;
			} else {
				float nx = Math.round(cx - (cx % Base.CHUNK_SIZE));
				float ny = Math.round(cy - (cy % Base.CHUNK_SIZE));
				
				newX = nx - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;
				newY = ny - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;
				
				this.cx = nx + Base.CHUNK_SIZE/2;
				this.cy = ny + Base.CHUNK_SIZE/2;
			}
		} else {
			this.cx = cx;
			this.cy = cy;
			
			newX = cx - (width/2);
			newY = cy - (height/2);
		}
		
		x = newX;
		y = newY;
	}
	
	@Override
	public Vector2 getCoordinates(float cx, float cy, boolean snap) {
		float newX = 0;
		float newY = 0;
		
		if (snap) {
			if (GameScreen.expandingANode && GameScreen.expandedConveyorNode != null) {
				int sx = (int) (cx / Base.CHUNK_SIZE);
				int sy = (int) (cy / Base.CHUNK_SIZE);
				
				int ex = (int) (GameScreen.expandedConveyorNode.getX() / Base.CHUNK_SIZE);
				int ey = (int) (GameScreen.expandedConveyorNode.getY() / Base.CHUNK_SIZE);
				if (Math.abs(GameScreen.expandedConveyorNode.getCX()-cx) > Math.abs(GameScreen.expandedConveyorNode.getCY()-cy)*2) {
					newX = sx * Base.CHUNK_SIZE;
					newY = ey * Base.CHUNK_SIZE;
				} else 
				if (Math.abs(GameScreen.expandedConveyorNode.getCY()-cy) > Math.abs(GameScreen.expandedConveyorNode.getCX()-cx)*2){
					newX = ex * Base.CHUNK_SIZE;
					newY = sy * Base.CHUNK_SIZE;
				} else {
					int offset = (Math.abs(ex-sx) + Math.abs(ey-sy))/2;
					if (sx > ex) {
						if (sy > ey) {
							newX = (ex + offset) * Base.CHUNK_SIZE;
							newY = (ey + offset) * Base.CHUNK_SIZE;
						} else {
							newX = (ex + offset) * Base.CHUNK_SIZE;
							newY = (ey - offset) * Base.CHUNK_SIZE;
						}
					} else {
						if (sy > ey) {
							newX = (ex - offset) * Base.CHUNK_SIZE;
							newY = (ey + offset) * Base.CHUNK_SIZE;
						} else {
							newX = (ex - offset) * Base.CHUNK_SIZE;
							newY = (ey - offset) * Base.CHUNK_SIZE;
						}
					}
				}
			} else {
				float nx = Math.round(cx - (cx % Base.CHUNK_SIZE));
				float ny = Math.round(cy - (cy % Base.CHUNK_SIZE));
				
				newX = nx - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;
				newY = ny - ((int)(width/2)/Base.CHUNK_SIZE) * Base.CHUNK_SIZE;
			}
		} else {		
			newX = cx - (width/2);
			newY = cy - (height/2);
		}
		
		return new Vector2(newX, newY);
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		batch.setColor(1f, 1f, 1f, 1f);		 
		batch.draw(s, x, y, width, height);				
	}

	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(s, getPrefabVector(cx, cy, snap).x, getPrefabVector(cx, cy, snap).y , width, height);
		batch.end();		
		
	}

	@Override
	public void build() {
		super.build();
		n = new ConveyorNode(x + (width/2), y + (height/2), Base.RADIUS, this);
		n.add();
	}

	@Override
	public void demolish() {
		super.demolish();
		n.remove();
	}

	public ConveyorNode getNode() {
		return n;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.NODE_BUILDING;
	}
}

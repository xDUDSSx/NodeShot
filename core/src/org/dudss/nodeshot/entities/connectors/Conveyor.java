package org.dudss.nodeshot.entities.connectors;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.buildings.ConveyorBuilding;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class Conveyor extends Connector {

	Animation<Texture> anim;
	
	TextureRegion reg;
	float angle;
	
	public boolean reversed = false;
	
	List<ConveyorBuilding> conveyorBuildings;
	
	/**Whether this {@link Conveyor} encountered building chunk collision while 
	 * generating its {@link ConveyorBuilding}s*/
	boolean encounteredBuildingCollision = false;
	
	public Conveyor(Node from, Node to) {
		super(from, to);
		conveyorBuildings = new ArrayList<ConveyorBuilding>();
		reg = new TextureRegion(SpriteLoader.conveyorTexture);	
		anim = new Animation<Texture>(0.0215f, SpriteLoader.conveyorVertical);	
		
		List<Chunk> intersectedChunks = GameScreen.chunks.retrieveChunksIntersectingLine((int) (from.getCX() / Base.CHUNK_SIZE),
				(int) (from.getCY() / Base.CHUNK_SIZE),
				(int)(to.getCX() / Base.CHUNK_SIZE), 
				(int) (to.getCY() / Base.CHUNK_SIZE));
		intersectedChunks.remove(GameScreen.chunks.getChunkAtWorldSpace(from.getCX(), from.getCY()));
		intersectedChunks.remove(GameScreen.chunks.getChunkAtWorldSpace(to.getCX(), to.getCY()));
				
		boolean canBeBuilt = true;
		int nOfConveyorBuildings = 0;
		int nOfBuildings = 0;
		
		//Checks if this conveyor can be built
		//If the conveyor intersects a regular building -> cannot be built
		//If the conveyor intersects a conveyor building of a single conveyor twice or more -> cannot be built
		//Conveyor intersects a conveyor building ONLY once -> can be built
		List<Conveyor> intersectedConveyors = new ArrayList<Conveyor>();
		
		for (Chunk c : intersectedChunks) {
			if (c.getBuilding() != null) {
				if (c.getBuilding() instanceof ConveyorBuilding) {
					if (intersectedConveyors.contains(((ConveyorBuilding) c.getBuilding()).getConveyor())) {
						canBeBuilt = false;
						encounteredBuildingCollision = true;
					}
					intersectedConveyors.add(((ConveyorBuilding) c.getBuilding()).getConveyor());
					nOfConveyorBuildings++;
				} else {
					nOfBuildings++;
				}
			}
		}
		/*if (nOfConveyorBuildings > 1) {
			canBeBuilt = false;
			encounteredBuildingCollision = true;
		}*/
		if (nOfBuildings > 0) {
			canBeBuilt = false;
			encounteredBuildingCollision = true;
		}
		
		if (canBeBuilt) {
			for (Chunk c : intersectedChunks) {
				conveyorBuildings.add(new ConveyorBuilding(c.getCX(), c.getCY(), this));
			}
		}
		
		Vector2 aimVector = new Vector2(this.to.getCX() - this.from.getCX(), this.to.getCY() - this.from.getCY());
		double aimLenght = Math.hypot(aimVector.x, aimVector.y);
		double alpha = Math.asin(aimVector.y/aimLenght);
		
		if (this.to.getCX() <= this.from.getCX()) {
			angle = (float) (360 - Math.toDegrees(alpha)) % 360;
		} else {
			angle = (float) (180 + Math.toDegrees(alpha)) % 360;
		}
		
		if (this.to.getCY() >= this.from.getCY()) {
			angle = Math.abs((angle - 180) % 360);
		} else {
			angle = 360 - Math.abs((angle - 180) % 360);
		}
	}
	
	/**Demolishes all the {@link ConveyorBuilding}s that are assigned to this conveyor.*/
	public void clearBuildingChunks() {
		for (ConveyorBuilding c : conveyorBuildings) {
			c.demolish();
		}	
	}
	
	public boolean isBuiltProperly() {
		return !encounteredBuildingCollision;
	}
	
	@Override
	public void draw(ShapeRenderer sR, SpriteBatch batch) {
		sR.end();
		
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);		
		Texture currentFrame = anim.getKeyFrame(GameScreen.stateTime, true);
		//float fac = (float) (this.lenght/SpriteLoader.conveyorFrames[0].getRegionWidth());
		//currentFrame.setRegion(0, 0, (int)(SpriteLoader.conveyorFrames[0].getRegionWidth()*(this.lenght/50f)), SpriteLoader.conveyorFrames[0].getRegionHeight());
		
		reg = new TextureRegion(currentFrame);
		reg.setRegion(0, 0, (int)(currentFrame.getWidth()*(this.lenght/50f)), currentFrame.getHeight());
		batch.draw(reg, this.from.getCX(), this.from.getCY() - 8, 0, 8, (float) this.lenght, 16, 1, 1, angle);
		
		batch.end();
		
		sR.begin(ShapeType.Filled);
		/*super.draw(sR);
		
		float distanceVector = ((float) this.lenght / 6);		
		int numberOfArrows = Math.round((float)(this.lenght / distanceVector));
		
		float[] verts = new float[10 * numberOfArrows];
				
		int index = 0;
		
		float lPX;
		float lPY;
		Vector2 connectorVector;
		if (!reversed) {
			lPX = from.getCX();
			lPY = from.getCY();
			connectorVector = new Vector2((to.getCX() - from.getCX()), (to.getCY() - from.getCY()));
		} else {
			lPX = to.getCX();
			lPY = to.getCY();
			connectorVector = new Vector2((from.getCX() - to.getCX()), (from.getCY() - to.getCY()));
		}
		
		Vector2 connectorNormalVector = new Vector2(connectorVector.y, -(connectorVector.x));
		double perc = this.lenght / 100;
		float linePerc = (float) (((float) Base.lineWidth/2) / perc);		
		float distanceVectorPerc = (float) ((float) distanceVector / perc);
		
		for (int i = 0; i < numberOfArrows; i++) {
			verts[index++] = lPX;
			verts[index++] = lPY;
			
			float a1 = lPX + connectorVector.x * (distanceVectorPerc/100);
			float a2 = lPY + connectorVector.y * (distanceVectorPerc/100);
			
			lPX = Float.valueOf(a1);
			lPY = Float.valueOf(a2);
			
			verts[index++] = lPX;
			verts[index++] = lPY;
			
			verts[index++] = new Float(lPX) + connectorNormalVector.x * (linePerc/100) - connectorVector.x * (linePerc/100);
			verts[index++] = new Float(lPY) + connectorNormalVector.y * (linePerc/100) - connectorVector.y * (linePerc/100);

			verts[index++] = lPX;
			verts[index++] = lPY;
			
			verts[index++] = new Float(lPX)- connectorNormalVector.x * (linePerc/100) - connectorVector.x * (linePerc/100);
			verts[index++] = new Float(lPY) - connectorNormalVector.y * (linePerc/100) - connectorVector.y * (linePerc/100);
		}

		sR.set(ShapeType.Line);
		Color c = sR.getColor();;
		Color newColor = new Color(c.r * 0.6f, c.g * 0.6f, c.b * 0.6f,1.0f);
		sR.setColor(newColor);	
		sR.polyline(verts);
		sR.set(ShapeType.Filled);
		*/
	}
	
	public void reverse() {
		reversed = !reversed;
	}
	
	public boolean isReversed() {
		return reversed;
	}
	
	/**Whether this conveyor faces the same direction as conveyor c, eg. Whether this connector can accept packages from connector c.*/
	public boolean facesTheSameDirection(Conveyor c) {
		if (!this.isReversed() && !c.isReversed()) {
			if (this.getFrom() != c.getFrom() && this.getTo() != c.getTo()) {
				return true;
			} else {
				return false;
			}
		} else
		if (this.isReversed() && !c.isReversed()) {
			if (this.getTo() != c.getFrom() && this.getFrom() != c.getTo()) {
				return true;
			} else {
				return false;
			}
		}
		new RuntimeException(this.getClass().getName() + " facesTheSameDirection() exception!");
		return false;
	}
	
	/**Whether a package sent from the {@link Node} n can access this conveyor.*/
	public boolean facesTheSameDirection(Node n) {
		if (!this.isReversed()) {
			if (this.getFrom() == n) {
				return true;
			}
		} else {
			if (this.getTo() == n) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.CONVEYOR;
	}
}

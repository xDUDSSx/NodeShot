package org.dudss.nodeshot.entities.connectors;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.buildings.ConveyorBuilding;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

/**A special type of a {@link Connector} that has a representation in the world as {@link ConveyorBuilding}s.
 * It's also ONE WAY.*/
public class Conveyor extends Connector {

	Animation<Texture> anim;
	
	TextureRegion reg;
	float angle;
	
	boolean reversed = false;

	List<ConveyorBuilding> conveyorBuildings;
	
	/**Whether this {@link Conveyor} encountered building chunk collision while 
	 * generating its {@link ConveyorBuilding}s*/
	boolean encounteredBuildingCollision = false;
	
	/**A special type of a {@link Connector} that has a representation in the world as {@link ConveyorBuilding}s.*/
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
					for (Conveyor con : intersectedConveyors) {
						if (((ConveyorBuilding) c.getBuilding()).getConveyors().contains(con)) {
							canBeBuilt = false;
							encounteredBuildingCollision = true;
						}			
					}
					intersectedConveyors.addAll(((ConveyorBuilding) c.getBuilding()).getConveyors());
					nOfConveyorBuildings++;
				} else {
					nOfBuildings++;
				}
			}
		}
		if (nOfBuildings > 0) {
			canBeBuilt = false;
			encounteredBuildingCollision = true;
		}
		
		if (canBeBuilt) {
			for (Chunk c : intersectedChunks) {
				//Guaranteed ConveyorBuilding, no need to check
				if (c.getBuilding() != null) {
					((ConveyorBuilding)c.getBuilding()).addConveyor(this);
					conveyorBuildings.add((ConveyorBuilding)c.getBuilding());
				} else {
					conveyorBuildings.add(new ConveyorBuilding(c.getCX(), c.getCY(), this));
				}
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
	
	/**A utility method used for determining whether a conveyor can be built and how many {@link ConveyorBuilding}s it will need.
	 * @param cx from center X coordinate.
	 * @param cy from center Y coordinate.
	 * @param cx2 to center X coordinate.
	 * @param cy2 to center Y coordinate.
	 * @return Returns a number of {@link ConveyorBuildings} required to build a conveyor inbetween those two points.
	 * Returns -1 if the conveyor CANNOT be built at all.
	 */
	public static int checkCollision(float cx, float cy, float cx2, float cy2) {
		int conveyorBuildings = 0;
		
		List<Chunk> intersectedChunks = GameScreen.chunks.retrieveChunksIntersectingLine((int) (cx / Base.CHUNK_SIZE),
				(int) (cy / Base.CHUNK_SIZE),
				(int)(cx2 / Base.CHUNK_SIZE), 
				(int) (cy2 / Base.CHUNK_SIZE));
		intersectedChunks.remove(GameScreen.chunks.getChunkAtWorldSpace(cx, cy));
		intersectedChunks.remove(GameScreen.chunks.getChunkAtWorldSpace(cx2, cy2));
				
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
					if (intersectedConveyors.contains(((ConveyorBuilding) c.getBuilding()).getConveyors())) {
						canBeBuilt = false;
					}
					intersectedConveyors.addAll(((ConveyorBuilding) c.getBuilding()).getConveyors());
					nOfConveyorBuildings++;
				} else {
					nOfBuildings++;
				}
			}
		}
		if (nOfBuildings > 0) {
			canBeBuilt = false;
		}
		
		if (canBeBuilt) {
			for (Chunk c : intersectedChunks) {
				conveyorBuildings++;
			}
		} else {
			conveyorBuildings = -1;
		}
		
		return conveyorBuildings;
	}
	
	/**Demolishes all the {@link ConveyorBuilding}s that are assigned to this conveyor.
	 * If a {@linkplain ConveyorBuilding} manages more than one conveyor, this conveyor is just removed from it.*/
	public void clearBuildingChunks() {
		for (ConveyorBuilding c : conveyorBuildings) {
			if (c.isBuilt() == false) {
				BaseClass.logger.fine("Demolished conveyor building encountered!");
			} else {
				if (c.getConveyors().size() == 1) {
					c.demolish(true);
				} else {
					c.removeConveyor(this);
				}
			}
		}	
	}
	
	/**Returns a list of its {@link ConveyorBuilding}s.
	 * @return */
	public List<ConveyorBuilding> getConveyorBuildings() {
		return conveyorBuildings;
	}
	
	/**Whether an obstacle was encountered during the last build attempt.*/
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
		
		if (!reversed) {
			batch.draw(reg, this.from.getCX(), this.from.getCY() - 8, 0, 8, (float) this.lenght, 16, 1, 1, angle);
		} else {
			reg.flip(true, false);
			batch.draw(reg, this.from.getCX(), this.from.getCY() - 8, 0, 8, (float) this.lenght, 16, 1, 1, angle);
		}
		
		batch.end();
		
		sR.begin(ShapeType.Filled);
	}
	
	/**Reverses the direction of {@linkplain Package}s in this conveyor.*/
	public void reverse() {
		reversed = !reversed;
		//recalculateReversePositions();
	}
	
	@Deprecated
	/**Recalculates positions of conveyor packages and swaps their targets.*/
	private void recalculateReversePositions() {
		for (Package p : packages) {
			Node n = p.from;
			p.from = p.to;
			p.to = n;
			
			p.percentage = 100 - p.percentage;
		}
	}
	
	/**Whether this conveyor is reversed (relative to its initial start/target {@link Node}s.*/
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
	
	public float getAngle() {
		return angle;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.CONVEYOR;
	}
}

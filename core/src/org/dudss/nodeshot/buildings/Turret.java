package org.dudss.nodeshot.buildings;

import java.util.Arrays;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.Bullet;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**A turret that targets chunks with corruption. Requires ammo to be stored in order to fire a {@link Bullet}.*/
public class Turret extends AbstractStorage {

	static float width = Base.CHUNK_SIZE*3, height = Base.CHUNK_SIZE*3;
	
	/**Minimum delay between shots*/
	int rechargeRate = 100;
	/**Sim tick of the next possible shot*/
	int nextShot = SimulationThread.simTick + rechargeRate;
	/**Last shot tick*/
	int lastShot = 0;	
	/**Whether the turrets head is in position*/
	boolean aimed = false;	
	/**Current head angle*/
	float angle = 0;	
	/**The target head angle*/
	float targetAngle = 0;	
	/**Rotation speed of the turret head*/
	float rotationSpeed = 5f;
	/**Number of degrees between two separate turning animation frames*/
	float animStep = 5f;	
	/**Previous turret target*/
	Vector2 lastTarget;
	/**Current turret targer*/
	Vector2 target;	
	/**Minimal range*/
	int minRadius = 4*2;
	/**Maximum range*/
	int maxRadius = 20*2;
	
	/**A turret that targets chunks with corruption. Requires ammo to be stored in order to fire a {@link Bullet}.*/
	public Turret(float cx, float cy) {
		super(cx, cy, width, height);
		accepted = Arrays.asList(ItemType.AMMO);
		
		maxStorage = 5;
		//activateIONode(true);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (SimulationThread.simTick >= nextShot && aimed && this.storage.size() > 0) {				
			fire();
			this.storage.remove(0);
		} else {
			if (target != null) {
				aim(target);
			} else {
				target = findTarget();
			}
		}
	}
	
	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {	
		batch.begin();
		batch.setColor(1f, 1f, 1f, 1f);		
		if ((int)(angle/animStep) == 72 || (int)(angle/animStep) == 0) {
			batch.draw(SpriteLoader.turretFrames[0], x, y, width, height);
		} else {
			batch.draw(SpriteLoader.turretFrames[(int) (angle/animStep) - 1], x, y, width, height);	
		}
		batch.end();
	}
	
	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {
		batch.begin();
		batch.setColor(1f, 1f, 1f, 0.5f);
		batch.draw(SpriteLoader.turretFrames[(int) (angle/animStep)], getPrefabX(cx, snap), getPrefabY(cy, snap), width, height);
		batch.end();		
	}
	
	/**Creates a new {@link Bullet} and launches it towards it's {@link #target}.*/
	protected void fire() {
		Bullet b = new Bullet(this.cx, this.cy, target);
		GameScreen.bulletHandler.addBullet(b);
		
		lastShot = SimulationThread.simTick;
		nextShot = SimulationThread.simTick + rechargeRate;
		lastTarget = target;						
		target = findTarget();
		aimed = false;
	}
	
	/**Aims the turret gun itself towards the target. Turret turns with a {@link #rotationSpeed} towards the target and once 
	 * aimed it sets {@link #aimed} to true.*/
	protected void aim(Vector2 target) {
		Vector2 aimVector = new Vector2(target.x - cx, target.y - cy);
		double aimLenght = Math.hypot(aimVector.x, aimVector.y);
		double alpha = Math.asin(aimVector.y/aimLenght);
		
		if (target.x <= cx) {
			targetAngle = (float) (360 - Math.toDegrees(alpha)) % 360;
		} else {
			targetAngle = (float) (180 + Math.toDegrees(alpha)) % 360;
		}
		
		if ((targetAngle - animStep/2) < angle && (targetAngle + animStep/2) > angle) {
			aimed = true;
		} else {
			float a = Base.angleDist(targetAngle, angle);
			if (Base.angleDist(targetAngle, angle + a) < Base.angleDist(targetAngle, angle - a)) {
				angle += rotationSpeed;
			} else {
				angle -= rotationSpeed;
				if (angle < 0) {
					angle = 360 + angle;
				}
			}
			angle = angle % 360;
		}
	}
	
	/**Sets the turret {@link target} appropriately and in regard with the turrets {@link #minRadius} and {@link #maxRadius}.*/
	protected Vector2 findTarget() {
		Vector2 target = null;
	
		Chunk targetChunk = GameScreen.chunks.getClosestCorruptionChunkToWorldSpace(this.cx, this.cy, minRadius, maxRadius, 0f);
		
		if (targetChunk == null) {
			return null;
		}
		
		target = new Vector2(targetChunk.getX() + (Base.CHUNK_SIZE/2), targetChunk.getY() + (Base.CHUNK_SIZE/2));		
		return target;
	}
}

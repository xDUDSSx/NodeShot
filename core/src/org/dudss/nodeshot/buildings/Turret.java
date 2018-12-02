package org.dudss.nodeshot.buildings;

import java.util.Arrays;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.entities.Bullet;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector2;

public class Turret extends BasicStorage {
	static float width = 32;
	static float height = 32;
	
	int rechargeRate = 32;
	int nextShot = SimulationThread.simTick + rechargeRate;
	
	int lastShot = 0;
	Vector2 lastTarget;
	Vector2 target;
	
	int radius = 32;
	float minStorage = 0.1f;
	
	TurretHead head;
	Sprite turretSprite;
	
	Bullet b;
	
	public Turret(float cx, float cy) {
		super(cx, cy, width, height);
		accepted = Arrays.asList(ItemType.AMMO);
		color = new Color(255/255f, 144/255f, 0, 1.0f);
		prefabColor = new Color(255/255f, 144/255f, 0, 0.5f);
		head = new TurretHead(this);
		turretSprite = new Sprite(SpriteLoader.turret);
		
		height = 48;
		width = 48;
		
		maxStorage = 5;
	}

	@Override 
	public void setLocation(float cx, float cy, boolean snap) {
		super.setLocation(cx, cy, snap);
		turretSprite.setPosition(x, y);
		head.setPosition(this.cx - 56, this.cy - 12);
	}
	
	@Override
	public void update() {
		super.update();

		if (SimulationThread.simTick >= nextShot && storage >= minStorage) {			
			boolean shotSuccessful = shoot();
			if (shotSuccessful) {
				storage -= minStorage; 
				nextShot = SimulationThread.simTick + rechargeRate;
			}			
		}
	}
	
	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {	
		if (storage < maxStorage) {
			r.setColor(Color.GREEN);
		} else {
			r.setColor(Color.RED);
		}	
		r.rectLine(this.x, this.y - 2, this.x + (width*((float) (storage/maxStorage))), this.y - 2, 3);
		
		r.end();
		batch.begin();
		turretSprite.draw(batch);
		input.draw(batch);
		head.setRotation((float) head.rotation); 
		head.draw(batch);
		batch.end();
		r.begin(ShapeType.Filled);
	}
	
	protected boolean shoot() {
		target = findTarget();
		if (target != null) {
			
			head.aim(target);
			
			Bullet b = new Bullet(this.cx, this.cy, target);
			GameScreen.bulletHandler.addBullet(b);
			
			lastShot = SimulationThread.simTick;
			lastTarget = target;						
			target = null;
			return true;
		}
		return false;
	}
	
	protected Vector2 findTarget() {
		Vector2 target = null;
	
		Chunk targetChunk = getClosestChunk();
		if (targetChunk == null) {
			return null;
		}
		
		target = new Vector2(targetChunk.getX() + (Base.CHUNK_SIZE/2), targetChunk.getY() + (Base.CHUNK_SIZE/2));		
		return target;
	}
	
	protected Chunk getClosestChunk() {
		//(dx, dy) is a vector - direction in which we move right now
        int dx = 0;
        int dy = 1;
        // length of current segment
        int segment_length = 1;

        //current position (x, y) and how much of current segment we passed
        int x = (Math.round(this.x)/Base.CHUNK_SIZE) - 1;
        int y = Math.round(this.y)/Base.CHUNK_SIZE;
        int segment_passed = 0;
        
        Chunk chunk;
        
        for (int n = 0; n < radius*radius; ++n) {
            //make a step, add "direction" vector (dx, dy) to current position (x, y)
            x += dx;
            y += dy;
            ++segment_passed;
            
            //Check if the chunk has any corruption in it (Since we are spiraling outwards from the turrets cx, cy, the first corrupted chunk
            //we encounter is going to be the closest as well (No guarantee that this is the only closest one)
            chunk = GameScreen.chunks.getChunk(x, y);
            if (chunk != null) {
	            if (GameScreen.chunks.getChunk(x, y).getCreeperLevel() > 0) {
	            	return chunk;
	            }
            }
            
            if (segment_passed == segment_length) {
                //done with current segment
                segment_passed = 0;

                //"rotate" directions
                int buffer = dy;
                dy = -dx;
                dx = buffer;

                // increase segment length if necessary
                if (dx == 0) {
                    ++segment_length;
                }
            }
        }
        
        return null;
	}
	
	@Override
	public boolean canStore(ItemType type) {
		if (accepted.contains(type)) {
			if (type == ItemType.AMMO) {
				return true;
			}
		}
		return false;
	}
	
}

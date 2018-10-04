package org.dudss.nodeshot.buildings;

import java.util.Arrays;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.SimulationThread;
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

	int rechargeRate = 16;
	int nextShot = SimulationThread.simTick += rechargeRate;
	
	int lastShot = 0;
	Vector2 lastTarget;
	Vector2 target;
	
	int radius = 32;
	
	TurretHead head;
	Sprite turretSprite;
	
	public Turret(float cx, float cy) {
		super(cx, cy);
		accepted = Arrays.asList(ItemType.AMMO);
		color = new Color(255/255f, 144/255f, 0, 1.0f);
		prefabColor = new Color(255/255f, 144/255f, 0, 0.5f);
		head = new TurretHead(this);
		turretSprite = new Sprite(SpriteLoader.turret);
		turretSprite.setPosition(x, y);
		
		height = 48;
		width = 48;
		
		maxStorage = 5;
	}

	@Override
	public void update() {
		super.update();
		
		if (SimulationThread.simTick >= nextShot) {
			nextShot = SimulationThread.simTick + rechargeRate;
			shoot();
		}
	}
	
	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {	
		if (storage < maxStorage) {
			r.setColor(Color.GREEN);
		} else {
			r.setColor(Color.RED);
		}	
		r.rectLine(this.x, this.y - 2, this.x + (this.width*((float) (storage/maxStorage))), this.y - 2, 3);
		
		if (this.lastShot > SimulationThread.simTick - 8) {
			r.setColor(Color.YELLOW);
			r.rectLine(this.cx, this.cy, lastTarget.x, lastTarget.y, 1f);
		}
		
		r.end();
		batch.begin();
		turretSprite.setPosition(x, y); 
		turretSprite.draw(batch);
		head.setPosition(cx - 55, cy - 15);
		head.setOrigin(55, 17);
		//head.setRotation((float) head.rotation); 
		head.setRotation((float) head.rotation); 
		head.draw(batch);
		/*System.out.println();
		System.out.println((cx - 55) + "and" + (cy - 15));
		System.out.println("or: " + cx + "/" + cy);
		System.out.println((float) head.rotation);
		*/
		batch.end();
		r.begin(ShapeType.Filled);
	}
	
	protected void shoot() {
		//System.out.println("Shooting!");
		target = findTarget();
		if (target != null) {
			System.out.println("shooting at " + target.x + " " + target.y);
			System.out.println("aiming...");
			head.aim(target);
			
			lastShot = SimulationThread.simTick;
			lastTarget = target;			
			GameScreen.chunks.getChunk((int)(target.x/Base.CHUNK_SIZE) - 1, (int)(target.y/Base.CHUNK_SIZE)).setCreeperLevel(0);
			GameScreen.chunks.getChunk((int)(target.x/Base.CHUNK_SIZE) + 1, (int)(target.y/Base.CHUNK_SIZE)).setCreeperLevel(0);
			GameScreen.chunks.getChunk((int)(target.x/Base.CHUNK_SIZE), (int)(target.y/Base.CHUNK_SIZE)).setCreeperLevel(0);
			GameScreen.chunks.getChunk((int)(target.x/Base.CHUNK_SIZE), (int)(target.y/Base.CHUNK_SIZE) + 1).setCreeperLevel(0);
			GameScreen.chunks.getChunk((int)(target.x/Base.CHUNK_SIZE), (int)(target.y/Base.CHUNK_SIZE) - 1).setCreeperLevel(0);
			target = null;
		}
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
		// (dx, dy) is a vector - direction in which we move right now
        int dx = 0;
        int dy = 1;
        // length of current segment
        int segment_length = 1;

        // current position (x, y) and how much of current segment we passed
        int x = (Math.round(this.x)/Base.CHUNK_SIZE) - 1;
        int y = Math.round(this.y)/Base.CHUNK_SIZE;
        int segment_passed = 0;
        
        Chunk chunk;
        
        for (int n = 0; n < radius*radius; ++n) {
            // make a step, add 'direction' vector (dx, dy) to current position (x, y)
            x += dx;
            y += dy;
            ++segment_passed;
            
            //System.out.println("Checking chunk at: " + x + "/" + y + " rx: " + GameScreen.chunks.getChunk(x, y).getX() + " ry: " + GameScreen.chunks.getChunk(x, y).getY());
            if (GameScreen.chunks.getChunk(x, y).getCreeperLevel() > 0) {
            	chunk = GameScreen.chunks.getChunk(x, y);
            	return chunk;
            }
            
            if (segment_passed == segment_length) {
                // done with current segment
                segment_passed = 0;

                // 'rotate' directions
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

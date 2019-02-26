package org.dudss.nodeshot.misc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.entities.Bullet;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**A manager class that manages all bullets in the game world. Calls logic updates and render calls*/
public class BulletHandler {

	List<Bullet> bullets;

	/**A manager class that manages all bullets in the game world. Calls logic updates and render calls*/
	public BulletHandler() {
		bullets = new CopyOnWriteArrayList<Bullet>();
	}
	
	/**Updates all the bullets in the manager.*/
	public void updateAll() {
		for (Bullet b : bullets) {
			b.update();
		}
	}
	
	/**Updates all the bullets in the manager.*/
	public void drawAll(ShapeRenderer r, SpriteBatch batch) {
		batch.begin();
		for (Bullet b : bullets) {
			b.draw(batch);
		}
		batch.end();
	}
	
	/**Adds a {@link Bullet} to the manager.*/
	public void addBullet(Bullet b) {
		bullets.add(b);
	}
	
	/**Removes a {@link Bullet} from the manager.*/
	public void removeBullet(Bullet b) {
		bullets.remove(b);
	}
	
	/**@return A list of all {@link Bullet}s in this manager.*/
	public List<Bullet> getAllBullets() {
		return bullets;
	}	
}

package org.dudss.nodeshot.misc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.entities.Bullet;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BulletHandler {

	List<Bullet> bullets;

	public BulletHandler() {
		bullets = new CopyOnWriteArrayList<Bullet>();
	}
	
	public void updateAll() {
		for (Bullet b : bullets) {
			b.update();
		}
	}
	
	public void drawAll(ShapeRenderer r, SpriteBatch batch) {
		batch.begin();
		for (Bullet b : bullets) {
			b.draw(batch);
		}
		batch.end();
	}
	
	public void addBullet(Bullet b) {
		bullets.add(b);
	}
	
	public void removeBullet(Bullet b) {
		bullets.remove(b);
	}
	
	public List<Bullet> getAllBullets() {
		return bullets;
	}	
}

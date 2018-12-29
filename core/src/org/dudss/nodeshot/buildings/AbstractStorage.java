package org.dudss.nodeshot.buildings;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.nodes.IONode;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.items.StorableItem;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**Building that can accept and hold items in a single shared storage pool.*/
public abstract class AbstractStorage extends AlertableBuilding implements Connectable {	
	IONode ioNode;
	boolean ioActive = false;
	
	List<StorableItem> storage = new ArrayList<StorableItem>();
	float maxStorage = 50;
	
	protected Color prefabColor = new Color(218f/255f, 165f/255f, 32f/255f, 0.5f);
	protected Color color = Color.GOLDENROD;
	
	public AbstractStorage(float cx, float cy, float width, float height) {
		super(cx, cy, width, height);
		accepted = new ArrayList<ItemType>();		
	}
		
	@Override
	public void update() {
		super.update();
		if (ioActive) {
			if (storage.size() < maxStorage) {
				ioNode.update();
			}
		}
	}
	
	@Override
	public boolean alert(StorableItem p) {
		if (canStore(p)) {
			storage.add(p);	
			System.out.println("caled at " + storage.size());
			return true;
		}
		return false;
	}

	@Override
	public boolean canStore(StorableItem p) {
		if (this.accepted.size() > 0) {
			if (this.accepted.contains(p.getType()) && storage.size() < maxStorage) {
				return true;
			}
			return false;
		}
		return true;
	}
	
	@Override
	public void draw(ShapeRenderer r, SpriteBatch batch) {	
		r.set(ShapeType.Filled);
		r.setColor(color);
		r.rect(x, y, width, height);
		 
		if (storage.size() < maxStorage) {
			r.setColor(Color.GREEN);
		} else {
			r.setColor(Color.RED);
		}	
		r.rectLine(this.x, this.y - 2, this.x + (width*((float) (storage.size()/maxStorage))), this.y - 2, 3);
		if (ioActive) {
			this.ioNode.draw(batch);
		}
	}
	
	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {				
		r.set(ShapeType.Filled);
		r.setColor(prefabColor);
		r.rect(getPrefabX(cx, snap), getPrefabY(cy, snap), width, height);
	}
	
	@Override
	public void build() {
		if (ioActive) {
			ioNode = new IONode(x + (width/2), y + (height/2), Base.RADIUS, this);
			ioNode.setInputSprite();
			GameScreen.nodelist.add(ioNode);
		}
		GameScreen.buildingManager.addBuilding(this);
		
		System.out.println("BUILT");
		updateFogOfWar(true);
	}

	@Override
	public void demolish() {
		GameScreen.buildingManager.removeBuilding(this);
		if (ioActive) this.ioNode.remove();
		
		clearBuildingChunks();
		System.out.println("DEMO");
		updateFogOfWar(false);
	}
	
	public void activateIONode(boolean b) {
		this.ioActive = b;
	}
	
	public void empty() {
		storage.clear();
	}
	
	public IONode getNode() {
		return ioNode;
	}

	public List<StorableItem> getStoredItems() {
		return this.storage;
	}
}

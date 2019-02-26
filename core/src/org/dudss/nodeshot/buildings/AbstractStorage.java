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
	int maxStorage = 50;
	
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
	public void build() {
		super.build();
		if (ioActive) {
			ioNode = new IONode(x + (width/2), y + (height/2), Base.RADIUS, this);
			ioNode.setInputSprite();
			GameScreen.nodelist.add(ioNode);
		}
	}

	@Override
	public void demolish() {
		super.demolish();
		if (ioActive) this.ioNode.remove();	
	}
	
	public void activateIONode(boolean b) {
		this.ioActive = b;
	}
	
	public void empty() {
		storage.clear();
	}
	
	public int getMaxStorage() {
		return maxStorage;
	}
	
	public IONode getNode() {
		return ioNode;
	}

	public List<StorableItem> getStoredItems() {
		return this.storage;
	}
}

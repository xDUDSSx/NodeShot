package org.dudss.nodeshot.items;

import org.dudss.nodeshot.items.Item.ItemType;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Item {
	public enum ItemType {
		COAL, IRON, PACKAGE, AMMO
	}
	
	public void draw(SpriteBatch batch);		
	public void drawHighlight(SpriteBatch batch);	
	public ItemType getItemType();
}

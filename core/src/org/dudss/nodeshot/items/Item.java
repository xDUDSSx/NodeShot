package org.dudss.nodeshot.items;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**A {@link Package} representing an item of a certain {@link ItemType}. This item then exists in the game world and can move along transfer networks.
 * An item that can be stored in a storage (building) pool is {@link StorableItem}.*/
public interface Item {
	/**Types of individual items.*/
	public enum ItemType {
		COAL, IRON, PACKAGE, AMMO
	}
	
	public void draw(SpriteBatch batch);		
	public void drawHighlight(SpriteBatch batch);	
	public ItemType getItemType();
}

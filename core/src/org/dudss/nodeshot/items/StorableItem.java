package org.dudss.nodeshot.items;

import org.dudss.nodeshot.items.Item.ItemType;

/**Represents an item stored in a storage pool.*/
public class StorableItem {
	ItemType type;
	
	/**Represents an item of an {@link ItemType} stored in a storage pool*/
	public StorableItem(ItemType type) {
		this.type = type;
	}
	
	public ItemType getType() {
		return type;
	}
}

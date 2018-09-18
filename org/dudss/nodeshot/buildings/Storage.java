package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.items.Item.ItemType;

public interface Storage {
	//Method that checks if the designated storage is able to contain one more package of said ItemType
	public boolean canStore(ItemType type);
}

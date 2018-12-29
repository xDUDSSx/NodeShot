package org.dudss.nodeshot.items;

import org.dudss.nodeshot.entities.Package;
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
	
	public Package getPackage() {
		switch(type) {
			case COAL: return new Coal();
			case IRON: return new Iron();
			case AMMO: return new Ammo();
			case PACKAGE: return new Package();
			case  PROCESSED_MATERIAL: return new ProcessedMaterial();
			default: break;
			
		}
		return null;
	}
}

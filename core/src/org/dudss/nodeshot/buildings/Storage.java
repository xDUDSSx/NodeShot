package org.dudss.nodeshot.buildings;

import java.util.List;

import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.items.StorableItem;

public interface Storage {
	
	public abstract boolean alert(StorableItem p);
	
	//Method that checks if the designated storage is able to contain one more package of said ItemType
	public boolean canStore(StorableItem p);
	
	//Accepted item types
	public void setAccepted(List<ItemType> accepted);
	public List<ItemType> getAccepted();
}

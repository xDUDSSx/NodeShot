package org.dudss.nodeshot.buildings;

import java.util.Arrays;

import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.items.StorableItem;
import org.dudss.nodeshot.entities.Package;

import com.badlogic.gdx.graphics.Color;

public class AmmoStorage extends AbstractStorage  {
	
	static float width = 32;
	static float height = 32;
	
	public AmmoStorage(float cx, float cy) {
		super(cx, cy, width, height);

		accepted = Arrays.asList(ItemType.AMMO);
		
		prefabColor = new Color(255f/255f, 33f/255f, 0f/255f, 0.5f);
		color = new Color(255f/255f, 33f/255f, 0f/255f, 1f);
	}

	@Override
	public boolean canStore(StorableItem p) {
		if (accepted.contains(p.getType())) {		
			return true;
		}
		return false;
	}

	@Override
	public void outline(boolean outline) {
		// TODO Auto-generated method stub
		
	}
}

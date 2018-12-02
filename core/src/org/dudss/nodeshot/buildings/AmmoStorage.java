package org.dudss.nodeshot.buildings;

import java.util.Arrays;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.items.Item.ItemType;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class AmmoStorage extends BasicStorage  {
	
	static float width = 32;
	static float height = 32;
	
	public AmmoStorage(float cx, float cy) {
		super(cx, cy, width, height);

		accepted = Arrays.asList(ItemType.AMMO);
		
		prefabColor = new Color(255f/255f, 33f/255f, 0f/255f, 0.5f);
		color = new Color(255f/255f, 33f/255f, 0f/255f, 1f);
	}

	@Override
	public boolean canStore(ItemType type) {
		if (accepted.contains(type)) {
			if (type == ItemType.AMMO) {
				return true;
			}
		}
		return false;
	}
}

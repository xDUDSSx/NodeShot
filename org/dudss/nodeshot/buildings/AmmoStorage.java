package org.dudss.nodeshot.buildings;

import java.util.Arrays;

import org.dudss.nodeshot.items.Item.ItemType;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class AmmoStorage extends BasicStorage  {

	private Color prefabColor = new Color(255f/255f, 33f/255f, 0f/255f, 0.5f);
	private Color color = new Color(255f/255f, 33f/255f, 0f/255f, 1f);
	
	public AmmoStorage(float cx, float cy) {
		super(cx, cy);
		// TODO Auto-generated constructor stub
		accepted = Arrays.asList(ItemType.PACKAGE);
	}

	@Override
	public boolean canStore(ItemType type) {
		if (accepted.contains(type)) {
			System.out.println("canStore ammo");
			if (type == ItemType.PACKAGE) {
				System.out.println("can store");
				return true;
			}
		}
		System.out.println("false");
		return false;
	}

	@Override
	public void draw(ShapeRenderer r) {
		r.set(ShapeType.Filled);
		r.setColor(color);
		r.rect(x, y, width, height);
		 
		if (storage < maxStorage) {
			r.setColor(Color.GREEN);
		} else {
			r.setColor(Color.RED);
		}	
		r.rectLine(this.x, this.y - 2, this.x + (this.width*((float) (storage/maxStorage))), this.y - 2, 3);
	}

	@Override
	public void drawPrefab(ShapeRenderer r, float cx, float cy) {
		float x = cx - (width/2);
		float y = cy - (height/2);
		
		r.set(ShapeType.Filled);
		r.setColor(prefabColor);
		r.rect(x, y, width, height);
	}
}

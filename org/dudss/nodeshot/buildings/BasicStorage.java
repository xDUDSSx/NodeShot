package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.InputNode;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

public class BasicStorage implements Building, Storage{
	
	InputNode input;
	
	float x,y;
	float cx,cy;
	float width = 32;
	float height = 32;
	
	public float storage = 0;
	float maxStorage = 50;
	
	public boolean full = false;
	
	private Color prefabColor = new Color(218f/255f, 165f/255f, 32f/255f, 0.5f);
	
	public BasicStorage(float cx, float cy) {
		this.cx = cx;
		this.cy = cy;
		
		x = cx - (width/2);
		y = cy - (height/2);
		
		System.out.println((float)MathUtils.clamp(218, 0.0, 1.0));
	}
	
	public void setLocation(float cx, float cy) {
		this.cx = cx;
		this.cy = cy;
		
		x = cx - (width/2);
		y = cy - (height/2);
	}
	
	@Override
	public void update() {
		if (storage < maxStorage) {
			input.update();
			full = false;
		} else {
			full = true;
		}
	}
	
	@Override
	public void draw(ShapeRenderer r) {	
		r.set(ShapeType.Filled);
		r.setColor(Color.GOLDENROD);
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
	
	@Override
	public void build() {
		input = new InputNode(x + (width/2), y + (height/2), Base.RADIUS, this);
		GameScreen.nodelist.add(input);
		GameScreen.buildingHandler.addBuilding(this);
	}

	@Override
	public void demolish() {
		GameScreen.buildingHandler.removeBuilding(this);
		this.input.remove();
	}
	
	public void empty() {
		System.out.println("emtpying");
		storage = 0;
		full = false;
	}
	
	public Node getInputNode() {
		return input;
	}

	@Override
	public void alert(Package p) {
		storage++;	
	}

	@Override
	public boolean canStore(ItemType type) {
		return true;
	}
}

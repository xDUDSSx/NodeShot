package org.dudss.nodeshot.buildings;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.nodes.InputNode;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**Building that can accept and hold items*/
public abstract class AbstractStorage extends AbstractBuilding implements Storage {
	
	InputNode input;
	
	public float storage = 0;
	float maxStorage = 50;
	
	public boolean full = false;
	
	List<ItemType> accepted;
	
	protected Color prefabColor = new Color(218f/255f, 165f/255f, 32f/255f, 0.5f);
	protected Color color = Color.GOLDENROD;
	
	public AbstractStorage(float cx, float cy, float width, float height) {
		super(cx, cy, width, height);
		accepted = new ArrayList<ItemType>();				
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
	public void draw(ShapeRenderer r, SpriteBatch batch) {	
		r.set(ShapeType.Filled);
		r.setColor(color);
		r.rect(x, y, width, height);
		 
		if (storage < maxStorage) {
			r.setColor(Color.GREEN);
		} else {
			r.setColor(Color.RED);
		}	
		r.rectLine(this.x, this.y - 2, this.x + (width*((float) (storage/maxStorage))), this.y - 2, 3);
	}
	
	@Override
	public void drawPrefab(ShapeRenderer r, SpriteBatch batch, float cx, float cy, boolean snap) {		
		float prefX;
		float prefY;
		
		if (snap) {
			float nx = Math.round(cx - (cx % Base.CHUNK_SIZE));
			float ny = Math.round(cy - (cy % Base.CHUNK_SIZE));
			
			prefX = nx - ((int)(width/2)/16) * 16;
			prefY = ny - ((int)(width/2)/16) * 16;	
		} else {
			prefX = cx - (width/2);
			prefY = cy - (height/2);
		}
		
		r.set(ShapeType.Filled);
		r.setColor(prefabColor);
		r.rect(prefX, prefY, width, height);
	}
	
	@Override
	public void build() {
		input = new InputNode(x + (width/2), y + (height/2), Base.RADIUS, this);
		//GameScreen.nodelist.add(input);
		GameScreen.buildingHandler.addBuilding(this);
		GameScreen.nodelist.add(input);
		
		updateFogOfWar(true);
	}

	@Override
	public void demolish() {
		GameScreen.buildingHandler.removeBuilding(this);
		this.input.remove();
		
		updateFogOfWar(false);
	}
	
	public void empty() {
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
	
	@Override
	public void setAccepted(List<ItemType> accepted) {
		this.accepted = accepted;
	}
	
	@Override	
	public List<ItemType> getAccepted() {
		return this.accepted;
	}
}

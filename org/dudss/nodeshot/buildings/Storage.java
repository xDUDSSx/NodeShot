package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class Storage implements Building {
	
	Node input;
	
	float x,y;
	float cx,cy;
	float width = 32;
	float height = 32;
	
	public Storage(float cx, float cy) {
		this.cx = cx;
		this.cy = cy;
		
		x = cx - (width/2);
		y = cy - (height/2);
	}
	
	@Override
	public void update() {

	}
	
	@Override
	public void draw(ShapeRenderer r) {	
		r.set(ShapeType.Filled);
		r.setColor(Color.GOLDENROD);
		r.rect(x, y, width, height);
	}
	
	@Override
	public void build() {
		input = new Node(x + (width/2), y + (height/2), Base.RADIUS);
		GameScreen.nodelist.add(input);
	}

	@Override
	public void demolish() {
		
	}
	
	public Node getInputNode() {
		return input;
	}
}

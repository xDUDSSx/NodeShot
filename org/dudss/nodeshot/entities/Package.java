package org.dudss.nodeshot.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;

public class Package extends Sprite implements Entity{

	public Node from;
	public Node to;
	//public Package triggerPackage = null;

	public double percentage = 0;
	public double speed;
	
	public float x;
	public float y;
	
	public int radius;
	
	public int id;
	
	public Color color;
	
	public double distance;
	
	public Boolean going = false;
	public Boolean finished = false;
	public Node nextNode;
	
	public Vector2 currentMovePos;
	
	//Simple two node connection
	public Package(Node from, Node to) {
		this.from = from;
		this.to = to;
		this.radius = Base.PACKAGE_RADIUS;
		this.id = System.identityHashCode(this);
		
		this.x = from.getCX() - radius/2;
		this.y = from.getCY() - radius/2;
		
		this.distance = from.getDistance(to);
		
		currentMovePos = new Vector2(from.getCX(), from.getCY());
		
		System.out.println("Setting package sprite!");
		this.set(new Sprite(GameScreen.spriteSheet, 0, 0, 16, 16));
		this.setPosition(x, y);
	}
	//TODO: implement speed

	public void draw(SpriteBatch batch) {
		Sprite packageSprite = new Sprite(GameScreen.spriteSheet, 0, 0, 16, 16);

		if (this.color != null) {
			packageSprite.setColor(color);
			//System.out.println("Package COLOR: " + color.r + " " + color.g + " " + color.b + " ");
		}
		
		packageSprite.setScale(0.6f);
		packageSprite.setPosition((float) x, (float) y); 
		packageSprite.draw(batch);
	}
	
	public void reset(Node from, Node to) {
		going = false;
		finished = false;
		this.percentage = 0;
		this.from = from;
		this.to = to;
	}
	
	public void destroy() {
		GameScreen.packagelist.remove(this); //wont be rendered anymore
		going = false;
		finished = true;
		System.out.println("REMOVING PACKAGE");
	}
	
	public void alert() {
		going = false;
		System.out.println("Package ALERT!");
	}
	
	public void transform(float x, float y) {
		this.setPosition(x, y);
		this.x = x;
		this.y = y;
	}
	
	public void go() {
		going = true;
		GameScreen.packagelist.add(this);
	}
	
	public Boolean isFinished() {
		return finished;
	}
	
	public void setNextNode(Node n) {
		nextNode = n;
	}	
	public Node getNextNode() {
		return nextNode;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	public Color getColor() {
		return color;
	}
	
	public int getID() {
		return id;
	}
	
	public int getIndex() {
		return GameScreen.packagelist.indexOf(this);
	}
	
}

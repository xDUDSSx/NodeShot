package org.dudss.nodeshot.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.misc.PathHandler;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

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
	
	public Boolean going = false;
	public Boolean finished = false;

	public Vector2 currentMovePos;
	
	public PathHandler pathHandler;
	
	public Sprite packageSprite;
	public Sprite highlightSprite;
	
	//Simple two node connection
	public Package(Node from, Node to) {
		this.from = from;
		this.to = to;
		this.radius = Base.PACKAGE_RADIUS;
		this.id = System.identityHashCode(this);
		
		this.x = from.getCX() - radius/2;
		this.y = from.getCY() - radius/2;
		
		currentMovePos = new Vector2(from.getCX(), from.getCY());
		
		this.set(new Sprite(SpriteLoader.packageSprite));
		this.setPosition(x, y);
		
		GameScreen.packagelist.add(this);
	}
	//TODO: implement speed
	
	//No params
	public Package(Node from) {
		this.from = from;
		this.to = null;
		this.radius = Base.PACKAGE_RADIUS;
		this.id = System.identityHashCode(this);
		
		this.x = from.getCX() - radius/2;
		this.y = from.getCY() - radius/2;
		
		currentMovePos = new Vector2(from.getCX(), from.getCY());
		
		this.set(new Sprite(SpriteLoader.packageSprite));
		this.setPosition(x, y);
		
		GameScreen.packagelist.add(this);
	}

	public void draw(SpriteBatch batch) {
		packageSprite = SpriteLoader.packageSprite;

		if (this.color != null) {
			packageSprite.setColor(color);
			//System.out.println("Package COLOR: " + color.r + " " + color.g + " " + color.b + " ");
		}
		
		packageSprite.setScale(0.6f);
		packageSprite.setPosition((float) x, (float) y); 
		packageSprite.draw(batch);
	}
	
	public void drawHighlight(SpriteBatch batch) {
		highlightSprite = SpriteLoader.packageHighlightSprite;
		highlightSprite.setPosition(x, y);
		highlightSprite.setOrigin(radius/2, radius/2);
		highlightSprite.setScale(0.65f);
		highlightSprite.draw(batch);
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
		this.pathHandler.finish();
	}
	
	public void alert() {
		going = false;
	}
	
	public void transform(float x, float y) {
		this.setPosition(x, y);
		this.x = x;
		this.y = y;
	}
	
	public void go() {
		going = true;
	}
	
	public Boolean isFinished() {
		return finished;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	public Color getColor() {
		return color;
	}
	
	public void setPathHandler(PathHandler pathHandler) {
		this.pathHandler = pathHandler;
	}
	public PathHandler getPathHandler() {
		return pathHandler;
	}
	
	public int getID() {
		return id;
	}
	
	public int getIndex() {
		return GameScreen.packagelist.indexOf(this);
	}
	
	@Override
	public EntityType getType() {
		return EntityType.PACKAGE;
	}
	
	public ItemType getItemType() {
		return ItemType.PACKAGE;
	}
	
}

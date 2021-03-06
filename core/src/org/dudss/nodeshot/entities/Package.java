package org.dudss.nodeshot.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.items.StorableItem;
import org.dudss.nodeshot.misc.PathHandler;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.SpriteLoader;

/**An entity that can move along {@link Connector}s between {@link Node}s and can be processed by them.
 * A carrier of {@link StorableItem}s.
 * */
public class Package extends Sprite implements Entity{

	/**Starting {@link Node} of the package*/
	public Node from;
	
	/**Target {@link Node} of the package*/
	public Node to;

	/**Percentage representing how far in the current {@link Connector} the package has travelled.*/
	public double percentage = 0;
	public double speed;
	
	public float x;
	public float y;
	
	public int radius;
	
	public int id;
	
	public Color color;
	
	/**Whether the package is still moving through a connector, when true,
	 * the package has finished the connector segment path and awaits further instructions of its {@link PathHandler}*/
	public Boolean going = false;

	public Vector2 currentMovePos;
	
	public PathHandler pathHandler;
	
	public Sprite packageSprite;
	public Sprite highlightSprite;
	
	/**Whether the package has all data needed for proper function*/
	public boolean notSet = true;
	
	/**A proper two node connection*/
	public Package(Node from, Node to) {
		setParams(from, to);
		notSet = false;
	}
	
	/**Package with no target node yet, used for initialisation of position parameters*/
	public Package(Node from) {
		setParams(from, null);
		notSet = true;
	}
	
	/**An empty constructor that needs {@link #setParams(Node, Node)} to be called before use*/
	public Package() {
		notSet = true;
	}
		
	/**A method usually called from the constructor that initialises the package positions, variables and adds it to the simulation.
	 * Needs to be called manually when an empty constructor is used.*/
	public void setParams(Node from, Node to) {
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
		
	/**Draws the packages {@link #packageSprite}.*/
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
	
	/**Draws the package highlight.*/
	public void drawHighlight(SpriteBatch batch) {
		highlightSprite = SpriteLoader.packageHighlightSprite;
		highlightSprite.setPosition(x, y);
		highlightSprite.setOrigin(radius/2, radius/2);
		highlightSprite.setScale(0.65f);
		highlightSprite.draw(batch);
	}
	
	/**Resets the starting and target node of the package.*/
	public void resetState(Node from, Node to) {
		going = false;
		this.percentage = 0;
		this.from = from;
		this.to = to;
	}
	
	/**Removes the package from the game.*/
	public void destroy() {
		GameScreen.packagelist.remove(this); //wont be rendered anymore
		going = false;
		this.pathHandler.finish();
	}
	
	/**Changes the going state of this package to false.
	 * This is used for alerting its {@link PathHandler}*/
	public void alert() {
		going = false;
	}
	
	/**Moves the package.*/
	public void transform(float x, float y) {
		this.setPosition(x, y);
		this.x = x;
		this.y = y;
	}
	
	/**Flags the package as moving along a {@link Connector}.*/
	public void go() {
		going = true;
	}
	
	public Boolean isGoing() {
		return going;
	}
	
	@Deprecated
	public void setColor(Color color) {
		this.color = color;
	}
	
	@Deprecated
	public Color getColor() {
		return color;
	}
	
	/**Sets the packages {@link PathHandler}.*/
	public void setPathHandler(PathHandler pathHandler) {
		this.pathHandler = pathHandler;
	}
	
	public PathHandler getPathHandler() {
		return pathHandler;
	}
	
	@Override
	public int getID() {
		return id;
	}
	
	@Override
	public int getIndex() {
		return GameScreen.packagelist.indexOf(this);
	}
	
	@Override
	public EntityType getType() {
		return EntityType.PACKAGE;
	}
	
	/**Returns the {@link ItemType} of the item this package is representing.*/
	public ItemType getItemType() {
		return ItemType.PACKAGE;
	}
	
	/**Gets a {@link StorableItem} that is the same type as this package.*/
	public StorableItem getStorable() {
		return new StorableItem(getItemType());
	}
}

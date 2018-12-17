package org.dudss.nodeshot.items;

import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Coal extends Package implements Item{
	
	public Sprite coalSprite = SpriteLoader.coalSprite;
	
	//Package representing an item
	public Coal(Node from, Node to) {
		super(from, to);	
		highlightSprite = SpriteLoader.coalHighlightSprite;
	}
	
	public Coal(Node from) {
		super(from);		
		highlightSprite = SpriteLoader.coalHighlightSprite;
	}
	
	public Coal() {
		super();	
		highlightSprite = SpriteLoader.coalHighlightSprite;
	}
	
	@Override
	public void draw(SpriteBatch batch) {
		packageSprite = coalSprite;
		coalSprite.setScale(0.6f);
		coalSprite.setPosition((float) x, (float) y); 
		coalSprite.draw(batch);
	}
	
	@Override
	public void drawHighlight(SpriteBatch batch) {
		highlightSprite.setPosition(x, y);
		highlightSprite.setOrigin(radius/2, radius/2);
		highlightSprite.setScale(0.65f);
		highlightSprite.draw(batch);	
	}
	
	@Override
	public ItemType getItemType() {
		return ItemType.COAL;
	}
}

package org.dudss.nodeshot.items;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Iron extends Package implements Item{
	
	public Sprite ironSprite = SpriteLoader.ironSprite;
	
	//Package representing an item
	public Iron(Node from, Node to) {
		super(from, to);
		
		highlightSprite = SpriteLoader.ironHighlightSprite;
	}
	
	public Iron(Node from) {
		super(from);
		
		highlightSprite = SpriteLoader.ironHighlightSprite;
	}

	@Override
	public void draw(SpriteBatch batch) {
		packageSprite = ironSprite;
		ironSprite.setScale(0.6f);
		ironSprite.setPosition((float) x, (float) y); 
		ironSprite.draw(batch);
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
		return ItemType.IRON;
	}
	
}

package org.dudss.nodeshot.items;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.items.Item.ItemType;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Iron extends Package {
	
	public Sprite ironSprite = SpriteLoader.ironSprite;
	
	//Package representing an item
	public Iron(Node from, Node to) {
		super(from, to);
	}
	
	public Iron(Node from) {
		super(from);
	}

	@Override
	public void draw(SpriteBatch batch) {
		packageSprite = ironSprite;
		ironSprite.setScale(0.6f);
		ironSprite.setPosition((float) x, (float) y); 
		ironSprite.draw(batch);
	}
	
	@Override
	public ItemType getItemType() {
		return ItemType.IRON;
	}
	
}

package org.dudss.nodeshot.utils;
 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

/**Class that holds and loads all texture resources*/
public class SpriteLoader {
	
	public static Texture spriteSheet;
	public static Texture gridOverlay;
	public static Texture gridOverlay2;
	public static Texture sectionOutline;
	
	public static Texture savanaTex;
	
	public static Texture ironTex;
	public static Texture coalTex;
	public static Texture coalLowerTex;
	public static Texture coalLowTex;

	public static Sprite packageSprite;
	public static Sprite packageHighlightSprite;
	public static Sprite nodeSprite;
	public static Sprite nodeHighlightSprite;
	public static Sprite highlightSprite;
	public static Sprite coalSprite;
	public static Sprite ironSprite;
	public static Sprite coalHighlightSprite;
	public static Sprite ironHighlightSprite;
	public static Sprite nodeClosedSprite;
	public static Sprite nodeInputSprite;
	public static Sprite nodeOutputSprite;
	public static Sprite nodeCoalSprite;
	public static Sprite nodeIronSprite;                                         
	public static Sprite nodeConveyorSprite;
	
	public static Sprite dirtTileSprite;
	public static Sprite bigdirtTileSprite;
	
	public static Sprite coalTileSprite;
	public static Sprite coalTileLowSprite;
	public static Sprite coalTileLowerSprite;
	
	public static Sprite ironTileSprite;
	
	public static TextureAtlas tileAtlas;
	public static Texture corrTex;
	
	public static Sprite turret;
	public static Sprite turretHead;
	public static Sprite bullet;
	
	/**Loads all textures*/
	public static void loadAll() {
		System.out.println("Loading sprites ...");
		double currentTime = System.currentTimeMillis();
		if (Gdx.app.getType() == ApplicationType.Android) {
        	spriteSheet = new Texture(Gdx.files.internal("spritesheet16x16.png"));
        	gridOverlay = new Texture(Gdx.files.internal("res/sectionGridOverlay.png"));
        	savanaTex = new Texture(Gdx.files.internal("res/seamlesssand32.png"));
        	ironTex = new Texture(Gdx.files.internal("tiledTex.png"));
        	coalTex = new Texture(Gdx.files.internal("tiledCoal.png"));
        	coalLowerTex = new Texture(Gdx.files.internal("tiledCoalLower.png"));
        	coalLowTex = new Texture(Gdx.files.internal("tiledCoalLow.png"));
        	tileAtlas = new TextureAtlas(Gdx.files.internal("tiles.atlas"));
        } else if (Gdx.app.getType() == ApplicationType.Desktop) {
        	spriteSheet = new Texture("res/spritesheet16x16.png");
        	gridOverlay = new Texture("res/sectionGridOverlay.png");
        	gridOverlay2 = new Texture("res/sectionGridOverlay2.png");
        	savanaTex = new Texture("res/seamlesssand32.png");
        	ironTex = new Texture("res/tiledIron.png");
        	coalTex = new Texture("res/tiledCoal.png");
        	coalLowerTex = new Texture("res/tiledCoallower.png");
        	coalLowTex = new Texture("res/tiledCoallow.png");       	
        	tileAtlas = new TextureAtlas("res/tiles.atlas");
        	corrTex = new Texture("res/corr16.png");
        	sectionOutline = new Texture("res/sectionOutline.png");
        }
		
		corrTex.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		
		packageSprite = new Sprite(spriteSheet, 0, 0, 16, 16);
		packageHighlightSprite = new Sprite(spriteSheet, 17, 0, 16, 16);
		
		nodeSprite = new Sprite(spriteSheet, 34, 0, 16, 16);
		nodeHighlightSprite = new Sprite(spriteSheet, 51, 0, 16, 16);
		
		highlightSprite = new Sprite(spriteSheet, 68, 0, 16, 16);
		
		coalSprite = new Sprite(spriteSheet, 0, 17, 16, 16);
		ironSprite = new Sprite(spriteSheet, 0, 34, 16, 16);
		coalHighlightSprite = new Sprite(spriteSheet, 17, 17, 16, 16);
		ironHighlightSprite = new Sprite(spriteSheet, 17, 34, 16, 16);
		
		nodeClosedSprite = new Sprite(spriteSheet, 34, 17, 16, 16);
		nodeInputSprite = new Sprite(spriteSheet, 68, 17, 16, 16);
		nodeOutputSprite = new Sprite(spriteSheet, 51, 17, 16, 16);
		nodeCoalSprite = new Sprite(spriteSheet, 34, 34, 16, 16);
		nodeIronSprite = new Sprite(spriteSheet, 51, 34, 16, 16);	
		nodeConveyorSprite = new Sprite(spriteSheet, 68, 34, 16, 16);
		
		dirtTileSprite = new Sprite(spriteSheet, 85, 0, 16, 16);
		bigdirtTileSprite = new Sprite(savanaTex);
		
		coalTileSprite = new Sprite(coalTex);		
		coalTileLowerSprite = new Sprite(coalLowerTex);	
		coalTileLowSprite = new Sprite(coalLowTex);
		
		ironTileSprite = new Sprite(ironTex);
		
		turret = new Sprite(spriteSheet, 0, 68, 48, 48);
		turretHead = new Sprite(spriteSheet, 49, 68, 64, 23);
		bullet = new Sprite(spriteSheet, 49, 92, 7, 3);
		
		double nextTime = System.currentTimeMillis();	
		System.out.println("Sprites loaded! (time: " + (nextTime - currentTime) + " ms)");
	}
}

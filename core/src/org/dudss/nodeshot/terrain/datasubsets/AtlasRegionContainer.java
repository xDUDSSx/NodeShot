package org.dudss.nodeshot.terrain.datasubsets;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

/**Container class that can hold multiple {@link AtlasRegion}s*/
public class AtlasRegionContainer {	
	int size = 0;
	AtlasRegion[] textures;
	
	/**Shade factor of the second texture*/
	float secondaryShade; 
	
	/**Container class that can hold multiple {@link AtlasRegion}s
	 * @param textures Array of specified textures, they can be accessed with {@link #getTexture(int)} method
	 * with an index corresponding to their position in this array*/
	public AtlasRegionContainer(AtlasRegion... textures) {
		size = textures.length;
		this.textures = new AtlasRegion[textures.length];
		for (int i = 0; i < textures.length; i++) {
			this.textures[i] = textures[i];
		}
	}
	
	/**An {@link AtlasRegionContainer} that has an additional c_height value of the secondary texture. Used with corruption rendering.<br>
	 * <b>The number of textures and shades must be the same!</b>*/
	public AtlasRegionContainer(float secondaryShade, AtlasRegion... textures) {
		this(textures);
		this.secondaryShade = secondaryShade;
	} 
	
	/**Returns an {@linkplain AtlasRegion} corresponding to the index
	 * @param index Index of the {@linkplain AtlasRegion}
	 * @return {@link AtlasRegion}, null if the index is out of bounds
	 * */
	public AtlasRegion getTexture(int index) {
		if (index > textures.length - 1) {
			return null;
		}
		return textures[index];
	}
	/**Gets the c_height value of the second texture*/
	public float getSecondaryShade() {
		return secondaryShade;
	}
	
	/**Get number of textures in this container*/
	public int getSize() {
		return size;
	}
}
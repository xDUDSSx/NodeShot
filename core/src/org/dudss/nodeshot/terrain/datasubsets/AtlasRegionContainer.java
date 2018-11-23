package org.dudss.nodeshot.terrain.datasubsets;

import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

/**Container class that can hold multiple {@link AtlasRegion}s*/
public class AtlasRegionContainer {	
	int size = 0;
	AtlasRegion[] textures;
	
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
	
	/**Get number of textures in this container*/
	public int getSize() {
		return size;
	}
}
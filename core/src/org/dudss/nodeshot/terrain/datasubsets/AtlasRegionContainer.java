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
	 * with an index corresponding to their position in this array
	 * */
	public AtlasRegionContainer(AtlasRegion... textures) {
		size = textures.length;
		this.textures = new AtlasRegion[textures.length];
		for (int i = 0; i < textures.length; i++) {
			this.textures[i] = textures[i];
		}
	}

	/**An {@link AtlasRegionContainer} that has an additional c_height value of the secondary texture. Used with corruption rendering.<br>
	 * <b>The number of textures and shades must be the same!</b>
	 * */
	public AtlasRegionContainer(float secondaryShade, AtlasRegion... textures) {
		this(textures);
		this.secondaryShade = secondaryShade;
	} 

	/**Adds a texture to the last unoccupied slot of this container.
	 * @param texture The {@link AtlasRegion} that will be added.
	 */
	public void addTexture(AtlasRegion texture) {
		size++;
		AtlasRegion[] tempTextures = new AtlasRegion[size];		
		for (int i = 0; i < textures.length; i++) {
			tempTextures[i] = textures[i];
		}
		tempTextures[tempTextures.length-1] = texture;
		this.textures = tempTextures;
	}
	
	/**Adds all the textures of an {@link AtlasRegionContainer} to the end of this one.
	 * @param arc The {@link AtlasRegionContainer} that will be added.
	 */
	public void addContainer(AtlasRegionContainer arc) {
		int startSize = size;
		size += arc.size;
		
		AtlasRegion[] tempTextures = new AtlasRegion[size];		
		for (int i = 0; i < startSize; i++) {
			tempTextures[i] = textures[i];
		}
		for (int i = 0; i < arc.size; i++) {
			tempTextures[startSize + i] = arc.getTextures()[i];
		}
		
		this.textures = tempTextures;
	}
	
	/**Returns an {@linkplain AtlasRegion} corresponding to the index
	 * @param index Index of the {@linkplain AtlasRegion}
	 * @return {@link AtlasRegion}, null if the index is out of bounds
	 */
	public AtlasRegion getTexture(int index) {
		if (index > textures.length - 1) {
			return null;
		}
		return textures[index];
	}
	
	public AtlasRegion[] getTextures() {
		return textures;
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
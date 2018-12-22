package org.dudss.nodeshot.terrain.datasubsets;

import com.badlogic.gdx.graphics.Texture;

/**Container class that can hold multiple {@link Texture}s*/
public class TextureContainer {	
	int size = 0;
	Texture[] textures;
	
	/**Container class that can hold multiple {@link Texture}s
	 * @param textures Array of specified textures, they can be accessed with {@link #getTexture(int)} method
	 * with an index corresponding to their position in this array*/
	public TextureContainer(Texture... textures) {
		size = textures.length;
		this.textures = new Texture[textures.length];
		for (int i = 0; i < textures.length; i++) {
			this.textures[i] = textures[i];
		}
	}
	
	/**Adds a texture to the last unoccupied slot of this container.
	 * @param texture The {@link Texture} that will be added.
	 * */
	public void addTexture(Texture texture) {
		size++;
		Texture[] tempTextures = new Texture[size];		
		for (int i = 0; i < textures.length; i++) {
			tempTextures[i] = textures[i];
		}
		tempTextures[tempTextures.length-1] = texture;
		this.textures = tempTextures;
	}
	
	/**Returns an {@linkplain Texture} corresponding to the index
	 * @param index Index of the {@linkplain Texture}
	 * @return {@link Texture}, null if the index is out of bounds
	 * */
	public Texture getTexture(int index) {
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



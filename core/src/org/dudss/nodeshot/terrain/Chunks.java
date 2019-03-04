package org.dudss.nodeshot.terrain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.algorithms.SimplexNoiseGenerator;
import org.dudss.nodeshot.buildings.CreeperGenerator;
import org.dudss.nodeshot.misc.ChunkOperation;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.datasubsets.AtlasRegionContainer;
import org.dudss.nodeshot.terrain.datasubsets.MeshVertexData;
import org.dudss.nodeshot.terrain.datasubsets.TextureContainer;
import org.dudss.nodeshot.utils.Shaders;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**Manages terrain and corruption generation, updating, optimising and rendering.
 * Both terrain and corruption are rendered as direct OpenGL draw calls with their own
 * vertex and index buffers that get uploaded to the GPU. They are also rendered using custom GLSL shaders
 * @see Shaders
 * */
public class Chunks {
	
	Chunk[][] chunks;
	public Section[][] sections;
	
	/**Names of textures and their layer numbers*/
	final public static String[] terrainLayerNames = 
		{
		"0void",
		"1rock",
		"2rock",
		"3rock",
		"4sand",
		"5sand",
		"6dirt",
		"7grass",
		"8stone",
		"9concrete",
		"10concrete"
		};
	
	/**All {@link Section} instances that are currently visible by the camera.
	 * Updated by the {@link #updateView(OrthographicCamera)} method.
	 */
	public List<Section> sectionsInView;
	
	/**Time of the last {@link #updateView(OrthographicCamera)}, used to prevent unnecessary view updates*/
	public long lastViewPoll = System.currentTimeMillis();
	/**Minimum delay(ms) in-between {@link #updateView(OrthographicCamera)} calls*/
	public long pollRate = 2;
	
	public boolean created = false;
	public boolean generated = false;
	
	Color transparent = new Color(0, 0, 0, 0.0f);
	
	public enum OreType {
		COAL, IRON, NONE
	}
	
	/**Fog of war visibility types*/
	public enum Visibility {
		ACTIVE, SEMIACTIVE, DEACTIVATED
	}
	
	Rectangle viewBounds;
	Rectangle imageBounds = new Rectangle();
	
	/**Initialises chunks, sections and camera culling utilities.*/
	public void create() {		
		chunks = new Chunk[Base.CHUNK_AMOUNT][Base.CHUNK_AMOUNT];		
		sections = new Section[Base.CHUNK_AMOUNT/Base.SECTION_SIZE][Base.CHUNK_AMOUNT/Base.SECTION_SIZE];
		sectionsInView = new CopyOnWriteArrayList<Section>();
		
		//Initialising chunks
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				chunks[x][y] = new Chunk(x * Base.CHUNK_SIZE, y * Base.CHUNK_SIZE);
			}
		}
		
		//Setting each chunk it's neighbours, this way they don't have to be initialized and then dumped at runtime
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				chunks[x][y].updateNeighbour();
			}
		}
		
		//Camera view bounds
		viewBounds = new Rectangle();
		imageBounds = new Rectangle();
		
		//Initialising sections
        generateSections();
        
		created = true;
	}
	
	/**Sends a single logic update to a {@link Chunk}.
	 * @param ch The {@linkplain Chunk}.
	 */
	public void updateChunk(Chunk ch) {
		ch.update();
	}
	
	/**Updates section camera culling and updates sections that got into the camera view and aren't updated yet.
	 * This method has a inbuilt polling feature that prevents unnecessary view updates. The rate is defined by {@link #pollRate}.
	 * @param cam The main game camera
	 * */
	public void updateView(OrthographicCamera cam) {
		if ((lastViewPoll + pollRate) < System.currentTimeMillis()) {
			float width = cam.viewportWidth * cam.zoom;
			float height = cam.viewportHeight * cam.zoom;
			float w = width * Math.abs(cam.up.y) + height * Math.abs(cam.up.x);
			float h = height * Math.abs(cam.up.y) + width * Math.abs(cam.up.x);
			
			viewBounds.set(cam.position.x - w / 2 - 50, cam.position.y - h / 2 - 50, w + 100, h + 100);
			
			//Making a set that will be used later to define if the new view includes new (not yet updated) sections
			//I can use a Set here since the sectionsInView ArrayList is guaranteed to have no duplicates
			Set<Section> previousSectionsInView = new HashSet<Section>(sectionsInView);
			
			sectionsInView.clear();
			
			//Checking which sections are in the current view
			for (int y = 0; y < Base.SECTION_AMOUNT; y++) {
				for (int x = 0; x < Base.SECTION_AMOUNT; x++) {
					imageBounds.set(sections[x][y].sectionChunks[0][0].x, sections[x][y].sectionChunks[0][0].y, Base.SECTION_SIZE*Base.CHUNK_SIZE, Base.SECTION_SIZE*Base.CHUNK_SIZE);	
					if (viewBounds.contains(imageBounds) || viewBounds.overlaps(imageBounds)) {
						sectionsInView.add(sections[x][y]);
					}						 
				}
			}
			
			//Getting only the sections that are new in the view and need to be updated
			Set<Section> differenceSections = new HashSet<Section>(previousSectionsInView);
			for (Section s : sectionsInView) {
				if (!differenceSections.add(s) || !sectionsInView.contains(s)) {
					differenceSections.remove(s);
				}
			}

			//All the new sections get updated (just the ones that weren't in the view before}
			for (Section s : differenceSections) {
				updateSectionMesh(s, true);	//Updates all corruption in the section
				updateSectionMesh(s, false); //Updates terrain
				updateFogOfWarMesh(s);
			}
				
			
			lastViewPoll = System.currentTimeMillis();			
		}
	}

	/**Updates terrain or corruption meshes of the section, if corr == true, corruption mesh of a selected layer will be updated
	 * If layer == -1, all corruption meshes of the section will be updated
	 * @param s The assigned section
	 * @param corr Whether a terrain or corruption mesh should be updated
	 * **/
	public void updateSectionMesh(Section s, boolean corr) {      
    	if (!corr) {
        	MeshVertexData mvdTerrain = this.generateMeshVertexData(s, false);
        	s.updateTerrainMesh(mvdTerrain.getVerts(), mvdTerrain.getIndices());
        	s.requestTerrainUpdate();
    	} else {    		
        	MeshVertexData mvdCorruption = this.generateMeshVertexData(s, true);
        	s.updateCorruptionMesh(mvdCorruption.getVerts(), mvdCorruption.getIndices());   
        	s.requestCorruptionUpdate();
    	}
	}
	
	/**Updates terrain or corruption meshes of all the sections in main camera view, if corr == true, corruption mesh will be updated.
	 * @param corr Whether a terrain or corruption mesh should be updated
	 * **/
	public void updateAllSectionMeshes(boolean corr) {
		if (!corr) {
			for (Section s : this.sectionsInView) {
				MeshVertexData mvdTerrain = this.generateMeshVertexData(s, false);
        		s.updateTerrainMesh(mvdTerrain.getVerts(), mvdTerrain.getIndices());
        		s.requestTerrainUpdate();
        	}
    	} else {
    		for (Section s : this.sectionsInView) {	    			
				MeshVertexData mvdCorruption = this.generateMeshVertexData(s, true);
	        	s.updateCorruptionMesh(mvdCorruption.getVerts(), mvdCorruption.getIndices());
	        	s.requestCorruptionUpdate();
    		}   			
    	}
	}

	/**Updates terrain or corruption meshes of all the sections in main camera view, if corr == true, corruption mesh will be updated.
	 * TheWholeMap boolean states whether to only update {@link #sectionsInView} or the whole {@link #sections} array, eg. every {@link Section} on the map.
	 * @param corr Whether a terrain or corruption mesh should be updated
	 * @param theWholeMap Whether all the sections in the world should be updated. If false this method behaves like {@link #updateAllSectionMeshes(boolean)}
	 * @see #updateAllSectionMeshes(boolean)
	 */
	public void updateAllSectionMeshes(boolean corr, boolean theWholeMap) {
		if (theWholeMap) {
			if (!corr) {
				for (int y = 0; y < Base.SECTION_AMOUNT; y++) {
					for (int x = 0; x < Base.SECTION_AMOUNT; x++) {
						Section s = sections[x][y];
						MeshVertexData mvdTerrain = this.generateMeshVertexData(s, false);
		        		s.updateTerrainMesh(mvdTerrain.getVerts(), mvdTerrain.getIndices());
		        		s.requestTerrainUpdate();
					}
				}
	    	} else {
	    		for (int y = 0; y < Base.SECTION_AMOUNT; y++) {
					for (int x = 0; x < Base.SECTION_AMOUNT; x++) {
						Section s = sections[x][y];
						MeshVertexData mvdCorruption = this.generateMeshVertexData(s, true);
			        	s.updateCorruptionMesh(mvdCorruption.getVerts(), mvdCorruption.getIndices());
			        	s.requestCorruptionUpdate();
					}
				}		
	    	}
		} else {
			if (!corr) {
				for (Section s : this.sectionsInView) {
					MeshVertexData mvdTerrain = this.generateMeshVertexData(s, false);
	        		s.updateTerrainMesh(mvdTerrain.getVerts(), mvdTerrain.getIndices());
	        		s.requestTerrainUpdate();
	        	}
	    	} else {
	    		for (Section s : this.sectionsInView) {	    			
					MeshVertexData mvdCorruption = this.generateMeshVertexData(s, true);
		        	s.updateCorruptionMesh(mvdCorruption.getVerts(), mvdCorruption.getIndices());
		        	s.requestCorruptionUpdate();
	    		}   			
	    	}
		}
	}
	
	//TODO: Make this depend and be handled by a section (for performance reasons, no need to update chunks with no corruption or buildings)
	@Deprecated
	public void updateAllChunks() {
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				chunks[x][y].update();
			}
		}
		
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				chunks[x][y].applyUpdate();
			}
		}
	}
				
	/**Update fog mesh data of a {@link Section}.*/
	public void updateFogOfWarMesh(Section s) {
		MeshVertexData mvdFogOfWar = this.generateFogOfWarMeshVertexData(s, Base.SECTION_SIZE+1, Base.SECTION_SIZE+1);
    	s.updateFogOfWarMesh(mvdFogOfWar.getVerts(), mvdFogOfWar.getIndices());
    	s.requestFogOfWarUpdate();
	}
	
	/**Update fog mesh data of all {@link #sectionsInView}s.*/
	public void updateAllFogOfWarMeshes() {
		for (Section s : sectionsInView) {
			MeshVertexData mvdFogOfWar = this.generateFogOfWarMeshVertexData(s, Base.SECTION_SIZE+1, Base.SECTION_SIZE+1);
    		s.updateFogOfWarMesh(mvdFogOfWar.getVerts(), mvdFogOfWar.getIndices());
    		s.requestFogOfWarUpdate();
		}
	}
	
	public void drawTerrain() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);     
		SpriteLoader.terrainAtlas.findRegion("tiledCoal").getTexture().bind();   
	    Shaders.terrainShader.begin();
	    Shaders.terrainShader.setUniformMatrix("u_projTrans", GameScreen.cam.combined);
	    Shaders.terrainShader.setUniformi("u_texture", 0);
	    for (Section s : sectionsInView) {
	    	if (s.needsTerrainUpdate() == true) {		
	    		s.getTerrainMesh().setVertices(s.getTerrainVerts());
		    	s.getTerrainMesh().setIndices(s.getTerrainIndices());	 
 				s.updatedTerrain();
 			}
	    	s.getTerrainMesh().render(Shaders.terrainShader, GL20.GL_TRIANGLES);
	    }
	    Shaders.terrainShader.end();	
	    Gdx.gl.glDisable(GL20.GL_BLEND);
	}	
	
	/**Draws the {@link Section#corrMesh} of every {@link Section} in the {@link #sectionsInView} list. Corruption is drawn as a textured grid rendered using
	 * the OpenGL GL_TRIANGLES primitive and a custom glsl corruption shader ({@link Shaders}).
	 * @since As of the version <b>v5.0 (30.11.2018)</b>. Corruption and its edge resolving operates in a single mesh. Before, there were separate meshes for
	 * every corruption layer. That allowed me to tint different layers a specific shade. I've overcome this need by using a glsl shader that blends textures together.
	 * Thus I didn't need to keep corruption layers as separate meshes but as a single mesh (per {@link Section}) which increases performance.
	 * @see Chunk#getCorruptionTexture()
	 */
	public void drawCorruption() {
	 	Gdx.gl.glEnable(GL20.GL_BLEND);
	 	Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);  

        //For blur
	    //GameScreen.corrBuffers.get(layer).begin();
        //Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
 		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
 		SpriteLoader.terrainAtlas.findRegion("tiledCoal").getTexture().bind();   
 		 		
		Shaders.corruptionShader.begin();
		Shaders.corruptionShader.setUniformMatrix("u_projTrans", GameScreen.cam.combined);
		Shaders.corruptionShader.setUniformi("u_texture", 0);
		 
		for (Section s : sectionsInView) {	   	 
 			if (s.needsCorruptionMeshUpdate() == true) {		
 				s.getCorruptionMesh().setVertices(s.getCorruptionVerts());
		    	s.getCorruptionMesh().setIndices(s.getCorruptionIndices());
 				s.updatedCorruptionMesh();
 			}
 			s.getCorruptionMesh().render(Shaders.corruptionShader, GL20.GL_TRIANGLES);
 		}	 	 		
 		Shaders.corruptionShader.end();
 		
 		Gdx.gl.glDisable(GL20.GL_BLEND);
 		//GameScreen.corrBuffers.get(layer).end();	
 		//GameScreen.blurBuffer(GameScreen.corrBuffers.get(layer), GameScreen.blurBuffer, GameScreen.corrBuffers.get(layer).getColorBufferTexture(), 0, 0);
	}
	
	public void drawFogOfWar() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_SRC_ALPHA, GL20.GL_DST_ALPHA);
		
	    Shaders.fogOfWarShader.begin();
	    Shaders.fogOfWarShader.setUniformMatrix("u_projTrans", GameScreen.cam.combined);
	    for (Section s : sectionsInView) {
	    	if (s.needsFogOfWarMeshUpdate() == true) {		
	    		s.getFogOfWarMesh().setVertices(s.getFogOfWarVerts());
		    	s.getFogOfWarMesh().setIndices(s.getFogOfWarIndices());	 
 				s.updatedFogOfWarMesh();
 			}
	    	s.getFogOfWarMesh().render(Shaders.fogOfWarShader, GL20.GL_TRIANGLE_STRIP);
	    }
	    
	    Shaders.fogOfWarShader.end();	
	    Gdx.gl.glDisable(GL20.GL_BLEND);	    
	}
	
	/**
	 * Changes the fog of war visibility around an origin (in world space coordinates) and sets the appropriate visibility in the surrounding circular radius (in tiles).
	 * The radius cannot be more than 2*{@link Base#SECTION_SIZE}, because only the current, and neighbouring {@link Section}s are updated! TODO: improve.
	 * @param x Origin x world space coordinate.
	 * @param y Origin y world space coordinate.
	 * @param radius The radius of the circle, in tiles.
	 * @param visibility Type of visibility.
	 */
	public void setVisibility(float x, float y, int radius, Visibility visibility) {
		Set<Section> sectionsToUpdate = new HashSet<Section>();
		
		int centerAX = (int) (x / Base.CHUNK_SIZE);
		int centerAY = (int) (y / Base.CHUNK_SIZE);
		
		int originAX = centerAX - (int)(radius);
		int originAY = centerAY - (int)(radius);
		
		for (int sx = originAX; sx < originAX + radius*2; sx++) {
			for (int sy = originAY; sy < originAY + radius*2; sy++) {
				Chunk c = GameScreen.chunks.getChunkAtTileSpace(sx, sy);
				if (c != null) {
					double dist = Math.hypot(centerAX * Base.CHUNK_SIZE - c.getX(), centerAY * Base.CHUNK_SIZE - c.getY());
					double boundary = ((float)(radius))*Base.CHUNK_SIZE;					
					if (dist <= boundary) {		
						switch(visibility) {
							case ACTIVE: c.visibility = Chunk.active; c.visionProviderNumber++; break;
							case SEMIACTIVE: c.visibility = Chunk.semiactive; c.visionProviderNumber--; break;
							case DEACTIVATED: c.visibility = Chunk.deactivated; c.visionProviderNumber--; break;
						}
						
						if (c.visionProviderNumber > 0) {
							c.visibility = Chunk.active;
						} else {
							c.visibility = Chunk.semiactive;
							c.visionProviderNumber = 0;
						}
						
						sectionsToUpdate.add(c.getSection());
					}
				}
			}
		}
		
		for (Section s : sectionsToUpdate) {
			GameScreen.chunks.updateFogOfWarMesh(s);
		}
		
		//OLD
		/*List<Section> sectionsToUpdate = getSectionsAroundWorldSpacePoint(x, y);
		for (Section sec : sectionsToUpdate) {
			for (int sx = 0; sx < Base.SECTION_SIZE; sx++) {
				for (int sy = 0; sy < Base.SECTION_SIZE; sy++) {
					Chunk c = sec.getChunk(sx, sy);
					if (Math.hypot(x - c.getX(), y - c.getY()) < radius*Base.CHUNK_SIZE) {						
						switch(visibility) {
							case ACTIVE: c.visibility = Chunk.active; c.visionProviderNumber++; System.out.println("adding visibility " + c.visionProviderNumber); break;
							case SEMIACTIVE: c.visibility = Chunk.semiactive; c.visionProviderNumber--; break;
							case DEACTIVATED: c.visibility = Chunk.deactivated; c.visionProviderNumber--; break;
						}
						if (c.visionProviderNumber > 0) {
							c.visibility = Chunk.active;
						} else {
							c.visibility = Chunk.semiactive;
							c.visionProviderNumber = 0;
						}
					}
				}
			}
		};
		
		for (Section s : sectionsToUpdate) {
			GameScreen.chunks.updateFogOfWarMesh(s);
		}
		*/
	}
	
	/**Generates the fog of war {@link Mesh} for the specified {@link Section}. The generated mesh uses GL_TRIANGLE_STRIP and degenerate
	 * triangles at the end of rows.
	 * @param s The mesh section.
	 * @return The generated {@link Mesh}.
	 */
	public Mesh generateFogOfWarMesh(Section s) {
		//The grid size is bigger by one so that section borders are filled.
		int sizeX = Base.SECTION_SIZE+1;
		int sizeY = Base.SECTION_SIZE+1;
		
		int numberOfRectangles = sizeX * sizeY;
	    int numberOfVertices = numberOfRectangles;
		
		MeshVertexData fogOfWarData = generateFogOfWarMeshVertexData(s, sizeX, sizeY);
	    
	    int indicesSize = ((sizeY-1)*(sizeX*2)) + (2*(sizeY-2)) + 2;
	    Mesh mesh = new Mesh(false, numberOfVertices, indicesSize, 
				new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
	    
	    mesh.setVertices(fogOfWarData.getVerts());
	    mesh.setIndices(fogOfWarData.getIndices());
	    
	    return mesh;    
	}
	
	/**Generates the fog of war {@link MeshVertexData} for the specified {@link Section}. The generated mesh uses GL_TRIANGLE_STRIP and degenerate
	 * triangles at the end of rows.
	 * @param s The mesh section.
	 * @return The generated {@link MeshVertexData}.
	 */
	public MeshVertexData generateFogOfWarMeshVertexData(Section s, int sizeX, int sizeY) {
		int numberOfRectangles = sizeX * sizeY;
	    int numberOfVertices = numberOfRectangles;
	    
		int vertexPositionValue = 2; //x,y position values
		int vertexColorValue = 1; //A single packed color value
		int vertexTexCoordValue = 2; //Unnecessary uv values
	    
	    int valuesPerVertex = vertexPositionValue + vertexColorValue + vertexTexCoordValue; //5
	    
	    int ax = s.sectionChunks[0][0].ax;
	    int ay = s.sectionChunks[0][0].ay;
	    
	    //Grid size adjusted to map edges
	    if (this.chunks[ax][ay].ax + sizeX >= Base.CHUNK_AMOUNT) {
	    	sizeX -= 1;
	    }
	    if (this.chunks[ax][ay].ay + sizeY >= Base.CHUNK_AMOUNT) {
	    	sizeY -= 1;
	    }
	    
	    short[] indices = new short[((sizeY-1)*(sizeX*2)) + (2*(sizeY-2)) + 2];
	    float[] vertices = new float[numberOfVertices * valuesPerVertex];
	    
	    int i = 0;
	    int pointer = 0;
	    for (int y = sizeY-1; y >= 0; y--) {
	    	for (int x = 0; x < sizeX; x++) {	    		
	    		Chunk c = this.chunks[ax + x][ay + y];
	    		
	    		vertices[i * valuesPerVertex + 0] = c.getX() + Base.CHUNK_SIZE/2;
	    		vertices[i * valuesPerVertex + 1] = c.getY() + Base.CHUNK_SIZE/2;
	    		vertices[i * valuesPerVertex + 2] = Color.toFloatBits(0f, 0f, 0f, c.visibility);
	    		vertices[i * valuesPerVertex + 3] = 0;
	    		vertices[i * valuesPerVertex + 4] = 0;
	    		
	    		if (y > 0) {
	    			indices[pointer++] = (short) i;
	    			indices[pointer++] = (short) (i + sizeX);
	    		}
	    		i++;
	    	}
	    	if (y > 0) {
	    		indices[pointer++] = (short) indices[pointer-2];
	    		indices[pointer++] = (short) i;
	    	}
	    }
		return new MeshVertexData(vertices, indices);
	}
	
	/**Generates and initialises a terrain or a corruption mesh. Should be only called once 
	 * and the initialised mesh than can get updated using the the {@link #generateMeshVertexData(Section, boolean)} method.
	 * @param s The assigned section.
	 * @param corr Whether a terrain or a corruption mesh should be generated
	 * @return Returns the generated Mesh object*/
	public Mesh generateMesh(Section s, boolean corr) {		
	    int numberOfRectangles = Base.SECTION_SIZE*Base.SECTION_SIZE;
	    int numberOfVertices = 4 * numberOfRectangles;
	    
	    MeshVertexData newMeshData = generateVertexArrays(s, corr, numberOfRectangles, numberOfVertices);
	    
	    Mesh mesh = new Mesh(false, numberOfVertices, numberOfRectangles * 6, 
				new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "1"),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "2"),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "3"),
				new VertexAttribute(Usage.Generic, 2, "a_shade")); //Two shade components, for each texture (max of 2 corruption textures per tile)
	    
	    
	    mesh.setVertices(newMeshData.getVerts());
	    mesh.setIndices(newMeshData.getIndices());
	    
	    if (corr) {
	    	s.updateCorruptionMesh(newMeshData.getVerts(), newMeshData.getIndices());
	    } else {
	    	s.updateTerrainMesh(newMeshData.getVerts(), newMeshData.getIndices());
	    }
	    
	    return mesh;
	}
	
	/**Returns an updated {@link MeshVertexData} object used to update the {@link Section} meshes.
	 * @param s The assigned section.
	 * @param corr Whether a terrain or a corruption mesh should be generated
	 * @return Updated {@link MeshVertexData} object.*/
	public MeshVertexData generateMeshVertexData(Section s, boolean corr) {	
		int numberOfRectangles = Base.SECTION_SIZE*Base.SECTION_SIZE;
	    int numberOfVertices = 4 * numberOfRectangles;
	    return generateVertexArrays(s, corr, numberOfRectangles, numberOfVertices);
	}

	/**Generates the vertices and indices arrays for a Mesh with 13 vertex attributes
	 * @param s The assigned section.
	 * @param corr Whether a terrain or a corruption mesh should b e generated
	 * @param numberOfRectangles Number of rectangles in the vertex array
	 * @param numberOfVertices Number of vertices in the vertex array
	 * @return Updated {@link MeshVertexData} object.
	 * */
	private MeshVertexData generateVertexArrays(Section s, boolean corr, int numberOfRectangles, int numberOfVertices) {			      			
			int vertexPositionValue = 2; //x,y position values
			int vertexColorValue = 1; //A single packed color value
		    int vertexTexCordsValue = 8; //u,v texture coordinates (4 textures)
		    int vertexShadeValue = 2; //Custom value representing shade of corruption
		    
		    int valuesPerVertex = vertexPositionValue + vertexColorValue + vertexTexCordsValue + vertexShadeValue; //6
		    
		    short[] vertexIndices = new short[numberOfRectangles * 6];
		    float[] verticesWithColor = new float[numberOfVertices * valuesPerVertex];

		    int i = 0;		 
		    
		    for (int y = 0; y < Math.sqrt(numberOfRectangles); y++) {
		    	 for (int x = 0; x < Math.sqrt(numberOfRectangles); x++) {
		    		Chunk c = s.sectionChunks[x][y];
		    		 
					float tileX = c.getX();
					float tileY = c.getY();						
					
		  	        int rectangleOffsetInArray = i * valuesPerVertex * 4;  	        
		  	        
		  	        //Four texture regions and texture coordinates are used for mixing multiple texture variants using a shader
		  	        //Currently used when rendering terrain (eg. terrainShader, corruptionShader)
		  	      	
		  	        //All the texture coordinates are set to a blank (alpha = 0) texture to properly work with the terrain shaders
		  	        //(Since the texture layering checks are based on alpha values)
		  	      	//Texture 1 texture coordinates
		  	      	float au = SpriteLoader.terrainAtlas.findRegion("0void").getU();
		  	        float av = SpriteLoader.terrainAtlas.findRegion("0void").getV();
		  	        float au2 = SpriteLoader.terrainAtlas.findRegion("0void").getU2();
		  	        float av2 = SpriteLoader.terrainAtlas.findRegion("0void").getV2();
		  	        
		  	        //Texture 2 texture coordinates
		  	        float bu = au;
		  	        float bv = av;
		  	        float bu2 = au2;
		  	        float bv2 = av2;
		  	        
		  	        //Texture 3 texture coordinates
		  	        float cu = au;   
		  	        float cv = av;   
		  	        float cu2 = au2; 
		  	        float cv2 = av2; 
		  	        
		  	        //Texture 4 texture coordinates
		  	        float du = au;   
		  	        float dv = av;   
		  	        float du2 = au2; 
		  	        float dv2 = av2; 
		  	        
		  	        AtlasRegionContainer tC;
		  	        if (corr) {
		  	        	tC = c.getCorruptionTexture();
		  	        } else {
		  	        	tC = c.getTerrainTexture();
		  	        }
		  	           
		  	        if (tC == null) {
		  	        	continue;
		  	        }
		  	        if (tC.getTexture(0) != null) {  
		  	        	
		  	        	float shade = 0; //Main texture shade
		  	        	float shade1 = -1; //Secondary texture shade (Only used when a tile has mutiple shades)
		  	        					  	        	
			  	        //First set of texture coordinates for the texture1
			  	        au = tC.getTexture(0).getU();
			  	      	av = tC.getTexture(0).getV();
			  	        au2 = tC.getTexture(0).getU2();
			  	        av2 = tC.getTexture(0).getV2();	
			  	        
			  	        //Second set of texture coordinates IF supplied by the AtlasRegionContainer
			  	        if (tC.getTexture(1) != null) {
			  	        	bu = tC.getTexture(1).getU();  
			  	        	bv = tC.getTexture(1).getV();  
			  	        	bu2 = tC.getTexture(1).getU2();
			  	        	bv2 = tC.getTexture(1).getV2();
			  	        	
			  	        	//Get the shade of the secondary texture (This is used to shade the corruption mesh)
			  	        	if (corr) shade1 = Chunk.calculateShadeForValue(tC.getSecondaryShade());		
			  	        }
			  	        
			  	        //Third set of texture coordinates IF supplied by the AtlasRegionContainer
			  	        if (tC.getTexture(2) != null) {
			  	        	cu = tC.getTexture(2).getU();  
			  	        	cv = tC.getTexture(2).getV();  
			  	        	cu2 = tC.getTexture(2).getU2();
			  	        	cv2 = tC.getTexture(2).getV2();	  	        		 
			  	        }
			  	        
			  	        //Fourth set of texture coordinates IF supplied by the AtlasRegionContainer
			  	        if (tC.getTexture(3) != null) {
			  	        	du = tC.getTexture(3).getU();  
			  	        	dv = tC.getTexture(3).getV();  
			  	        	du2 = tC.getTexture(3).getU2();
			  	        	dv2 = tC.getTexture(3).getV2();	  	        		 
			  	        }

			  	        //Assign a color attribute for further color / alpha modifications.
			  	        /*The color attribute is a 4 component vector and I can use its positions to indicate
			  	         *alpha changes that depend on different shade values. I don't want to recalculate the alpha value 
			  	         *on the gpu. I can do it here on the cpu and then pass the values through color attribute.
			  	        */
			  	        float f = 0;
			  	        if (corr) {
			  	        	shade = c.calculateShade();	  	        		
			  	        	if (shade1 == -1) shade1 = Chunk.calculateShadeForValue(1);
			  	        	//Transparency and shade interpolation assigns different gradient types to the overall structure
			  	        	float alpha = Interpolation.exp5Out.apply(0.8f, 1f, shade);			  	        	
			  	        	float alpha1 = Interpolation.exp5Out.apply(0.8f, 1f, shade1);			  	        	
			  	        	f = Color.toFloatBits(alpha, alpha1, 1f, 1f);	
			  	        	shade = 0.3f + (0.65f - (Interpolation.exp5Out.apply(0.3f, 0.95f, shade) - 0.3f));
			  	        	shade1 = 0.3f + (0.65f - (Interpolation.exp5Out.apply(0.3f, 0.95f, shade1) - 0.3f));
			  	        } else {
			  	        	f = Color.toFloatBits(1f, 1f, 1f, 1f);
			  	        }
			  	        
			  	        //Set the individual vertex attributes to each of the 4 vertexes of this square
			  	     	setValuesInArrayForVertex(verticesWithColor, au, av2, bu, bv2, cu, cv2, du, dv2, tileX, tileY, f, shade, shade1, rectangleOffsetInArray, 0);
			  	        setValuesInArrayForVertex(verticesWithColor, au2, av2, bu2, bv2, cu2, cv2, du2, dv2, tileX + Base.CHUNK_SIZE, tileY, f, shade, shade1, rectangleOffsetInArray, 1);
			  	        setValuesInArrayForVertex(verticesWithColor, au2, av, bu2, bv, cu2, cv, du2, dv, tileX + Base.CHUNK_SIZE, tileY + Base.CHUNK_SIZE, f, shade, shade1, rectangleOffsetInArray, 2);
			  	        setValuesInArrayForVertex(verticesWithColor, au, av, bu, bv, cu, cv, du, dv, tileX, tileY + Base.CHUNK_SIZE, f, shade, shade1, rectangleOffsetInArray, 3);			  	       
			  	        
			  	        //Set the index buffer object (IBO) indices that specify the triangle structure
			  	        vertexIndices[i * 6 + 0] = (short) (i * 4 + 0);
			  	        vertexIndices[i * 6 + 1] = (short) (i * 4 + 1);
			  	        vertexIndices[i * 6 + 2] = (short) (i * 4 + 3);
			  	        vertexIndices[i * 6 + 3] = (short) (i * 4 + 3);
			  	        vertexIndices[i * 6 + 4] = (short) (i * 4 + 2);
			  	        vertexIndices[i * 6 + 5] = (short) (i * 4 + 1);
			  	        
			  	        i++;
		  	        }
		    	}
		    }	 	
		    
		    return new MeshVertexData(verticesWithColor, vertexIndices);
	}
	
	/**Method that populates the mesh vertex array (OpenGL VBO)
	 * @param vertices The array that is being modified
	 * @param au U texture1 coordinate
	 * @param av V texture1 coordinate
	 * @param bu U texture2 coordinate
	 * @param bv V texture2 coordinate
	 * @param cu U texture3 coordinate
	 * @param cv V texture3 coordinate
	 * @param du U texture4 coordinate
	 * @param dv V texture4 coordinate
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param c The packed color that can be used with shaders
	 * @param shade A special shade attribute represented as "a_shade" in shaders and which is used to tint corruption mesh tiles
	 * @param rectangeOffsetInArray Index of the current position in the array
	 * @param vertexNumberInRect Rectangle (tile) vertex which attributes are being set
	 * */
	private void setValuesInArrayForVertex(float[] vertices, float au, float av, float bu, float bv, float cu, float cv, float du, float dv, float x, float y, float c, float shade, float shade1, int rectangleOffsetInArray, int vertexNumberInRect) {
	    int valuesPerVertex = 13; //Constant representing the number of individual attributes (x,y,c,4x u, v,shade)
		int vertexOffsetInArray = rectangleOffsetInArray + vertexNumberInRect * valuesPerVertex; 

	    // x position
	    vertices[vertexOffsetInArray + 0] = x;
	    // y position
	    vertices[vertexOffsetInArray + 1] = y;
	    // color 
	    vertices[vertexOffsetInArray + 2] = c;  	 	
	    // u texture1 coord
	    vertices[vertexOffsetInArray + 3] = au;
	    // v texture1 coord
	    vertices[vertexOffsetInArray + 4] = av;
	    // tu texture2 coord
	    vertices[vertexOffsetInArray + 5] = bu;
	    // tv texture2 coord
	    vertices[vertexOffsetInArray + 6] = bv;
	    // u texture1 coord
	    vertices[vertexOffsetInArray + 7] = cu;
	    // v texture1 coord
	    vertices[vertexOffsetInArray + 8] = cv;
	    // tu texture2 coord
	    vertices[vertexOffsetInArray + 9] = du;
	    // tv texture2 coord
	    vertices[vertexOffsetInArray + 10] = dv;
	    // texture shade
	    vertices[vertexOffsetInArray + 11] = shade;
	    // texture1 shade (if used)
	    vertices[vertexOffsetInArray + 12] = shade1;
	}
	
	public void setCoalLevel(float level, int x, int y) {
		chunks[x][y].setCoalLevel(level);
	}
	
	public void setIronLevel(float level, int x, int y) {
		chunks[x][y].setIronLevel(level);
	}
	
	/**Gets the chunk located at x/y coordinates in tile space*/
	public Chunk getChunkAtTileSpace(int x, int y) {
		if (x < 0 || y < 0 || x > Base.CHUNK_AMOUNT-1 || y > Base.CHUNK_AMOUNT-1) {
			return null;
		}
		return chunks[x][y];
	}
	
	public void setChunk(int x, int y, Chunk chunk) {
		chunks[x][y] = chunk;
	}
	
	public Section getSection(int x, int y) {
		if (x < 0 || y < 0 || x > Base.SECTION_AMOUNT-1 || y > Base.SECTION_AMOUNT-1) {
			return null;
		}
		return sections[x][y];
	}
	
	/**Generates all the {@link Section}s and initialises their terrain and corruption meshes */
	public void generateSections() {
		for (int y = 0; y < Base.CHUNK_AMOUNT; y+=Base.SECTION_SIZE) {
			for (int x = 0; x < Base.CHUNK_AMOUNT; x+=Base.SECTION_SIZE) {
				Section s = new Section(null);
				Chunk[][] sectionChunks = new Chunk[Base.SECTION_SIZE][Base.SECTION_SIZE];
				for(int y1 = 0; y1 < Base.SECTION_SIZE; y1++) {
					for (int x1 = 0; x1 < Base.SECTION_SIZE; x1++) {					
						sectionChunks[x1][y1] = chunks[x + x1][y + y1]; 
						sectionChunks[x1][y1].setSection(s);
						if (x1 == 0 || x1 == Base.SECTION_SIZE-1 || y1 == 0 || y1 == Base.SECTION_SIZE-1) {
							sectionChunks[x1][y1].setBorderChunk(true);
						}
					}
				}

				s.setChunks(sectionChunks);
				s.terrainMesh = generateMesh(s, false);
				s.corrMesh = generateMesh(s, true);
				s.fogMesh = generateFogOfWarMesh(s);
				
				sections[x/Base.SECTION_SIZE][y/Base.SECTION_SIZE] = s;				
				s.updateNeighbours();
			}
		}
	}
	
	/**Generates the terrain relief and ores.
	 * @return Returns a {@link TextureContainer} that holds gray-scale pixmaps of generated maps.<br>
	 * Textures are in the following order: 1. terrain height map, 2. coal map, 3. iron map.
	 * This method has to be called after {@link Chunks#create()}.
	 * */
	public TextureContainer generateAll() {
		SimplexNoiseGenerator sn = new SimplexNoiseGenerator();
		BaseClass.logger.info("Generating noise (1/3)");
		float[][] coalMap = sn.generateOctavedSimplexNoise(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, 4, 0.45f, 0.018f);
		sn.randomizeMutatorTable();
		BaseClass.logger.info("Generating noise (2/3)");
		float[][] ironMap = sn.generateOctavedSimplexNoise(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, 4, 0.45f, 0.018f);
		sn.randomizeMutatorTable();
		BaseClass.logger.info("Generating noise (3/3)");
		//float[][] heightMap = sn.generateOctavedSimplexNoise(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, 3, 0.35f, 0.009f); //Smooth noise
		//float[][] heightMap = sn.generateOctavedSimplexNoise(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, 5, 0.5f, 0.009f);
		float[][] heightMap = sn.generateOctavedSimplexNoise(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, 4, 0.5f, 0.017f);
		
		BaseClass.logger.info("Creating pixmaps");
		Pixmap coalPixmap = new Pixmap(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, Format.RGBA8888);
		for (int x1 = 0; x1 < Base.CHUNK_AMOUNT; x1++) {
			for (int y1 = 0; y1 < Base.CHUNK_AMOUNT; y1++) {   			
				float newVal = Base.range(coalMap[x1][y1], -1.4f, 1.4f, -1.0f, 1.0f);    
				
	    	    //Converting [-1.0,1.0] to [0,1]
	    	    float val = (((newVal - (-1.0f)) * (1.0f - 0)) / (1.0f - (-1.0f))) + 0;
	     	    coalPixmap.setColor(Color.rgba8888(val, val, val, 1.0f));
	     		coalPixmap.drawPixel(x1, y1);
         	}
		}
		
 	   	Pixmap ironPixmap = new Pixmap(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, Format.RGBA8888);
 	   	for (int x2 = 0; x2 < Base.CHUNK_AMOUNT; x2++) {
 	   		for (int y2 = 0; y2 < Base.CHUNK_AMOUNT; y2++) {   
	    		float newVal = Base.range(ironMap[x2][y2], -1.4f, 1.4f, -1.0f, 1.0f);    
	    		
	    	    //Converting [-1.0,1.0] to [0,1]
	    	    float val = (((newVal - (-1.0f)) * (1.0f - 0)) / (1.0f - (-1.0f))) + 0;	    	    
	     	    ironPixmap.setColor(Color.rgba8888(val, val, val, 1.0f));
	     		ironPixmap.drawPixel(x2, y2);
 	   		}
 	   	}
 	   	
 	   	Pixmap heightPixmap = new Pixmap(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, Format.RGBA8888);
 	   	for (int x2 = 0; x2 < Base.CHUNK_AMOUNT; x2++) {
 	   		for (int y2 = 0; y2 < Base.CHUNK_AMOUNT; y2++) {   	    		
 	   			//Converting [-1.0,1.0] to [0,1]
 	   			float val = (((heightMap[x2][y2] - (-1.0f)) * (1.0f - 0)) / (1.0f - (-1.0f))) + 0;	    	    
 	   			heightPixmap.setColor(Color.rgba8888(val, val, val, 1.0f));
 	   			heightPixmap.drawPixel(x2, y2);
 	   		}
 	   	}

		//CHUNK GEN
		BaseClass.logger.info("Generating chunks (n. " + Base.CHUNK_AMOUNT*Base.CHUNK_AMOUNT + ")");
		BaseClass.logger.info("Generating terrain heights");
		heightPixmap = generateHeights(heightPixmap);
		BaseClass.logger.info("Resolving initial terrain mesh edges!");
		updateAllSectionMeshes(false, true); //Global mesh update
		BaseClass.logger.info("Generating coal ore");
		coalPixmap = generateCoalPatches(coalPixmap);
		BaseClass.logger.info("Generating iron ore");
		ironPixmap = generateIronPatches(ironPixmap);
		generated = true;
				
		return new TextureContainer(new Texture(heightPixmap), new Texture(coalPixmap), new Texture(ironPixmap));
	}
	
	public Pixmap generateCoalPatches(Pixmap pixmap) {
		Pixmap patchPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Format.RGBA8888);
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {
				Color c = new Color(pixmap.getPixel(x, y));				
				if (c.r > Base.COAL_THRESHOLD && !chunks[x][y].isTerrainEdge()) {
					float level = Base.range(c.r, Base.COAL_THRESHOLD, 1f, 0.2f, 1.0f);
					chunks[x][y].setCoalLevel(level);
				} else {
					chunks[x][y].setCoalLevel(0);
				}
			}
		}
		return patchPixmap;
	}
	
	public Pixmap generateIronPatches(Pixmap pixmap) {
		Pixmap patchPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Format.RGBA8888);
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {
				Color c = new Color(pixmap.getPixel(x, y));						
				if (c.r > Base.IRON_THRESHOLD && !chunks[x][y].isTerrainEdge()) {
					float level = Base.range(c.r, Base.IRON_THRESHOLD, 1f, 0.2f, 1.0f);
					chunks[x][y].setIronLevel(level);
				} else {
					chunks[x][y].setIronLevel(0);
				}
			}
		}
		return patchPixmap;
	}
	
	public Pixmap generateHeights(Pixmap pixmap) {
 		Pixmap patchPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Format.RGBA8888);
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {
				Color c = new Color(pixmap.getPixel(x, y));		
				//float iVal = Interpolation.exp5In.apply(c.r);
				int val = (int) Base.range(c.r, 0, 1f, 1, Base.MAX_HEIGHT-1);			
				chunks[x][y].setHeight(val);
			}
		}
		return patchPixmap;
	}
	
	/**Places {@link CreeperGenerator}s randomly in the world.*/
	public void generateCreeperSpawners() {
		for (int y = 0; y < Base.SECTION_AMOUNT; y++) {
			for (int x = 0; x < Base.SECTION_AMOUNT; x++) {
				Section s = sections[x][y];
				int chance = Base.getRandomIntNumberInRange(0, 100);
				if (chance < 5) {
					int rX = Base.getRandomIntNumberInRange(0, Base.SECTION_SIZE);
					int rY = Base.getRandomIntNumberInRange(0, Base.SECTION_SIZE);
					
					CreeperGenerator gen = new CreeperGenerator(0, 0); 
					
					float bX = s.getChunk(0, 0).getX() + (rX*Base.CHUNK_SIZE);
					float bY = s.getChunk(0, 0).getY() + (rY*Base.CHUNK_SIZE);
					
					double dist = Math.hypot(bX - Base.WORLD_SIZE/2, bY - Base.WORLD_SIZE/2);				
					if (dist > Base.GENERATOR_SAFEZONE) {
						if (bX > gen.getWidth() && bX < Base.WORLD_SIZE - gen.getWidth() && bY > gen.getHeight() && bY < Base.WORLD_SIZE - gen.getHeight()) {
							gen.setLocation(s.getChunk(0, 0).getX() + (rX*Base.CHUNK_SIZE), s.getChunk(0, 0).getY() + (rY*Base.CHUNK_SIZE), true);
							gen.buildAndLevel();
						}
					}
				}
				if (chance < 50) {
					int rX = Base.getRandomIntNumberInRange(0, Base.SECTION_SIZE);
					int rY = Base.getRandomIntNumberInRange(0, Base.SECTION_SIZE);
					
					CreeperGenerator gen = new CreeperGenerator(0, 0); 
					
					float bX = s.getChunk(0, 0).getX() + (rX*Base.CHUNK_SIZE);
					float bY = s.getChunk(0, 0).getY() + (rY*Base.CHUNK_SIZE);
					
					double dist = Math.hypot(bX - Base.WORLD_SIZE/2, bY - Base.WORLD_SIZE/2);				
					if (dist > Base.GENERATOR_SAFEZONE) {
						if (bX > gen.getWidth() && bX < Base.WORLD_SIZE - gen.getWidth() && bY > gen.getHeight() && bY < Base.WORLD_SIZE - gen.getHeight()) {
							gen.setLocation(s.getChunk(0, 0).getX() + (rX*Base.CHUNK_SIZE), s.getChunk(0, 0).getY() + (rY*Base.CHUNK_SIZE), true);
							gen.buildAndLevel();
						}
					}
				}
			}
		}
	}
	
	/**Returns all 9 sections around this particular point in world space (Including the middle section).
	 * @param x X world space coordinate
	 * @param y Y world space coordinate
	 * */
	public List<Section> getSectionsAroundWorldSpacePoint(float x, float y) {
		List<Section> sections = new ArrayList<Section>();
		
		Section s = getSectionByWorldSpace(x, y);
		sections = getNeighbourSections(s);
		sections.add(s);
		
		return sections;
	}
	
	/**Returns a list of all neighbouring sections, if there is no neighbouring section, it is skipped and not included in the list
	 * @param s The {@link Section} which neighbours will be returned.*/
	public List<Section> getNeighbourSections(Section s) {
		List<Section> sections = new ArrayList<Section>();
		Chunk sectionOrigin = s.getChunk(0, 0);
		int sectionAx = (int) ((int) (sectionOrigin.getX() / Base.CHUNK_SIZE) / Base.SECTION_SIZE);
		int sectionAy = (int) ((int) (sectionOrigin.getY() / Base.CHUNK_SIZE) / Base.SECTION_SIZE);
		
		if (sectionAy < Base.SECTION_AMOUNT-1) 									sections.add(GameScreen.chunks.sections[sectionAx][sectionAy + 1]);
		if (sectionAy < Base.SECTION_AMOUNT-1 && sectionAx < Base.SECTION_AMOUNT-1) sections.add(GameScreen.chunks.sections[sectionAx + 1][sectionAy + 1]);
		if (sectionAx < Base.SECTION_AMOUNT-1) 									sections.add(GameScreen.chunks.sections[sectionAx + 1][sectionAy]);
		if (sectionAx < Base.SECTION_AMOUNT-1 && sectionAy > 0) 					sections.add(GameScreen.chunks.sections[sectionAx + 1][sectionAy - 1]);
		if (sectionAy > 0) 														sections.add(GameScreen.chunks.sections[sectionAx][sectionAy - 1]);
		if (sectionAy > 0 && sectionAx > 0) 									sections.add(GameScreen.chunks.sections[sectionAx - 1][sectionAy - 1]);
		if (sectionAx > 0) 														sections.add(GameScreen.chunks.sections[sectionAx - 1][sectionAy]);
		if (sectionAy < Base.SECTION_AMOUNT-1 && sectionAx > 0) 					sections.add(GameScreen.chunks.sections[sectionAx - 1][sectionAy + 1]);
		return sections;
	}
	
	/**Returns the {@link Section} that holds this points {@link Chunk}.
	 * @param x X tile space coordinate.
	 * @param y Y tile space coordinate.
	 */
	public Section getSectionByTileSpace(int ax, int ay) {
		return GameScreen.chunks.sections[ax / Base.SECTION_SIZE][ay / Base.SECTION_SIZE];
	}

	/**Returns the {@link Section} that holds this points {@link Chunk}.
	 * @param x X world space coordinate.
	 * @param y Y world space coordinate.
	 */
	public Section getSectionByWorldSpace(float x, float y) {
		int ax = (int) (x / Base.CHUNK_SIZE);
		int ay = (int) (y / Base.CHUNK_SIZE);
		
		return GameScreen.chunks.sections[ax / Base.SECTION_SIZE][ay / Base.SECTION_SIZE];
	}
	
	/**Gets the {@link Chunk} located at world space x, y*/
	public Chunk getChunkAtWorldSpace(float x, float y) {
		int tileX = (int) (x / Base.CHUNK_SIZE);
		int tileY = (int) (y / Base.CHUNK_SIZE);
		
		return GameScreen.chunks.getChunkAtTileSpace(tileX, tileY);
	}
	
	/**Converts the world-space coordinates to tile-space.*/
	public Vector2 toTileSpace(float x, float y) {
		return new Vector2((int) (x / Base.CHUNK_SIZE), (int) (y / Base.CHUNK_SIZE));
	}
	
	/**Converts the tile-space coordinates to world-space.*/
	public Vector2 toWorldSpace(int x, int y) {
		return new Vector2((x * Base.CHUNK_SIZE), (y * Base.CHUNK_SIZE));
	}
	
	/**Gets the chunks around the world space point in a certain diameter (diameter is in tile space).*/
	public List<Chunk> getChunksAroundWorldSpacePoint(float x, float y, float diameter) {
		return getChunksAroundWorldSpacePoint(x, y, diameter, null);
	}
	
	/**Gets the chunks around the world space point in a certain diameter (diameter is in tile space).
	 * @param x Center X coordinate.
	 * @param y Center Y coordinate.
	 * @param diameter Diameter of the selection.
	 * @param o {@link ChunkOperation} to be executed for each {@link Chunk}.
	 * @return A list of all selected {@linkplain Chunk}s.
	 */
	public List<Chunk> getChunksAroundWorldSpacePoint(float x, float y, float diameter, ChunkOperation o) {
		List<Chunk> chunksInRadius = new ArrayList<Chunk>();
		
		int centerAX = (int) (x / Base.CHUNK_SIZE);
		int centerAY = (int) (y / Base.CHUNK_SIZE);
		
		int originAX = centerAX - (int)(diameter/2);
		int originAY = centerAY - (int)(diameter/2);
		
		for (int sx = originAX; sx < originAX + diameter; sx++) {
			for (int sy = originAY; sy < originAY + diameter; sy++) {
				Chunk c = GameScreen.chunks.getChunkAtTileSpace(sx, sy);
				if (c != null) {
					double dist = Math.hypot(centerAX * Base.CHUNK_SIZE - c.getX(), centerAY * Base.CHUNK_SIZE - c.getY());
					double boundary = ((float)(diameter/2f))*Base.CHUNK_SIZE;
					
					if (dist <= boundary) {		
						chunksInRadius.add(c);
						if (o != null) {
							o.execute(c, dist, boundary);
						}
					}
				}
			}
		}
		
		return chunksInRadius;
	}
	
	/**Gets the closest chunk that has its creeper level above the threshold around the world space point in a certain diameter (diameter is in world space).*/
	public Chunk getClosestCorruptionChunkToWorldSpace(float x, float y, float minDiameter, float maxDiameter, float creeperThreshold) {
		Chunk closestChunk = null;
		float closestDist = Float.MAX_VALUE;
		
		//Center chunk should not be null (since the requesting object / building should be standing on it)
		Vector2 originChunk = toTileSpace(x - ((maxDiameter/2)), y - ((maxDiameter/2)));
		
		for (int sx = (int) originChunk.x; sx < originChunk.x + maxDiameter; sx++) {
			for (int sy = (int) originChunk.y; sy < originChunk.y + maxDiameter; sy++) {
				Chunk c = GameScreen.chunks.getChunkAtTileSpace(sx, sy);
				if (c != null) {
					float dist = (float) Math.hypot(x - c.getX(), y - c.getY());
					if (dist <= ((float)(maxDiameter/2f))*Base.CHUNK_SIZE && c.getCreeperLevel() > creeperThreshold) {		
						if (dist < closestDist && dist > minDiameter) {
							closestChunk = c;
							closestDist = dist;
						}
					}
				}
			}
		}
		
		return closestChunk;
	}
	
	/**Returns the list of {@link Chunk}s that a world space line intersects.*/
	public List<Chunk> retrieveChunksIntersectingLine(int x0, int y0, int x1, int y1) 
	{
		List<Chunk> line = new ArrayList<Chunk>();
 
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
 
        int sx = x0 < x1 ? 1 : -1; 
        int sy = y0 < y1 ? 1 : -1; 
 
        int err = dx-dy;
        int e2;
 
        while (true) 
        {
        	line.add(getChunkAtTileSpace(x0, y0));
 
        	if (x0 == x1 && y0 == y1) 
        		break;
 
        	e2 = 2 * err;
        	if (e2 > -dy) 
        	{
        		err = err - dy;
        		x0 = x0 + sx;
        	}
 
        	if (e2 < dx) 
        	{
        		err = err + dx;
        		y0 = y0 + sy;
        	}
        }
        return line;
	}
	
}

package org.dudss.nodeshot.terrain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.algorithms.SimplexNoiseGenerator;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.datasubsets.MeshVertexData;
import org.dudss.nodeshot.terrain.datasubsets.TextureContainer;
import org.dudss.nodeshot.terrain.datasubsets.AtlasRegionContainer;
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
import com.badlogic.gdx.math.Rectangle;

/**Manages terrain and corruption generation, updating, optimising and rendering.
 * Both terrain and corruption are rendered as direct OpenGL draw calls with their own
 * vertex and index buffers that get uploaded to the GPU. They are also rendered using custom GLSL shaders.
 * @see {@link Shaders#corruptionShader} and {@link Shaders#terrainShader}.
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
		
		//Initializing sections
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
				updateSectionMesh(s, true, -1);	//Updates all corruption in the section
				updateSectionMesh(s, false, 0);	//Updates terrain
			}
						
			lastViewPoll = System.currentTimeMillis();			
		}
	}

	/**Updates terrain or corruption meshes of the section, if corr == true, corruption mesh of a selected layer will be updated
	 * If layer == -1, all corruption meshes of the section will be updated
	 * @param s The assigned section
	 * @param corr Whether a terrain or corruption mesh should be updated
	 * @param level Which corruption level should be updated
	 * **/
	public void updateSectionMesh(Section s, boolean corr, int level) {      
    	if (!corr) {
        	MeshVertexData mvdTerrain = this.generateMeshVertexData(s, false, 0);
        	s.updateTerrainMesh(mvdTerrain.getVerts(), mvdTerrain.getIndices());
        	s.requestTerrainUpdate();
    	} else {
    		if (level == -1) {
    			for (int i = 0; i < Base.MAX_CREEP; i++) {
    				MeshVertexData mvdCorruption = this.generateMeshVertexData(s, true, i);
    	        	s.updateCorruptionMesh(i, mvdCorruption.getVerts(), mvdCorruption.getIndices());
    	        	for (int c = 0; c < Base.MAX_CREEP; c++) {
    	        		s.requestCorruptionUpdate(c);
    	        	}
    			}
    		} else {
	        	MeshVertexData mvdCorruption = this.generateMeshVertexData(s, true, level);
	        	s.updateCorruptionMesh(level, mvdCorruption.getVerts(), mvdCorruption.getIndices());   
	        	s.requestCorruptionUpdate(level);
    		}
    	}
	}
	
	/**Updates terrain or corruption meshes of all the sections in main camera view, if corr == true, corruption mesh of a selected layer will be updated
	 * If layer == -1, all corruption meshes of the section will be updated
	 * @param corr Whether a terrain or corruption mesh should be updated
	 * @param level Which corruption level should be updated
	 * **/
	public void updateAllSectionMeshes(boolean corr, int level) {
		if (!corr) {
			for (Section s : GameScreen.chunks.sectionsInView) {
				MeshVertexData mvdTerrain = this.generateMeshVertexData(s, false, 0);
        		s.updateTerrainMesh(mvdTerrain.getVerts(), mvdTerrain.getIndices());
        		s.requestTerrainUpdate();
        	}
    	} else {
    		if (level == -1) {
    			for (Section s : GameScreen.chunks.sectionsInView) {
	    			for (int i = 0; i < Base.MAX_CREEP; i++) {
	    				MeshVertexData mvdCorruption = this.generateMeshVertexData(s, true, i);
	    	        	s.updateCorruptionMesh(i, mvdCorruption.getVerts(), mvdCorruption.getIndices());
	    	        	for (int c = 0; c < Base.MAX_CREEP; c++) {
	    	        		s.requestCorruptionUpdate(c);
	    	        	}
	    			}
    			}
    		} else {
    			for (Section s : GameScreen.chunks.sectionsInView) {
		        	MeshVertexData mvdCorruption = this.generateMeshVertexData(s, true, level);
		        	s.updateCorruptionMesh(level, mvdCorruption.getVerts(), mvdCorruption.getIndices());   
		        	s.requestCorruptionUpdate(level);
    			}
    		}
    	}
	}
	
	//TODO: Make this depend and be handled by a section (for performance reasons, no need to update chunks with no corruption or buildings)
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
				
	public void drawTerrain() {
			Gdx.gl.glEnable(GL20.GL_BLEND);
			Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);	      
			SpriteLoader.tileAtlas.findRegion("tiledCoal").getTexture().bind();   
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
	
	public void drawCorruption(int layer) {
		 	Gdx.gl.glEnable(GL20.GL_BLEND);
	        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);	      	       			
		    //GameScreen.corrBuffers.get(layer).begin();
	        //Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	 		//Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	 		SpriteLoader.tileAtlas.findRegion("tiledCoal").getTexture().bind();   
	 		 		
			Shaders.corruptionShader.begin();
			Shaders.corruptionShader.setUniformMatrix("u_projTrans", GameScreen.cam.combined);
			Shaders.corruptionShader.setUniformi("u_texture", 0);			
			    
	 		for (Section s : sectionsInView) {	   	 
	 			if (s.needsCorruptionMeshUpdate(layer) == true) {		
	 				s.getCorruptionMesh(layer).setVertices(s.getCorruptionVerts(layer));
			    	s.getCorruptionMesh(layer).setIndices(s.getCorruptionIndices(layer));
	 				s.updatedCorruptionMesh(layer);
	 			}
	 			//float f = layer % 2 == 0 ? 1f : 0f;
	 			//Shaders.testShader.setUniformf("shade", 1f - (float)layer/(float)Base.MAX_CREEP);
	 			s.getCorruptionMesh(layer).render(Shaders.corruptionShader, GL20.GL_TRIANGLES);
	 		}	 	 		
	 		Shaders.corruptionShader.end();
	 		Gdx.gl.glDisable(GL20.GL_BLEND);
	 		//GameScreen.corrBuffers.get(layer).end();	
	 		//GameScreen.blurBuffer(GameScreen.corrBuffers.get(layer), GameScreen.blurBuffer, GameScreen.corrBuffers.get(layer).getColorBufferTexture(), 0, 0);
	}
	
	/**Generates and initializes a terrain or a corruption mesh. Should be only called once 
	 * and the initialized mesh than can get updated using the the {@link #generateMeshVertexData(Section, boolean, int)} method.
	 * @param s The assigned section.
	 * @param corr Whether a terrain or a corruption mesh should be generated
	 * @param level The layer of the corruption mesh (In case of corruption mesh generation)
	 * @return Returns the generated Mesh object*/
	public Mesh generateMesh(Section s, boolean corr, int level) {		
	    int numberOfRectangles = Base.SECTION_SIZE*Base.SECTION_SIZE;
	    int numberOfVertices = 4 * numberOfRectangles;
	    
	    MeshVertexData newMeshData = generateVertexArrays(s, corr, level, numberOfRectangles, numberOfVertices);
	    
	    Mesh mesh = new Mesh(false, numberOfVertices, numberOfRectangles * 6, 
				new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "1"),
				new VertexAttribute(Usage.Generic, 1, "a_shade"));
	    
	    
	    mesh.setVertices(newMeshData.getVerts());
	    mesh.setIndices(newMeshData.getIndices());
	    
	    if (corr) {
	    	s.updateCorruptionMesh(level, newMeshData.getVerts(), newMeshData.getIndices());
	    } else {
	    	s.updateTerrainMesh(newMeshData.getVerts(), newMeshData.getIndices());
	    }
	    
	    return mesh;
	}
	
	/**Returns an updated {@link MeshVertexData} object used to update the {@link Section} meshes.
	 * @param s The assigned section.
	 * @param corr Whether a terrain or a corruption mesh should be generated
	 * @param level The layer of the corruption mesh (In case of corruption mesh generation)
	 * @return Updated {@link MeshVertexData} object.*/
	public MeshVertexData generateMeshVertexData(Section s, boolean corr, int level) {	
		int numberOfRectangles = Base.SECTION_SIZE*Base.SECTION_SIZE;
	    int numberOfVertices = 4 * numberOfRectangles;
	    return generateVertexArrays(s, corr, level, numberOfRectangles, numberOfVertices);
	}

	/**Generates the vertices and indices arrays for a Mesh with 6 vertex attributes
	 * @param s The assigned section.
	 * @param corr Whether a terrain or a corruption mesh should b e generated
	 * @param level The layer of the corruption mesh (In case of corruption mesh generation)
	 * @param numberOfRectangles Number of rectangles in the vertex array
	 * @param numberOfVertices Number of vertices in the vertex array
	 * @return Updated {@link MeshVertexData} object.
	 * */
	private MeshVertexData generateVertexArrays(Section s, boolean corr, int level, int numberOfRectangles, int numberOfVertices) {			      			
			int vertexPositionValue = 2; //x,y position values
			int vertexColorValue = 1; //A single packed color value
		    int vertexTexCordsValue = 4; //u,v texture1 and 2 coordinates
		    int vertexShadeValue = 1; //Custom value representing shade of corruption
		    
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

		  	        //Calculating the shade factor based on current chunk height
		  	        if (c.terrainEdge && c.getAbsoluteCreeperLevel() == 0) {
		  	        	
		  	        }
		  	        float shade = 1 - (0.9f * ((level - c.c_height)/(float)Base.MAX_CREEP));		  	     
		  	        
		  	        //Two texture regions and texture coordinates are used for mixing multiple texture variants using a shader
		  	        //Currently used when rendering terrain (eg. terrainShader)
		  	      	
		  	      	//Texture 1 texture coordinates
		  	      	float u = 0;
		  	        float v = 0;
		  	        float u2 = 1;
		  	        float v2 = 1;
		  	        
		  	        //Texture 2 texture coordinates
		  	        float tu;
		  	        float tv;
		  	        float tu2;
		  	        float tv2;

		  	        AtlasRegionContainer tC;
		  	        if (corr) {
		  	        	tC = c.getCorruptionTexture(level);
		  	        } else {
		  	        	tC = c.getTerrainTexture();
		  	        }
		  	           
		  	        if (tC == null) {
		  	        	continue;
		  	        }
		  	        if (tC.getTexture(0) != null) {  
			  	        /*u = t.getU();
			  	        v = t.getV();
			  	        u2 = t.getU2();
			  	        v2 = t.getV2();
			  	          
					    float width = u2 - u;
					    //float height = v2 - v;
			  	        float fix = width * 0.1f;
				        float nU = (u + fix);
				        float nV = (v + fix);
				        float nU2 = (u2 - fix);
				        float nV2 = (v2 - fix);
				        
				        u = nU;
				        v = nV;
				        u2 = nU2;
				        v2 = nV2;
				  	    */
				  	        
			  	        float f = 0;
			  	        if (corr) {
			  	        	f = Color.toFloatBits(1f, 1f, 1f, 1f);
			  	        } else {
			  	        	f = Color.toFloatBits(1f, 1f, 1f, 1f);
			  	        }
		  	       
			  	        //First set of texture coordinates for the texture1
			  	        u = tC.getTexture(0).getU();
			  	      	v = tC.getTexture(0).getV();
			  	        u2 = tC.getTexture(0).getU2();
			  	        v2 = tC.getTexture(0).getV2();
			  	        
			  	        if (tC.getTexture(1) != null) {
			  	        	tu = tC.getTexture(1).getU();  
			  	        	tv = tC.getTexture(1).getV();  
			  	        	tu2 = tC.getTexture(1).getU2();
			  	        	tv2 = tC.getTexture(1).getV2();
			  	        } else {
			  	        	tu = 0;
			  	        	tv = 0;
			  	        	tu2 = 0;
			  	        	tv2 = 0;
			  	        }
			  	        
			  	     	setValuesInArrayForVertex(verticesWithColor, u, v2, tu, tv2, tileX, tileY, f, shade, rectangleOffsetInArray, 0);
			  	        setValuesInArrayForVertex(verticesWithColor, u2, v2, tu2, tv2, tileX + Base.CHUNK_SIZE, tileY, f, shade, rectangleOffsetInArray, 1);
			  	        setValuesInArrayForVertex(verticesWithColor, u2, v, tu2, tv, tileX + Base.CHUNK_SIZE, tileY + Base.CHUNK_SIZE, f, shade, rectangleOffsetInArray, 2);
			  	        setValuesInArrayForVertex(verticesWithColor, u, v, tu, tv, tileX, tileY + Base.CHUNK_SIZE, f, shade, rectangleOffsetInArray, 3);			  	       
			  	        
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
	 * @param u U texture1 coordinate
	 * @param v V texture1 coordinate
	 * @param tu U texture2 coordinate
	 * @param tv V texture2 coordinate
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param c The packed color that can be used with shaders
	 * @param shade A special shade attribute represented as "a_shade" in shaders and which is used to tint corruption mesh tiles
	 * @param rectangeOffsetInArray Index of the current position in the array
	 * @param vertexNumberInRect Rectangle (tile) vertex which attributes are being set
	 * */
	private void setValuesInArrayForVertex(float[] vertices, float u, float v, float tu, float tv, float x, float y, float c, float shade, int rectangleOffsetInArray, int vertexNumberInRect) {
	    int valuesPerVertex = 8; //Constant representing the number of individual attributes (x,y,c,u,v,tu,tv,shade)
		int vertexOffsetInArray = rectangleOffsetInArray + vertexNumberInRect * valuesPerVertex; 

	    // x position
	    vertices[vertexOffsetInArray + 0] = x;
	    // y position
	    vertices[vertexOffsetInArray + 1] = y;
	    // color 
	    vertices[vertexOffsetInArray + 2] = c;  	 	
	    // u texture1 coord
	    vertices[vertexOffsetInArray + 3] = u;
	    // v texture1 coord
	    vertices[vertexOffsetInArray + 4] = v;
	    // tu texture2 coord
	    vertices[vertexOffsetInArray + 5] = tu;
	    // tv texture2 coord
	    vertices[vertexOffsetInArray + 6] = tv;
	    // shade
	    vertices[vertexOffsetInArray + 7] = shade;
	}
	
	public void setCoalLevel(float level, int x, int y) {
		chunks[x][y].setCoalLevel(level);
	}
	
	public void setIronLevel(float level, int x, int y) {
		chunks[x][y].setIronLevel(level);
	}
	
	public Chunk getChunk(int x, int y) {
		if (x < 0 || y < 0 || x > Base.CHUNK_AMOUNT-1 || y > Base.CHUNK_AMOUNT-1) {
			return null;
		}
		return chunks[x][y];
	}
	
	public void setChunk(int x, int y, Chunk chunk) {
		chunks[x][y] = chunk;
	}
	
	/**Generates all the {@link Section}s and initializes their terrain and corruption meshes */
	public void generateSections() {
		for (int y = 0; y < Base.CHUNK_AMOUNT; y+=Base.SECTION_SIZE) {
			for (int x = 0; x < Base.CHUNK_AMOUNT; x+=Base.SECTION_SIZE) {
				Chunk[][] sectionChunks = new Chunk[Base.SECTION_SIZE][Base.SECTION_SIZE];
				for(int y1 = 0; y1 < Base.SECTION_SIZE; y1++) {
					for (int x1 = 0; x1 < Base.SECTION_SIZE; x1++) {
						sectionChunks[x1][y1] = chunks[x + x1][y + y1]; 
					}
				}

				Section s = new Section(sectionChunks);
				s.terrainMesh = generateMesh(s, false, 0);
				
				for (int i = 0; i < Base.MAX_CREEP; i++) {
					s.corrMeshes.set(i, generateMesh(s, true, i));
				}
				
				sections[x/Base.SECTION_SIZE][y/Base.SECTION_SIZE] = s;				
			}
		}
	}
	
	/**Generates the terrain relief and ores.
	 * @return Returns a {@link TextureContainer} that holds gray-scale pixmaps of generated maps.<br>
	 * Textures are in the following order: 1. terrain height map, 2. coal map, 3. iron map
	 * */
	public TextureContainer generateAll() {
		SimplexNoiseGenerator sn = new SimplexNoiseGenerator();
		System.out.println("\nGenerating noise (1/3)");
		float[][] coalMap = sn.generateOctavedSimplexNoise(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, 4, 0.45f, 0.018f);
		sn.randomizeMutatorTable();
		System.out.println("Generating noise (2/3)");
		float[][] ironMap = sn.generateOctavedSimplexNoise(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, 4, 0.45f, 0.018f);
		sn.randomizeMutatorTable();
		System.out.println("Generating noise (3/3)");
		float[][] heightMap = sn.generateOctavedSimplexNoise(Base.CHUNK_AMOUNT, Base.CHUNK_AMOUNT, 4, 0.5f, 0.012f);
		
		System.out.println("Creating pixmaps");
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
		System.out.println("Generating chunks (n. " + Base.CHUNK_AMOUNT*Base.CHUNK_AMOUNT + ")");
		System.out.println("Generating terrain heights");
		heightPixmap = generateHeights(heightPixmap);
		System.out.println("Generating coal ore");
		coalPixmap = generateCoalPatches(coalPixmap);
		System.out.println("Generating iron ore");
		ironPixmap = generateIronPatches(ironPixmap);
		generated = true;
				
		return new TextureContainer(new Texture(heightPixmap), new Texture(coalPixmap), new Texture(ironPixmap));
	}
	
	public Pixmap generateCoalPatches(Pixmap pixmap) {
		Pixmap patchPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Format.RGBA8888);
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {
				Color c = new Color(pixmap.getPixel(x, y));				
				if (c.r > Base.COAL_THRESHOLD) {
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
				if (c.r > Base.IRON_THRESHOLD) {
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
				int val = (int) Base.range(c.r, 0, 1f, 0, Base.MAX_CREEP);
				chunks[x][y].setHeight(val);
			}
		}
		return patchPixmap;
	}
}

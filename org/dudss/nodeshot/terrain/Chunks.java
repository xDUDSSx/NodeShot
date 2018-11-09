package org.dudss.nodeshot.terrain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.algorithms.SimplexNoiseGenerator;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.datasubsets.MeshVertexData;
import org.dudss.nodeshot.utils.Shaders;
import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;

public class Chunks {
	
	Chunk[][] chunks;
	public Section[][] sections;
	
	/**All {@link Section} instances that are currently visible by the camera.
	 * Updated by the {@link #updateView(OrthographicCamera)} method.
	 */
	public List<Section> sectionsInView;
	
	/**Time of the last {@link #updateView(OrthographicCamera)}, used to prevent unnecessary view updates*/
	public long lastViewPoll = System.currentTimeMillis();
	/**Minimum delay(ms) inbetween {@link #updateView(OrthographicCamera)} calls*/
	public long pollRate = 2;
	
	public boolean created = false;
	public boolean generated = false;
	
	Color transparent = new Color(0, 0, 0, 0.0f);
	
	public enum OreType {
		COAL, IRON, NONE
	}
	
	Rectangle viewBounds;
	Rectangle imageBounds = new Rectangle();
	
	//No viewport set, needs to have updateCam() called
	public void create() {		
		chunks = new Chunk[Base.CHUNK_AMOUNT][Base.CHUNK_AMOUNT];		
		sections = new Section[Base.CHUNK_AMOUNT/Base.SECTION_SIZE][Base.CHUNK_AMOUNT/Base.SECTION_SIZE];
		sectionsInView = new CopyOnWriteArrayList<Section>();
		
		//Initializing chunks
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				chunks[x][y] = new Chunk(x * Base.CHUNK_SIZE, y * Base.CHUNK_SIZE);
			}
		}
		
		//Setting each chunk their neighbours, this way they dont have to initialized and then dumped at runtime
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
	
	public void create(OrthographicCamera cam) {		
		create();		
		updateView(cam);	
	}
	
	public void updateChunk(Chunk ch) {
		ch.update();
	}
	
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
			SpriteLoader.tileAtlas.findRegion("tiledCoal").getTexture().bind();   
		    Shaders.defaultShader.begin();
		    Shaders.defaultShader.setUniformMatrix("u_projTrans", GameScreen.cam.combined);
		    Shaders.defaultShader.setUniformi("u_texture", 0);
		    for (Section s : sectionsInView) {
		    	if (s.needsTerrainUpdate() == true) {		
		    		s.getTerrainMesh().setVertices(s.getTerrainVerts());
			    	s.getTerrainMesh().setIndices(s.getTerrainIndices());	 
	 				s.updatedTerrain();
	 			}
		    	s.getTerrainMesh().render(Shaders.defaultShader, GL20.GL_TRIANGLES);
		    }
		    Shaders.defaultShader.end();	
	}	

	public void drawCorruption(int layer) {
		 	Gdx.gl.glEnable(GL20.GL_BLEND);
	        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);	      	       			
		    GameScreen.corrBuffers.get(layer).begin();
	        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	 		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	 		SpriteLoader.tileAtlas.findRegion("tiledCoal").getTexture().bind();   
	 		 		
			Shaders.testShader.begin();
			Shaders.testShader.setUniformMatrix("u_projTrans", GameScreen.cam.combined);
			Shaders.testShader.setUniformi("u_texture", 0);			
			    
	 		for (Section s : sectionsInView) {	   	 
	 			if (s.needsCorruptionMeshUpdate(layer) == true) {		
	 				s.getCorruptionMesh(layer).setVertices(s.getCorruptionVerts(layer));
			    	s.getCorruptionMesh(layer).setIndices(s.getCorruptionIndices(layer));
	 				s.updatedCorruptionMesh(layer);
	 			}
	 			Shaders.testShader.setUniformf("shade", 1f - (0.5f * ((float)(layer + 1) / (Base.MAX_CREEP + 1))));
		    	s.getCorruptionMesh(layer).render(Shaders.testShader, GL20.GL_TRIANGLES);
	 		}	 	 		
	 		Shaders.testShader.end();
	 		
	 		GameScreen.corrBuffers.get(layer).end();	
	 		GameScreen.blurBuffer(GameScreen.corrBuffers.get(layer), GameScreen.blurBuffer, GameScreen.corrBuffers.get(layer).getColorBufferTexture(), 0, 0);
	}
	
	/**Generates and initializes a terrain or a corruption mesh. Should be only called once 
	 * and the initialized meshes than can get updated using the the {@link #generateMeshVertexData(Section, boolean, int)} method.
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
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));
	    
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

	/**Generates the vertices and indices arrays
	 * @param s The assigned section.
	 * @param corr Whether a terrain or a corruption mesh should b e generated
	 * @param level The layer of the corruption mesh (In case of corruption mesh generation)
	 * @param numberOfRectangles Number of rectangles in the vertex array
	 * @param numberOfVertices Number of vertices in the vertex array
	 * @return Updated {@link MeshVertexData} object.
	 * */
	private MeshVertexData generateVertexArrays(Section s, boolean corr, int level, int numberOfRectangles, int numberOfVertices) {			      			
			int vertexPositionValue = 2;
			int vertexColorValue = 1;
		    int vertexTexCordsValue = 2;
		    
		    int valuesPerVertex = vertexPositionValue + vertexColorValue + vertexTexCordsValue;

		    short[] vertexIndices = new short[numberOfRectangles * 6];
		    float[] verticesWithColor = new float[numberOfVertices * valuesPerVertex];

		    int i = 0;
		    
		    int nullTiles = 0;
		    
		    for (int y = 0; y < Math.sqrt(numberOfRectangles); y++) {
		    	 for (int x = 0; x < Math.sqrt(numberOfRectangles); x++) {
		    		Chunk c = s.sectionChunks[x][y];
		    		 
					float tileX = c.getX();
					float tileY = c.getY();						
					
		  	        int rectangleOffsetInArray = i * valuesPerVertex * 4;  	        
		  	        
		  	        float u = 0;
		  	        float v = 0;
		  	        float u2 = 1;
		  	        float v2 = 1;
		  	        
		  	      	AtlasRegion t = null;
		  	        if (corr) {
		  	        	t = c.getCorruptionTexture(level);
		  	        } else {
		  	        	t = c.getAppropriateTexture(corr);
		  	        }
		  	           
		  	        if (t == null && corr) {  
		  	        	nullTiles++;
		  	        } else {
			  	        u = t.getU();
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
				        
			  	        float f = 0;
			  	        if (corr) {
			  	        	//float tint = 1.0f - (Base.range((int)(c.getCreeperLevel()), 0f, Base.MAX_CREEP, 0f, 0.6f));
			  	        	//f = Color.toFloatBits(tint, tint, tint, 0.9f);
			  	        	f = Color.toFloatBits(1f, 1f, 1f, 0.95f);
			  	        } else {
			  	        	f = Color.toFloatBits(1f, 1f, 1f, 1f);
			  	        }

		  	       
			  	        u = t.getU();
			  	      	v = t.getV();
			  	        u2 = t.getU2();
			  	        v2 = t.getV2();
			  	        
			  	        setValuesInArrayForVertex(verticesWithColor, u, v, tileX, tileY, f, rectangleOffsetInArray, 0);
			  	        setValuesInArrayForVertex(verticesWithColor, u2, v, tileX + Base.CHUNK_SIZE, tileY, f, rectangleOffsetInArray, 1);
			  	        setValuesInArrayForVertex(verticesWithColor, u2, v2, tileX + Base.CHUNK_SIZE, tileY + Base.CHUNK_SIZE, f, rectangleOffsetInArray, 2);
			  	        setValuesInArrayForVertex(verticesWithColor, u, v2, tileX, tileY + Base.CHUNK_SIZE, f, rectangleOffsetInArray, 3);
			  	        
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
		    
		    for (int y = 0; y < Math.sqrt(numberOfRectangles); y++) {
		    	for (int x = 0; x < Math.sqrt(numberOfRectangles); x++) {
		    		s.sectionChunks[x][y].updateEdges();
		    	}
		    }
		    
		    if (nullTiles > 0) {
		    	//float[] newVerts = new float[1];
		    	//TODO: implement verts array shortening to remove the not initialized vertexes of missing grid tiles, 
		    	//System.arraycopy(src, srcPos, dest, destPos, length);
		    }
		    return new MeshVertexData(verticesWithColor, vertexIndices);
	}
	
	private void setValuesInArrayForVertex(float[] vertices, float u, float v, float x, float y, float c, int rectangleOffsetInArray, int vertexNumberInRect) {
	    int vertexOffsetInArray = rectangleOffsetInArray + vertexNumberInRect * 5;

	    // x position
	    vertices[vertexOffsetInArray + 0] = x;
	    // y position
	    vertices[vertexOffsetInArray + 1] = y;
	    // color 
	    vertices[vertexOffsetInArray + 2] = c;  	    
	    // u texture coord
	    vertices[vertexOffsetInArray + 3] = u;
	    // v texture coord
	    vertices[vertexOffsetInArray + 4] = v;
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
	
	public void generateAll() {
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
		System.out.println("Generating sections");
		generateSections();
		generated = true;
	}
	
	public Pixmap generateCoalPatches(Pixmap pixmap) {
		Pixmap patchPixmap = new Pixmap(pixmap.getWidth(), pixmap.getHeight(), Format.RGBA8888);
		for (int x = 0; x < pixmap.getWidth(); x++) {
			for (int y = 0; y < pixmap.getHeight(); y++) {
				Color c = new Color(pixmap.getPixel(x, y));				
				if (c.r > Base.COAL_THRESHOLD) {
					float level = Base.range(c.r, Base.COAL_THRESHOLD, 1f, 0.2f, 1.0f);
					chunks[x][y].setCoalLevel(level);
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
				//if (c.r > 0.1f) {
					//float height = Base.range(c.r, Base.TERRAIN_THRESHOLD, 1f, 0.1f, 1.0f);
					int val = (int) (c.r / 0.2f);
					if (val > 4) {
						val = 4;
					} else if (val < 0) {
						val = 0;
					}
					chunks[x][y].setHeight(val);
					
				/*} else {
					chunks[x][y].setHeight(-1);
				}*/
			}
		}
		return patchPixmap;
	}
}

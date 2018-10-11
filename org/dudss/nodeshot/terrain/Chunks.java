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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;

public class Chunks {
	
	Chunk[][] chunks;
	public Section[][] sections;
	
	/**All Section instances that are currently visible by the camera.
	 * Updated by the updateView() method.
	 */
	public List<Section> sectionsInView;
	
	/**Time of the last updateView(), used to prevent unnecessary view updates*/
	public long lastViewPoll = System.currentTimeMillis();
	/**Minimum delay(ms) inbetween updateView() calls*/
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

	public void updateSectionMesh(Section s, boolean corr, int layer) {      
    	if (!corr) {
        	MeshVertexData mvdTerrain = this.generateMeshVertexData(s, false, 0);
        	s.updateTerrainMesh(mvdTerrain.getVerts(), mvdTerrain.getIndices());
    	} else {
    		if (layer == -1) {
    			for (int i = 0; i < 10; i++) {
    				MeshVertexData mvdCorruption = this.generateMeshVertexData(s, true, i);
    	        	s.updateCorruptionMesh(i, mvdCorruption.getVerts(), mvdCorruption.getIndices());    	
    			}
    		} else {
	        	MeshVertexData mvdCorruption = this.generateMeshVertexData(s, true, layer);
	        	s.updateCorruptionMesh(layer, mvdCorruption.getVerts(), mvdCorruption.getIndices());    
    		}
    	}
	}
	
	/*public void updateSectionMeshes(Section s, boolean corr, int layer) {      
    	if (!corr) {
        	MeshVertexData mvdTerrain = this.generateMeshVertexData(s, false, 0);
        	s.updateTerrainMesh(mvdTerrain.getVerts(), mvdTerrain.getIndices());
    	} else {
    		if (layer == -1) {
    			for (int i = 0; i < 10; i++) {
    				MeshVertexData mvdCorruption = this.generateMeshVertexData(s, true, i);
    	        	s.updateCorruptionMesh(i, mvdCorruption.getVerts(), mvdCorruption.getIndices());    	
    			}
    		} else {
	        	MeshVertexData mvdCorruption = this.generateMeshVertexData(s, true, layer);
	        	s.updateCorruptionMesh(layer, mvdCorruption.getVerts(), mvdCorruption.getIndices());    
    		}
    	}
	}*/
	
	public void updateAllChunks() {
		for (int x = 0; x < Base.CHUNK_AMOUNT; x++) {
			for (int y = 0; y < Base.CHUNK_AMOUNT; y++) {
				chunks[x][y].update();
			}
		}
	}
				
	public void drawTerrain() {
			SpriteLoader.tileAtlas.findRegion("tiledCoal").getTexture().bind();   
		    Shaders.defaultShader.begin();
		    Shaders.defaultShader.setUniformMatrix("u_projTrans", GameScreen.cam.combined);
		    Shaders.defaultShader.setUniformi("u_texture", 0);
		    for (Section s : sectionsInView) {
		    	s.getTerrainMesh().setVertices(s.getTerrainVerts());
		    	s.getTerrainMesh().setIndices(s.getTerrainIndices());	    	
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
		    Shaders.defaultShader.begin();
		    Shaders.defaultShader.setUniformMatrix("u_projTrans", GameScreen.cam.combined);
		    Shaders.defaultShader.setUniformi("u_texture", 0);
	 		for (Section s : sectionsInView) {	   	 			
		    	s.getCorruptionMesh(layer).setVertices(s.getCorruptionVerts(layer));
		    	s.getCorruptionMesh(layer).setIndices(s.getCorruptionIndices(layer));
		    	s.getCorruptionMesh(layer).render(Shaders.defaultShader, GL20.GL_TRIANGLES);
	 		}
	 		Shaders.defaultShader.end();
	 		GameScreen.corrBuffers.get(layer).end();	
	 		GameScreen.blurBuffer(GameScreen.corrBuffers.get(layer), GameScreen.blurBuffer, GameScreen.corrBuffers.get(layer).getColorBufferTexture(), 0, 0);
			Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	@Deprecated
	public void drawCorruption() {
	 	Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
       			
        SpriteLoader.tileAtlas.findRegion("tiledCoal").getTexture().bind();   
        for (int i = 0; i < 10; i++) {
		    GameScreen.corrBuffers.get(i).begin();
	        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	 		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	 				
	 		Shaders.defaultShader.begin();
		    Shaders.defaultShader.setUniformMatrix("u_projTrans", GameScreen.cam.combined);
		    Shaders.defaultShader.setUniformi("u_texture", 0);
	 		for (Section s : sectionsInView) {	   
	 			s.getCorruptionMesh(i).setVertices(s.getCorruptionVerts(i));
		    	s.getCorruptionMesh(i).setIndices(s.getCorruptionIndices(i));
		    	s.getCorruptionMesh(i).render(Shaders.defaultShader, GL20.GL_TRIANGLES);
	 		}
	        
	 		Shaders.defaultShader.end();
	 		GameScreen.corrBuffers.get(i).end();	
	 		GameScreen.blurBuffer(GameScreen.corrBuffers.get(i), GameScreen.blurBuffer, GameScreen.corrBuffers.get(i).getColorBufferTexture(), 0, 0);
	        }
		Gdx.gl.glDisable(GL20.GL_BLEND);
}
	
	/*public void drawTestCorruption() {
	 	Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
       			
	    GameScreen.testBuffer.begin();
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
 		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
 		SpriteLoader.tileAtlas.findRegion("tiledCoal").getTexture().bind();   
	    Shaders.defaultShader.begin();
	    Shaders.defaultShader.setUniformMatrix("u_projTrans", GameScreen.cam.combined);
	    Shaders.defaultShader.setUniformi("u_texture", 0);
 		for (Section s : sectionsInView) {	    	
	    	s.getTestMesh().setVertices(s.getTestVerts());
	    	s.getTestMesh().setIndices(s.getTestIndices());
	    	s.getTestMesh().render(Shaders.defaultShader, GL20.GL_TRIANGLES);
	    }
 		Shaders.defaultShader.end();
 		GameScreen.testBuffer.end();	
 		GameScreen.blurBuffer(GameScreen.testBuffer, GameScreen.blurBuffer, GameScreen.testBuffer.getColorBufferTexture(), 0, 0);
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	*/
	
	public Mesh generateMesh(Section s, boolean corr, int level) {		
	    int numberOfRectangles = Base.SECTION_SIZE*Base.SECTION_SIZE;
	    int numberOfVertices = 4 * numberOfRectangles;
	    Mesh mesh = new Mesh(true, numberOfVertices, numberOfRectangles * 6, 
				new VertexAttribute(Usage.Position, 2, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute(Usage.ColorPacked, 4, ShaderProgram.COLOR_ATTRIBUTE),
				new VertexAttribute(Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

	    int vertexPositionValue = 2;
	    int vertexColorValue = 1;
	    int vertexTexCordsValue = 2;
	    
	    int valuesPerVertex = vertexPositionValue + vertexColorValue + vertexTexCordsValue;

	    short[] vertexIndices = new short[numberOfRectangles * 6];
	    float[] verticesWithColor = new float[numberOfVertices * valuesPerVertex];

	    int i = 0;
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
	  	        
	  	        TextureRegion t = null;
	  	        if (corr) {
	  	        	t = c.getCorruptionTexture(level);
	  	        } else {
	  	        	t = c.getAppropriateTexture(corr);
	  	        }
	  	        
	  	        u = t.getU();
	  	        v = t.getV();
	  	        u2 = t.getU2();
	  	        v2 = t.getV2();
	  	        
	  	        float f = 0;
	  	        if (corr) {
	  	        	f = Color.toFloatBits(1f, 1f, 1f, 0.75f);
	  	        } else {
	  	        	f = Color.toFloatBits(1f, 1f, 1f, 1f);
	  	        }

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
	    mesh.setVertices(verticesWithColor);
	    mesh.setIndices(vertexIndices);
	    if (corr) {
	    	s.updateCorruptionMesh(level, verticesWithColor, vertexIndices);
	    } else {
	    	s.updateTerrainMesh(verticesWithColor, vertexIndices);
	    }
	    
	    return mesh;
	}
	
	public MeshVertexData generateMeshVertexData(Section s, boolean corr, int level) {		
	    int numberOfRectangles = Base.SECTION_SIZE*Base.SECTION_SIZE;	       
	    int numberOfVertices = 4 * numberOfRectangles;
	    int vertexPositionValue = 2;
	    int vertexColorValue = 1;
	    int vertexTexCordsValue = 2;
	    
	    int valuesPerVertex = vertexPositionValue + vertexColorValue + vertexTexCordsValue;

	    short[] vertexIndices = new short[numberOfRectangles * 6];
	    float[] verticesWithColor = new float[numberOfVertices * valuesPerVertex];

	    int i = 0;
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
	  	        	f = Color.toFloatBits(1f, 1f, 1f, 0.9f);
	  	        } else {
	  	        	f = Color.toFloatBits(1f, 1f, 1f, 1f);
	  	        }

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
	    
	    return new MeshVertexData(verticesWithColor, vertexIndices);
	}

	private void setValuesInArrayForVertex(float[] verticesWithColor, float u, float v, float x, float y, float c, int rectangleOffsetInArray, int vertexNumberInRect) {
	    int vertexOffsetInArray = rectangleOffsetInArray + vertexNumberInRect * 5;

	    //Can use this to implement fog of war
	    /*switch (vertexNumberInRect) {
	    	case 0:
	    	case 1: c = Color.toFloatBits(1f, 1f, 1f, 1f); break; 
	    	case 2:  
	    	case 3: c = Color.toFloatBits(0.5f, 0.5f, 0.5f, 1f);break;
	    }*/
	    // x position
	    verticesWithColor[vertexOffsetInArray + 0] = x;
	    // y position
	    verticesWithColor[vertexOffsetInArray + 1] = y;
	    // color 
	    verticesWithColor[vertexOffsetInArray + 2] = c;  	    
	    // u texture coord
	    verticesWithColor[vertexOffsetInArray + 3] = u;
	    // v texture coord
	    verticesWithColor[vertexOffsetInArray + 4] = v;
	}

	private AtlasRegion fixBleeding(AtlasRegion region) {
	        float fix = 0.05f;
	        /*float x = region.getRegionX();
	        float y = region.getRegionY();
	        float width = region.getRegionWidth();
	        float height = region.getRegionHeight();
	        float invTexWidth = 1f / region.getRegionWidth();
	        float invTexHeight = 1f / region.getRegionHeight();
	        
	        //System.out.println("x: " + x + " y: " + y + " regwidth: " + width + " regheight: " + height + " texWidth: " + region.getTexture().getWidth() + " texHeight: " + region.getTexture().getHeight() + " invTexWi: " + invTexWidth + " invTexHeight: " + invTexHeight);
	        
	        region.setRegion((x + fix) * invTexWidth,
	                (y + fix) * invTexHeight,
	                (x + width - fix) * invTexWidth,
	                (y + height - fix) * invTexHeight);
	        return region;
	        */
	        float u = region.getU();
	        float v = region.getV();
	        float u2 = region.getU2();
	        float v2 = region.getV2();
	        float width = u2 - u;
	        float height = v2 - v;
	        //float invTexWidth = 1f / region.getRegionWidth();
	        //float invTexHeight = 1f / region.getRegionHeight();
	        
	        System.out.println("u: " + u + " v: " + v + " u2: " + u2 + " v2: " + v2 + " regwidth: " + width + " regheight: " + height + " texWidth: " + region.getTexture().getWidth() + " texHeight: " + region.getTexture().getHeight());
	        
	        region.setRegion((u + fix),
	                (v + fix),
	                (u2 - fix),
	                (v2 - fix));
	                
	        return region;
			
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
				
				for (int i = 0; i < 10; i++) {
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
	    		//float newVal = Base.range(heightMap[x2][y2], -1.4f, 1.4f, -1.0f, 1.0f);    
	    		
	    	    //Converting [-1.0,1.0] to [0,1]
	    	    float val = (((heightMap[x2][y2] - (-1.0f)) * (1.0f - 0)) / (1.0f - (-1.0f))) + 0;	    	    
	    	    heightPixmap.setColor(Color.rgba8888(val, val, val, 1.0f));
	    	    heightPixmap.drawPixel(x2, y2);
	   		}
	   	}

		//CHUNK GEN
		System.out.println("Generating chunks (n. " + Base.CHUNK_AMOUNT*Base.CHUNK_AMOUNT + ")");
		System.out.println("Generating terrain heights");
		ironPixmap = generateHeights(heightPixmap);
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
				//System.out.println("Color val: " + c.r);
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
				//System.out.println("Color val: " + c.r);				
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
				//System.out.println("Color val: " + c.r);
				if (c.r > 0.50f) {
					float height = Base.range(c.r, Base.TERRAIN_THRESHOLD, 1f, 0.1f, 1.0f);
					int val = (int)(height/0.2f);
					if (val > 3) {
						val = 3;
					} else if (val < 0) {
						val = 0;
					}
					chunks[x][y].setHeight(val);
					
				} else {
					chunks[x][y].setHeight(0);
				}
			}
		}
		return patchPixmap;
	}
}

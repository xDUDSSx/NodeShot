package org.dudss.nodeshot.terrain;

import java.util.HashSet;
import java.util.Set;

import org.dudss.nodeshot.buildings.CreeperGenerator;
import org.dudss.nodeshot.misc.ChunkOperation;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;

/**A helper class that manages some terrain operations.*/
public class TerrainEditor {

	public static int terrainLayerSelected = 2;
	public static int terrainBrushSize = 3;
	
	/**Sets the terrain to the editors {@link #terrainLayerSelected} around a world-space point in a {@link TerrainEditor#terrainBrushSize} diameter.*/
	public void setTerrain(Vector3 at) {
		Set<Section> sections = new HashSet<Section>();
		
		GameScreen.chunks.getChunksAroundWorldSpacePoint(at.x, at.y, TerrainEditor.terrainBrushSize, new ChunkOperation() {
			@Override
			public void execute(Chunk c, double dist, double boundary) {
				c.setHeight(TerrainEditor.terrainLayerSelected);
				sections.add(c.getSection());
			}
			
		});
	
		for (Section s : sections) {
			GameScreen.chunks.updateSectionMesh(s, false);
		}					
	}
		
	/**
	 * Creates a crater in the terrain.
	 * @param at The world-space coordinates of the crater.
	 * @param diameter The tile-space size of the crater. 
	 */
	public void explosion(Vector3 at, float diameter) {
		Set<Section> sections = new HashSet<Section>();
		Chunk c = GameScreen.chunks.getChunkAtWorldSpace(at.x, at.y);
		if (c != null) {		
			GameScreen.chunks.getChunksAroundWorldSpacePoint(at.x, at.y, diameter, new ChunkOperation() {
				@Override
				public void execute(Chunk c, double dist, double boundary) {
					float per = 1f - (float)(dist/boundary);			
					int hdiff = (int)(4f*Interpolation.fade.apply(per));	
					c.setHeight((int)c.getHeight() - (int) (hdiff * 0.75f));
					if (c.creeper > 0) {
						c.setCreeperLevel(c.getCreeperLevel() - (hdiff) );
					}
					sections.add(c.getSection());
				}
				
			});
		
			for (Section s : sections) {
				GameScreen.chunks.updateSectionMesh(s, false);
			}				
		}
	}
	
	/**
	 * Creates a crater in the terrain.
	 * @param at The world-space coordinates of the crater.
	 * @param diameter The tile-space size of the crater. 
	 * @param Strenght of the explosion
	 */
	public void creeperExplosion(Vector3 at, float diameter, float power) {
		Set<Section> sections = new HashSet<Section>();
		Chunk c = GameScreen.chunks.getChunkAtWorldSpace(at.x, at.y);
		if (c != null) {		
			GameScreen.chunks.getChunksAroundWorldSpacePoint(at.x, at.y, diameter, new ChunkOperation() {
				@Override
				public void execute(Chunk c, double dist, double boundary) {
					float per = 1f - (float)(dist/boundary);			
					int hdiff = (int)(power*Interpolation.fade.apply(per));	
					if (c.creeper > 0) {
						c.setCreeperLevel(c.getCreeperLevel() - (hdiff) );
					}
					if (c.getBuilding() instanceof CreeperGenerator) {
						((CreeperGenerator)c.getBuilding()).damage += 1;
					}
					sections.add(c.getSection());
				}
				
			});
		
			for (Section s : sections) {
				GameScreen.chunks.updateSectionMesh(s, true);
			}				
		}
	}
	
	
	/**Changes the creeper level of the {@link Chunk}s around a world-space point in a {@link TerrainEditor#terrainBrushSize} diameter.*/
	public void modifyCreeper(Vector3 at, boolean add) {
		Set<Section> sections = new HashSet<Section>();
		
		GameScreen.chunks.getChunksAroundWorldSpacePoint(at.x, at.y, TerrainEditor.terrainBrushSize, new ChunkOperation() {
			@Override
			public void execute(Chunk c, double dist, double boundary) {
				if (add) {
					c.setCreeperLevel(c.getCreeperLevel() + 1);				
				} else {
					c.setCreeperLevel(c.getCreeperLevel() - 1);	
				}
				sections.add(c.getSection());
			}
			
		});
	
		for (Section s : sections) {
			GameScreen.chunks.updateSectionMesh(s, true);
			s.setActive(true);
		}					
	}
}

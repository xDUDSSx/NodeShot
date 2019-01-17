package org.dudss.nodeshot.terrain.datasubsets;

import org.dudss.nodeshot.terrain.Chunk.EdgeType;
import org.dudss.nodeshot.terrain.TerrainEdgeResolver;

/**An object representing a terrain edge. Holding some data used by {@link TerrainEdgeResolver}.*/
public class TerrainEdge {
	public char[] mask;
	public char[] outerEdgesMask;
	public String name;
	public EdgeType type;
	public boolean isTerrainEdge;
	
	public TerrainEdge(String name, EdgeType type, char[] mask, boolean isEdge) {
		this.name = name;
		this.type = type;
		this.mask = mask;
		this.isTerrainEdge = isEdge;
	}
	
	public TerrainEdge(String name, EdgeType type, char[] mask, char[] outerEdgesMask, boolean isEdge) {
		this.name = name;
		this.type = type;
		this.mask = mask;
		this.outerEdgesMask = outerEdgesMask;
		this.isTerrainEdge = isEdge;
	}
}

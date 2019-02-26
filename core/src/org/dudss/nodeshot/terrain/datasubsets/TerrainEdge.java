package org.dudss.nodeshot.terrain.datasubsets;

import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.terrain.Chunk.EdgeType;
import org.dudss.nodeshot.terrain.TerrainEdgeResolver;

/**An object representing a terrain edge. Holding some data used by {@link TerrainEdgeResolver}.*/
public class TerrainEdge {
	public char[] mask;
	public char[] outerEdgesMask;
	public String name;
	public EdgeType type;
	public boolean isTerrainEdge;
	
	/**
	 * An object representing a terrain edge. Holding some data used by {@link TerrainEdgeResolver}.
	 * @param name Name of the edge texture.
	 * @param type Type of the edge.
	 * @param mask An associated mask that represents the {@linkplain EdgeType}.
	 * @param isEdge Whether this edge covers the entire {@link Chunk}.
	 * @see TerrainEdgeResolver
	 */
	public TerrainEdge(String name, EdgeType type, char[] mask, boolean isEdge) {
		this.name = name;
		this.type = type;
		this.mask = mask;
		this.isTerrainEdge = isEdge;
	}
	
	/**
	 * An object representing a terrain edge. Holding some data used by {@link TerrainEdgeResolver}.
	 * @param name Name of the edge texture.
	 * @param type Type of the edge.
	 * @param mask An associated mask that represents the {@linkplain EdgeType}.
	 * @param outerEdgesMask An additional mask that represents the {@linkplain EdgeType} distant neighbours.
	 * @param isEdge Whether this edge covers the entire {@link Chunk}.
	 */
	public TerrainEdge(String name, EdgeType type, char[] mask, char[] outerEdgesMask, boolean isEdge) {
		this.name = name;
		this.type = type;
		this.mask = mask;
		this.outerEdgesMask = outerEdgesMask;
		this.isTerrainEdge = isEdge;
	}
}

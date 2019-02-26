package org.dudss.nodeshot.misc;

import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.terrain.TerrainEditor;

/**A small wrapper object used by {@link TerrainEditor}*/
public abstract class ChunkOperation {
	public abstract void execute(Chunk c, double dist, double boundary);
}

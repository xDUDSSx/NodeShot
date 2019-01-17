package org.dudss.nodeshot.misc;

import org.dudss.nodeshot.terrain.Chunk;

public abstract class ChunkOperation {
	public abstract void execute(Chunk c, double dist, double boundary);
}

package org.dudss.nodeshot.terrain;

/**Wrapper for some utility {@link TerrainEdge} data.*/
public class TerrainEdgeInfo {
	char[] diffs;
	char[] outerDiffs;
	int highestNeighbourLevel;
	
	/**Wrapper for some utility {@link TerrainEdge} data.*/
	TerrainEdgeInfo(char[] diffs, char[] outerDiffs, int highestNeighbourLevel) {
		this.diffs = diffs;
		this.outerDiffs = diffs;
		this.highestNeighbourLevel = highestNeighbourLevel;
	}
	
	public char[] getDiffs() {
		return diffs;
	}
	
	public char[] getOuterDiffs() {
		return outerDiffs;
	}
	
	public int getHighestNeighbourLevel() {
		return highestNeighbourLevel;
	}
}

package org.dudss.nodeshot.terrain;

public class SubSection {
	public Chunk[][] subSectionChunks;
	int size;
	
	public SubSection(Chunk[][] chunks, int size) {
		subSectionChunks = chunks;
		this.size = size;
	}
	
	public boolean isFull() {
		for (int y = 0; y < size; y++) {
			for (int x = 0; x < size; x++) {	
				if (subSectionChunks[x][y].getCreeperLevel() == 0) {
					return false;
				}
			}
		}
		return true;
	}
}

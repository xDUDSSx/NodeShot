package org.dudss.nodeshot.terrain;

import java.util.Arrays;

import org.dudss.nodeshot.terrain.Chunk.EdgeType;
import org.dudss.nodeshot.terrain.datasubsets.TerrainEdge;

/**A static class that provides utility methods for terrain smoothing.
 * Terrain smoothing works by comparing height differences to static masks as well as by using special {@link TerrainEdge} data objects.
 * This prevents hard-coded nested condition (if) trees and makes the code less error-prone and more manageable.
 * @since <b>16.1.2019</b>*/
public class TerrainEdgeResolver {
	public static final TerrainEdge solid = new TerrainEdge("", EdgeType.NONE, new char[]  {'0', '0', '0', '0', '0', '0', '0', '0'}, false);
	
	public static final TerrainEdge single = new TerrainEdge("Single", EdgeType.SINGLE, new char[]  {'1', '?', '1', '?', '1', '?', '1', '?'}, true);
	
	public static final TerrainEdge BL = new TerrainEdge("BL", EdgeType.BOTTOM_LEFT, new char[]  {'1', '?', '1', '?', '0', '?', '0', '?'}, true);
	public static final TerrainEdge TL = new TerrainEdge("TL", EdgeType.TOP_LEFT, new char[] 	 {'0', '?', '1', '?', '1', '?', '0', '?'}, true);
	public static final TerrainEdge TR = new TerrainEdge("TR", EdgeType.TOP_RIGHT, new char[] 	 {'0', '?', '0', '?', '1', '?', '1', '?'}, true);
	public static final TerrainEdge BR = new TerrainEdge("BR", EdgeType.BOTTOM_RIGHT, new char[] {'1', '?', '0', '?', '0', '?', '1', '?'}, true);
	
	public static final TerrainEdge ST = new TerrainEdge("ST", EdgeType.STRAIGHT_TOP, new char[] 	{'1', '?', '0', '?', '0', '?', '0', '?'}, true);
	public static final TerrainEdge SR = new TerrainEdge("SR", EdgeType.STRAIGHT_RIGHT, new char[]  {'0', '?', '1', '?', '0', '?', '0', '?'}, true);
	public static final TerrainEdge SB = new TerrainEdge("SB", EdgeType.STRAIGHT_BOTTOM, new char[]	{'0', '?', '0', '?', '1', '?', '0', '?'}, true);
	public static final TerrainEdge SL = new TerrainEdge("SL", EdgeType.STRAIGHT_LEFT, new char[] 	{'0', '?', '0', '?', '0', '?', '1', '?'}, true);
	
	public static final TerrainEdge TB = new TerrainEdge("TB", EdgeType.END_TOP, new char[] 	{'1', '?', '1', '?', '0', '?', '1', '?'}, true);
	public static final TerrainEdge RB = new TerrainEdge("RB", EdgeType.END_RIGHT, new char[]  {'1', '?', '1', '?', '1', '?', '0', '?'}, true);
	public static final TerrainEdge BB = new TerrainEdge("BB", EdgeType.END_BOTTOM, new char[]	{'0', '?', '1', '?', '1', '?', '1', '?'}, true);
	public static final TerrainEdge LB = new TerrainEdge("LB", EdgeType.END_LEFT, new char[] 	{'1', '?', '0', '?', '1', '?', '1', '?'}, true);
	
	//Fills
	public static final TerrainEdge BL_fill = new TerrainEdge("BL_fill", EdgeType.NONE, new char[]  {'0', '1', '0', '?', '0', '?', '0', '?'}, false);
	public static final TerrainEdge TL_fill = new TerrainEdge("TL_fill", EdgeType.NONE, new char[] 	 {'0', '?', '0', '1', '0', '?', '0', '?'}, false);
	public static final TerrainEdge TR_fill = new TerrainEdge("TR_fill", EdgeType.NONE, new char[] 	 {'0', '?', '0', '?', '0', '1', '0', '?'}, false);
	public static final TerrainEdge BR_fill = new TerrainEdge("BR_fill", EdgeType.NONE, new char[] {'0', '?', '0', '?', '0', '?', '0', '1'}, false);
	
	public static final TerrainEdge CornerBL = new TerrainEdge("CornerBL", EdgeType.NONE, new char[] {'0', '1', '0', '?', '0', '?', '0', '?'}, false);
	public static final TerrainEdge CornerTL = new TerrainEdge("CornerTL", EdgeType.NONE, new char[] {'0', '?', '0', '1', '0', '?', '0', '?'}, false);
	public static final TerrainEdge CornerTR = new TerrainEdge("CornerTR", EdgeType.NONE, new char[]	{'0', '?', '0', '?', '0', '1', '0', '?'}, false);
	public static final TerrainEdge CornerBR = new TerrainEdge("CornerBR", EdgeType.NONE, new char[] {'0', '?', '0', '?', '0', '?', '0', '1'}, false);
	
	public static final TerrainEdge BL_corner_top 
	= new TerrainEdge("BL_corner_top", EdgeType.STRAIGHT_TOP, new char[]   {'1', '1', '0', '?', '0', '?', '0', '?'}, new char[] {'?', '1', '?', '?'}, true);
	public static final TerrainEdge TL_corner_top 
	= new TerrainEdge("TL_corner_top", EdgeType.STRAIGHT_RIGHT, new char[] {'0', '?', '1', '1', '0', '?', '0', '?'}, new char[] {'?', '?', '1', '?'}, true);
	public static final TerrainEdge TR_corner_top 
	= new TerrainEdge("TR_corner_top", EdgeType.STRAIGHT_LEFT, new char[]  {'0', '?', '0', '?', '0', '1', '1', '?'}, new char[] {'?', '?', '1', '?'}, true);
	public static final TerrainEdge BR_corner_top 
	= new TerrainEdge("BR_corner_top", EdgeType.STRAIGHT_TOP, new char[]   {'1', '?', '0', '?', '0', '?', '0', '1'}, new char[] {'?', '?', '?', '1'}, true);
	
	public static TerrainEdge[] diagonalEdges = {BL, TL, TR, BR};
	public static TerrainEdge[] diagonalFill = {BL_fill, TL_fill, TR_fill, BR_fill};
	
	public static TerrainEdge[] straightEdges = {ST, SR, SB, SL};
	public static TerrainEdge[] endEdges = {TB, RB, BB, LB};
	public static TerrainEdge[] cornerFill = {CornerBL, CornerTL, CornerTR, CornerBR};
	public static TerrainEdge[] cornerStraightFill = {BL_corner_top, TL_corner_top, TR_corner_top, BR_corner_top};
	
	public static TerrainEdge resolveDiagonalEdges(char[] diffs, Chunk c) {
		for (int i = 0; i < diagonalEdges.length; i++) { 
			if (mask(diffs, diagonalEdges[i].mask)) {
 				return diagonalEdges[i];
			}
		}
		return null;
	}
	
	public static TerrainEdge resolveSolidEdges(char[] diffs, Chunk c) {
		if (mask(diffs, solid.mask)) {
			return solid;
		}
		
		for (int i = 0; i < endEdges.length; i++) { 
			if (mask(diffs, endEdges[i].mask)) {
 				return endEdges[i];
			}
		}
		
		//Generate chunk outer edge mask
		char[] outerDiffs = new char[4]; 
		for (int i = 0; i < outerDiffs.length*2; i+=2) {
			if (c.neighbours[i].neighbours[i] != null) {
				outerDiffs [i/2] = (char) (c.neighbours[i].neighbours[i].height < c.height ? '1' : '0');
			} else {
				outerDiffs [i/2] = '0';
			}
		}
		
		for (int i = 0; i < straightEdges.length; i++) { 
			if (mask(diffs, straightEdges[i].mask)) {
				for (int i2 = 0; i2 < straightEdges.length; i2++) { 
					if (mask(diffs, cornerStraightFill[i2].mask)) {
						if (mask(outerDiffs, cornerStraightFill[i2].outerEdgesMask)) {
							return cornerStraightFill[i2];	
						}		
					}
				}
 				return straightEdges[i];
			}
		}
		
		for (int i = 0; i < diagonalFill.length; i++) { 
			boolean confirm = false;
			switch(diagonalFill[i].name) {
				case "BL_fill": if (c.neighbours[0].neighbours[0] != null && c.neighbours[0].neighbours[0].getHeight() < c.height && c.neighbours[2].neighbours[2] != null && c.neighbours[2].neighbours[2].getHeight() < c.height) confirm = true; break;
				case "TL_fill": if (c.neighbours[2].neighbours[2] != null && c.neighbours[2].neighbours[2].getHeight() < c.height && c.neighbours[4].neighbours[4] != null && c.neighbours[4].neighbours[4].getHeight() < c.height) confirm = true; break;
				case "TR_fill": if (c.neighbours[4].neighbours[4] != null && c.neighbours[4].neighbours[4].getHeight() < c.height && c.neighbours[6].neighbours[6] != null && c.neighbours[6].neighbours[6].getHeight() < c.height) confirm = true; break;
				case "BR_fill": if (c.neighbours[6].neighbours[6] != null && c.neighbours[6].neighbours[6].getHeight() < c.height && c.neighbours[0].neighbours[0] != null && c.neighbours[0].neighbours[0].getHeight() < c.height) confirm = true; break;
			}
			if (confirm) return diagonalFill[i];	
		}
		
		
		
		//Leave last of fills
		for (int i = 0; i < cornerFill.length; i++) { 
			if (mask(diffs, cornerFill[i].mask)) {
 				return cornerFill[i];
			}
		}
		return null;
	}
	
	public static TerrainEdge resolveStraight(char[] diffs, Chunk c) {
		for (int i = 0; i < straightEdges.length; i++) { 
			if (mask(diffs, straightEdges[i].mask)) {
 				return straightEdges [i];
			}
		}
		return null;
	}
	
	/**A slightly modified {@link Arrays#equals(char[], char[])} method that allows positions with '?' to act interchangeably with other elements.*/
	private static boolean mask(char[] diffs, char[] mask) {
		if (diffs==mask)
            return true;
        if (diffs==null || mask==null)
            return false;

        int length = diffs.length;
        if (mask.length != length)
            return false;

        for (int i=0; i<length; i++)
            if (diffs[i] != mask[i] && mask[i] != '?')
                return false;

        return true;
	}
}

package org.dudss.nodeshot.terrain;

import java.util.Arrays;

import org.dudss.nodeshot.terrain.Chunk.EdgeType;
import org.dudss.nodeshot.terrain.datasubsets.TerrainEdge;

/**A static class that provides utility methods for terrain smoothing / edge recognition.
 * Terrain smoothing works by comparing height differences to static masks as well as by using special {@link TerrainEdge} data objects.
 * This prevents hard-coded nested condition (if) trees and makes the code less error-prone and more manageable.
 * 
 * Most importantly it adds flexibility to the terrain smoothing algorithms and makes this resolver universal. This means I can use it for grid systems other 
 * than terrain. Currently implemented for terrain and fluids (creeper or possibly water / lava in the future).
 * 
 * The masks have 3 states:
 * '1' - negative height difference (source > target)
 * '0' - positive height difference (source <= target)
 * '?' - irrelevant to the edge recognition (the edge doesn't need this information)
 * 
 * Using the question marks as a indecisive state allows me to overlay different masks. Some masks would be the same otherwise. But they require some additional details.
 * 
 * Masks represent tiles in a clockwise order from the y+1 chunk. Secondary "outer" masks represent y+2, x+2, y-2, x-2 neighbours accordingly.
 * 
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
	
	public static final TerrainEdge XBS = new TerrainEdge("XBS", EdgeType.DOUBLE_X, new char[]	{'1', '?', '0', '?', '1', '?', '0', '?'}, true);
	public static final TerrainEdge YBS = new TerrainEdge("YBS", EdgeType.DOUBLE_Y, new char[] 	{'0', '?', '1', '?', '0', '?', '1', '?'}, true);
	
	public static final TerrainEdge TB = new TerrainEdge("TB", EdgeType.END_TOP, new char[]    {'1', '?', '1', '?', '0', '?', '1', '?'}, true);
	public static final TerrainEdge RB = new TerrainEdge("RB", EdgeType.END_RIGHT, new char[]  {'1', '?', '1', '?', '1', '?', '0', '?'}, true);
	public static final TerrainEdge BB = new TerrainEdge("BB", EdgeType.END_BOTTOM, new char[] {'0', '?', '1', '?', '1', '?', '1', '?'}, true);
	public static final TerrainEdge LB = new TerrainEdge("LB", EdgeType.END_LEFT, new char[]   {'1', '?', '0', '?', '1', '?', '1', '?'}, true);
	
	//Fills
	public static final TerrainEdge BL_fill = new TerrainEdge("BL_fill", EdgeType.NONE, new char[] {'0', '1', '0', '?', '0', '?', '0', '?'}, new char[] {'1', '1', '?', '?'}, false);
	public static final TerrainEdge TL_fill = new TerrainEdge("TL_fill", EdgeType.NONE, new char[] {'0', '?', '0', '1', '0', '?', '0', '?'}, new char[] {'?', '1', '1', '?'}, false);
	public static final TerrainEdge TR_fill = new TerrainEdge("TR_fill", EdgeType.NONE, new char[] {'0', '?', '0', '?', '0', '1', '0', '?'}, new char[] {'?', '?', '1', '1'}, false);
	public static final TerrainEdge BR_fill = new TerrainEdge("BR_fill", EdgeType.NONE, new char[] {'0', '?', '0', '?', '0', '?', '0', '1'}, new char[] {'1', '?', '?', '1'}, false);
	
	public static final TerrainEdge BL_fill_corner_SR_BL = new TerrainEdge("BL_fill_corner_SR_BL", EdgeType.NONE, new char[] {'0', '1', '0', '0', '0', '?', '0', '0'}, new char[] {'?', '1', '?', '?'}, false);
	public static final TerrainEdge TL_fill_corner_SR_TL = new TerrainEdge("TL_fill_corner_SR_TL", EdgeType.NONE, new char[] {'0', '0', '0', '1', '0', '0', '0', '?'}, new char[] {'?', '1', '?', '?'}, false);
	public static final TerrainEdge BR_fill_corner_ST_BR = new TerrainEdge("BR_fill_corner_ST_BR", EdgeType.NONE, new char[] {'0', '0', '0', '?', '0', '0', '0', '1'}, new char[] {'1', '?', '?', '?'}, false);
	public static final TerrainEdge BL_fill_corner_BL_ST = new TerrainEdge("BL_fill_corner_BL_ST", EdgeType.NONE, new char[] {'0', '1', '0', '0', '0', '?', '0', '0'}, new char[] {'1', '?', '?', '?'}, false);
	public static final TerrainEdge TR_fill_corner_SB_TR = new TerrainEdge("TR_fill_corner_SB_TR", EdgeType.NONE, new char[] {'0', '?', '0', '0', '0', '1', '0', '0'}, new char[] {'?', '?', '1', '?'}, false);
	public static final TerrainEdge TL_fill_corner_TL_SB = new TerrainEdge("TL_fill_corner_TL_SB", EdgeType.NONE, new char[] {'0', '0', '0', '1', '0', '0', '0', '?'}, new char[] {'?', '?', '1', '?'}, false);
	public static final TerrainEdge TR_fill_corner_TR_SL = new TerrainEdge("TR_fill_corner_TR_SL", EdgeType.NONE, new char[] {'0', '?', '0', '0', '0', '1', '0', '0'}, new char[] {'?', '?', '?', '1'}, false);
	public static final TerrainEdge BR_fill_corner_BR_SL = new TerrainEdge("BR_fill_corner_BR_SL", EdgeType.NONE, new char[] {'0', '0', '0', '?', '0', '0', '0', '1'}, new char[] {'?', '?', '?', '1'}, false);
	
	public static final TerrainEdge CornerBL = new TerrainEdge("CornerBL", EdgeType.NONE, new char[] {'0', '1', '0', '?', '0', '?', '0', '?'}, false);
	public static final TerrainEdge CornerTL = new TerrainEdge("CornerTL", EdgeType.NONE, new char[] {'0', '?', '0', '1', '0', '?', '0', '?'}, false);
	public static final TerrainEdge CornerTR = new TerrainEdge("CornerTR", EdgeType.NONE, new char[] {'0', '?', '0', '?', '0', '1', '0', '?'}, false);
	public static final TerrainEdge CornerBR = new TerrainEdge("CornerBR", EdgeType.NONE, new char[] {'0', '?', '0', '?', '0', '?', '0', '1'}, false);
	
	public static final TerrainEdge BL_corner_top 
	= new TerrainEdge("BL_corner_top", EdgeType.STRAIGHT_TOP, new char[]   {'1', '1', '0', '0', '0', '?', '0', '?'}, new char[] {'?', '1', '?', '?'}, true);
	public static final TerrainEdge TL_corner_top 
	= new TerrainEdge("TL_corner_top", EdgeType.STRAIGHT_RIGHT, new char[] {'0', '?', '1', '1', '0', '0', '0', '?'}, new char[] {'?', '?', '1', '?'}, true);
	public static final TerrainEdge TR_corner_top 
	= new TerrainEdge("TR_corner_top", EdgeType.STRAIGHT_LEFT, new char[]  {'0', '?', '0', '0', '0', '1', '1', '?'}, new char[] {'?', '?', '1', '?'}, true);
	public static final TerrainEdge BR_corner_top 
	= new TerrainEdge("BR_corner_top", EdgeType.STRAIGHT_TOP, new char[]   {'1', '?', '0', '?', '0', '0', '0', '1'}, new char[] {'?', '?', '?', '1'}, true);
	
	public static final TerrainEdge BL_corner_bottom
	= new TerrainEdge("BL_corner_bottom", EdgeType.STRAIGHT_RIGHT, new char[]   {'0', '1', '1', '?', '0', '?', '0', '0'}, new char[] {'1', '?', '?', '?'}, true);
	public static final TerrainEdge TL_corner_bottom
	= new TerrainEdge("TL_corner_bottom", EdgeType.STRAIGHT_BOTTOM, new char[] {'0', '0', '0', '1', '1', '?', '0', '?'}, new char[] {'?', '1', '?', '?'}, true);
	public static final TerrainEdge TR_corner_bottom
	= new TerrainEdge("TR_corner_bottom", EdgeType.STRAIGHT_BOTTOM, new char[]  {'0', '?', '0', '?', '1', '1', '0', '0'}, new char[] {'?', '?', '?', '1'}, true);
	public static final TerrainEdge BR_corner_bottom
	= new TerrainEdge("BR_corner_bottom", EdgeType.STRAIGHT_LEFT, new char[]   {'0', '0', '0', '?', '0', '?', '1', '1'}, new char[] {'1', '?', '?', '?'}, true);
	
	public static final TerrainEdge ST_corner_mid
	= new TerrainEdge("ST_corner_mid", EdgeType.STRAIGHT_TOP, new char[]   {'1', '1', '0', '?', '0', '?', '0', '1'}, new char[] {'?', '1', '?', '1'}, true);
	public static final TerrainEdge SR_corner_mid
	= new TerrainEdge("SR_corner_mid", EdgeType.STRAIGHT_RIGHT, new char[] {'0', '1', '1', '1', '0', '?', '0', '?'}, new char[] {'1', '?', '1', '?'}, true);
	public static final TerrainEdge SB_corner_mid
	= new TerrainEdge("SB_corner_mid", EdgeType.STRAIGHT_BOTTOM, new char[]  {'0', '?', '0', '1', '1', '1', '0', '?'}, new char[] {'?', '1', '?', '1'}, true);
	public static final TerrainEdge SL_corner_mid
	= new TerrainEdge("SL_corner_mid", EdgeType.STRAIGHT_LEFT, new char[]   {'0', '?', '0', '?', '0', '1', '1', '1'}, new char[] {'1', '?', '1', '?'}, true);
	
	public static TerrainEdge[] diagonalEdges = {BL, TL, TR, BR};
	public static TerrainEdge[] diagonalFill = {BL_fill, TL_fill, TR_fill, BR_fill};	
	public static TerrainEdge[] diagonalFillCorner =	{
														BL_fill_corner_SR_BL,TL_fill_corner_SR_TL,
														BR_fill_corner_ST_BR,
														BL_fill_corner_BL_ST,
														TR_fill_corner_SB_TR,
														TL_fill_corner_TL_SB,
														TR_fill_corner_TR_SL,
														BR_fill_corner_BR_SL
														};
	
	public static TerrainEdge[] straightEdges = {ST, SR, SB, SL};
	public static TerrainEdge[] cornerFill = {CornerBL, CornerTL, CornerTR, CornerBR};
	public static TerrainEdge[] cornerStraightFillTop = {BL_corner_top, TL_corner_top, TR_corner_top, BR_corner_top};
	public static TerrainEdge[] cornerStraightFillBottom = {BL_corner_bottom, TL_corner_bottom, TR_corner_bottom, BR_corner_bottom};
	public static TerrainEdge[] cornerStraightFillMid = {ST_corner_mid, SR_corner_mid, SB_corner_mid, SL_corner_mid};
	
	public static TerrainEdge[] endEdges = {TB, RB, BB, LB};
	public static TerrainEdge[] straightDoubleEdges = {XBS, YBS};
	
	public static TerrainEdge resolveDiagonalEdges(char[] diffs) {
		for (int i = 0; i < diagonalEdges.length; i++) { 
			if (mask(diffs, diagonalEdges[i].mask)) {
 				return diagonalEdges[i];
			}
		}
		return null;
	}
	
	public static TerrainEdge resolveSolidEdges(char[] diffs, char[] outerDiffs, int height) {
		if (mask(diffs, solid.mask)) {
			return solid;
		}
		
		for (int i = 0; i < endEdges.length; i++) { 
			if (mask(diffs, endEdges[i].mask)) {
 				return endEdges[i];
			}
		}
		
		for (int i = 0; i < straightDoubleEdges.length; i++) { 
			if (mask(diffs, straightDoubleEdges[i].mask)) {
 				return straightDoubleEdges[i];
			}
		}
			
		for (int i = 0; i < straightEdges.length; i++) { 
			if (mask(diffs, straightEdges[i].mask)) {
				for (int i2 = 0; i2 < straightEdges.length; i2++) { 
					if (mask(diffs, cornerStraightFillBottom[i2].mask)) {									
						if (mask(outerDiffs, cornerStraightFillBottom[i2].outerEdgesMask)) {
							return cornerStraightFillBottom[i2];
						}
					}
					if (mask(diffs, cornerStraightFillTop[i2].mask)) {
						if (mask(outerDiffs, cornerStraightFillTop[i2].outerEdgesMask)) {
							return cornerStraightFillTop[i2];		
						}
					}
					if (mask(diffs, cornerStraightFillMid[i2].mask)) {
						if (mask(outerDiffs, cornerStraightFillMid[i2].outerEdgesMask)) {
							return cornerStraightFillMid[i2];	
						}		
					}						
				}				
 				return straightEdges[i];
			}
		}
		
		for (int i = 0; i < diagonalFill.length; i++) { 
			if (mask(diffs, diagonalFill[i].mask) && mask(outerDiffs, diagonalFill[i].outerEdgesMask)) {			
				return diagonalFill[i];		
			}
		}

		for (int i = 0; i < diagonalFillCorner.length; i++) { 
			if (mask(diffs, diagonalFillCorner[i].mask) && mask(outerDiffs, diagonalFillCorner[i].outerEdgesMask)) {
 				return diagonalFillCorner[i];
			}
		}
		
		//Leave last of fills
		for (int i = 0; i < cornerFill.length; i++) { 
			if (mask(diffs, cornerFill[i].mask)) {
 				return cornerFill[i];
			}
		}
		
		if (mask(diffs, single.mask)) {
			return single;
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

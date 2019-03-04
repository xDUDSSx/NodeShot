
package org.dudss.nodeshot;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.dudss.nodeshot.buildings.*;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.terrain.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/**A static class that functions as of wrapper of some constant variables and utility methods.*/
public class Base {
	static Boolean running = true;	

	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;

	/**Minimal camera zoom.*/
	public static float MIN_ZOOM = 0.3f;

	public static int START_BITS = 1000;
	public static int START_POWER = 100;
	
	/**The size of {@link Node}s.*/
	public static int RADIUS = 16;
	/**The size of {@link Package}s.*/
	public static int PACKAGE_RADIUS = 16;

	/**The unadjusted speed of {@link Package}s.*/
	public static float PACKAGE_SPEED = 0.5f;
	/**The space that the {@link Package}s take up on the {@link Connector}.*/
	public static float PACKAGE_BLOCK_RANGE = 8;

	public static int MAX_CONNECTIONS = 9;
	public static float CONNECTOR_COLLIDER_RADIUS = 4;
	
	public static int lineWidth = 3;
	public static int HUD_FONT_SIZE = 16;  //Mobile 32
	public static int HUD_FONT_LARGE_SIZE = 36;
	
	//TERRAIN
	/**The size of the game world (in world units).*/
	public static int WORLD_SIZE = 2048*2;
	
	//Values that alter the terrain generation properties.*/
	public static float COAL_THRESHOLD = 0.74f;
	public static float IRON_THRESHOLD = 0.74f;
	public static float TERRAIN_THRESHOLD = 0.6f;
	
	/**Default fog of war visibility.*/
	public static float DEFAULT_VISIBILITY = Chunk.deactivated;
	
	/**Distance from the centre of the map where {@link CreeperGenerator}s won't spawn.*/
	public static float GENERATOR_SAFEZONE = 800;
	
	/**Maximal layer of creeper/corruption.*/
	public static int MAX_CREEP = 15;
	/**The highest layer of terrain.*/
	public static int MAX_HEIGHT = 10; 
	
	/**Size of a {@link Chunk} in world units-*/
	public static int CHUNK_SIZE = 16;
	
	/**Size of a {@link Section} in world units-*/
	public static int SECTION_SIZE = 32;
	/*SECTION_SIZE directly affects the number of draw calls. A single section mesh takes 1 draw call.

	Tested 1.12.18 with 8192 world size. (GTX 1070 @ i5 6600k 4.1Ghz)
	
	+--------------+-----+------------+
	| Section size | FPS | Draw calls |
	+--------------+-----+------------+
	| 128          | 408 |            |
	+--------------+-----+------------+
	| 64           | 405 | 64         |
	+--------------+-----+------------+
	| 32           | 397 | 257        |
	+--------------+-----+------------+
	| 16           | 242 | 1024       |
	+--------------+-----+------------+
	| 8            | 71  |            |
	+--------------+-----+------------+
	| 4            | 25  |            |
	+--------------+-----+------------+
	| 2            | 4   |            |
	+--------------+-----+------------+
	| 1            | 3   |            |
	+--------------+-----+------------+
	*/
	
	public static int CHUNK_AMOUNT = WORLD_SIZE / CHUNK_SIZE;
	public static int SECTION_AMOUNT = CHUNK_AMOUNT / SECTION_SIZE;
	
	//UI ELEMENT STATES
	public static int buildMenuImgSize = 80;
	
	public static boolean settingsOpened = false;
	public static boolean enableGlProgilerLogging = false;
	public static boolean hoverChunkHighlight = false;
	public static boolean drawTerrainEdges = false;
	public static boolean drawCorruptionEdges = false;
	public static boolean drawCHeightInequality = false;
	public static boolean drawOres = true;
	public static boolean drawGeneralStats = false;
	public static boolean drawBorderChunks = false;
	public static boolean drawActiveSections = false;
	public static boolean drawBuildingTiles = false;
	public static boolean drawSectionBorders = false;
	public static boolean drawCreeperLevel = false;
	public static boolean drawConnectorColliders = false;
	
	//Game settings
	public static boolean infiniteResources = false;
	
	//Graphical settings
	public static boolean vSyncEnabled = true;
	public static boolean fullscreen = false;
	public static int foregroundFps = 0;
	public static Dimension defaultWindowSize = new Dimension(1300, 795);
	public static int MSAAsamples = 10;
	
	//Render settings
	public static boolean disableBackground = false;
	public static boolean clipMap = false;
	public static boolean disableEdges = false;
	public static boolean enablePostProcessing = false;
	public static boolean enableBloom = false;	
	
	//Building costs	
	/**How often do building get their energy usage and generation updated.
	 * Energy updates are handled by the {@link BuildingManager}.*/
	public static int ENERGY_UPDATE_RATE = 30;
	public static int POWER_GENERATOR_GENERATION_AMOUNT = 3;
	
	/**Portion of the bits returned upon building demolition.*/
	public static float DEMOLISH_RETURN_VALUE = 0.5f;
	
	public static int DEFAULT_ENERGY_USAGE = 1;
	public static int DEFAULT_ENERGY_COST = 10;
	public static int DEFAULT_BUILD_COST = 100;
	
	public static int CONVEYOR_BUILD_COST = 5;
	public static int CONVEYOR_ENERGY_COST = 1;
	
	public static int NODE_BUILD_COST = 10;
	public static int NODE_ENERGY_COST = 5;
	
	public static int IOPORT_BUILD_COST = 10;
	public static int IOPORT_ENERGY_COST = 5;
	
	
	public static int CREEPER_GENERATOR_BUILD_COST = 0;
	public static int CREEPER_GENERATOR_ENERGY_COST = 0;
	public static int CREEPER_GENERATOR_ENERGY_USAGE = 0;
	
	
	/**Returns a random floating point number in the number range.*/
	public static float getRandomFloatNumberInRange(float min, float max) {
		if (min == 0 && max == 0) {
			return 0;
		}

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return min + r.nextFloat() * (max - min);
	}

	/**Returns a random integer number in the integer range.*/
	public static int getRandomIntNumberInRange(int min, int max) {
		if (min == 0 && max == 0) {
			return 0;
		}

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}
	
	/**Prints out a list.*/
	public static String listToString(List<Entity> list) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			String s = String.valueOf(list.get(i).getID());
			stringBuilder.append(s + ", ");
		}
		return stringBuilder.toString();
	}

	/**Enables OpenGL blending.*/
	public static void enableGlBlend() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	/**Disables OpenGL blending.*/
	public static void disableGlBlend() {
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	/**Rounds a float number to a specified number of decimal places.*/
	public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
	}
	
	/**Recalculates a float value into a different number range.
	 * @param OldValue The original value.
	 * @param OldMin The original lower limit of the number range.
	 * @param OldMax The original higher limit of the number range.
	 * @param NewMin The new lower limit of the number range.
 	 * @param NewMax The new higher limit of the number range.
	 */
	public static float range(float OldValue, float OldMin, float OldMax, float NewMin, float NewMax) {
		return (((OldValue - OldMin) * (NewMax - NewMin)) / (OldMax - OldMin)) + NewMin;
	}
	
	/**Returns an angular distance between two angles. In degrees.*/
	public static float angleDist(float a1, float a2) {
		return Math.abs((Math.abs((a1 - a2) + 180) % 360) - 180);
	}
}


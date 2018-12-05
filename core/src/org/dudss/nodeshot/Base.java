
package org.dudss.nodeshot;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunk;
import org.dudss.nodeshot.terrain.Section;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class Base {

	static Boolean running = true;	

	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;

	public static float MIN_ZOOM = 0.2f;
	
	public static int RADIUS = 16;
	public static int PACKAGE_RADIUS = 16;

	public static float PACKAGE_SPEED = 0.5f;
	public static float PACKAGE_BLOCK_RANGE = 8;

	public static int CONNECT_DISTANCE = 900;
	public static int MAX_CONNECTIONS = 9;

	public static int lineWidth = 3;
	public static int HUD_FONT_SIZE = 16;  //Mobile 32
	public static int HUD_FONT_LARGE_SIZE = 36;
	
	//TERRAIN
	public static int WORLD_SIZE = 2048*4;
	
	public static float COAL_THRESHOLD = 0.74f;
	public static float IRON_THRESHOLD = 0.74f;
	public static float TERRAIN_THRESHOLD = 0.6f;
	
	public static int MAX_CREEP = 15;
	public static int MAX_HEIGHT = 10; 
	
	public static int CHUNK_SIZE = 16;
	
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
	public static boolean settingsOpened = true;
	public static boolean enableGlProgilerLogging = false;
	public static boolean hoverChunkHighlight = false;
	public static boolean drawTerrainEdges = false;
	public static boolean drawCorruptionEdges = false;
	public static boolean drawCHeightInequality = false;
	public static boolean drawOres = true;
	public static boolean drawGeneralStats = false;
	public static boolean drawBorderChunks = false;
	public static boolean drawActiveSections = false;
	
	//Graphical settings
	public static boolean vSyncEnabled = false;
	public static boolean fullscreen = false;
	public static int foregroundFps = 144;
	public static Dimension defaultWindowSize = new Dimension(1300, 795);
	public static int MSAAsamples = 10;
	
	public static float getRandomFloatNumberInRange(int min, int max) {
		if (min == 0 && max == 0) {
			return 0;
		}

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return min + r.nextFloat() * (max - min);
	}

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

	public static String listToString(List<Entity> list) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			String s = String.valueOf(list.get(i).getID());
			stringBuilder.append(s + ", ");
		}
		return stringBuilder.toString();
	}

	public static String nodeListToString(List<Node> list) {
		List<Entity> entityList = new ArrayList<Entity>();
		for (final Node n : list) {
			entityList.add(new Entity() {
				@Override
				public int getID() {
					return n.getID();
				}

				@Override
				public int getIndex() {
					return 0;
				}

				@Override
				public EntityType getType() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public float getX() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public float getY() {
					// TODO Auto-generated method stub
					return 0;
				}
			});
		}
			return listToString(entityList);
	}
	public static String nodeConnectorListToString (List < Connector > list) {
		List<Entity> entityList = new ArrayList<Entity>();
		for (final Connector n : list) {
			entityList.add(new Entity() {
				@Override
				public int getID() {
					return n.getID();
				}

				@Override
				public int getIndex() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public EntityType getType() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public float getX() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public float getY() {
					// TODO Auto-generated method stub
					return 0;
				}
			});
		}
		return listToString(entityList);
	}

	public static String packageListToString (List < Package > list) {
		List<Entity> entityList = new ArrayList<Entity>();
		for (final Package n : list) {
			entityList.add(new Entity() {
				@Override
				public int getID() {
					return n.getID();
				}

				@Override
				public int getIndex() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public EntityType getType() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public float getX() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public float getY() {
					// TODO Auto-generated method stub
					return 0;
				}
			});
		}
		return listToString(entityList);
	}
	
	public static void enableGlBlend() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void disableGlBlend() {
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}
	
	public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
	}
	
	public static float range(float OldValue, float OldMin, float OldMax, float NewMin, float NewMax) {
		return (((OldValue - OldMin) * (NewMax - NewMin)) / (OldMax - OldMin)) + NewMin;
	}
	
	/**Returns the {@link Section} that holds this points {@link Chunk}.
	 * @param x X world space coordinate.
	 * @param y Y world space coordinate.
	 */
	public static Section locateSectionByWorldSpace(float x, float y) {
		int ax = (int) (x / Base.CHUNK_SIZE);
		int ay = (int) (y / Base.CHUNK_SIZE);
		
		return GameScreen.chunks.sections[ax / Base.SECTION_SIZE][ay / Base.SECTION_SIZE];
	}
	
	/**Returns the {@link Section} that holds this points {@link Chunk}.
	 * @param x X tile space coordinate.
	 * @param y Y tile space coordinate.
	 */
	public static Section locateSectionByTileSpace(int ax, int ay) {
		return GameScreen.chunks.sections[ax / Base.SECTION_SIZE][ay / Base.SECTION_SIZE];
	}
	
	/**Returns all 9 sections around this particular point in world space (Including the middle section).
	 * @param x X world space coordinate
	 * @param y Y world space coordinate
	 * */
 	public static List<Section> getSectionsAroundThePoint(float x, float y) {
 		List<Section> sections = new ArrayList<Section>();
 		
 		Section s = locateSectionByWorldSpace(x, y);
 		sections = getNeighbourSections(s);
 		sections.add(s);
 		
 		return sections;
 	}
 	
 	/**Returns a list of all neighbouring sections, if there is no neighbouring section, it is skipped and not included in the list
 	 * @param s The {@link Section} which neighbours will be returned.*/
 	public static List<Section> getNeighbourSections(Section s) {
 		List<Section> sections = new ArrayList<Section>();
 		Chunk sectionOrigin = s.getChunk(0, 0);
 		int sectionAx = (int) ((int) (sectionOrigin.getX() / Base.CHUNK_SIZE) / Base.SECTION_SIZE);
 		int sectionAy = (int) ((int) (sectionOrigin.getY() / Base.CHUNK_SIZE) / Base.SECTION_SIZE);
 		
 		if (sectionAy < Base.SECTION_AMOUNT) 									sections.add(GameScreen.chunks.sections[sectionAx][sectionAy + 1]);
 		if (sectionAy < Base.SECTION_AMOUNT && sectionAx < Base.SECTION_AMOUNT) sections.add(GameScreen.chunks.sections[sectionAx + 1][sectionAy + 1]);
 		if (sectionAx < Base.SECTION_AMOUNT) 									sections.add(GameScreen.chunks.sections[sectionAx + 1][sectionAy]);
 		if (sectionAy < Base.SECTION_AMOUNT && sectionAy > 0) 					sections.add(GameScreen.chunks.sections[sectionAx + 1][sectionAy - 1]);
 		if (sectionAy > 0) 														sections.add(GameScreen.chunks.sections[sectionAx][sectionAy - 1]);
 		if (sectionAy > 0 && sectionAx > 0) 									sections.add(GameScreen.chunks.sections[sectionAx - 1][sectionAy - 1]);
 		if (sectionAx > 0) 														sections.add(GameScreen.chunks.sections[sectionAx - 1][sectionAy]);
 		if (sectionAy < Base.SECTION_AMOUNT && sectionAx > 0) 					sections.add(GameScreen.chunks.sections[sectionAx - 1][sectionAy + 1]);
 		return sections;
 	}
}


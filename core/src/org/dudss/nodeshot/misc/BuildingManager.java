package org.dudss.nodeshot.misc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**Manages all buildings in the game world. Calls logic updates and render calls*/
public class BuildingManager {

	List<AbstractBuilding> buildings;
	List<AbstractBuilding> generators;
	List<AbstractBuilding> misc;
	
	public BuildingManager() {
		buildings = new CopyOnWriteArrayList<AbstractBuilding>();
		generators = new CopyOnWriteArrayList<AbstractBuilding>();
		misc = new CopyOnWriteArrayList<AbstractBuilding>();
	}
	
	public void updateAllBuildings() {
		for (AbstractBuilding b : buildings) {
			b.update();
		}
	}
	
	public void updateAllGenerators() {
		for (AbstractBuilding b : generators) {
			b.update();
		}
	}
	
	public void updateAllMisc() {
		for (AbstractBuilding b : misc) {
			b.update();
		}
	}
	
	public void drawAllBuildings(ShapeRenderer r, SpriteBatch batch) {
		for (AbstractBuilding b : buildings) {
			b.draw(r, batch);
		}
	}
	
	public void drawAllGenerators(ShapeRenderer r, SpriteBatch batch) {
		for (AbstractBuilding b : generators) {
			b.draw(r, batch);
		}
	}
	
	public void drawAllMisc(ShapeRenderer r, SpriteBatch batch) {
		for (AbstractBuilding b : misc) {
			b.draw(r, batch);
		}
	}
	
	public void addBuilding(AbstractBuilding b) {
		buildings.add(b);
	}
	
	public void removeBuilding(AbstractBuilding b) {
		buildings.remove(b);
	}
	
	public List<AbstractBuilding> getAllBuildings() {
		return buildings;
	}
	
	public void addGenerator(AbstractBuilding b) {
		generators.add(b);
	}
	
	public void removeGenerator(AbstractBuilding b) {
		generators.remove(b);
	}
	
	public List<AbstractBuilding> getAllGenerators() {
		return generators;
	}	
	
	public void addMisc(AbstractBuilding b) {
		misc.add(b);
	}
	
	public void removeMisc(AbstractBuilding b) {
		misc.remove(b);
	}
	
	public List<AbstractBuilding> getAllMisc() {
		return misc;
	}	
	
	/**Initialises a new {@link AbstractBuilding} and assigns it to build mode.
	 * @param b The built building type.
	 * */
	public void startBuildMode(AbstractBuilding b) {
		startBuildMode(b, false);
	}
	
	public void startBuildMode(AbstractBuilding b, boolean moving) {
		if (moving) {
			if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
				GameScreen.buildMode = true;
				GameScreen.buildMoving = true;
				GameScreen.builtBuilding = b;
				GameScreen.chunks.updateAllSectionMeshes(false);
			}
		} else {
			try {
				Class<? extends AbstractBuilding> buildingClass = b.getClass();
				Constructor<? extends AbstractBuilding> buildingConstructor = buildingClass.getConstructor(new Class[] {float.class, float.class});	
				Object[] buildingArgs = new Object[] { new Float(0), new Float(0) };
			
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.buildMoving = false;
					GameScreen.builtBuilding = (AbstractBuilding) buildingConstructor.newInstance(buildingArgs);
					GameScreen.chunks.updateAllSectionMeshes(false);
				}
				
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				BaseClass.errorManager.report(e, "Build mode building initialisation failed.");
			}
		}
	}
	
	/**Disables the build mode*/
	public void disableBuildMode() {
		GameScreen.expandedConveyorNode = null;
		GameScreen.expandingANode = false;
		GameScreen.builtBuilding = null;
		GameScreen.buildMoving = false;
		GameScreen.buildMode = false;
		
		//A terrain update necessary to clear build mode highlights
		GameScreen.chunks.updateAllSectionMeshes(false);
	}
}

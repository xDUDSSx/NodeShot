package org.dudss.nodeshot.misc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.SimulationThread;
import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.buildings.Headquarters;
import org.dudss.nodeshot.buildings.PowerGenerator;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.ui.util.dialog.Dialogs;

/**Manages all buildings in the game world. Calls logic updates and render calls*/
public class BuildingManager {

	List<AbstractBuilding> regularBuildings;
	List<AbstractBuilding> generators;
	List<AbstractBuilding> misc;
	
	int nextEnergyTick = SimulationThread.simTick + Base.ENERGY_UPDATE_RATE;
	
	//A lot of methods in this class are self-explanatory so I haven't written any documentation.
	/**Manages all buildings in the game world. Calls logic updates and render calls*/
	public BuildingManager() {
		regularBuildings = new CopyOnWriteArrayList<AbstractBuilding>();
		generators = new CopyOnWriteArrayList<AbstractBuilding>();
		misc = new CopyOnWriteArrayList<AbstractBuilding>();
		nextEnergyTick = SimulationThread.simTick + Base.ENERGY_UPDATE_RATE;
	}
	
	/**Updates regular buildings and manages power updates.*/
	public void updateAllRegularBuildings() {
		for (AbstractBuilding b : regularBuildings) {
			b.update();
			if (SimulationThread.simTick > this.nextEnergyTick) {
				if (b.isUsingEnergy()) {
					GameScreen.resourceManager.removePower(b.getEnergyUsage());
				}
				if (b instanceof PowerGenerator || b instanceof Headquarters) {
					GameScreen.resourceManager.addPower(Base.POWER_GENERATOR_GENERATION_AMOUNT);
				}
				if (Base.infiniteResources) {
					GameScreen.resourceManager.setPower(999999);
				}
			}
		}	
		if (SimulationThread.simTick > this.nextEnergyTick) {
			nextEnergyTick += Base.ENERGY_UPDATE_RATE;
		}
	}
	
	public void updateAllGenerators() {
		if (generators.size() == 0) {
			Dialogs.showOKDialog(GameScreen.stage, "You win", "All creeper generators destroyed! You WIN!");
		}
		for (AbstractBuilding b : generators) {
			b.update();
		}
	}
	
	public void updateAllMisc() {
		for (AbstractBuilding b : misc) {
			b.update();
		}
	}
	
	public void drawAllRegularBuildings(SpriteBatch batch) {
		batch.begin();
		for (AbstractBuilding b : regularBuildings) {
			b.draw(batch);
		}
		batch.end();
	}
	
	public void drawAllGenerators(SpriteBatch batch) {
		batch.begin();
		for (AbstractBuilding b : generators) {
			b.draw(batch);
		}
		batch.end();
	}
	
	public void drawAllMisc(SpriteBatch batch) {
		batch.begin();
		for (AbstractBuilding b : misc) {
			b.draw(batch);
		}
		batch.end();
	}
	
	public void addBuilding(AbstractBuilding b) {
		regularBuildings.add(b);
	}
	
	public void removeRegularBuilding(AbstractBuilding b) {
		regularBuildings.remove(b);
	}
	
	public List<AbstractBuilding> getAllRegularBuildings() {
		return regularBuildings;
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
	
	/**@return A combined list of all buildings handled by this manager.*/
	public List<AbstractBuilding> getAllBuildings() {
		List<AbstractBuilding> allBuildings = new ArrayList<AbstractBuilding>();
		allBuildings.addAll(regularBuildings);
		allBuildings.addAll(misc);
		allBuildings.addAll(generators);
		return allBuildings;
	}
	
	/**Initialises a new {@link AbstractBuilding} and assigns it to build mode.
	 * @param b The built building type.
	 * */
	public void startBuildMode(AbstractBuilding b) {
		startBuildMode(b, false);
	}
	
	/**Initialises a new {@link AbstractBuilding} and assigns it to build mode.
	 * @param b The built building type.
	 * @param moving Whether an already existing building is being just moved.
	 * */
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

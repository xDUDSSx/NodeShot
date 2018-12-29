package org.dudss.nodeshot;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Section;

/**The simulation daemon thread, runs a simulation loop*/
public class SimulationThread extends Thread {
	//double interpolation; //TODO: implement interpolation
	
	boolean paused = false;
	
	/**The corruption update calculation worker thread*/
	CorruptionUpdateThread corruptionUpdateThread;
	
	List<Section> updatedSections;
	
    public static int TICKS_PER_SECOND = 30;
    static int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
    int MAX_FRAMESKIP = 15; //30 (15)
    static long next_game_tick = getTickCount() + SKIP_TICKS;
    
    /**Corruption update speed in game ticks
     * Corruption is updated on separate thread because its calculation takes long and thus its update speed is reduced.
     * To prevent stalling of the main logic loop this calculation is moved to a separate thread so it can calculate within its slower loops free time.
     * */
    int next_chunk_tick = 10;
    int next_terrain_tick = 30;
    
    int chunkUpdateRate = 10;
    int terrainMeshUpdateRate = 30;
    
    public static int lastTicksPerSecond = 30;
    public static int simTick;
    
    /**The simulation daemon thread, runs a simulation loop
     * @throws InterruptedException */
    public SimulationThread() throws InterruptedException {
    	setDaemon(true);
    	this.setName("SimulationThread");
    	
    	updatedSections = new ArrayList<Section>();
    	
    	if (corruptionUpdateThread == null) {
    		BaseClass.logger.info("Starting the CorruptionUpdateThread daemon!");
    		corruptionUpdateThread = new CorruptionUpdateThread();
    		corruptionUpdateThread.setDaemon(true);
    		corruptionUpdateThread.start();
    	}
    }
    
    public void run() {
    	BaseClass.logger.info("SimulationThread daemon running!");       	
    	GameScreen.simFac = 1.0;
    	
    	//Start the simulation loop
    	simLoop();
	}   
    
    public void simLoop() {
    	long remainder;
    	long t1;
    	long t2;
    	long timeElapsed;
   
    	while(Base.running)
   	    {	
    		if(paused) {
    			next_game_tick = getTickCount() + SKIP_TICKS;
    			try {
					Thread.sleep(next_game_tick - getTickCount());
					continue;
				} catch (InterruptedException e) {
					BaseClass.errorManager.report(e, "Simulation thread pause sleep got interrupted.");
				}
    		}   	 	
   	        if(getTickCount() > next_game_tick) {	
   	        	next_game_tick += (SKIP_TICKS/GameScreen.simFac); //Set next expected sim calc, modify with simFactor
   	        	
   	        	t1 = getTickCount();
   	        	updateLogic();
   				t2 = getTickCount();
   				timeElapsed = t2 - t1;
   				
   				//Can't keep up?
   				if (timeElapsed > SKIP_TICKS) {
   					GameScreen.simFac = ((double)SKIP_TICKS/(double)timeElapsed);
   				} else if (GameScreen.simFac < 1.0) {
   					GameScreen.simFac = 1.0;
   				}
   				
   				//sFPS calculation
   				GameScreen.currentSimTimeTick = System.currentTimeMillis();
   				GameScreen.simFrameCount++;	 
	   	        if(GameScreen.currentSimTimeTick >= GameScreen.nextSimTimeTick) {
	   	        	GameScreen.nextSimTimeTick = GameScreen.currentSimTimeTick + 1000;
	   	        	GameScreen.sfps = GameScreen.simFrameCount;
	   	        	GameScreen.simFrameCount = 0;
   	            }	       
   	        }  	          
    		
    		//calculate remainder (with 1ms threshold for safe operation) (<- might make it unstable)
    		remainder = (next_game_tick - getTickCount());
    		if (remainder < 0) {
    			remainder = 0;
    		}
    		
    		//sleep the remainder
    		try {
				Thread.sleep(remainder);
				if (remainder == 0) {
					Thread.sleep(1);
				}			
			} catch (InterruptedException e) {
				BaseClass.errorManager.report(e, "Simulation thread encountered an exception while sleeping in-between ticks!");
			}    
   	    }
    }
    
    static long getTickCount() {
		return System.currentTimeMillis();
	}
	
    /**Call a single global game logic update*/
	void updateLogic() {
		simTick++;
		
		//Updating projectiles
		GameScreen.bulletHandler.updateAll();
		
		//Updating creeper generators
		GameScreen.buildingManager.updateAllGenerators();
		
		//Updating chunks and geometry of each section corruption mesh every chunkUpdateRate ticks
		//Terrain geometry is updated every terrainMeshUpdateRate ticks
		if (simTick >= next_chunk_tick) {
			//Notifying the corruption update thread, this will perform a single corruption update running on a different thread
			next_chunk_tick += chunkUpdateRate;
	    	
			/*
	    	//Selective per section updating, sections are only updated if they are active
	    	for (int x = 0; x < Base.SECTION_AMOUNT; x++) {
				for (int y = 0; y < Base.SECTION_AMOUNT; y++) {
					if (GameScreen.chunks.sections[x][y].isActive()) {
						updatedSections.add(GameScreen.chunks.sections[x][y]);
						GameScreen.chunks.sections[x][y].updateAll();
					}
				}
	    	}
	    	
	    	//Applies the update in all updated sections
	    	for (Section s : updatedSections) {
	    		s.applyUpdates();
	    	}
	    	
	    	//BaseClass.logger.info("CorruptionUpdateThread update report: " + updatedSections.size() + " out of " + Base.SECTION_AMOUNT*Base.SECTION_AMOUNT + " sections updated.");
	    	
	    	for (Section s : updatedSections) {
				GameScreen.chunks.updateSectionMesh(s, true);
	    	}
	    	updatedSections.clear();
			*/
			synchronized(corruptionUpdateThread) {
				corruptionUpdateThread.notify();
			}
		}		
		
		//Terrain update, currently unused
		if (simTick >= next_terrain_tick) {
			next_terrain_tick += terrainMeshUpdateRate;
			
			/*for (Section s : GameScreen.chunks.sectionsInView) {
				GameScreen.chunks.updateSectionMesh(s, false);
			}*/
		}

		//Updating pathHandler logic
		//GameScreen.packageHandler.update();
		
		//Updating connector logic
		GameScreen.connectorHandler.update();		
		
		//Updating misc buildings (importers/exporters)
		GameScreen.buildingManager.updateAllMisc();
		
		//Updating buildings
		GameScreen.buildingManager.updateAllBuildings();
		
		//Updating rightClickMenu text
		GameScreen.rightClickMenuManager.update();
	}
	
	/**Sets the targeted simulation loop tick rate
	 * @param newTick The new desired tick rate (in ticks per second).
	 * */
	public void recalculateSpeed(int newTick) {
	   	TICKS_PER_SECOND = newTick;
	   	SKIP_TICKS = 1000 / newTick;
	}
	
	/**Resumes the simulation loop*/
	public void resumeSim() {
		paused = false;
		//System.out.println("SimThread - Resuming at tick: " + simTick);
	}
	
	/**Pauses the simulation loop*/
	public void pauseSim() {
		paused = true;
		//System.out.println("SimThread - Pausing at tick: " + simTick);
	}	
}
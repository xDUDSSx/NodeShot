package org.dudss.nodeshot;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.entities.Bullet;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Section;

import com.badlogic.gdx.math.Vector2;

/**The simulation daemon thread, runs the simulation loop.
 * It's subsidiary thread is the {@link CorruptionUpdateThread} that updates the grid based cellular-automata corruption separately.*/
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
    int chunkUpdateRate = 10;
    
    long corruptionMeshUpdateDelayInMs = 200;
    long nextCorruptionMeshUpdate = getTickCount() + corruptionMeshUpdateDelayInMs;
      
    public static int lastTicksPerSecond = 30;
    public static int simTick;
    
    //Last tick
    long t1;
    private static float delta = 1f;
    public static float stateTime = 1f;
    
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
    	t1 = getTickCount(); 
    	nextCorruptionMeshUpdate = getTickCount() + corruptionMeshUpdateDelayInMs;
    	next_game_tick = getTickCount() + SKIP_TICKS;
    	
    	//Start the simulation loop
    	simLoop();
	}   
    
    /**Get the amount of seconds since the last update.*/
    public static float getDelta() {
    	return delta / 1000f;
    }
    
    public void simLoop() {
    	long remainder;
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
   	        	
  	        	delta = getTickCount() - t1;
  	        	stateTime += getDelta();
  	        	
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
			next_chunk_tick += chunkUpdateRate;
	
			//Notifying the corruption update thread, this will perform a single corruption update running on a different thread
			synchronized(corruptionUpdateThread) {
				corruptionUpdateThread.notify();
			}
		}		
		
		//Corruption mesh updates
		if (getTickCount() >= nextCorruptionMeshUpdate) {
			nextCorruptionMeshUpdate += corruptionMeshUpdateDelayInMs;
			for (Section s : GameScreen.chunks.sectionsInView) {
				if (s.isActive()) GameScreen.chunks.updateSectionMesh(s, true);			
    		}					
		}
		
		//Updating connector logic
		GameScreen.connectorHandler.update();		
		
		//Updating misc buildings (importers/exporters)
		GameScreen.buildingManager.updateAllMisc();
		
		//Updating buildings
		GameScreen.buildingManager.updateAllRegularBuildings();
		
		//Updating rightClickMenu text
		GameScreen.rightClickMenuManager.update();
		
		//Updating visual effects
		GameScreen.effectManager.updateAllEffects();
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
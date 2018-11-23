package org.dudss.nodeshot;

import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Section;

/**The simulation daemon thread, runs a simulation loop*/
public class SimulationThread extends Thread {
    //double interpolation; //TODO: implement interpolation
	int loops;
	
	boolean paused = false;
	
    public static int TICKS_PER_SECOND = 30;
    static int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
    int MAX_FRAMESKIP = 15; //30 (15)
    static long next_game_tick = getTickCount() + SKIP_TICKS;
    
    int next_chunk_tick = 10;
    int next_terrain_tick = 10;
    
    int chunkUpdateRate = 5;
    int terrainMeshUpdateRate = 30;
    
    public static int lastTicksPerSecond = 30;
    public static int simTick;
    
    /**The simulation daemon thread, runs a simulation loop*/
    public SimulationThread() {
    	setDaemon(true);
    }
    
    public void run() {
    	System.out.println("SimThread daemon running!");       	
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
					e.printStackTrace();
				}
    		}
    		//long startTime = System.currentTimeMillis();		   	 	
   	        if(getTickCount() > next_game_tick && loops < MAX_FRAMESKIP) {	
   	        	next_game_tick += (SKIP_TICKS/GameScreen.simFac); //Set next expected sim calc, modify with simFactor
   	        	
   	        	t1 = getTickCount();
   	        	updateLogic();
   				t2 = getTickCount();
   				timeElapsed = t2 - t1;
   				//System.out.println("Sim calculation finished on: " + Thread.currentThread().getName() + ", time elapsed: " + timeElapsed + ", loops: " + loops);
   				//System.out.println("Nodelist size: " + nodelist.size());
   				
   				//Cant keep up?
   				if (timeElapsed > SKIP_TICKS) {
   					//double prevSimFac = simFac;
   					GameScreen.simFac = ((double)SKIP_TICKS/(double)timeElapsed);
   					/*System.out.println("Sim calculation thread: " + Thread.currentThread().getName() + " can't keep up! "
   							+ "(" + timeElapsed + " vs " + SKIP_TICKS +"),"
   							+ "decreasing simFac (" + prevSimFac + "->" + simFac + ")");
   					*/
   				} else if (GameScreen.simFac < 1.0) {
   					//System.out.println("Sim thread is keeping up.");
   					GameScreen.simFac = 1.0;
   				}
   				
   				loops++;  
   				
   				//sFPS calculation
   				GameScreen.currentSimTimeTick = System.currentTimeMillis();
   				GameScreen.simFrameCount++;	 
	   	        if(GameScreen.currentSimTimeTick >= GameScreen.nextSimTimeTick) {
	   	        	GameScreen.nextSimTimeTick = GameScreen.currentSimTimeTick + 1000;
	   	        	GameScreen.sfps = GameScreen.simFrameCount;
	   	        	GameScreen.simFrameCount = 0;
   	            }
	   	        
	   	        if (loops > 1) {
	   	        	Thread.yield();             
	   	        }		
   	        }  	        
    		loops = 0;       
    		
    		//calculate remainder (with 1ms threshold for safe operation) (<- might make it unstable)
    		remainder = (next_game_tick - getTickCount());
    		if (remainder < 0) {
    			remainder = 0;
    		}
    		
    		//sleep the remainder
    		try {
				Thread.sleep(remainder);
				Thread.sleep(1);
			} catch (InterruptedException e) {
				System.out.println("Simulation thread encountered an exception while sleeping inbetween ticks!");
				e.printStackTrace();
			}
    		// interpolation = ((getTickCount() + SKIP_TICKS - next_game_tick ) / SKIP_TICKS );       
    		//render game with interpolation parameter if necessary (drawGame(interpolation) //TODO: create custom method for this and implement interpolation  	    
   	    }
    }
    
    static long getTickCount() {
		return System.currentTimeMillis();
	}
	
	void updateLogic() {
		simTick++;
		
		//Updating projectiles
		GameScreen.bulletHandler.updateAll();
		
		//Updating chunks and geometry of each section corruption mesh every chunkUpdateRate ticks
		//Terrain geometry is updated every terrainMeshUpdateRate ticks
		if (simTick >= next_chunk_tick) {
			next_chunk_tick += chunkUpdateRate;
			GameScreen.chunks.updateAllChunks();
			
			boolean terrainUpdate = false;
			if (simTick >= next_terrain_tick) {
				terrainUpdate = true;
				next_terrain_tick += terrainMeshUpdateRate;
			}
			
			for (Section s : GameScreen.chunks.sectionsInView) {
				GameScreen.chunks.updateSectionMesh(s, true, -1);
				if (terrainUpdate) {
					//GameScreen.chunks.updateSectionMesh(s, false, -1);
				}
			}
		}		
		
		//Updating pathHandler logic
		GameScreen.packageHandler.update();
		
		//Updating connector logic
		GameScreen.connectorHandler.update();		
		
		//Updating buildings
		GameScreen.buildingHandler.updateAll();
		
		//Updating rightClickMenu text
		GameScreen.rightClickMenuManager.update();
	}
	
	public void recalculateSpeed(int newTick) {
	   	TICKS_PER_SECOND = newTick;
	   	SKIP_TICKS = 1000 / newTick;
	}
	
	
	
	public void resumeSim() {
		paused = false;
		System.out.println("SimThread - Resuming at tick: " + simTick);
	}
	
	public void pauseSim() {
		paused = true;
		System.out.println("SimThread - Pausing at tick: " + simTick);
		
		/*System.out.println("SimThread - Pausing at tick: " + simTick);
		if (TICKS_PER_SECOND > 0) {
			lastTicksPerSecond = TICKS_PER_SECOND;
			TICKS_PER_SECOND = 0;
			SKIP_TICKS = 0;
			next_game_tick = getTickCount() + SKIP_TICKS;
		} else {
			TICKS_PER_SECOND = lastTicksPerSecond;
			SKIP_TICKS = 1000 / TICKS_PER_SECOND;
			next_game_tick = getTickCount() + SKIP_TICKS;
		}
		*/
	}	
}
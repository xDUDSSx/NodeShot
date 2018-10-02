package org.dudss.nodeshot;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.screens.GameScreen;

//SIMULATION THREAD
public class SimulationThread implements Runnable {
    //double interpolation; //TODO: implement interpolation
	int loops;
 
    public static int TICKS_PER_SECOND = 24;
    static int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
    int MAX_FRAMESKIP = 15; //30 (15)
    long next_game_tick = getTickCount() + SKIP_TICKS;
    
    int next_chunk_tick = 10;
    
    public static int simTick;
    
    public void run() {
    	System.out.println("SimThread daemon running!");    
    	
    	GameScreen.simFac = 1.0;
    	
    	long remainder;
    	long t1;
    	long t2;
    	long timeElapsed;
   
    	while(Base.running)
   	    {
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
   					System.out.println("Sim thread is keeping up.");
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
    
    long getTickCount() {
		return System.currentTimeMillis();
	}
	
	void updateLogic() {
		simTick++;
		//Node movement
		/*if (Base.randomMovement) {
				for (Node n : GameScreen.nodelist) {
				n.move();		       
			}
		}
		*/
		
		//Updating all chunks
		if (simTick >= next_chunk_tick) {
			next_chunk_tick += 10;
			System.out.println("update");
			GameScreen.chunks.updateAll();
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
	
	public static void recalculateSpeed(int newTick) {
	   	TICKS_PER_SECOND = newTick;
	   	SKIP_TICKS = 1000 / newTick;
	}
	
	public static void pauseSim() {
		System.out.println("SimThread - Pausing at tick: " + simTick);
		Base.running = false;
	}
	 
	public static void resumeSim() {
		System.out.println("SimThread - Resuming ...");
		Base.running = true;
	}
	
}
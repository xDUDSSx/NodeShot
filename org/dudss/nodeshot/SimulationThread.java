package org.dudss.nodeshot;

import org.dudss.nodeshot.entities.Node;
import static org.dudss.nodeshot.BaseClass.*;

//SIMULATION THREAD
public class SimulationThread implements Runnable {
    //double interpolation; //TODO: implement interpolation
		int loops;
 
   	    final int TICKS_PER_SECOND = 30;
   	    final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
   	    final int MAX_FRAMESKIP = 15; //30 (15)
    long next_game_tick = getTickCount() + SKIP_TICKS;
    
    public void run() {
    	System.out.println("Thread running!");    
    	
    	simFac = 1.0;
    	
    	long remainder;
    	long t1;
    	long t2;
    	long timeElapsed;
   
    	while(Base.running)
   	    {
    		//long startTime = System.currentTimeMillis();		   	 	
   	        if(getTickCount() > next_game_tick && loops < MAX_FRAMESKIP) {	
   	        	next_game_tick += (SKIP_TICKS/simFac); //Set next expected sim calc, modify with simFactor
   	        	
   	        	t1 = getTickCount();
   	        	updateLogic();
   				t2 = getTickCount();
   				timeElapsed = t2 - t1;
   				//System.out.println("Sim calculation finished on: " + Thread.currentThread().getName() + ", time elapsed: " + timeElapsed + ", loops: " + loops);
   				//System.out.println("Nodelist size: " + nodelist.size());
   				
   				//Cant keep up?
   				if (timeElapsed > SKIP_TICKS) {
   					//double prevSimFac = simFac;
   					simFac = ((double)SKIP_TICKS/(double)timeElapsed);
   					/*System.out.println("Sim calculation thread: " + Thread.currentThread().getName() + " can't keep up! "
   							+ "(" + timeElapsed + " vs " + SKIP_TICKS +"),"
   							+ "decreasing simFac (" + prevSimFac + "->" + simFac + ")");
   					*/
   				} else if (simFac < 1.0) {
   					System.out.println("Sim thread is keeping up.");
   					simFac = 1.0;
   				}
   				
   				loops++;  
   				
   				//sFPS calculation
	   	        currentSimTimeTick = System.currentTimeMillis();
	   	        simFrameCount++;	 
	   	        if(currentSimTimeTick >= nextSimTimeTick) {
		   	        nextSimTimeTick = currentSimTimeTick + 1000;
		   	        sfps = simFrameCount;
		   	        simFrameCount = 0;
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
		//Node movement
		if (Base.randomMovement) {
				for (Node n : BaseClass.nodelist) {
				n.move();		       
			}
		}
		//Updating pathHandler logic
		BaseClass.packageHandler.update();
		
		//Updating connector logic
		BaseClass.nodeConnectorHandler.update();		
	}

}
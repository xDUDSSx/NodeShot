package org.dudss.nodeshot;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Section;

/**Corruption update calculation worker thread. Serves as a <b>re-usable</b> {@link Thread} that updates chunks at a slower rate than the main {@link SimulationThread#simLoop()}
 * @since <b>v5.1</b> (3.12.18) Part of the corruption optimisation update.*/
public class CorruptionUpdateThread extends Thread {
	
	long lastTick = System.currentTimeMillis();
	
	List<Section> updatedSections;
	
	long elapsedTime;
	
	/**Corruption update calculation worker thread.
	 * @since <b>v5.1</b> (3.12.18) Part of the corruption optimisation update.*/
	CorruptionUpdateThread() {
		this.setName("CorruptionUpdateThread");
		updatedSections = new ArrayList<Section>();
	}
	
	public void run() {
		//try {
		    while(Base.running) {   	
		    	System.out.println(this.getName() + " thread loop executed, last interval: " + (System.currentTimeMillis() - lastTick) + " Logic update time: " + elapsedTime);
		    	lastTick = System.currentTimeMillis();
		    	
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
		    	elapsedTime = System.currentTimeMillis() - lastTick; 
		    	//synchronized(this) {
		    		
		    		//this.wait();
		    	//}
		    }
		//} catch (InterruptedException e) {
			//BaseClass.errorManager.report(e, "CorruptionUpdateThread error!");
		//}
	}   
}

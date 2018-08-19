package org.dudss.nodeshot.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.entities.Node;

import com.badlogic.gdx.graphics.Color;

public class PackageHandler {
	
	List<PackagePathHandler> pathHandlers;
	
	public PackageHandler() {
		pathHandlers = new CopyOnWriteArrayList<PackagePathHandler>();
	}
	
	//To add a Path
	public void addPath(Node from, Node to) {
		PackagePathHandler newPPH = new PackagePathHandler(from, to);
		pathHandlers.add(newPPH);
		newPPH.start();
	}
	
	public void addPath(Node from, Node to, Color c) {
		PackagePathHandler newPPH = new PackagePathHandler(from, to);
		newPPH.setColor(c);
		pathHandlers.add(newPPH);
		newPPH.start();
	}
	
	public void clear() {
		pathHandlers.clear();
		BaseClass.packagelist.clear();
		BaseClass.nodeConnectorHandler.removeAllPackagesInConnectors();
	}
	
	public void update() {
		List<PackagePathHandler> toRemove = new ArrayList<PackagePathHandler>();
		
		//Cycle through pPHs, update them, and select ones that are done for removal (after iterating!)
		for (PackagePathHandler pPH : pathHandlers) {
			if (pPH.isDone() == true) {
				toRemove.add(pPH);
			} else {
				pPH.update();
			}
		}
		pathHandlers.removeAll(toRemove);
	
	}
	
	public List<PackagePathHandler> getAllPackagePathHandlers() {
		return pathHandlers;
	}
	
	public int getNumberOfPaths() {
		return pathHandlers.size();
	}
}

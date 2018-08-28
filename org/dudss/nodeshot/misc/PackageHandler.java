package org.dudss.nodeshot.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.entities.Connector;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;

import com.badlogic.gdx.graphics.Color;

public class PackageHandler {
	
	List<PathHandler> pathHandlers;
	
	public PackageHandler() {
		pathHandlers = new CopyOnWriteArrayList<PathHandler>();
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

	public void addPath(Node from, Node to, Package p) {
		PackagePathHandler newPPH = new PackagePathHandler(from, to, p);
		if (newPPH.failed != true) {
			pathHandlers.add(newPPH);
			newPPH.start();
		}
	}
	
	public void addIndefinitePath(Package p, Connector c) {
		IndefinitePathHandler newIPH = new IndefinitePathHandler(p, c);
		pathHandlers.add(newIPH);
		newIPH.start();		
	}
	
	public void clear() {
		pathHandlers.clear();
		GameScreen.packagelist.clear();
		GameScreen.nodeConnectorHandler.removeAllPackagesInConnectors();
	}
	
	public void update() {
		List<PackagePathHandler> toRemove = new ArrayList<PackagePathHandler>();
		
		//Cycle through pPHs, update them, and select ones that are done for removal (after iterating!)
		for (PathHandler pPH : pathHandlers) {
			if (pPH.isDone() == true) {
				toRemove.add((PackagePathHandler) pPH);
			} else {
				pPH.update();
			}
		}
		pathHandlers.removeAll(toRemove);
	
	}
	
	public List<PathHandler> getAllPathHandlers() {
		return pathHandlers;
	}
	
	public int getNumberOfPaths() {
		return pathHandlers.size();
	}
}

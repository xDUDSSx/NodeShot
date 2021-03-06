package org.dudss.nodeshot.misc;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.connectors.Conveyor;
import org.dudss.nodeshot.entities.nodes.Node;

import com.badlogic.gdx.graphics.Color;

/**A manager object that keeps track of all {@link PathHandler}s.*/
public class PackageHandler {
	
	List<PathHandler> pathHandlers;
	
	public PackageHandler() {
		pathHandlers = new CopyOnWriteArrayList<PathHandler>();
	}
	
	/**Dont use anymore*/
	@Deprecated
	public void addPath(Node from, Node to) {
		DefinitePathHandler newPPH = new DefinitePathHandler(from, to);
		pathHandlers.add(newPPH);
		newPPH.start();
	}
	
	/**Dont use anymore*/
	@Deprecated
	public void addPath(Node from, Node to, Color c) {
		DefinitePathHandler newPPH = new DefinitePathHandler(from, to);
		newPPH.setColor(c);
		pathHandlers.add(newPPH);
		newPPH.start();
	}
	
	/**Create a path handler with the distance algorithm and add it to the package handler*/
	public void addPath(Node from, Node to, Package p) {
		DefinitePathHandler newPPH = new DefinitePathHandler(from, to, p);
		if (newPPH.failed != true) {
			p.setPathHandler(newPPH);
			Connector firstConnector = GameScreen.connectorHandler.getConnectorInbetween(from, newPPH.getNextNode(), from.getConnectors());
			boolean clear = firstConnector.checkEntrance(from, Base.PACKAGE_BLOCK_RANGE);
			if (clear) {
				pathHandlers.add(newPPH);
				newPPH.start();
			}
		}
	}
	
	/**Create an indefinite path handler and add it to the package handler*/
	public void addIndefinitePath(Package p, Conveyor c) {
		IndefinitePathHandler newIPH = new IndefinitePathHandler(p, c);
		p.setPathHandler(newIPH);
		pathHandlers.add(newIPH);
		newIPH.start();		
	}
	
	/**Removes all packages from the game.*/
	public void clear() {
		pathHandlers.clear();
		GameScreen.packagelist.clear();
		GameScreen.connectorHandler.removeAllPackagesInConnectors();
	}
	
	
	/*public void update() {
		List<PathHandler> toRemove = new ArrayList<PathHandler>();
		
		//Cycle through pHs, update them, and select ones that are done for removal (after iterating!)
		for (PathHandler pH : pathHandlers) {
			if (pH.isDone() == true) {
				toRemove.add(pH);
			} else {
				pH.update();
			}
		}
		pathHandlers.removeAll(toRemove);
	
	}*/
	
	public List<PathHandler> getAllPathHandlers() {
		return pathHandlers;
	}
	
	/**Returns the number of {@link PathHandler}s.*/
	public int getNumberOfPaths() {
		return pathHandlers.size();
	}
}

package org.dudss.nodeshot.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.connectors.Conveyor;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.screens.GameScreen;

/**A {@link PathHandler} that has no path-finding algorithm backing it up. This handler moves packages forward along one-way {@link Conveyor}s.*/
public class IndefinitePathHandler implements PathHandler {

	Package currentPackage;
	Conveyor currentConnector;
		
	boolean done = false;
	
	/**A {@link PathHandler} that has no path-finding algorithm backing it up. This handler moves packages forward along one-way {@link Conveyor}s.*/
	IndefinitePathHandler(Package p, Conveyor c) {
		currentPackage = p;
		currentConnector = c;
	}
	
	@Override
	public void start() {
		if (currentPackage.to == null) {
			currentPackage.resetState(currentPackage.from, currentConnector.getFurtherNode(currentPackage.from));
		}
		currentConnector.add(currentPackage);	
		currentPackage.go();
	}
	
	@Override
	public boolean nextNode() {
		if (currentPackage.going == false) {		
			//Gets all possible connectors to go
			List<Connector> connectors = new CopyOnWriteArrayList<Connector>(currentPackage.to.getConnectors());
			
			//Removing currentConnector (we wont go again)
			connectors.remove(currentConnector);
			
			//remove all regular connectors and conveyors facing the opposite direction from the connector list, (leave only conveyors THAT face the appropriate direction)
			List<Connector> toBeRemoved = new ArrayList<Connector>();
			for (Connector c : connectors) {
				if (c.getType() == EntityType.CONNECTOR) {
					toBeRemoved.add(c);
				}
				
				if (c.getType() == EntityType.CONVEYOR_BUILDING) {
					if (!currentConnector.facesTheSameDirection((Conveyor)c)) {
						toBeRemoved.add(c);
					}
				}
				
			}
			connectors.removeAll(toBeRemoved);
			
			//Selecting route
			if (connectors.size() != 0) {				
				//Selecting a random conveyor
				Conveyor nextConveyor = (Conveyor) connectors.get(Base.getRandomIntNumberInRange(0, connectors.size() - 1));						
				boolean isNextConnectorClear = nextConveyor.checkEntrance(nextConveyor.getFrom(), Base.PACKAGE_BLOCK_RANGE);
				if (isNextConnectorClear) {
					currentPackage.resetState(nextConveyor.getFrom(), nextConveyor.getTo());							
					currentConnector.remove(currentPackage);
					nextConveyor.add(currentPackage);
					currentConnector = (Conveyor) nextConveyor;	
					currentPackage.go();	
					return true;
				}
			}
			return false; //No nodes to go, return unsuccessful
		} else {
			throw new RuntimeException(this.getClass().getName() + " .nextNode() called on an active package!");
		}
	}
	
	@Override
	public void finish() {
		done = true;
		GameScreen.packageHandler.pathHandlers.remove(this);
	}

	@Override
	public boolean isDone() {
		return done;
	}
	
	@Override
	public PathHandlerType getType() {
		return PathHandlerType.IndefinitePathHandler;
	}
}

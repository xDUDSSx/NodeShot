package org.dudss.nodeshot.utils;

import static org.dudss.nodeshot.screens.GameScreen.selectedID;
import static org.dudss.nodeshot.screens.GameScreen.selectedIndex;
import static org.dudss.nodeshot.screens.GameScreen.selectedType;
import static org.dudss.nodeshot.screens.GameScreen.selectedEntity;

import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.entities.Package;

/**A utility class that handles {@link Entity} highlighting.*/
public class Selector {
	
	/**Deselects the currently selected {@link Entity}.*/
	public static void deselect() {
		if (selectedEntity instanceof AbstractBuilding) {
			((AbstractBuilding) selectedEntity).outline(false);
		}
		selectedEntity = null;
    	selectedID = -1;
    	selectedIndex = -1;
    	selectedType = EntityType.NONE;
    	GameScreen.toolbarMenu.updateMainPanel();
    }
    
	/**Selects the specified {@link Node}.*/
    public static void selectNode(Node n) {
    	selectedEntity = n;
    	selectedID = n.getID();
        selectedIndex = n.getIndex();
        selectedType = EntityType.NODE;            
    }
    
    /**Selects the specified {@link Package}.*/
    public static void selectPackage(Package p) {
    	selectedEntity = p;
    	selectedID = p.getID();
        selectedIndex = p.getIndex();    
        selectedType = EntityType.PACKAGE;    
    }
    
    /**Selects the specified {@link Connector}.*/
    public static void selectNodeConnector(Connector nC) {
    	selectedEntity = nC;
    	selectedID =  nC.getID();
        selectedIndex = nC.getIndex();
        selectedType = EntityType.CONNECTOR;
    }
    
    /**Selects the specified {@link AbstractBuilding}.*/
    public static void selectBuilding(AbstractBuilding b) {
    	Selector.deselect();
    	selectedEntity = b;
    	b.outline(true);
    	selectedID =  b.getID();
        selectedIndex = b.getIndex();
        selectedType = EntityType.BUILDING;   
    }
}

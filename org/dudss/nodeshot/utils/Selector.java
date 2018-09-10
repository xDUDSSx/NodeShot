package org.dudss.nodeshot.utils;

import static org.dudss.nodeshot.screens.GameScreen.selectedID;
import static org.dudss.nodeshot.screens.GameScreen.selectedIndex;
import static org.dudss.nodeshot.screens.GameScreen.selectedType;

import org.dudss.nodeshot.entities.Connector;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Package;

public class Selector {
	public static void deselect() {
    	selectedID = -1;
    	selectedIndex = -1;
    	selectedType = EntityType.NONE;
    }
    
    public static void selectNode(Node n) {
    	selectedID = n.getID();
        selectedIndex = n.getIndex();
        selectedType = EntityType.NODE;            
    }
    
    public static void selectPackage(Package p) {
    	selectedID = p.getID();
        selectedIndex = p.getIndex();    
        selectedType = EntityType.PACKAGE;    
    }
    
    public static void selectNodeConnector(Connector nC) {
    	selectedID =  nC.getID();
        selectedIndex = nC.getIndex();
        selectedType = EntityType.CONNECTOR;
    }
}

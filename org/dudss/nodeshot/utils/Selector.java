package org.dudss.nodeshot.utils;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.Connector;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.screens.GameScreen.EntityType;

import static org.dudss.nodeshot.screens.GameScreen.*;

public class Selector {
	public static void deselect() {
    	selectedID = -1;
    	selectedIndexo = -1;
    	selectedType = EntityType.NONE;
    }
    
    public static void selectNode(Node n) {
    	selectedID = n.getID();
        selectedIndexo = n.getIndex();
        selectedType = EntityType.NODE;            
    }
    
    public static void selectPackage(Package p) {
    	selectedID = p.getID();
        selectedIndexo = p.getIndex();    
        selectedType = EntityType.PACKAGE;    
    }
    
    public static void selectNodeConnector(Connector nC) {
    	selectedID =  nC.getID();
        selectedIndexo = nC.getIndex();
        selectedType = EntityType.CONNECTOR;
    }
}

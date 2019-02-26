package org.dudss.nodeshot.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.screens.GameScreen;

/**Unsed = undocumented*/
public class GraphComponentsAlgorithm {
	
	ArrayList<String> state = new ArrayList<String>();
	
	ArrayList<Node> bufferNodes = new ArrayList<Node>();
	List<ArrayList<Node>> webNodesList = new ArrayList<ArrayList<Node>>();
	
	int webs;
	
	public GraphComponentsAlgorithm() {
		this.process();
	}
	
	void process() {
		    for (int i = 0; i < GameScreen.nodelist.size(); i++) {		
				state.add("FRESH");
				GameScreen.nodelist.get(i).setState("FRESH");
			}
			
		    int counter = 0;
			
			for (Node n : GameScreen.nodelist) {			
				if (state.get(GameScreen.nodelist.indexOf(n)).equals("FRESH")) {
					counter++;
												
					try {
						goThrough(n);
					} catch (InterruptedException e) {
						BaseClass.errorManager.report(e, "GCA InterruptedException");
					}
							
					webNodesList.add(new ArrayList<Node>(bufferNodes));
					bufferNodes.clear();
				}
			}	

	    	for (Node n : GameScreen.nodelist) {
				n.setState("");
	    	}
	    	state.clear();	
	    	webs = counter;
	}
	
	void goThrough(Node n) throws InterruptedException {
		if(state.get(GameScreen.nodelist.indexOf(n)).equals("FRESH")) {
			state.set(GameScreen.nodelist.indexOf(n), "OPENED");
			n.setState("OPENED");
			bufferNodes.add(n);
			for (Node conNode : n.getAllConnectedNodes()) {
				goThrough(conNode);
			}
			state.set(GameScreen.nodelist.indexOf(n), "CLOSED");
			n.setState("CLOSED");
		}
	}
	
	//Getter
	public int getNumberOfWebs() {
		return webs;
	}
	
	//Getter
	public List<ArrayList<Node>> getWebNodesList() {	
		//Prints out lists
		/*for(ArrayList<Node> arN : webNodesList) {
			System.out.println("\nList:");
			for (Node n : arN) {
				System.out.print(n.getIndex() + ", ");
			}
			System.out.println("\n");
		}
		*/
		return webNodesList;
	}
}

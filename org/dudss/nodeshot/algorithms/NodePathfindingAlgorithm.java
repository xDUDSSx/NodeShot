package org.dudss.nodeshot.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.algorithms.GraphComponentsAlgorithm;

public class NodePathfindingAlgorithm {
	
	int steps = -1;
	int step;
	
	Node start;
	Node target;
	
	ArrayList<Node> exploredZoneNodes = new ArrayList<Node>();
	ArrayList<Node> medianZoneNodes = new ArrayList<Node>();
	ArrayList<Node> activeZoneNodes = new ArrayList<Node>();
	
	List<List<Node>> possiblePathways = new ArrayList<List<Node>>();
	Map<Integer, ArrayList<Node[]>> nodeDuosMap; 
	
	public NodePathfindingAlgorithm(Node start, Node target) {
		this.start = target;	 
		this.target = start;
		exploredZoneNodes.add(this.start);
		nodeDuosMap = new HashMap<Integer, ArrayList<Node[]>>(); 
		
		//Does all the calculations and makes Path and Steps accessible on init
		this.process();
	}
	
	void process() {
		
		//Steps calculation, nodeDuosMap filling
		if (start == target) {
			steps = 0;
			return;
		}
		
		//Check if nodes are in the same web
		GraphComponentsAlgorithm gCA = new GraphComponentsAlgorithm();
		List<ArrayList<Node>> allWebNodes = gCA.getWebNodesList();
		
		Boolean nodesInTheSameWeb = false;
		for (ArrayList<Node> web : allWebNodes) {
			if (web.contains(start) && web.contains(target)) {
				nodesInTheSameWeb = true;
				break;
			}
		}
		
		if(nodesInTheSameWeb == false) {
			System.out.println("Start/Target aren't in the same web!");
			steps = -1;
			return;
		}
		
		step++;		
		nextZone(true);
	
		//Path calculation (Uses nodeDuosMap generated by previous code)
		newFork(target.getIndex(), nodeDuosMap.size(), new ArrayList<Node>());
		
	}
	
	void nextZone(Boolean first) {
		ArrayList<Node[]> listOfStepNodeDuos = new ArrayList<Node[]>();
		
		if (first == true) {
			medianZoneNodes.add(start);
		}
		
		for (Node n : medianZoneNodes) {
			ArrayList<Node> temp = new ArrayList<Node>(n.getAllConnectedNodes());
			for(Node node : temp) {
				if (!medianZoneNodes.contains(node) && !exploredZoneNodes.contains(node)) {
					Node[] duoArray = new Node[]{n, node};
					
					Boolean isDuplicate = false;	
					for (Node[] array : listOfStepNodeDuos) {
						if (Arrays.equals(array, duoArray)) {
							isDuplicate = true;
						}
					}
					if (!isDuplicate) {
						listOfStepNodeDuos.add(duoArray);
					}
					
					activeZoneNodes.add(node);
				}
			}
		}
		
		nodeDuosMap.put(step, listOfStepNodeDuos);

		if(activeZoneNodes.size() == 0) {
			return;
		}
		
		if (checkList(activeZoneNodes) == true) {
			return;
		}
		
		step++;
		exploredZoneNodes.addAll(medianZoneNodes);
		medianZoneNodes.clear();
		medianZoneNodes.addAll(activeZoneNodes);
		activeZoneNodes.clear();
		
		
		nextZone(false);
	}
	
	Boolean checkList(ArrayList<Node> list) {
		for (Node n : list) {
			if (n == target) {
				int step_t = step;
				if (steps == -1 || steps > step) {
					steps = step_t;
				}
				return true;
			}
		}
		return false;
	}
	
	void newFork(int previousIndex, int step, ArrayList<Node> currentChain) {
		
		int tempStep = new Integer(step);
		int tempPreviousIndex = new Integer(previousIndex);
		ArrayList<Node> tempCurrentChain = new ArrayList<Node>(currentChain);
		
		ArrayList<Node[]> currentStepDuos = null;
		
		if (!(tempStep <= 0)) {
			currentStepDuos = new ArrayList<Node[]>(nodeDuosMap.get(tempStep));
		} else {
			
			//Removing duplicates (4-3 3-2 2-1 --> 4 - 3 - 2 - 1)
			ArrayList<Node> newPathway = new ArrayList<Node>();
			
			Node previous = null;
			for (Node n : tempCurrentChain) {
				if (previous == null || previous != n) {
					newPathway.add(n);
				}
				previous = n;
			}

			possiblePathways.add(newPathway);

			return;
		}
		
		for (Node[] nA : currentStepDuos) {
			if (tempPreviousIndex == nA[1].getIndex() || tempPreviousIndex == -1) {
				tempCurrentChain.add(nA[1]);
				tempCurrentChain.add(nA[0]);
				int newStep = step - 1;
				
				newFork(nA[0].getIndex(), new Integer(newStep), tempCurrentChain);
				
				tempCurrentChain.remove(tempCurrentChain.size() - 1);
			} 
		}
	}
	
	//Getter
	public int getSteps() {
		return steps;
	}
	
	//Getter
	public List<List<Node>> getPathway() {
		return possiblePathways;
	}
}
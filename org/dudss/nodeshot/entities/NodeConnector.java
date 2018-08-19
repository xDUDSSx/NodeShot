package org.dudss.nodeshot.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.BaseClass;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.dudss.nodeshot.Base.PACKAGE_BLOCK_RANGE;
import static org.dudss.nodeshot.Base.PACKAGE_SPEED;

public class NodeConnector implements Entity{
	Node from;
	Node to;
	
	int id;
	
	List<Package> packages;
	
	public NodeConnector(Node from, Node to) {
		this.from = from;
		this.to = to;
		
		this.id = java.lang.System.identityHashCode(this);
		
		packages = new CopyOnWriteArrayList<Package>();
	}
	
	public void draw(ShapeRenderer sR) {
		if (BaseClass.highlightedIndex == this.getID()) {
			sR.setColor(Color.WHITE);
			sR.rectLine(from.getCX(), from.getCY(), to.getCX(), to.getCY(), Base.lineWidth + 1);
		}
		
		if (packages.size() == 0) {
			sR.setColor(new Color(255, 0, 0, 0.8f));
		} else {
			sR.setColor(new Color(0, 255, 0, 0.8f));
		}
			
		sR.rectLine(from.getCX(), from.getCY(), to.getCX(), to.getCY(), Base.lineWidth);
		sR.setColor(Color.WHITE);	
	} 
		
	/**
	* Call a connector update, updating logic>
	*/
	public void update() {
       	for (Package packAge : packages) {
	 		if(packAge.going == true) {
	 			
	 			Vector2 p1 = new Vector2(packAge.from.getCX(), packAge.from.getCY());
                Vector2 p2 = new Vector2(packAge.to.getCX(), packAge.to.getCY());
                Vector2 vector = new Vector2(p2.x - p1.x, p2.y - p1.y);
	   	 		
	   	 		Boolean packageJam = false;
	   	 		Boolean toBeJammed = false;
	   	 		for (Package p : packages) {
	   	 			if (p != packAge) {
	   	 				if (p.from == packAge.from) {
	   	 					//System.out.println("p.from: " + p.from.getID() + " p.to: " + p.to.getID() + " isFinished " + p.isFinished());	
	   	 					//System.out.println("RIGHT WAY");
		   	 				//if ((packAge.percentage == p.percentage && p.isFinished() == false) ) {
			   	 				
		   	 				//} 
	   	 					
		   	 				if (((p.percentage <= (packAge.percentage + PACKAGE_SPEED + PACKAGE_BLOCK_RANGE)) && (packAge.percentage <= (p.percentage + PACKAGE_BLOCK_RANGE))) && p.isFinished() == false) {		   	 				
		   	 					if (packAge.percentage > p.percentage) {
		   	 						packageJam = false;
		   	 					} else 
		   	 					if (packAge.percentage == p.percentage) {
			   	 					if(!(this.packages.indexOf(packAge) > this.packages.indexOf(p))) {
			   	 						//System.out.println(this.packages.indexOf(packAge) + " FROM: " + packAge.from.getID() + " -- case!");
			   	 						packageJam = true;
				   	 				}	   	 					
		   	 					} else {
		   	 						packageJam = true;
		   	 					}
		   	 					//System.out.println("packageJam true p.percentage: " + p.percentage + " thisPacPercentage: " + packAge.percentage);
		   	 				} 
	   	 				} else {
	   	 					//System.out.println("OPPOSITE WAY");
	   	 					//if (((p.percentage + (PACKAGE_BLOCK_RANGE/2) + PACKAGE_SPEED) + (packAge.percentage + (PACKAGE_BLOCK_RANGE) + PACKAGE_SPEED) >= 100) && ((p.percentage + (PACKAGE_BLOCK_RANGE) + PACKAGE_SPEED) + (packAge.percentage + (PACKAGE_BLOCK_RANGE/2) + PACKAGE_SPEED) <= 110) && p.isFinished() == false) {
		   	 					//packageJam = true;
		   	 					//System.out.println("INVERSE - packageJam true p.percentage: " + p.percentage + " thisPacPercentage: " + packAge.percentage);
		   	 				//}
			 				//System.out.println("p.from: " + p.from.getID() + " p.to: " + p.to.getID() + " isFinished " + p.isFinished());	
							//System.out.println("RIGHT WAY");
			 				//if ((packAge.percentage == p.percentage && p.isFinished() == false) ) {
			   	 				
			 				//} 
							
			 				//**
			 				/*if (((p.percentage <= (packAge.percentage + PACKAGE_SPEED + PACKAGE_BLOCK_RANGE)) && (packAge.percentage <= (p.percentage + PACKAGE_BLOCK_RANGE))) && p.isFinished() == false) {		   	 				
		   	 					if (packAge.percentage > p.percentage) {
		   	 						packageJam = false;
		   	 					} else 
		   	 					if (packAge.percentage == p.percentage) {
			   	 					if(!(this.packages.indexOf(packAge) > this.packages.indexOf(p))) {
			   	 						//System.out.println(this.packages.indexOf(packAge) + " FROM: " + packAge.from.getID() + " -- case!");
			   	 						packageJam = true;
				   	 				}	   	 					
		   	 					} else {
		   	 						packageJam = true;
		   	 					}
		   	 					//System.out.println("packageJam true p.percentage: " + p.percentage + " thisPacPercentage: " + packAge.percentage);
		   	 				} 
			 				//**
			 				*/
		   	 			}
	   	 			}
	   	 		}
	   	 		
	   	 		if (toBeJammed == true) {
	   	 			packageJam = true;
	   	 		}
	   	 		
	   	 		//System.out.println("packageJAM: " + packageJam);
	   	 		//
				// System.out.println("Package going ack pkgJam: " + packageJam + " " + packAge.getID() + " AT: " + System.currentTimeMillis());
				
	   	 		
	   	 		if (!(packAge.percentage >= 95) && packageJam == false) {
	   	 			//System.out.println(this.packages.indexOf(packAge) + " FROM: " + packAge.from.getID() + " Percentage up " + packAge.percentage);
	 				packAge.percentage += PACKAGE_SPEED;	 	
	 				
	 				Vector2 finalVector = new Vector2((float)(vector.x * (0.01 * packAge.percentage)), (float)(vector.y * (0.01 * packAge.percentage)));
	 				
	 				//System.out.println("Moving package " + packAge.getID() + " AT: " + System.currentTimeMillis());
	 				packAge.transform((float)((p1.x - packAge.radius/2) + finalVector.x), (float)((p1.y - packAge.radius/2) + finalVector.y));
	 				packAge.currentMovePos = new Vector2((float)((p1.x - packAge.radius/2) + finalVector.x),  (float)((p1.y - packAge.radius/2) + finalVector.y));
	   	 		} else if (packAge.percentage >= 95 && packageJam == false && packAge.to.isClosed() == false) {
	   	 			//Check if the path for the next pkg is free
	   	 			Boolean nextPackageJam = false;
	   	 			if (packAge.getNextNode() != null) {
	   	 				NodeConnector nextConnector = BaseClass.nodeConnectorHandler.getConnectorInbetween(packAge.to, packAge.getNextNode(), packAge.to.connectors);
	   	 				//System.out.println(this.packages.indexOf(packAge) + " FROM: " + packAge.from.getID() + " packageTO: " + packAge.to.getID() + " packageNEXT: " + packAge.getNextNode().getID() + " nextConnector: " + nextConnector.getID());
			   	 		for (Package p : nextConnector.packages) {			   	 			
	   	 					//System.out.println("SAMEPKGinNextnode");
		   	 				if (0 <= (p.percentage + PACKAGE_BLOCK_RANGE) && p.percentage <= (0 + PACKAGE_BLOCK_RANGE) && p.isFinished() == false) {
		   	 					nextPackageJam = true;
		   	 					//System.out.println(this.packages.indexOf(packAge) + " FROM: " + packAge.from.getID() + " nextPackageJam true p.percentage: " + p.percentage + " 0Percentage: " + 0);
			   	 			}			
			   	 		}
	   	 			}
	   	 			
	   	 			if (!nextPackageJam) {
	   	 				//System.out.println("Destroying package FROM: " + packAge.from.getID());
		   	 			packAge.destroy();
		   	 			packages.remove(packAge);
		   	 		}
		   	 	}
	 		}
	 	}
	}
	
	/**
	* Check if the area around {@link Node} n (0 to blockRange plus packageSpeed) is clear for a new {@link Package}
	* @param n
	*/
	public Boolean checkEntrance(Node n, float blockRange, float packageSpeed) {
		Boolean clear = true;
		for (Package p : this.packages) {
			if (this.from == n) {
				if (p.from == this.from) {
					if (0 <= (p.percentage + PACKAGE_BLOCK_RANGE) && p.percentage <= (0 + PACKAGE_BLOCK_RANGE) && p.isFinished() == false) {
						clear = false;				
			 		}	
				} else {
					if ((100 - PACKAGE_BLOCK_RANGE) <= (p.percentage + PACKAGE_BLOCK_RANGE) && p.percentage <= 100 && p.isFinished() == false) {
						clear = false;				
			 		}	
				}
			} else {
				if (p.from == this.from) {
					if ((100 - PACKAGE_BLOCK_RANGE) <= (p.percentage + PACKAGE_BLOCK_RANGE) && p.percentage <= 100 && p.isFinished() == false) {
						clear = false;				
			 		}	
				} else {
					if (0 <= (p.percentage + PACKAGE_BLOCK_RANGE) && p.percentage <= (0 + PACKAGE_BLOCK_RANGE) && p.isFinished() == false) {
						clear = false;				
			 		}	
				}
			}	
		}
		
		return clear;
	}
	
	//Add pkg to connector
	public void add(Package pkg) {
		if (pkg.from == this.from) {
			pkg.transform(from.getCX() - pkg.radius/2, from.getCY() - pkg.radius/2);
		} else {
			pkg.transform(to.getCX() - pkg.radius/2, to.getCY() - pkg.radius/2);
		}
		
		packages.add(pkg);
	}
	
	//Remove pkg from connector
	public void remove(Package pkg) {
		packages.remove(pkg);
	}
	
	//Remove all packages from connector
	public void removeAllPackages() {
		packages.clear();
	}
	
	public Node getFrom() {
		return from;
	}
	
	public Node getTo() {
		return to;
	}
	
	public int getID() {
		return id;
	}
	
	public List<Package> getPackages() {
		return packages;
	}
}

package org.dudss.nodeshot;

import com.badlogic.gdx.Gdx;

import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.NodeConnector;
import org.dudss.nodeshot.entities.Package;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


//TODO: Add node connect dragging 

public class Base {

	private static final long serialVersionUID = 1L;

	static Boolean running = true;
	static Boolean noupdate = false;
	static Boolean toggleBrush = false;
	public static Boolean randomMovement = false;
	static Boolean logFirst = false;
	static Boolean showWeb = true;

	public static int WIDTH = 250;
	public static int HEIGHT = 400;

	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;

	public static int RADIUS = 16;
	public static int PACKAGE_RADIUS = 16;

	public static float PACKAGE_SPEED = 0.5f;
	public static float PACKAGE_BLOCK_RANGE = 10;

	public static int CONNECT_DISTANCE = 900;
	public static int MAX_CONNECTIONS = 9;

	public static int lineWidth = 3;
	public static int HUD_FONT_SIZE = 32;

	static int paint_spacing = 2;

	//static Controls controls;
	//static JButton btnSet;

	//private JTextField textField_radius;
	//private JTextField textField_connections;
	//private JTextField textField_connectDistance;

	Base() {
		//This off
		//basePanel.gameLoop();

		//Getting screen (monitor) resolution
		SCREEN_WIDTH = (int) Gdx.graphics.getWidth();
		SCREEN_HEIGHT = (int) Gdx.graphics.getHeight();
	}

	public static float getRandomFloatNumberInRange(int min, int max) {
		if (min == 0 && max == 0) {
			return 0;
		}

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return min + r.nextFloat() * (max - min);
	}

	public static int getRandomIntNumberInRange(int min, int max) {
		if (min == 0 && max == 0) {
			return 0;
		}

		if (min >= max) {
			throw new IllegalArgumentException("max must be greater than min");
		}

		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
	}

	public static String listToString(List<Entity> list) {
		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < list.size(); i++) {
			String s = String.valueOf(list.get(i).getID());
			stringBuilder.append(s + ", ");
		}
		return stringBuilder.toString();
	}

	public static String nodeListToString(List<Node> list) {
		List<Entity> entityList = new ArrayList<Entity>();
		for (final Node n : list) {
			entityList.add(new Entity() {
				@Override
				public int getID() {
					return n.getID();
				}
			});
		}
			return listToString(entityList);
	}
		public static String nodeConnectorListToString (List < NodeConnector > list) {
			List<Entity> entityList = new ArrayList<Entity>();
			for (final NodeConnector n : list) {
				entityList.add(new Entity() {
					@Override
					public int getID() {
						return n.getID();
					}
				});
			}
			return listToString(entityList);
		}

		public static String packageListToString (List < Package > list) {
			List<Entity> entityList = new ArrayList<Entity>();
			for (final Package n : list) {
				entityList.add(new Entity() {
					@Override
					public int getID() {
						return n.getID();
					}
				});
			}
			return listToString(entityList);
		}

	}
/*
class GraphNumbering {
	ArrayList<String> state = new ArrayList<String>();
	
	public void getNumberOfGraphs() {

		class WebWorker extends SwingWorker<Void, Void> {

			int webs = 0;
			
		    protected Void doInBackground() throws Exception {
		    	for (int i = 0; i < BasePanel.nodelist.size(); i++) {		
					try {
						Thread.sleep(10);
						//repaint();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					state.add("FRESH");
					BasePanel.nodelist.get(i).setState("FRESH");
				}
				int counter = 0;
				for (Node n : BasePanel.nodelist) {			
					if (state.get(BasePanel.nodelist.indexOf(n)).equals("FRESH")) {
						counter++;
						
						try {
							Thread.sleep(100);
							//repaint();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						try {
							goThrough(n);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				webs = counter;
				System.out.println("Number of webs: " + counter);
				state.clear();
				return null;
		    }
		    
		    protected void done() {
		    	for (Node n : BasePanel.nodelist) {
					n.setState("");
				}
		    	
		    	Thread t = new Thread(new Runnable() {
		    	    public void run() {
		    	    	BasePanel.stringToWrite = String.valueOf(webs);
				    	BasePanel.drawString = true;
				    	try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
				    	BasePanel.drawString = false;
				    	BasePanel.stringToWrite = "";
		    	    }
		    	});  			
		    	t.start(); 	
		    }
		}							
		new WebWorker().execute();	
	}
	
	void goThrough(Node n) throws InterruptedException {
		if(state.get(BasePanel.nodelist.indexOf(n)).equals("FRESH")) {
			Thread.sleep(50);
			//repaint();
			state.set(BasePanel.nodelist.indexOf(n), "OPENED");
			n.setState("OPENED");
			for (Node conNode : n.getAllConnectedNodes()) {
				goThrough(conNode);
			}
			state.set(BasePanel.nodelist.indexOf(n), "CLOSED");
			n.setState("CLOSED");
			Thread.sleep(50);
			//repaint();
		}
	}
}
*/


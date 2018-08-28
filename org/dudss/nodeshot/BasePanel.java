package org.dudss.nodeshot;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.NodeConnector;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.misc.NodeConnectorHandler;
import org.dudss.nodeshot.misc.PackageHandler;

public class BasePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	Timer timer;
	
	long currentRenderTimeTick;
	long nextRenderTimeTick;
	
	long currentSimTimeTick;
	long nextSimTimeTick;
	
	static int rate = 60;
	static int renderRate = 60;
	
	public int fps;
	static int frameCount;
	public int sfps;
	static int simFrameCount;
	public double simFac;
	
	//Unused currently
	//static final Object renderSyncObject = new Object();
	
	static int radius = Base.RADIUS;
	
	static Point mousePos;
	
	static Graphics2D g2d; 
	BufferedImage spritesheet;
	public static BufferedImage[] sprites;
	BufferedImage offImg;
	
	public static CopyOnWriteArrayList<Node> nodelist = new CopyOnWriteArrayList<Node>();
	public static CopyOnWriteArrayList<Package> packagelist = new CopyOnWriteArrayList<Package>();
	
	public static PackageHandler packageHandler;
	public static NodeConnectorHandler nodeConnectorHandler;
	
	static Boolean NodeInfoHidden = false;
	static Boolean NodeSelectiveInfo = true;
	static Boolean NodeConnectRadiusHidden = true;
	
	static Boolean toggleConnectMode = false;
	static Boolean activeNewConnection = false;
	static int newConnectionFromIndex;
	
	static Boolean draggingConnection = false;
	
	static int mouseX;
	static int mouseY;
	static Point lastMousePress;
	static enum MouseType {
		MOUSE_1, MOUSE_2, MOUSE_3
	}
	static MouseType lastMousePressType;
	
	static int selectedIndex = -1;
	static enum EntityType {
		NODE, CONNECTOR, PACKAGE, NONE
	}
	static EntityType selectedType = EntityType.NONE;
	
	public static int indexOfHighlightedNode = -1;
	
	static Boolean drawString = false;
	static String stringToWrite = "";
	
	public static BasicStroke defaultStroke;
	
	BasePanel() {
		defaultStroke = new BasicStroke(1);
		packageHandler = new PackageHandler();
		nodeConnectorHandler = new NodeConnectorHandler();
		
		setFocusable(true);
		
		//NodeGenMouseListener nodeMouseListener = new NodeGenMouseListener();
		
		NodeKeyListener nodeKeyListener = new NodeKeyListener();
		
		//addMouseListener(nodeMouseListener);
		//addMouseMotionListener(nodeMouseListener);
		addKeyListener(nodeKeyListener);
		//Loading and subdividing spreadshe
		
		//TODO: finish - temporary spritesheet loading for packages
		spritesheet = null;
		
		try {
			spritesheet = ImageIO.read(new File("src/res/spritesheet16x16.png"));
		} catch (IOException e) {
			System.out.println("Error loading spritesheet!");
			e.printStackTrace();
		}
		
		final int width = 16;
		final int height = 16;
		final int rows = 2;
		final int cols = 2;
		
		sprites = new BufferedImage[rows * cols];

		for (int i = 0; i < rows; i++)
		{
		    for (int j = 0; j < cols; j++)
		    {
		        sprites[(i * cols) + j] = spritesheet.getSubimage(
		            j * width,
		            i * height,
		            width,
		            height
		        );
		    }
		}
	}
	
	public void gameLoop()
	{	
		//init
		/*if (Base.enableDoubleBuffering) {
			//Creating off-screen bufferedImage
			BufferedImage bI = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
			offImg = toCompatibleImage(bI); 
		}
		*/
		
		//RENDER
   		Runnable renderRunnable = new Runnable() {
   			public void run() { 
   				if(Base.noupdate == false) {
   					repaint();   				
   				}
   			}
   	    };
   	        
   	    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
   	    service.scheduleAtFixedRate(renderRunnable, 0, (1000/renderRate), TimeUnit.MILLISECONDS);
   	  	
   	    //SIMULATION
		class GameLoopThread implements Runnable {	   	   
	   	    //double interpolation; //TODO: implement interpolation
			int loops;
	 
	   	    final int TICKS_PER_SECOND = 30;
	   	    final int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
	   	    final int MAX_FRAMESKIP = 15; //30 (15)
	   	    long next_game_tick = getTickCount() + SKIP_TICKS;
	   	    
	   	    public void run() {
	   	    	System.out.println("Thread running!");    
	   	    	
	   	    	simFac = 1.0;
	   	    	
	   	    	long remainder;
	   	    	long t1;
	   	    	long t2;
	   	    	long timeElapsed;
	   	   
	   	    	while(Base.running)
		   	    {
	   	    		//long startTime = System.currentTimeMillis();		   	 	
		   	        if(getTickCount() > next_game_tick && loops < MAX_FRAMESKIP) {	
		   	        	next_game_tick += (SKIP_TICKS/simFac); //Set next expected sim calc, modify with simFactor
		   	        	
		   	        	t1 = getTickCount();
		   	        	updateLogic();
		   				t2 = getTickCount();
		   				timeElapsed = t2 - t1;
		   				//System.out.println("Sim calculation finished on: " + Thread.currentThread().getName() + ", time elapsed: " + timeElapsed + ", loops: " + loops);
		   				
		   				//Cant keep up?
		   				if (timeElapsed > SKIP_TICKS) {
		   					//double prevSimFac = simFac;
		   					simFac = ((double)SKIP_TICKS/(double)timeElapsed);
		   					/*System.out.println("Sim calculation thread: " + Thread.currentThread().getName() + " can't keep up! "
		   							+ "(" + timeElapsed + " vs " + SKIP_TICKS +"),"
		   							+ "decreasing simFac (" + prevSimFac + "->" + simFac + ")");
		   					*/
		   				} else if (simFac < 1.0) {
		   					System.out.println("Sim thread is keeping up.");
		   					simFac = 1.0;
		   				}
		   				
		   				loops++;  
		   				
		   				//sFPS calculation
			   	        currentSimTimeTick = System.currentTimeMillis();
			   	        simFrameCount++;	 
			   	        if(currentSimTimeTick >= nextSimTimeTick) {
				   	        nextSimTimeTick = currentSimTimeTick + 1000;
				   	        sfps = simFrameCount;
				   	        simFrameCount = 0;
		   	            }
			   	        
			   	        if (loops > 1) {
			   	        	Thread.yield();             
			   	        }		
		   	        }  	        
	   	    		loops = 0;       
	   	    		
	   	    		//calculate remainder (with 1ms threshold for safe operation) (<- might make it unstable)
	   	    		remainder = (next_game_tick - getTickCount());
	   	    		if (remainder < 0) {
	   	    			remainder = 0;
	   	    		}
	   	    		
	   	    		//sleep the remainder
	   	    		try {
						Thread.sleep(remainder);
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	   	    		// interpolation = ((getTickCount() + SKIP_TICKS - next_game_tick ) / SKIP_TICKS );       
	   	    		//render game with interpolation parameter if necessary (drawGame(interpolation) //TODO: create custom method for this and implement interpolation  	    
		   	    }
			}   
		}
		
		Thread simulationThread = new Thread(new GameLoopThread());
		simulationThread.start();
	}
	
	long getTickCount() {
		return System.currentTimeMillis();
	}
	
	void updateLogic() {
		//Node movement
		if (Base.randomMovement) {
				for (Node n : BasePanel.nodelist) {
				n.move();		       
			}
		}
		
		//Updating pathHandler logic
		BasePanel.packageHandler.update();
		
		//Updating connector logic
		BasePanel.nodeConnectorHandler.update();
		
       	/*for (Package packAge : BasePanel.packagelist) {
	 		if(packAge.going == true) {
		        Point2D.Double p1 = new Point2D.Double(packAge.from.getCenterX(), packAge.from.getOriginY());
	   	 		Point2D.Double p2 = new Point2D.Double(packAge.to.getCenterX(), packAge.to.getOriginY());
	   	 		Point2D.Double vector = new Point2D.Double(p2.x - p1.x, p2.y - p1.y);	  	
	   	 		
	   	 		if (!(packAge.percentage >= 100)) {
	 				packAge.percentage += 4;	 	
	 				
	 				Point2D.Double finalVector = new Point2D.Double(vector.x * (0.01 * packAge.percentage), vector.y * (0.01 * packAge.percentage));	 
	 				
	 				packAge.transform((p1.x - packAge.radius/2) + finalVector.x, (p1.y - packAge.radius/2) + finalVector.y, packAge.radius);
	 				packAge.currentMovePos = new Point2D.Double((p1.x - packAge.radius/2) + finalVector.x,  (p1.y - packAge.radius/2) + finalVector.y);
	   	 		} else {
	   	 			packAge.destroy();
	   	 			if(packAge.triggerPackage != null) {
	   	 				packAge.triggerPackage.go();
		   	 		}
		   	 	}
	 		}
	 	} */
	}

	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		
		g2d = (Graphics2D) g;
		
		//TODO: Implement double-buffering
		/*if(Base.enableDoubleBuffering) {
			BufferedImage bI = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
			offImg = toCompatibleImage(bI); 
			Graphics2D offG2d = offImg.createGraphics();
			//org.jogamp.glg2d.GLGraphics2D glg2d = new org.jogamp.glg2d.GLGraphics2D();
			render(offG2d);			
			g2d.drawImage(offImg, 0, 0, null);
		} else {
			render(g2d);			
		}
		*/
		
		//fps tracking
		currentRenderTimeTick = System.currentTimeMillis();
		frameCount++;
		if(currentRenderTimeTick >= nextRenderTimeTick) {
			nextRenderTimeTick = currentRenderTimeTick + 1000;
			fps = frameCount;
			frameCount = 0;
		}
	}
	
	void render(Graphics2D g2d) {
		if (toggleConnectMode == true) {
			g2d.setColor(new Color(25, 20, 0));
		} else {
			g2d.setColor(Color.BLACK);
				
		}
		//Background
		g2d.fillRect(0, 0, getWidth(), getHeight());
	
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);  
		g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		AlphaComposite alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
		g2d.setComposite(alcom);	
		
		if(NodeConnectRadiusHidden == false) {
			drawRangeCircles(g2d);
		}
		
		if (Base.showWeb == true) {
			alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
			g2d.setComposite(alcom);
			
			drawGrid(g2d);
		}
		
		alcom = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
		g2d.setComposite(alcom);
		
		//Sprite test render
		//g2d.drawImage(sprites[0], 60, 60, null);
		
		renderNodes(g2d);

		//Drawing packages
		for (Package p : packagelist) {
			//p.draw(g2d);
		}
		
		if(NodeInfoHidden == false) {
			drawNodeInfo(g2d);
		}
	
		drawFPS(g2d);
		drawStats(g2d);
		
		if(drawString == true) {
			drawString(g2d);
		}
	}
	
	//Optimalization method
	private BufferedImage toCompatibleImage(BufferedImage image)
	{
	    // obtain the current system graphical settings
	    GraphicsConfiguration gfxConfig = GraphicsEnvironment.
	        getLocalGraphicsEnvironment().getDefaultScreenDevice().
	        getDefaultConfiguration();

	    /*
	     * if image is already compatible and optimized for current system 
	     * settings, simply return it
	     */
	    if (image.getColorModel().equals(gfxConfig.getColorModel()))
	        return image;

	    // image is not optimized, so create a new image that is
	    BufferedImage newImage = gfxConfig.createCompatibleImage(
	            image.getWidth(), image.getHeight(), image.getTransparency());

	    // get the graphics context of the new image to draw the old image on
	    Graphics2D g2d = newImage.createGraphics();

	    // actually draw the image and dispose of context no longer needed
	    g2d.drawImage(image, 0, 0, null);
	    g2d.dispose();

	    // return the new optimized image
	    return newImage; 
	}
	
	void renderNodes(Graphics2D g2d) {
		for (int i = 0; i < nodelist.size(); i++) {
			Node n = nodelist.get(i);
			if (n.getIndex() == indexOfHighlightedNode) {
			//	n.draw(g2d, true);
			} else {
				//n.draw(g2d);	
			}
		}
	}
	
	void drawGrid(Graphics2D g2d) {
		if(!nodelist.isEmpty()) {
			g2d.setStroke(new BasicStroke(3));
			g2d.setColor(Color.RED);
			
		//Draw connectMode connection
		if ((toggleConnectMode == true && activeNewConnection == true) || BasePanel.draggingConnection == true) {
			//g2d.drawLine(nodelist.get(newConnectionFromIndex).getCX(), nodelist.get(newConnectionFromIndex).getCY(), mouseX, mouseY);
		}
		
		/* Hardcoded grid rendering //old
		for(int c = 0; c < nodelist.size(); c++) {			
				ArrayList<Node> connectednodes = nodelist.get(c).getConnectedToNodes();
				
				for(int x = 0; x < connectednodes.size(); x++) {
					Node targetnode = connectednodes.get(x);		
					g2d.drawLine(nodelist.get(c).getCX(), nodelist.get(c).getCY(), targetnode.getCX(), targetnode.getCY());			
				}
			}
		
		g2d.setStroke(defaultStroke);
		*/
		
		//nodeConnectorHandler.drawAll(g2d);
		
		}
	}
	
	void drawFPS(Graphics2D g2d) {
		int textheight = g2d.getFontMetrics().getHeight();
		int textwidth = g2d.getFontMetrics().stringWidth("FPS: " + fps);
		int text2width = g2d.getFontMetrics().stringWidth("sFPS: " + sfps);
		
		DecimalFormat df = new DecimalFormat("#.####");
		df.setRoundingMode(RoundingMode.CEILING);
		
		g2d.setColor(Color.gray);
		g2d.fillRect(0, 0, 5 + 5 + textwidth + text2width + 5, textheight*2 + 2 + 2);
		
		g2d.setColor(Color.white);
		g2d.setFont(new Font("Helvetica", Font.PLAIN, 10)); 
		g2d.drawString("FPS: " + fps, 5, textheight + 2);
		g2d.drawString("sFPS: " + sfps, 5 + 5 + textwidth, textheight + 2);
		g2d.drawString("simFac: " + df.format(simFac), 5, textheight*2 + 2);
	}
	
	void drawString(Graphics2D g2d) {
		g2d.setColor(Color.white);
		int WIDTH = this.getWidth();
		int HEIGHT = this.getHeight();

		g2d.setColor(new Color(220, 220, 220));
		g2d.setFont(new Font("Helvetica", Font.PLAIN, 100)); 
		
		int textwidth = g2d.getFontMetrics().stringWidth(stringToWrite);
		int textheight = g2d.getFontMetrics().getHeight();
		
		g2d.drawString(stringToWrite, WIDTH/2 - textwidth/2, HEIGHT - 50 - textheight);
	}
	
	void drawStats(Graphics2D g2d) {
		int textheight = g2d.getFontMetrics().getHeight();
		String stat = "Nodes: " + nodelist.size();
		String stat2 = "Packages: " + packagelist.size();
		String stat3 = "Connectors: " + nodeConnectorHandler.getAllConnectors().size();
		String stat4 = "PackagePaths: " + packageHandler.getNumberOfPaths();
		String stat5 = "ConnectMode: " + toggleConnectMode;
		String stat6 = "activeNewConnection: " + activeNewConnection;
		String stat7 = "selectedIndex: " + selectedIndex;
		String stat8 = "selectedType: " + selectedType;
		int textstatwidth = g2d.getFontMetrics().stringWidth(stat);
		int textstat2width = g2d.getFontMetrics().stringWidth(stat2);
		int textstat3width = g2d.getFontMetrics().stringWidth(stat3);
		int textstat4width = g2d.getFontMetrics().stringWidth(stat4);
		int textstat5width = g2d.getFontMetrics().stringWidth(stat5);
		int textstat6width = g2d.getFontMetrics().stringWidth(stat6);
		int textstat7width = g2d.getFontMetrics().stringWidth(stat7);
		int textstat8width = g2d.getFontMetrics().stringWidth(stat8);
		
		g2d.setColor(Color.white);
		g2d.drawString(stat, this.getWidth() - textstatwidth - 10, textheight);
		g2d.drawString(stat2, this.getWidth() - textstat2width - 10, textheight*2);
		g2d.drawString(stat3, this.getWidth() - textstat3width - 10, textheight*3);
		g2d.drawString(stat4, this.getWidth() - textstat4width - 10, textheight*4);
		g2d.drawString(stat5, this.getWidth() - textstat5width - 10, textheight*5);
		g2d.drawString(stat6, this.getWidth() - textstat6width - 10, textheight*6);
		g2d.drawString(stat7, this.getWidth() - textstat7width - 10, textheight*7);
		g2d.drawString(stat8, this.getWidth() - textstat8width - 10, textheight*8);
	}
	void drawRangeCircles(Graphics2D g2d) {
		for(Node n : nodelist) {
			double x = n.getCX(); 
			double y = n.getCY();
		  	
		  	g2d.setColor(Color.orange);
		    g2d.fill(new Ellipse2D.Double(x - Base.CONNECT_DISTANCE, y - Base.CONNECT_DISTANCE, Base.CONNECT_DISTANCE*2, Base.CONNECT_DISTANCE*2));
		}
	}
	
	void setRadius(int i) {
		radius = i;
	}
	
	void drawNodeInfo(Graphics2D g2d) {
		int textheight = g2d.getFontMetrics().getHeight();
		g2d.setFont(new Font("Helvetica", Font.PLAIN, 10)); 
		
		g2d.setColor(Color.yellow);
		
		switch(selectedType) {
		
		case NODE: 
			if(NodeSelectiveInfo == true) {
				if(selectedIndex != -1) {
					for(Node n : nodelist) {
						if(n.getID() == selectedIndex) {
							double x = n.getX();
							double y = n.getY();
							
							g2d.drawString("ID: " + n.getID(), (int)x + 35, (int)y - textheight*2 - 3);
							g2d.drawString("Index: " + nodelist.indexOf(n), (int)x + 35, (int)y - textheight - 3);
							
							g2d.drawString("Node X: " + n.getX(), (int)x + 35, (int)y + 3);
							g2d.drawString("Node Y: " + n.getY(), (int)x + 35, (int)y + textheight*1 + 3);
							g2d.drawString("Radius: " + n.radius, (int)x + 35, (int)y + textheight*2 + 3);
							g2d.drawString("Connections: " + n.getNumberOfConnections(), (int)x + 35, (int)y + textheight*3 + 3);
							g2d.drawString("Connectable: " + n.connectable, (int)x + 35, (int)y + textheight*4 + 3);
							g2d.drawString("Connected To: " + listToString(n.connected_to), (int)x + 35, (int)y + textheight*5 + 3);
							g2d.drawString("Connected By: " + listToString(n.connected_by), (int)x + 35, (int)y + textheight*6 + 3);
							g2d.drawString("Closed: " + n.isClosed(), (int)x + 35, (int)y + textheight*7 + 3);
						}
					}
				}
			} else {
				for (Node n : nodelist) {
					double x = n.getX();
					double y = n.getY();
					
					g2d.drawString("ID: " + n.getID(), (int)x + 35, (int)y - textheight*2 - 3);
					g2d.drawString("Index: " + nodelist.indexOf(n), (int)x + 35, (int)y - textheight - 3);
					
					g2d.drawString("Node X: " + n.getX(), (int)x + 35, (int)y + 3);
					g2d.drawString("Node Y: " + n.getY(), (int)x + 35, (int)y + textheight*1 + 3);
					g2d.drawString("Radius: " + n.radius, (int)x + 35, (int)y + textheight*2 + 3);
					g2d.drawString("Connections: " + n.getNumberOfConnections(), (int)x + 35, (int)y + textheight*3 + 3);
					g2d.drawString("Connectable: " + n.connectable, (int)x + 35, (int)y + textheight*4 + 3);
					g2d.drawString("Connected To: " + listToString(n.connected_to), (int)x + 35, (int)y + textheight*5 + 3);
					g2d.drawString("Connected By: " + listToString(n.connected_by), (int)x + 35, (int)y + textheight*6 + 3);
					g2d.drawString("Closed: " + n.isClosed(), (int)x + 35, (int)y + textheight*7 + 3);
				}	
			}
			break;
		case CONNECTOR:
			for (NodeConnector nC : nodeConnectorHandler.getAllConnectors()) {
				if (nC.getID() == selectedIndex) {
					double x1, y1, x2, y2;
					/*x1 = nC.getFrom().getCenterX();
					x2 = nC.getTo().getCenterX();
					y1 = nC.getFrom().getOriginY();
					y2 = nC.getTo().getOriginY();
					
					double vX = (x2 - x1) / 2;
					double vY = (y2 - y1) / 2;
					
					double fX = x1 + vX;
					double fY = y1 + vY;
					
					g2d.drawString("cID: " + nC.getID(), (int)fX, (int)fY);
					
					g2d.setColor(Color.white);
					g2d.setStroke(new BasicStroke(4));
					g2d.drawLine((int) x1, (int)y1, (int)x2, (int)y2);
					g2d.setStroke(defaultStroke);
					*/
				}
			}
		default: break;
		}
 	}

	String listToString(List<Node> list) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 0; i < list.size(); i++) {
			String s = String.valueOf(list.get(i).getID());
			stringBuilder.append(s + ", ");
		}
	return stringBuilder.toString();
	}
	
	/*class NodeGenMouseListener extends MouseAdapter	{
		@Override
	    public void mousePressed(MouseEvent e) {	
			mouseX = e.getX();
			mouseY = e.getY();
			mousePos = e.getPoint();
			lastMousePress = e.getPoint();
			
			//Mouse 1 (LMB)
			if(e.getButton() == MouseEvent.BUTTON1 && toggleConnectMode == true) {	
				
				lastMousePressType = MouseType.MOUSE_1;
				
				System.out.println(lastMousePressType);
				
				Rectangle2D rect = new Rectangle2D.Double(0,0,1,1);
				rect.setRect(mouseX-4, mouseY-4, 8, 8);
				
				Boolean nodeIntersected = false;
				for(int i = 0; i < nodelist.size(); i++) {
					if(nodelist.get(i).intersects(rect)) {
						if (activeNewConnection == false) {
							indexOfHighlightedNode = nodelist.get(i).getIndex();
							newConnectionFromIndex = indexOfHighlightedNode;
							activeNewConnection = true;
							System.out.println("Highlighted: " + nodelist.get(i).getIndex());
						} else {
							indexOfHighlightedNode = nodelist.get(i).getIndex();
							activeNewConnection = false;
							nodelist.get(newConnectionFromIndex).connectTo(nodelist.get(i));
							newConnectionFromIndex = -1;
						}
						nodeIntersected = true;
					break;
					} 
				}
				if (!nodeIntersected) {
					//If newConnection is active -> By clicking on an empty space you can create a new node and replace the previous node's role
					Node newnode = new Node(mouseX - (radius/2), mouseY - (radius/2), radius);
					nodelist.add(newnode);
					nodelist.get(indexOfHighlightedNode).connectTo(newnode);
					indexOfHighlightedNode = newnode.getIndex();
					newConnectionFromIndex = indexOfHighlightedNode;				
				}
			} else
			if(e.getButton() == MouseEvent.BUTTON1 && Base.toggleBrush == true) {
				lastMousePressType = MouseType.MOUSE_1;
				//Do nothing when brush is on (all nodes will be created within the drag method)
			} else
			if(e.getButton() == MouseEvent.BUTTON1) {	
				lastMousePressType = MouseType.MOUSE_1;
				mouseX = e.getX();
				mouseY = e.getY();
				
				Rectangle2D rect = new Rectangle2D.Double(0,0,1,1);
				rect.setRect(mouseX-4, mouseY-4, 8, 8);
				
				//If there is no node yet, create one
				if ((nodelist.size() > 0)) {
					Boolean nodeIntersected = false;
					for(int i = 0; i < nodelist.size(); i++) {		
						//If cursor hits a node -> highlight it
						if(nodelist.get(i).intersects(rect)) {
							indexOfHighlightedNode = nodelist.get(i).getIndex();
							System.out.println("Highlighted: " + nodelist.get(i).getIndex());
							nodeIntersected = true;
							break;
						
						} 					
					}
					
					//If it doesnt, create a new node (no highlight);
					if (!nodeIntersected) {
						indexOfHighlightedNode = -1;
					}
					
					if (indexOfHighlightedNode == -1) {
						Node newnode = new Node(mousePos.x - (radius/2), mousePos.y - (radius/2), radius);
						//Auto-connect off
						/*for(Node n : nodelist) {
							if(newnode.getDistance(n) <= Base.CONNECT_DISTANCE) {
								newnode.connectTo(n);
							}
						}	
						
						nodelist.add(newnode);
					}
					
				} else {
					//Creating first node (no nodes in nodelist)
					Node newnode = new Node(mousePos.x - (radius/2), mousePos.y - (radius/2), radius);
					for(Node n : nodelist) {
						if(newnode.getDistance(n) <= Base.CONNECT_DISTANCE) {
							newnode.connectTo(n);
						}
					}	
					nodelist.add(newnode);
				} 
			}
			
			//Mouse 3 (RMB)
			if(e.getButton() == MouseEvent.BUTTON3 && toggleConnectMode == true) {
				lastMousePressType = MouseType.MOUSE_3;
				if (activeNewConnection == true) {
					activeNewConnection = false;
				} else {
					toggleConnectMode = false;
				}	
			} else
	        if(e.getButton() == MouseEvent.BUTTON3) {
	        	lastMousePressType = MouseType.MOUSE_3;
	        	if(nodelist.size() != 0) {
	        		nodelist.get(BasePanel.nodelist.size() - 1).remove();
	        	}
	        }
			repaint(); //TODO: Maybe remove in the future
	    }
		
		@Override
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			mousePos = e.getPoint();
			
			Rectangle2D rect = new Rectangle2D.Double(0,0,1,1);
			rect.setRect(mouseX-4, mouseY-4, 8, 8);
		
			/*
			for(int i = 0; i < nodelist.size(); i++)
				if(nodelist.get(i).intersects(rect)) {
					selected_index = nodelist.get(i).getID();
					break;
				} else {
					if(Base.logFirst == true) {
						selected_index = nodelist.get(0).getID();
					} else {
						selected_index = -1;
					}
				}
			
			
			for(int i = 0; i < (nodelist.size()); i++) {
				if(nodelist.get(i).intersects(rect)) {
selectedIndex = nodelist.get(i).getID();
					selectedType = EntityType.NODE;
					break;
				} else {
					selectedIndex = -1;
				}
			}
			
			if(selectedIndex == -1) {
				List<NodeConnector> connectors = nodeConnectorHandler.getAllConnectors();
				for(int i = 0; i < connectors.size(); i++) {
					NodeConnector nC = connectors.get(i);
					if (nC == null) {
						System.out.println("MouseMoved - nullPointer at nC");
					} else {
						Line2D.Double connectorLine = new Line2D.Double(nC.getFrom().getCX(), nC.getFrom().getCY(), nC.getTo().getCX(), nC.getTo().getCY());
						
						if(rect.intersectsLine(connectorLine)) {
							selectedIndex = nC.getID();
							selectedType = EntityType.CONNECTOR;
							break;
						} else {
							selectedIndex = -1;
						}
					}
				}
			}
			
			if(selectedIndex == -1) {
				selectedType = EntityType.NONE;
			}
		}

		long milibuffer = System.currentTimeMillis() + Base.paint_spacing;

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			mousePos = e.getPoint();
			
			if (SwingUtilities.isLeftMouseButton(e)) {
				if(Base.toggleBrush == true) {
					long current = System.currentTimeMillis();
					
			    	if(current >= milibuffer) {
			    		milibuffer = System.currentTimeMillis() + Base.paint_spacing; 		
						Node newnode = new Node(mousePos.x - (radius/2), mousePos.y - (radius/2), radius);
						for(Node n : nodelist) {
							if(newnode.getDistance(n) <= Base.CONNECT_DISTANCE) {
								newnode.connectTo(n);
							}
						}	
						nodelist.add(newnode);
			    	}
				} else {
					double distance = Math.hypot(mouseX - lastMousePress.x, mouseY - lastMousePress.y);
					if (BasePanel.indexOfHighlightedNode != -1 && draggingConnection == false) {
						Node highlightedNode = nodelist.get(BasePanel.indexOfHighlightedNode);
						
						//Basically, if the cursor is still in the node area when first drag is called, initiate a new dragging connection
						//A way to prevent bugs, a more simple way could be used, but this should not cause issues
						if (distance <= highlightedNode.radius) {
							newConnectionFromIndex = highlightedNode.getIndex();
							draggingConnection = true;
						}
					} else if (BasePanel.indexOfHighlightedNode != -1 && draggingConnection == true ) {
						//Dragging a connection action //TODO: Maybe implement some info later
					}
				}
			}
		}
		
		@Override 
		public void mouseReleased(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
			mousePos = e.getPoint();
			
			if (draggingConnection == true) {
				Rectangle2D rect = new Rectangle2D.Double(0,0,1,1);
				rect.setRect(mouseX-4, mouseY-4, 8, 8);
				
				Boolean nodeIntersected = false;
				for(int i = 0; i < nodelist.size(); i++) {
					if(nodelist.get(i).intersects(rect)) {
						if (nodelist.get(i) != nodelist.get(indexOfHighlightedNode)) {
							nodelist.get(newConnectionFromIndex).connectTo(nodelist.get(i));
						}
						
						indexOfHighlightedNode = nodelist.get(i).getIndex();
						newConnectionFromIndex = -1;
						
						nodeIntersected = true;
						break;
					}
				}				
				if (!nodeIntersected) {
					Node newnode = new Node(mouseX - (radius/2), mouseY - (radius/2), radius);
					nodelist.add(newnode);
					nodelist.get(indexOfHighlightedNode).connectTo(newnode);
					indexOfHighlightedNode = newnode.getIndex();
					newConnectionFromIndex = indexOfHighlightedNode;				
				}
				
				draggingConnection = false;
			}
		}	
	}
	*/
	class NodeKeyListener extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			int keyCode = e.getKeyCode();
			
			if (keyCode == KeyEvent.VK_C) {
				toggleConnectMode = !toggleConnectMode;			
			}
		}
	}
}

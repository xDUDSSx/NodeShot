package org.dudss.nodeshot.ui;

import static org.dudss.nodeshot.screens.GameScreen.cam;
import static org.dudss.nodeshot.screens.GameScreen.mouseX;
import static org.dudss.nodeshot.screens.GameScreen.mouseY;
import static org.dudss.nodeshot.screens.GameScreen.nodelist;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.entities.Connector;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.InputNode;
import org.dudss.nodeshot.entities.Node;
import org.dudss.nodeshot.entities.OutputNode;
import org.dudss.nodeshot.entities.Package;
import org.dudss.nodeshot.items.Coal;
import org.dudss.nodeshot.items.Iron;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.utils.Selector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class RightClickWindow extends Window {
	
	Entity assignedEntity;
	
	Table table;
	
	Skin skin;
	
	Label emptyLabel;
	
	Vector2 mouseClickPos = new Vector2(0, 0);
	
	//General
	Label idLabel;
	Label indexLabel;
	         
	//Node
	TextButton deleteButton;
	Label xLabel;
	Label yLabel;	
	Label radiusLabel;
	Label connectionsLabel;
	Label connectorsLabel ;
	Label closedLabel;
	
	//Package					
	Label typeLabel;
	Label percentageLabel;
	Label goingLabel;
	Label finishedLabel;
	Label fromLabel;
	Label toLabel;
	
	//Connector	
	Label packagesLabel;
	Label jammedLabel;
	
	//NONE
	TextButton createButton;
	
	public RightClickWindow(Skin skin, Entity entity) {
		super(entity.getType().toString() + " - " + entity.getIndex() + " #" + entity.getID(), skin);
		assignedEntity = entity;
		this.setSize(160, 240);
		this.setMovable(false);
		this.setResizable(true);
		
		table = new Table();
		this.skin = skin;
		
		mouseClickPos.set(mouseX, mouseY);
		
		switch (entity.getType()) {
		case INPUTNODE:
			populateInputNode(skin, (InputNode) entity);
			this.setMovable(false);
			break;
		case OUTPUTNODE:
			populateOutputNode(skin, (OutputNode) entity);
			this.setMovable(false);
			break;
		case NODE: 
			populateNode(skin, (Node) entity);
			this.setMovable(false);
			break;
		case CONNECTOR:	
			populateConnector(skin, (Connector) entity);
			this.setPosition(GameScreen.mouseX + 10, Gdx.graphics.getHeight() - GameScreen.mouseY - this.getHeight() - 10);
			this.setMovable(false);
			break;
		case PACKAGE: 				
			populatePackage(skin, (Package) entity);
			this.setPosition(Gdx.graphics.getWidth() - this.getWidth() - 10 / 2, (float) (Gdx.graphics.getHeight() * 0.75) - this.getHeight());
			this.setMovable(true);
			break;
		default: break;
		}        
	}
	
	public RightClickWindow(Skin skin) {		
		super("Right click menu", skin);
		assignedEntity = null;
		this.setMovable(false);
		this.setResizable(true);
		
		table = new Table();
		this.skin = skin;
		
		mouseClickPos.set(mouseX, mouseY);
		
		populateNone(skin, null);
		
		this.setPosition(GameScreen.mouseX + 10, Gdx.graphics.getHeight() - GameScreen.mouseY - this.getHeight() - 10);		
		this.setMovable(false);
		
	}
	
	private void populateNode(Skin skin, Node entity) {		  
		table.top();
        table.left();
        table.setFillParent(true);  
        
        initalizeNewWindowComponents(EntityType.NODE, entity, table, skin);

        this.addActor(table);
	}
	
	private void populateInputNode(Skin skin, InputNode entity) {		  
		table.top();
        table.left();
        table.setFillParent(true);   

        initalizeNewWindowComponents(EntityType.INPUTNODE, entity, table, skin);

        this.addActor(table);
	}
	
	private void populateOutputNode(Skin skin, OutputNode entity) {		  
		table.top();
        table.left();
        table.setFillParent(true);   

        initalizeNewWindowComponents(EntityType.OUTPUTNODE, entity, table, skin);

        this.addActor(table);
	}
	
	private void populateConnector(Skin skin, Connector entity) {
		table.top();
        table.left();
        table.setFillParent(true);       
        
        initalizeNewWindowComponents(EntityType.CONNECTOR, entity, table, skin);

        this.addActor(table);   
	}
	
	private void populatePackage(Skin skin, Package entity) {
		table.top();
        table.left();
        table.setFillParent(true);  
        
        initalizeNewWindowComponents(EntityType.PACKAGE, entity, table, skin);
        
        this.addActor(table);   
	}
	
	private void populateNone(Skin skin, Package entity) {
		table.top();
        table.left();
        this.setSize(140, 100);
        table.setFillParent(true);       
        
        initalizeNewWindowComponents(EntityType.NONE, entity, table, skin);

        this.addActor(table);   
	}
	
	private void initalizeNewWindowComponents(EntityType eT, Entity entity, Table table, Skin skin) {
		if (entity == null) {
			eT = EntityType.NONE;
		}
		switch (eT) {
			case NODE: 
				Node n = (Node) entity;
				emptyLabel = new Label("", skin, "font15");
		        
				idLabel = new Label("ID: " + entity.getID(), skin, "font15");
			    indexLabel = new Label("Index: " + entity.getIndex(), skin, "font15");
				
			    xLabel = new Label("Node X: " + n.getX(), skin, "font15");
			    yLabel = new Label("Node Y: " + n.getY(), skin, "font15");
			    radiusLabel = new Label("Radius: " + n.radius, skin, "font15");
			    connectionsLabel = new Label("Connections: " + n.getNumberOfConnections(), skin, "font15");
			    connectorsLabel = new Label("Connectors: " + Base.nodeConnectorListToString(n.connectors), skin, "font15");
			    connectorsLabel.setWrap(true);
			    closedLabel = new Label("Closed: " + n.isClosed(), skin, "font15");
			   
		        table.add(emptyLabel);
		        table.row();
		        table.add(emptyLabel);
		        
		        table.row();
		        table.add(idLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(indexLabel).pad(1).fill(true).padLeft(10);
		        
		        deleteButton = new TextButton("Delete node", skin, "hoverfont15");			    
		        deleteButton.addListener(new ClickListener(){
		            @Override
		            public void clicked(InputEvent event, float x, float y) {				    
		            	if(nodelist.size() != 0) {
							n.remove();
							GameScreen.rightClickMenuManager.removeMenu();
						}
		            }
		        });             
		        
		        table.row();
		        table.add(deleteButton).pad(1).fill(true).padLeft(10);
		        
		        table.row();
		        table.add(emptyLabel);
		        table.row();		        
		        table.add(xLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(yLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(radiusLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(connectionsLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(connectorsLabel).pad(1).fill(true).padLeft(10);		       
		        table.row();
		        table.add(closedLabel).pad(1).fill(true).padLeft(10);
		        
		        if (n.connectors.size() > 2) {	        	
		        	this.setSize(this.getWidth(), (15 * n.connectors.size() + this.getHeight()));
		        }
		        
		        this.setPosition(GameScreen.mouseX + 10, Gdx.graphics.getHeight() - GameScreen.mouseY - this.getHeight() - 10);
		        
				break;
			case CONNECTOR:	
				Connector c = (Connector) entity;
				emptyLabel = new Label("", skin, "font15");
		        
				idLabel = new Label("ID: " + entity.getID(), skin, "font15");
			    indexLabel = new Label("Index: " + entity.getIndex(), skin, "font15");
							  
			    packagesLabel = new Label("Number of packages: " + c.getPackages().size(), skin, "font15");
			    jammedLabel = new Label("Jammed: " + c.isJammed(), skin, "font15");
			   
		        table.add(emptyLabel);
		        table.row();
		        table.add(emptyLabel);
		        
		        table.row();
		        table.add(idLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(indexLabel).pad(1).fill(true).padLeft(10);
		        
		        table.row();
		        table.add(emptyLabel);
		        
		        table.row();		        
		        table.add(packagesLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(jammedLabel).pad(1).fill(true).padLeft(10);
		                    		       		              								
				break;
			case PACKAGE: 
				Package p = (Package) entity;
				emptyLabel = new Label("", skin, "font15");
		        
				idLabel = new Label("ID: " + entity.getID(), skin, "font15");
			    indexLabel = new Label("Index: " + entity.getIndex(), skin, "font15");
				
			    typeLabel = new Label("Type: PACKAGE", skin, "font15");
			    if (p instanceof Coal) typeLabel.setText("Type: COAL");			    	
			    if (p instanceof Iron) typeLabel.setText("Type: IRON");
			    				   
			  	percentageLabel = new Label("Percentage: " + Base.round((float)p.percentage, 2)+ " %", skin, "font15");
			  	percentageLabel.setWrap(true);
			    goingLabel = new Label("Going: " + p.going, skin, "font15");
			    finishedLabel = new Label("Finished: " + p.finished, skin, "font15");
			    fromLabel = new Label("From: " + p.from.getID(), skin, "font15");
			    toLabel = new Label("From: " + p.to.getID(), skin, "font15");
			   
		        table.add(emptyLabel);
		        table.row();
		        table.add(emptyLabel);
		        
		        table.row();
		        table.add(idLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(indexLabel).pad(1).fill(true).padLeft(10);
		        
		        table.row();
		        table.add(emptyLabel);
		        
		        table.row();		        
		        table.add(typeLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(percentageLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(goingLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(finishedLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(fromLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(toLabel).pad(1).fill(true).padLeft(10);      		       		              				
				
				break;
			case OUTPUTNODE:
				OutputNode out = (OutputNode) entity;
				emptyLabel = new Label("", skin, "font15");
		        
				idLabel = new Label("ID: " + entity.getID(), skin, "font15");
			    indexLabel = new Label("Index: " + entity.getIndex(), skin, "font15");
				
			    xLabel = new Label("Node X: " + out.getX(), skin, "font15");
			    yLabel = new Label("Node Y: " + out.getY(), skin, "font15");
			    radiusLabel = new Label("Radius: " + out.radius, skin, "font15");
			    connectionsLabel = new Label("Connections: " + out.getNumberOfConnections(), skin, "font15");
			    connectorsLabel = new Label("Connectors: " + Base.nodeConnectorListToString(out.connectors), skin, "font15");
			    connectorsLabel.setWrap(true);
			    closedLabel = new Label("Closed: " + out.isClosed(), skin, "font15");
			   
		        table.add(emptyLabel);
		        table.row();
		        table.add(emptyLabel);
		        
		        table.row();
		        table.add(idLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(indexLabel).pad(1).fill(true).padLeft(10);
		        
		        deleteButton = new TextButton("Demolish building", skin, "hoverfont15");			    
		        deleteButton.addListener(new ClickListener(){
		            @Override
		            public void clicked(InputEvent event, float x, float y) {				    
		            	if(nodelist.size() != 0) {
		            		out.getAssignedBuilding().demolish();
							GameScreen.rightClickMenuManager.removeMenu();
						}
		            }
		        });             
		        
		        table.row();
		        table.add(deleteButton).pad(1).fill(true).padLeft(10);
		        
		        table.row();
		        table.add(emptyLabel);
		        table.row();		        
		        table.add(xLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(yLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(radiusLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(connectionsLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(connectorsLabel).pad(1).fill(true).padLeft(10);		       
		        table.row();
		        table.add(closedLabel).pad(1).fill(true).padLeft(10);
		        
		        if (out.connectors.size() > 2) {	        	
		        	this.setSize(this.getWidth(), (15 * out.connectors.size() + this.getHeight()));
		        }
		        
		        this.setPosition(GameScreen.mouseX + 10, Gdx.graphics.getHeight() - GameScreen.mouseY - this.getHeight() - 10);
				break;
			case INPUTNODE: 
				InputNode in = (InputNode) entity;
				emptyLabel = new Label("", skin, "font15");
		        
				idLabel = new Label("ID: " + entity.getID(), skin, "font15");
			    indexLabel = new Label("Index: " + entity.getIndex(), skin, "font15");
				
			    xLabel = new Label("Node X: " + in.getX(), skin, "font15");
			    yLabel = new Label("Node Y: " + in.getY(), skin, "font15");
			    radiusLabel = new Label("Radius: " + in.radius, skin, "font15");
			    connectionsLabel = new Label("Connections: " + in.getNumberOfConnections(), skin, "font15");
			    connectorsLabel = new Label("Connectors: " + Base.nodeConnectorListToString(in.connectors), skin, "font15");
			    connectorsLabel.setWrap(true);
			    closedLabel = new Label("Closed: " + in.isClosed(), skin, "font15");
			   
		        table.add(emptyLabel);
		        table.row();
		        table.add(emptyLabel);
		        
		        table.row();
		        table.add(idLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(indexLabel).pad(1).fill(true).padLeft(10);
		        
		        deleteButton = new TextButton("Demolish building", skin, "hoverfont15");			    
		        deleteButton.addListener(new ClickListener(){
		            @Override
		            public void clicked(InputEvent event, float x, float y) {				    
		            	if(nodelist.size() != 0) {
							in.getAssignedBuilding().demolish();
							GameScreen.rightClickMenuManager.removeMenu();
						}
		            }
		        });             
		        
		        table.row();
		        table.add(deleteButton).pad(1).fill(true).padLeft(10);
		        
		        table.row();
		        table.add(emptyLabel);
		        table.row();		        
		        table.add(xLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(yLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(radiusLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(connectionsLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(connectorsLabel).pad(1).fill(true).padLeft(10);		       
		        table.row();
		        table.add(closedLabel).pad(1).fill(true).padLeft(10);
		        
		        if (in.connectors.size() > 2) {	        	
		        	this.setSize(this.getWidth(), (15 * in.connectors.size() + this.getHeight()));
		        }
		        
		        this.setPosition(GameScreen.mouseX + 10, Gdx.graphics.getHeight() - GameScreen.mouseY - this.getHeight() - 10);
				break;
			case NONE:
				emptyLabel = new Label("", skin, "font15");
				table.add(emptyLabel);
				table.row();
				
			    createButton = new TextButton("Create node", skin, "font15");			    
		        createButton.addListener(new ClickListener(){
		            @Override
		            public void clicked(InputEvent event, float x, float y) {	
					    Vector3 worldPos = cam.unproject(new Vector3(mouseClickPos.x, mouseClickPos.y, 0));
		            	Node newnode = new Node(worldPos.x, worldPos.y, Base.RADIUS);
		            	System.out.println("IX: " + worldPos.x + " WY: " + worldPos.y) ;
						Selector.selectNode(newnode);
						nodelist.add(newnode);
						
						GameScreen.rightClickMenuManager.removeMenu();
		            }
		        });             

		        table.add(createButton).pad(1).fill(true).padLeft(10).padTop(10);
		        
				break;
			default: break;
		}
	}
	
	/**Updates all information in the rightclick menu based on the type of the assigned entity*/
	public void refreshText() {
		Entity entity = this.assignedEntity;
		EntityType eT;
		
		if (entity == null) {
			eT = EntityType.NONE;
		} else {
			eT = entity.getType();
		}
		
		switch (eT) {
			case NODE: 
				Node n = (Node) entity;
				
				idLabel.setText("ID: " + entity.getID());
				indexLabel.setText("Index: " + entity.getIndex());
				                
				xLabel.setText("Node X: " + n.getX());
				yLabel.setText("Node Y: " + n.getY());
				radiusLabel.setText("Radius: " + n.radius);
				connectionsLabel.setText("Connections: " + n.getNumberOfConnections());
				connectorsLabel .setText("Connectors: " + Base.nodeConnectorListToString(n.connectors));
				closedLabel.setText("Closed: " + n.isClosed());
				break;
			case CONNECTOR:	
				Connector c = (Connector) entity;
				
				idLabel.setText("ID: " + entity.getID());
				indexLabel.setText("Index: " + entity.getIndex());
				                
				packagesLabel.setText("Number of packages: " + c.getPackages().size());
				jammedLabel.setText("Jammed: " + c.isJammed());
				break;
			case PACKAGE: 
				Package p = (Package) entity;
				idLabel.setText("ID: " + entity.getID());
				indexLabel.setText("Index: " + entity.getIndex());
				                
				typeLabel.setText("Type: PACKAGE");
				if (p instanceof Coal) typeLabel.setText("Type: COAL");			    	
				if (p instanceof Iron) typeLabel.setText("Type: IRON");
				
				percentageLabel.setText("Percentage: " + Base.round((float)p.percentage, 2) + " %");
			    goingLabel.setText("Going: " + p.going);
			    finishedLabel.setText("Finished: " + p.finished);
			    fromLabel.setText("From: " + p.from.getID());
			    toLabel.setText("From: " + p.to.getID());
			    
				break;
			default: break;
		}
	}
	
	public Entity getAssignedEntity() {
		return assignedEntity;
	}
}
/*
"ID: " + n.getID(), (int)x, (int)y);                          
"Index: " + nodelist.indexOf(n), (int)x, (int)y - textheight);
"Node X: " + n.getX(), (int)x, (int)y - textheight*2);        
"Node Y: " + n.getY(), (int)x, (int)y - textheight*3);        
"Radius: " + n.radius, (int)x, (int)y - textheight*4);        
"Connections: " + n.getNumberOfConnections(), (int)x, (int)y -
"Connectable: " + n.connectable, (int)x, (int)y - textheight*6
"Connected To: " + Base.nodeListToString(n.connected_to), (int
"Connected By: " + Base.nodeListToString(n.connected_by), (int
"Connectors: " + Base.nodeConnectorListToString(n.connectors),
"Closed: " + n.isClosed(), (int)x, (int)y - textheight*10);   


"ID: " + p.getID(), (int)x + 35, (int
Coal) font.draw(batch, "COAL", (int)x
Iron) font.draw(batch, "IRON", (int)x
"Percentage: " + p.percentage + " %",
"Going: " + p.going, (int)x + 35, (in
"Finished: " + p.finished, (int)x + 3
"From: " + p.from.getID(), (int)x + 3
"To: " + p.to.getID(), (int)x + 35, (  
		
"pN: " + nC.getPackages().size()
*/
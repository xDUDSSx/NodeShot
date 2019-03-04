package org.dudss.nodeshot.ui;

import static org.dudss.nodeshot.screens.GameScreen.cam;
import static org.dudss.nodeshot.screens.GameScreen.mouseX;
import static org.dudss.nodeshot.screens.GameScreen.mouseY;
import static org.dudss.nodeshot.screens.GameScreen.nodelist;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.buildings.AbstractStorage;
import org.dudss.nodeshot.buildings.AbstractBuilding;
import org.dudss.nodeshot.entities.Entity;
import org.dudss.nodeshot.entities.Entity.EntityType;
import org.dudss.nodeshot.entities.connectors.Connector;
import org.dudss.nodeshot.entities.connectors.Conveyor;
import org.dudss.nodeshot.entities.nodes.Node;
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

/**A currently unused right click window*/
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
	TextButton closeButton;
	Label xLabel;
	Label yLabel;	
	Label radiusLabel;
	Label connectionsLabel;
	Label connectableLabel;
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
	TextButton deleteConnectorButton;
	TextButton reverseButton;
	Label packagesLabel;
	Label jammedLabel;
	
	//NONE
	TextButton createButton;
	
	//InputNode (Storage)
	TextButton emptyButton;
	Label level;
	
	public RightClickWindow(Skin skin, Entity entity) {
		super(entity.getType().toString() + " - " + entity.getIndex() + " #" + entity.getID(), skin);
		assignedEntity = entity;
		this.setSize(160, 270);
		this.setMovable(false);
		this.setResizable(true);
		
		table = new Table();
		this.skin = skin;
		
		mouseClickPos.set(mouseX, mouseY);
		
		switch (entity.getType()) {
		case NODE: 
			populateNode(skin, (Node) entity);
			this.setMovable(false);
			break;
		case CONNECTOR:	
			populateConnector(skin, (Connector) entity);
			this.setPosition(GameScreen.mouseX + 10, Gdx.graphics.getHeight() - GameScreen.mouseY - this.getHeight() - 10);
			this.setMovable(false);
			break;
		case CONVEYOR_BUILDING:	
			populateConveyor(skin, (Conveyor) entity);
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

	private void populateConnector(Skin skin, Connector entity) {
		table.top();
        table.left();
        table.setFillParent(true);       
        this.setSize(180, 270);
        
        
        initalizeNewWindowComponents(EntityType.CONNECTOR, entity, table, skin);

        this.addActor(table);   
	}
	
	private void populateConveyor(Skin skin, Conveyor entity) {
		table.top();
        table.left();
        table.setFillParent(true);       
        this.setSize(180, 270);
        
        initalizeNewWindowComponents(EntityType.CONVEYOR_BUILDING, entity, table, skin);

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
			    connectableLabel = new Label("Connectable: " + n.connectable, skin, "font15");
			    //connectorsLabel = new Label("Connectors: " + Base.nodeConnectorListToString(n.connectors), skin, "font15");
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
		        closeButton = new TextButton("Close node", skin, "hoverfont15");			    
		        closeButton.addListener(new ClickListener(){
		            @Override
		            public void clicked(InputEvent event, float x, float y) {				    
		            	n.setClosed(!(n.isClosed()));
		            }
		        });             
		        
		        table.row();
		        table.add(deleteButton).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(closeButton).pad(1).fill(true).padLeft(10);
		        
		        table.row();		        
		        table.add(xLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(yLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(radiusLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(connectionsLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(connectableLabel).pad(1).fill(true).padLeft(10);
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
							  
			    packagesLabel = new Label("Packages: " + c.getPackages().size(), skin, "font15");
			    jammedLabel = new Label("Jammed: " + c.isJammed(), skin, "font15");
			    
			    deleteConnectorButton = new TextButton("Remove", skin, "hoverfont15");			    
		        deleteConnectorButton.addListener(new ClickListener(){
		            @Override
		            public void clicked(InputEvent event, float x, float y) {
		            	c.getFrom().disconnect(c.getTo());
		            }
		        });      	        
			    
			    TextButton convertConnectorButton = new TextButton("Convert to conveyor", skin, "hoverfont15");			    
		        convertConnectorButton.addListener(new ClickListener(){
		            @Override
		            public void clicked(InputEvent event, float x, float y) {
		            	Node n1 = c.getFrom();
		            	Node n2 = c.getTo();
		            	c.getFrom().disconnect(c.getTo());
		            	n1.connectTo(n2);
		            	GameScreen.rightClickMenuManager.removeMenu();
		            }
		        });  
			    
		        table.add(emptyLabel);
		        table.row();
		        table.add(emptyLabel);
		        
		        table.row();
		        table.add(idLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(indexLabel).pad(1).fill(true).padLeft(10);		       
		        table.row();
		        table.add(convertConnectorButton).pad(1).fill(true).padLeft(10);	     
		        table.row();				    
		        table.add(deleteConnectorButton).pad(1).fill(true).padLeft(10);	    
		        table.row();		               
		        table.add(packagesLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(jammedLabel).pad(1).fill(true).padLeft(10);
		                    		       		              								
				break;
			case CONVEYOR_BUILDING: 				     
				Conveyor co = (Conveyor) entity;
				emptyLabel = new Label("", skin, "font15");
		        
				idLabel = new Label("ID: " + entity.getID(), skin, "font15");
			    indexLabel = new Label("Index: " + entity.getIndex(), skin, "font15");
							  
			    packagesLabel = new Label("Packages: " + co.getPackages().size(), skin, "font15");
			    jammedLabel = new Label("Jammed: " + co.isJammed(), skin, "font15");
			    
			    deleteConnectorButton = new TextButton("Remove", skin, "hoverfont15");			    
		        deleteConnectorButton.addListener(new ClickListener(){
		            @Override
		            public void clicked(InputEvent event, float x, float y) {
		            	co.getFrom().disconnect(co.getTo());
		            }
		        });      	        

		        TextButton convertConveyorButton = new TextButton("Convert to connector", skin, "hoverfont15");			    
		        convertConveyorButton.addListener(new ClickListener(){
		            @Override
		            public void clicked(InputEvent event, float x, float y) {
		            	Node n1 = co.getFrom();
		            	Node n2 = co.getTo();
		            	co.getFrom().disconnect(co.getTo());
		            	n1.connectTo(n2);
		            	GameScreen.rightClickMenuManager.removeMenu();
		            }
		        });  
		        
		        reverseButton = new TextButton("Reverse", skin, "hoverfont15");			    
		        reverseButton.addListener(new ClickListener(){
		            @Override
		            public void clicked(InputEvent event, float x, float y) {
		            	co.reverse();
		            }
		        });  
		        
		        table.add(emptyLabel);
		        table.row();
		        table.add(emptyLabel);
		        
		        table.row();
		        table.add(idLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(indexLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(reverseButton).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(convertConveyorButton).pad(1).fill(true).padLeft(10);	
		        table.row();
		        table.add(deleteConnectorButton).pad(1).fill(true).padLeft(10);			     
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
			    fromLabel = new Label("From: " + p.from.getID(), skin, "font15");
			    toLabel = new Label("From: " + p.to.getID(), skin, "font15");
			   
			    TextButton destroyPackageButton = new TextButton("Destroy", skin, "hoverfont15");			    
			    destroyPackageButton.addListener(new ClickListener(){
		            @Override
		            public void clicked(InputEvent event, float x, float y) {
		            	p.destroy();
		            }
		        });  
			    
		        table.add(emptyLabel);
		        table.row();
		        table.add(emptyLabel);
		        
		        table.row();
		        table.add(idLabel).pad(1).fill(true).padLeft(10);
		        table.row();
		        table.add(indexLabel).pad(1).fill(true).padLeft(10);
		        
		        table.row();
		        table.add(destroyPackageButton).pad(1).fill(true).padLeft(10);
		        
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
				//connectorsLabel .setText("Connectors: " + Base.nodeConnectorListToString(n.connectors));
				closedLabel.setText("Closed: " + n.isClosed());
				break;
			case CONNECTOR:	
				Connector c = (Connector) entity;
				
				idLabel.setText("ID: " + entity.getID());
				indexLabel.setText("Index: " + entity.getIndex());
				                
				packagesLabel.setText("Packages: " + c.getPackages().size());
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
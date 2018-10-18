package org.dudss.nodeshot.ui;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.buildings.AmmoStorage;
import org.dudss.nodeshot.buildings.BasicStorage;
import org.dudss.nodeshot.buildings.CoalMine;
import org.dudss.nodeshot.buildings.CreeperGenerator;
import org.dudss.nodeshot.buildings.Furnace;
import org.dudss.nodeshot.buildings.IronMine;
import org.dudss.nodeshot.buildings.Turret;
import org.dudss.nodeshot.buildings.TurretCheat;
import org.dudss.nodeshot.entities.nodes.ConveyorNode;
import org.dudss.nodeshot.entities.nodes.Node;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class BuildMenu extends Window {
	
	Table table;
	Skin skin;
	
	public BuildMenu(String title, Skin skin) {
			super(title, skin);		
			this.skin = skin;
			this.setMovable(false);
			this.setResizable(true);

	      	setSize();
			
	    	table = new Table();
	        table.top();
	        table.left();
	        table.setFillParent(true);  
	        table.setSize(this.getPrefWidth(), this.getPrefWidth());
	        
	        init();
	        
	        this.setVisible(false);
	        this.addActor(table);
	}
	
	public void resize() {
		setSize();
	}
	
	private void setSize() {
		this.setBounds(64 + 10 + 10, 10, Gdx.graphics.getWidth() - (64*1) - (10*2) - 10, (float) (Gdx.graphics.getHeight() * 0.16));
	}
	
	private void init() {
		Label emptyLabel = new Label("", skin, "font15");
		TextButton minesButton = new TextButton("Mines", skin, "hoverfont30");		
		TextButton nodesButton = new TextButton("Nodes", skin, "hoverfont30");
		TextButton otherButton = new TextButton("Other", skin, "hoverfont30");		
				
		table.add(emptyLabel);
		table.row();

		VerticalGroup vG = new VerticalGroup();
		vG.addActor(minesButton);
		vG.addActor(nodesButton);
		vG.addActor(otherButton);
		vG.fill();
		
		table.add(vG).pad(10).fill(true);
		
		HorizontalGroup mines = new HorizontalGroup();
		HorizontalGroup connectors = new HorizontalGroup();
		HorizontalGroup other = new HorizontalGroup();
		
		TextButton coalMineButton = new TextButton("Coal mine", skin, "hoverfont30");
		TextButton ironMineButton = new TextButton("Iron mine", skin, "hoverfont30");

		TextButton connectorButton = new TextButton("Connector node", skin, "hoverfont30");
		TextButton conveyorButton = new TextButton("Conveyor node", skin, "hoverfont30");
		
		TextButton storageButton = new TextButton("Basic storage", skin, "hoverfont30");	
		TextButton furnaceButton = new TextButton("Furnace", skin, "hoverfont30");		
		TextButton ammoStorageButton  = new TextButton("Ammo storage", skin, "hoverfont30");	
		TextButton turretButton = new TextButton("Turret", skin, "hoverfont30");	
		TextButton turretCheatButton = new TextButton("Turret Unlimited", skin, "hoverfont30");	
		TextButton creeperGenerator = new TextButton("Creeper Generator", skin, "hoverfont30");	
		storageButton.setSize(100,100);
		
		coalMineButton.setSize(500, 150);
		mines.addActor(coalMineButton);
		mines.addActor(ironMineButton);
		
		connectors.addActor(connectorButton);
		connectors.addActor(conveyorButton);
		
		other.addActor(storageButton);
		other.addActor(ammoStorageButton);
		other.addActor(furnaceButton);
		other.addActor(turretButton);
		other.addActor(turretCheatButton);
		other.addActor(creeperGenerator);

		
		other.setVisible(false);
		connectors.setVisible(false);
		mines.setVisible(false);
		
		Stack stack = new Stack();
		stack.add(mines);
		stack.add(connectors);
		stack.add(other);
		
		table.add(stack).pad(10).fill(true);		
		
		minesButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				other.setVisible(false);
				connectors.setVisible(false);
				mines.setVisible(!mines.isVisible());
		    }
	    });
		
		otherButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				mines.setVisible(false);
				connectors.setVisible(false);
				other.setVisible(!other.isVisible());				
		    }
	    });
		
		nodesButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				mines.setVisible(false);
				other.setVisible(false);
				connectors.setVisible(!connectors.isVisible());
		    }
	    });
		
		coalMineButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new CoalMine(0, 0);
				}
		    }
	    });

		ironMineButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new IronMine(0, 0);
				}		
		    }
	    });
		
		conveyorButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null && GameScreen.builtConnector == null) {
					GameScreen.buildMode = true;
					GameScreen.builtConnector = new ConveyorNode(0 ,0 , Base.RADIUS);
				}		
		    }
	    });
		
		connectorButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null && GameScreen.builtConnector == null) {
					GameScreen.buildMode = true;
					GameScreen.builtConnector = new Node(0 ,0 , Base.RADIUS);
				}	
		    }
	    });
		
		storageButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new BasicStorage(0, 0);
				}		
		    }
	    });
		
		furnaceButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new Furnace(0, 0);
				}		
		    }
	    });		
		
		ammoStorageButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new AmmoStorage(0, 0);
				}		
		    }
	    });
		turretButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new Turret(0, 0);
				}		
		    }
	    });
		turretCheatButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new TurretCheat(0, 0);
				}		
		    }
	    });
	    
		creeperGenerator.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new CreeperGenerator(0, 0);
				}		
		    }
	    });
	}
}

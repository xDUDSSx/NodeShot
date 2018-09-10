package org.dudss.nodeshot.ui;

import org.dudss.nodeshot.buildings.CoalMine;
import org.dudss.nodeshot.buildings.IronMine;
import org.dudss.nodeshot.buildings.Storage;
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
		this.setBounds(64 + 10 + 10, 25, Gdx.graphics.getWidth() - (64*1) - (10*2) - 10, (float) (Gdx.graphics.getHeight() * 0.2));
	}
	
	private void init() {
		TextButton minesButton = new TextButton("Mines", skin, "hoverfont30");		
		Label emptyLabel = new Label("", skin, "font30");
		TextButton otherButton = new TextButton("Other", skin, "hoverfont30");		
		
		table.add(emptyLabel);
		table.row();

		VerticalGroup vG = new VerticalGroup();
		vG.addActor(minesButton);
		vG.addActor(otherButton);
		
		table.add(vG).pad(10);
		
		
		HorizontalGroup mines = new HorizontalGroup();
		HorizontalGroup other = new HorizontalGroup();
		
		TextButton coalMineButton = new TextButton("Coal mine", skin, "hoverfont60");
		TextButton ironMineButton = new TextButton("Iron mine", skin, "hoverfont60");
		
		TextButton storageButton = new TextButton("Storage", skin, "hoverfont60");		
		
		mines.addActor(coalMineButton);
		mines.addActor(ironMineButton);
				
		other.addActor(storageButton);
		
		other.setVisible(false);
		mines.setVisible(false);
		
		Stack stack = new Stack();
		stack.add(mines);
		stack.add(other);	
		
		table.add(stack).pad(10);
		
		minesButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				other .setVisible(mines.isVisible());
				mines.setVisible(!mines.isVisible());
		    }
	    });
		
		otherButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				mines.setVisible(other.isVisible());
				other.setVisible(!other.isVisible());				
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

		storageButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				if (GameScreen.buildMode == false && GameScreen.builtBuilding == null) {
					GameScreen.buildMode = true;
					GameScreen.builtBuilding = new Storage(0, 0);
				}		
		    }
	    });
	}
}

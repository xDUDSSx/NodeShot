package org.dudss.nodeshot.ui;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;
import org.dudss.nodeshot.terrain.Chunks.OreType;

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

public class HudMenu extends Window {

	Table table;
	Table supertable;
	Table right;
	
	Skin skin;
	
	Label oreLevel;
	Label oreType;
	Label corruptionLevel;
	
	public HudMenu(String title, Skin skin) {
		super(title, skin);
		this.skin = skin;

		setSize();
		
    	table = new Table();
    	
        /*oreLevel = new Label("Ore level: 0.0", skin, "font15");
        table.add(oreLevel).fill().left().padLeft(10).padRight(10).padTop(17).padBottom(2);
        table.row();
        oreType = new Label("Ore type: NONE", skin, "font15");
        table.add(oreType).fill().left().padLeft(10).padRight(10).padTop(2).padBottom(2);
        table.row();
        corruptionLevel = new Label("Corruption: 0.0", skin, "font15");     
        table.add(corruptionLevel).fill().left().padLeft(10).padRight(10).padTop(2).padBottom(2);
        table.row();
        */
       
        right = new Table();

        TextButton debugButton = new TextButton("Debug", skin, "hoverfont15");
        debugButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	GameScreen.debug = !(GameScreen.debug);
            }
        });
           
        right.add(debugButton).padLeft(10).padRight(10).padTop(10).fill();
        
        supertable = new Table();
        supertable.left();
        supertable.setSize(this.getPrefWidth(), this.getPrefHeight());
        supertable.setFillParent(true);
        
        supertable.add(table).left().fill(true);
        supertable.add(right).right().fill(true);
        
        this.setResizable(false);
        this.setMovable(false);
        this.setVisible(true);
        this.addActor(supertable);
	}
	
	public void resize() {
		setSize();
	}
	
	private void setSize() {
		this.setBounds(Gdx.graphics.getWidth() * 0.5f - Gdx.graphics.getWidth() * 0.25f/2, 10, Gdx.graphics.getWidth() * 0.25f, 100);
	}
	
	public void update() {
		if (GameScreen.hoverChunk != null) {
			//Can show information about the chunk under the cursor
		}
	}
	
}

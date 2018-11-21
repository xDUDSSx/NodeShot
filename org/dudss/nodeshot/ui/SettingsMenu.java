package org.dudss.nodeshot.ui;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

public class SettingsMenu extends Window {

	final Window window = this;
	
	public SettingsMenu(String title, Skin skin) {
		super(title, skin);
		setVisible(true);
		setMovable(true);
		setResizable(true);
		setPosition(10, Gdx.graphics.getHeight()/2 - this.getHeight());
		align(Align.top);
		addWidgets();
	}
	
	private void addWidgets() {
		VisTable table = new VisTable(true);
		
		VisTextButton engineSettingsButton = new VisTextButton("Engine settings");
		VisTable engineSettingsTable = new VisTable(true);
		CollapsibleWidget engineSettingsCollapsibleWidget = new CollapsibleWidget(engineSettingsTable);	
		engineSettingsTable.defaults().left();
		
		engineSettingsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				engineSettingsCollapsibleWidget.setCollapsed(!engineSettingsCollapsibleWidget.isCollapsed());
			}
		});
		
		VisCheckBox loggingCheckbox = new VisCheckBox("Enable GlProfiler logging", Base.enableGlProgilerLogging);
		VisCheckBox debugCheckbox = new VisCheckBox("Debug", GameScreen.debug);
		VisCheckBox drawGeneralStatsCheckbox = new VisCheckBox("Draw general stats", Base.drawGeneralStats);
		VisCheckBox hoverChunkHighlightCheckbox = new VisCheckBox("Highlight chunks with cursor", Base.hoverChunkHighlight);
		VisCheckBox drawTerrainEdgesCheckbox = new VisCheckBox("Hightlight terrain edges", Base.drawTerrainEdges);
		VisCheckBox drawOresCheckbox = new VisCheckBox("Draw ores", Base.drawOres);
		
		engineSettingsTable.add(loggingCheckbox).left().row();
		engineSettingsTable.add(debugCheckbox).left().row();
		engineSettingsTable.add(drawGeneralStatsCheckbox).left().row();
		engineSettingsTable.add(hoverChunkHighlightCheckbox).left().row();
		engineSettingsTable.add(drawTerrainEdgesCheckbox).left().row();
		engineSettingsTable.add(drawOresCheckbox).left().row();		
		
		table.add(engineSettingsButton).fillX().row();
		table.add(engineSettingsCollapsibleWidget).expandX().fillX().row();
		
		VisTextButton graphicsSettingsButton = new VisTextButton("Graphics settings");
		VisTable graphicsSettingsTable = new VisTable(true);
		CollapsibleWidget graphicSettingsCollapsibleWidget = new CollapsibleWidget(graphicsSettingsTable);	
		graphicsSettingsButton.defaults().left();
		
		graphicsSettingsButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				graphicSettingsCollapsibleWidget.setCollapsed(!graphicSettingsCollapsibleWidget.isCollapsed());
			}
		});
		
		VisCheckBox vsyncCheckbox = new VisCheckBox("Enable vSync", Base.vSyncEnabled);
		VisCheckBox fullscreenCheckbox = new VisCheckBox("Fullscreen", Base.fullscreen);
		graphicsSettingsTable.add(vsyncCheckbox).left().row();	
		graphicsSettingsTable.add(fullscreenCheckbox).left().row();	
		
		table.add(graphicsSettingsButton).fillX().row();
		table.add(graphicSettingsCollapsibleWidget).expandX().fillX().row();	
		
		VisTextButton closeButton = new VisTextButton("Close");
		Container c = new Container(closeButton);			
		table.add(c).bottom().row();
		
		add(table);
		
		this.pack();
		
		loggingCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.enableGlProgilerLogging = loggingCheckbox.isChecked();
		    }
	    });		
		debugCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				GameScreen.debug = debugCheckbox.isChecked();
		    }
	    });		
		hoverChunkHighlightCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.hoverChunkHighlight = hoverChunkHighlightCheckbox.isChecked();
		    }
	    });		
		drawTerrainEdgesCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawTerrainEdges = drawTerrainEdgesCheckbox.isChecked();
		    }
	    });		
		drawOresCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawOres = drawOresCheckbox.isChecked();
				GameScreen.chunks.updateAllSectionMeshes(false, -1);
		    }
	    });		
		drawGeneralStatsCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 		
				Base.drawGeneralStats = drawGeneralStatsCheckbox.isChecked();
		    }
	    });		
		vsyncCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				Gdx.graphics.setVSync(vsyncCheckbox.isChecked());
		    }
	    });		
		fullscreenCheckbox.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 	
				if (fullscreenCheckbox.isChecked()) {
					Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
				} else {
					System.out.println("settings window mode");
					Gdx.graphics.setWindowedMode(Base.defaultWindowSize.width, Base.defaultWindowSize.height);
				}
		    }
	    });					
		closeButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 
				Base.settingsOpened = false;
				window.remove();
		    }
	    });	
	}	
}

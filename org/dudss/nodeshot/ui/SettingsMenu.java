package org.dudss.nodeshot.ui;

import org.dudss.nodeshot.Base;
import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class SettingsMenu extends Window {

	final Window window = this;
	
	public SettingsMenu(String title, Skin skin) {
		super(title, skin);
		setVisible(true);
		setMovable(true);
		setResizable(true);
		setPosition(10, Gdx.graphics.getHeight() - 200);
		
		//align(Align.top);
		addWidgets();
	}
	
	private void addWidgets() {
		VisTable buttonTable = new VisTable(true);
		buttonTable.align(Align.left);
		
		VisCheckBox loggingCheckbox = new VisCheckBox("Enable GlProfiler logging", Base.enableGlProgilerLogging);
		buttonTable.add(loggingCheckbox).left().row();
		VisCheckBox debugCheckbox = new VisCheckBox("Debug", GameScreen.debug);
		buttonTable.add(debugCheckbox).left().row();
		VisCheckBox drawGeneralStatsCheckbox = new VisCheckBox("Draw general stats", Base.drawGeneralStats);
		buttonTable.add(drawGeneralStatsCheckbox).left().row();;
		VisCheckBox hoverChunkHighlightCheckbox = new VisCheckBox("Highlight chunks with cursor", Base.hoverChunkHighlight);
		buttonTable.add(hoverChunkHighlightCheckbox).left().row();
		VisCheckBox drawTerrainEdgesCheckbox = new VisCheckBox("Hightlight terrain edges", Base.drawTerrainEdges);
		buttonTable.add(drawTerrainEdgesCheckbox).left().row();
		VisCheckBox drawOresCheckbox = new VisCheckBox("Draw ores", Base.drawOres);
		buttonTable.add(drawOresCheckbox).left().row();
		
		VisTextButton closeButton = new VisTextButton("Close");
		buttonTable.add(closeButton).center();
		
		add(buttonTable).row();
				
		
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
		closeButton.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {	 
				Base.settingsOpened = false;
				window.remove();
		    }
	    });
	}	
}

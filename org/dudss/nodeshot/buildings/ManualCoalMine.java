package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class ManualCoalMine extends CoalMine {

	Rectangle clickRect;
	
	public ManualCoalMine(float cx, float cy) {
		super(cx, cy);
		clickRect = new Rectangle((this.x + this.width) + 4, (this.y + this.height/2), 16, 16);
	}
	
	@Override
	public void update() {
		Vector3 worldPos = GameScreen.cam.unproject(new Vector3(GameScreen.mouseX, GameScreen.mouseY, 0));
		if (clickRect.contains(worldPos.x, worldPos.y) && Gdx.input.isButtonPressed(Buttons.LEFT)) {
			generate();
		}
	}
	
	@Override
	public void draw(ShapeRenderer r) {
		r.set(ShapeType.Filled);
		r.setColor(Color.BLACK);
		r.rect(x, y, width, height);
		r.setColor(Color.RED);
		r.rect(clickRect.x, clickRect.y, clickRect.width, clickRect.height);
	}
	
}

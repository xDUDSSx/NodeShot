package org.dudss.nodeshot.buildings;

import org.dudss.nodeshot.utils.SpriteLoader;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;

public class TurretHead extends Sprite  {
	double rotation;
	Turret assignedTurret;
	float x, y;
	float cx, cy;
	
	boolean inPosition = false;
	
	TurretHead(Turret turret) {
		assignedTurret = turret;
		cx = turret.cx;
		cy = turret.cy;
		this.set(new Sprite(SpriteLoader.turretHead));
		this.setPosition(cx - 56, cy - 12);
		this.setOrigin(56, 12);
	}
	
	public boolean inPosition() {
		return inPosition;
	}
	
	public void aim(Vector2 target) {
		//Once aimed at target, inPosition = true;
		Vector2 aimVector = new Vector2(target.x - assignedTurret.cx, target.y - assignedTurret.cy);
		double aimLenght = Math.hypot(aimVector.x, aimVector.y);
		double alpha = Math.asin(aimVector.y/aimLenght);
		
		if (target.x <= assignedTurret.cx) {
			rotation = 360 - Math.toDegrees(alpha);
		} else {
			rotation = 180 + Math.toDegrees(alpha);
		}
	};	
}

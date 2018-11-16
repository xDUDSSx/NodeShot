package org.dudss.nodeshot.utils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shaders {
	
	public static ShaderProgram defaultShader;
	public static ShaderProgram corruptionShader;
	public static ShaderProgram blurShader;
	public static ShaderProgram cloudShader;
	public static ShaderProgram solidCloudShader;
	public static ShaderProgram rotatingCloudShader;
	
	public static void load() {
		List<Boolean> compiled = new ArrayList<Boolean>();
		
		String vertexDefaultShader = Gdx.files.internal("res/defaultVertex.glsl").readString();
		String fragmentDefaultShader = Gdx.files.internal("res/defaultFragment.glsl").readString();
		defaultShader = new ShaderProgram(vertexDefaultShader, fragmentDefaultShader);
		compiled.add(defaultShader.isCompiled());
		
		String fragShader = Gdx.files.internal("res/corruptionFrag.glsl").readString();
		String vertShader = Gdx.files.internal("res/corruptionVertex.glsl").readString();		
		corruptionShader = new ShaderProgram(vertShader, fragShader);	
		compiled.add(corruptionShader.isCompiled());
		
		String blurfragShader = Gdx.files.internal("res/blurFragment.glsl").readString();
		String blurvertShader = Gdx.files.internal("res/blurVertex.glsl").readString();		
		blurShader = new ShaderProgram(blurvertShader, blurfragShader);
		compiled.add(blurShader.isCompiled());
		
		String cloudFragShader = Gdx.files.internal("res/cloudFragment.glsl").readString();
		cloudShader = new ShaderProgram(vertexDefaultShader, cloudFragShader);
		compiled.add(cloudShader.isCompiled());
		
		String solidcloudFragShader = Gdx.files.internal("res/solidcloudFragment.glsl").readString();
		solidCloudShader = new ShaderProgram(vertexDefaultShader, solidcloudFragShader);
		compiled.add(solidCloudShader.isCompiled());
		
		String rotatingcloudFragShader = Gdx.files.internal("res/rotatingsolidcloudFragment.glsl").readString();
		rotatingCloudShader = new ShaderProgram(vertexDefaultShader, rotatingcloudFragShader);
		compiled.add(rotatingCloudShader.isCompiled());
		
		boolean bool = true;
		for (Boolean b : compiled) {
			if (b == false) {
				bool = false;
			}
		}
		if (bool) {System.out.println("All shaders compiled successfully!");} else {System.err.println("Shader compilation error!");}
		
	}
}

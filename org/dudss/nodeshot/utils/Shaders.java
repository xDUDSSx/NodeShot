package org.dudss.nodeshot.utils;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**Class that holds and checks proper compilation of GLSL shaders*/
public class Shaders {
	
	public static ShaderProgram defaultShader;
	public static ShaderProgram corruptionShader;
	public static ShaderProgram blurShader;
	public static ShaderProgram cloudShader;
	public static ShaderProgram solidCloudShader;
	public static ShaderProgram rotatingCloudShader;
	public static ShaderProgram terrainShader;
	
	/**Loads and compiles all GLSL shaders*/
	public static void load() {
		List<Boolean> compiled = new ArrayList<Boolean>();
		
		String vertexDefaultShader = Gdx.files.internal("res/defaultVertex.glsl").readString();
		String fragmentDefaultShader = Gdx.files.internal("res/defaultFragment.glsl").readString();
		defaultShader = new ShaderProgram(vertexDefaultShader, fragmentDefaultShader);
		boolean defaultCompiled = defaultShader.isCompiled();				
		compiled.add(defaultCompiled);
		
		String fragShader = Gdx.files.internal("res/corruptionFrag.glsl").readString();
		String vertShader = Gdx.files.internal("res/corruptionVertex.glsl").readString();		
		corruptionShader = new ShaderProgram(vertShader, fragShader);	
		boolean corruptionCompiled = corruptionShader.isCompiled();			
		compiled.add(corruptionCompiled);
		
		String terrainVertShader = Gdx.files.internal("res/terrainVertex.glsl").readString();
		String terrainFragShader = Gdx.files.internal("res/terrainFragment.glsl").readString();
		terrainShader = new ShaderProgram(terrainVertShader, terrainFragShader);
		boolean terrainCompiled = terrainShader.isCompiled();			
		compiled.add(terrainCompiled);
		
		String blurfragShader = Gdx.files.internal("res/blurFragment.glsl").readString();
		String blurvertShader = Gdx.files.internal("res/blurVertex.glsl").readString();		
		blurShader = new ShaderProgram(blurvertShader, blurfragShader);
		boolean blurCompiled = blurShader.isCompiled();			
		compiled.add(blurCompiled);
		
		String cloudFragShader = Gdx.files.internal("res/cloudFragment.glsl").readString();
		cloudShader = new ShaderProgram(vertexDefaultShader, cloudFragShader);
		boolean cloudCompiled = cloudShader.isCompiled();			
		compiled.add(cloudCompiled);
		
		String solidcloudFragShader = Gdx.files.internal("res/solidcloudFragment.glsl").readString();
		solidCloudShader = new ShaderProgram(vertexDefaultShader, solidcloudFragShader);
		boolean solidCloudCompiled = solidCloudShader.isCompiled();			
		compiled.add(solidCloudCompiled);
		
		String rotatingcloudFragShader = Gdx.files.internal("res/rotatingsolidcloudFragment.glsl").readString();
		rotatingCloudShader = new ShaderProgram(vertexDefaultShader, rotatingcloudFragShader);
		boolean rotatingCloudCompiled = rotatingCloudShader.isCompiled();			
		compiled.add(rotatingCloudCompiled);
		
		//Compilation error logging
		if (!defaultCompiled) {
			System.out.println(defaultShader.getLog());
		}
		if (!corruptionCompiled) {
			System.out.println(corruptionShader.getLog());
		}		
		if (!terrainCompiled) {
			System.out.println(terrainShader.getLog());
		}		
		if (!blurCompiled) {
			System.out.println(blurShader.getLog());
		}		
		if (!cloudCompiled) {
			System.out.println(cloudShader.getLog());
		}		
		if (!solidCloudCompiled) {
			System.out.println(solidCloudShader.getLog());
		}		
		if (!rotatingCloudCompiled) {
			System.out.println(rotatingCloudShader.getLog());
		}		
		
		boolean bool = true;
		for (Boolean b : compiled) {
			if (b == false) {
				bool = false;
			}
		}
		if (bool) {System.out.println("All shaders compiled successfully!");} else {System.err.println("Shader compilation error!");}		
	}
}

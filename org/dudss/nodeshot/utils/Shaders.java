package org.dudss.nodeshot.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shaders {
	
	public static ShaderProgram defaultShader;
	public static ShaderProgram testShader;
	public static ShaderProgram blurShader;
	public static ShaderProgram cloudShader;
	public static ShaderProgram solidCloudShader;
	
	public static void load() {
		String vertexDefaultShader = Gdx.files.internal("res/defaultVertex.glsl").readString();
		String fragmentDefaultShader = Gdx.files.internal("res/defaultFragment.glsl").readString();
		
		defaultShader = new ShaderProgram(vertexDefaultShader, fragmentDefaultShader);
		System.out.println(defaultShader.isCompiled());
		System.out.println(defaultShader.getLog());
		
		String fragShader = Gdx.files.internal("res/testFrag.glsl").readString();
		String vertShader = Gdx.files.internal("res/testVertex.glsl").readString();
		
		testShader = new ShaderProgram(vertShader, fragShader);
		System.out.println(testShader.isCompiled());
		System.out.println(testShader.getLog());
		
		
		String blurfragShader = Gdx.files.internal("res/blurFragment.glsl").readString();
		String blurvertShader = Gdx.files.internal("res/blurVertex.glsl").readString();
		
		blurShader = new ShaderProgram(blurvertShader, blurfragShader);
		
		String cloudFragShader = Gdx.files.internal("res/cloudFragment.glsl").readString();
		cloudShader = new ShaderProgram(vertexDefaultShader, cloudFragShader);
		System.out.println(cloudShader.isCompiled());
		System.out.println(cloudShader.getLog());
		
		String solidcloudFragShader = Gdx.files.internal("res/solidcloudFragment.glsl").readString();
		solidCloudShader = new ShaderProgram(vertexDefaultShader, solidcloudFragShader);
		System.out.println(solidCloudShader.isCompiled());
		System.out.println(solidCloudShader.getLog());
	}
}

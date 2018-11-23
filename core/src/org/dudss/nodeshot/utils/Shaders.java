package org.dudss.nodeshot.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dudss.nodeshot.BaseClass;

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
		//List<Boolean> compiled = new ArrayList<Boolean>();
		HashMap<ShaderProgram, String> compiled = new HashMap<ShaderProgram, String>();
	
		String vertexDefaultShader = Gdx.files.internal("shaders/defaultVertex.glsl").readString();
		String fragmentDefaultShader = Gdx.files.internal("shaders/defaultFragment.glsl").readString();
		defaultShader = new ShaderProgram(vertexDefaultShader, fragmentDefaultShader);			
		
		System.out.println(defaultShader.isCompiled());
		
		String fragShader = Gdx.files.internal("shaders/corruptionFrag.glsl").readString();
		String vertShader = Gdx.files.internal("shaders/corruptionVertex.glsl").readString();		
		corruptionShader = new ShaderProgram(vertShader, fragShader);		
		
		String terrainVertShader = Gdx.files.internal("shaders/terrainVertex.glsl").readString();
		String terrainFragShader = Gdx.files.internal("shaders/terrainFragment.glsl").readString();
		terrainShader = new ShaderProgram(terrainVertShader, terrainFragShader);			
		
		String blurfragShader = Gdx.files.internal("shaders/blurFragment.glsl").readString();
		String blurvertShader = Gdx.files.internal("shaders/blurVertex.glsl").readString();		
		blurShader = new ShaderProgram(blurvertShader, blurfragShader);		
		
		String cloudFragShader = Gdx.files.internal("shaders/cloudFragment.glsl").readString();
		cloudShader = new ShaderProgram(vertexDefaultShader, cloudFragShader);		
		
		String solidcloudFragShader = Gdx.files.internal("shaders/solidcloudFragment.glsl").readString();
		solidCloudShader = new ShaderProgram(vertexDefaultShader, solidcloudFragShader);		
		
		String rotatingcloudFragShader = Gdx.files.internal("shaders/rotatingsolidcloudFragment.glsl").readString();
		rotatingCloudShader = new ShaderProgram(vertexDefaultShader, rotatingcloudFragShader);
		
		//Shader compilation diagnostics and logging
		compiled.put(defaultShader, "default shader");
		compiled.put(defaultShader, "corruption shader");
		compiled.put(terrainShader, "terrain shader");
		compiled.put(blurShader, "blur shader");
		compiled.put(cloudShader, "cloud shader");
		compiled.put(solidCloudShader, "solidCloud shader");
		compiled.put(rotatingCloudShader, "rotatingCloud shader");
		
		StringBuilder sb = new StringBuilder();
		Iterator it = compiled.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<ShaderProgram, String> pair = (Map.Entry<ShaderProgram, String>)it.next();
			
			if (pair.getKey().isCompiled() == false) {
				sb.append(pair.getValue() + " did not compile successfully!\nCompilation log: \nLOG START\n" + pair.getKey().getLog() + "\nLOG END\n");
			}
			
			it.remove();
		}
		
		//Call the error manager to create an error reporter displaying which shader did not compile and their respective compilation logs
		if (sb.length() > 0) {
			BaseClass.errorManager.reportWithCustomDetails(new RuntimeException(), "Some shaders did not compile successfully!", sb.toString());
		} else {
			System.out.println("All shaders compiled successfully!");
		}
	}
}

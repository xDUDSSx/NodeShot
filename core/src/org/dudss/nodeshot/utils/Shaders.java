package org.dudss.nodeshot.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dudss.nodeshot.BaseClass;
import org.dudss.nodeshot.error.ErrorManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**Class that holds and checks proper compilation of GLSL shaders*/
public class Shaders {
	/**Just a basic shader that can sample textures.*/ 
	public static ShaderProgram defaultShader;
	/**Shader used for rendering corruption.*/
	public static ShaderProgram corruptionShader;
	/**Shader used for rendering corruption without edges.*/
	public static ShaderProgram simpleCorruptionShader;
	/**Shader that blurs in one direction (used for bilateral blur)*/
	public static ShaderProgram blurShader;
	/**Shader used for rendering terrain.*/
	public static ShaderProgram terrainShader;
	/**Shader used for rendering fog of war.*/
	public static ShaderProgram fogOfWarShader;
	
	/**Cloud shader is fractal brownian motion shader from: https://github.com/patriciogonzalezvivo/thebookofshaders*/
	public static ShaderProgram cloudShader;
	public static ShaderProgram solidCloudShader;
	public static ShaderProgram rotatingCloudShader;

	/**A special shader that distorts image based on a dedicated height-map.*/
	public static ShaderProgram waveShader;
	
	/**A shader used for the bloom effect.*/
	public static ShaderProgram thresholdShader;
	
	/**Loads and compiles all GLSL shaders.
	 * @throws Throws {@link RuntimeException} when one or more shaders did not compile successfully. Also dumps the compilation logs to the {@link ErrorManager}. 
	 * */
	public static void load() {
		HashMap<ShaderProgram, String> compiled = new HashMap<ShaderProgram, String>();
	
		String vertexDefaultShader = Gdx.files.internal("shaders/defaultVertex.glsl").readString();
		String fragmentDefaultShader = Gdx.files.internal("shaders/defaultFragment.glsl").readString();
		defaultShader = new ShaderProgram(vertexDefaultShader, fragmentDefaultShader);			
		
		String fragShader = Gdx.files.internal("shaders/corruptionFrag.glsl").readString();
		String vertShader = Gdx.files.internal("shaders/corruptionVertex.glsl").readString();		
		corruptionShader = new ShaderProgram(vertShader, fragShader);		
		corruptionShader.pedantic = true;
		
		String simpleCorrFragShader = Gdx.files.internal("shaders/simpleCorruptionFrag.glsl").readString();
		simpleCorruptionShader = new ShaderProgram(vertShader, simpleCorrFragShader);		
		simpleCorruptionShader.pedantic = true;

		String fogVertShader = Gdx.files.internal("shaders/fogVertex.glsl").readString();
		String fogFragShader = Gdx.files.internal("shaders/fogFragment.glsl").readString();
		fogOfWarShader = new ShaderProgram(fogVertShader, fogFragShader);			
		
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
		
		String waveFragmentShader = Gdx.files.internal("shaders/waveFragment.glsl").readString();
		waveShader = new ShaderProgram(vertexDefaultShader, waveFragmentShader);
		
		//Shader for thresholding bright areas, used for explosion bloom effect
		String thresholdFragmentShader = Gdx.files.internal("shaders/thresholdFragment.glsl").readString();
		thresholdShader = new ShaderProgram(vertexDefaultShader, thresholdFragmentShader);
		
		//Shader compilation diagnostics and logging
		compiled.put(defaultShader, "default shader");
		compiled.put(defaultShader, "corruption shader");
		compiled.put(simpleCorruptionShader, "simple corruption shader");
		compiled.put(fogOfWarShader, "fog of war shader");
		compiled.put(terrainShader, "terrain shader");
		compiled.put(blurShader, "blur shader");
		compiled.put(cloudShader, "cloud shader");
		compiled.put(solidCloudShader, "solidCloud shader");
		compiled.put(rotatingCloudShader, "rotatingCloud shader");
		compiled.put(waveShader, "wave shader");
		compiled.put(thresholdShader, "brightness threshold shader");
		
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
			BaseClass.logger.info("All shaders compiled successfully!");
		}
	}
}

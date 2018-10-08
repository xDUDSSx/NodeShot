package org.dudss.nodeshot.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shaders {
	
	public static ShaderProgram defaultShader;
	public static ShaderProgram testShader;
	public static ShaderProgram blurShader;
	
	public static void load() {
		String vertexDefaultShader = "attribute vec4 a_position;    \n" + 
                "attribute vec4 a_color;\n" +
                "attribute vec2 a_texCoord0;\n" + 
                "uniform mat4 u_projTrans;\n" + 
                "varying vec4 v_color;" + 
                "varying vec2 v_texCoords;" + 
                "void main()                  \n" + 
                "{                            \n" + 
                "   v_color = a_color; \n" + 
                "   v_texCoords = a_texCoord0; \n" + 
                "   gl_Position =  u_projTrans * a_position;  \n"      + 
                "}                            \n" ;
		String fragmentDefaultShader = "#ifdef GL_ES\n" +
                  "precision mediump float;\n" + 
                  "#endif\n" + 
                  "varying vec4 v_color;\n" + 
                  "varying vec2 v_texCoords;\n" + 
                  "uniform sampler2D u_texture;\n" + 
                  "void main()                                  \n" + 
                  "{                                            \n" + 
                  "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" +
                  "}";
		
		defaultShader = new ShaderProgram(vertexDefaultShader, fragmentDefaultShader);
		
		String fragmentOutlineShader = "#ifdef GL_ES\r\nprecision mediump float;\r\nprecision mediump int;\r\n#endif\r\n\r\nuniform sampler2D u_texture;\r\n\r\n// The inverse of the viewport dimensions along X and Y\r\nuniform vec2 u_viewportInverse;\r\n\r\n// Color of the outline\r\nuniform vec3 u_color;\r\n\r\n// Thickness of the outline\r\nuniform float u_offset;\r\n\r\n// Step to check for neighbors\r\nuniform float u_step;\r\n\r\nvarying vec4 v_color;\r\nvarying vec2 v_texCoord;\r\n\r\n#define ALPHA_VALUE_BORDER 0.5\r\n\r\nvoid main() {\r\n   vec2 T = v_texCoord.xy;\r\n\r\n   float alpha = 0.0;\r\n   bool allin = true;\r\n   for( float ix = -u_offset; ix < u_offset; ix += u_step )\r\n   {\r\n      for( float iy = -u_offset; iy < u_offset; iy += u_step )\r\n       {\r\n          float newAlpha = texture2D(u_texture, T + vec2(ix, iy) * u_viewportInverse).a;\r\n          allin = allin && newAlpha > ALPHA_VALUE_BORDER;\r\n          if (newAlpha > ALPHA_VALUE_BORDER && newAlpha >= alpha)\r\n          {\r\n             alpha = newAlpha;\r\n          }\r\n      }\r\n   }\r\n   if (allin)\r\n   {\r\n      alpha = 0.0;\r\n   }\r\n\r\n   gl_FragColor = vec4(u_color,alpha);\r\n}";
		String vertexOutlineShader = "uniform mat4 u_projTrans;\r\n\r\nattribute vec4 a_position;\r\nattribute vec2 a_texCoord0;\r\nattribute vec4 a_color;\r\n\r\nvarying vec4 v_color;\r\nvarying vec2 v_texCoord;\r\n\r\nuniform vec2 u_viewportInverse;\r\n\r\nvoid main() {\r\n    gl_Position = u_projTrans * a_position;\r\n    v_texCoord = a_texCoord0;\r\n    v_color = a_color;\r\n}";

		String fragShader = Gdx.files.internal("res/testFrag.glsl").readString();
		String vertShader = Gdx.files.internal("res/testVertex.glsl").readString();
		
		testShader = new ShaderProgram(vertShader, fragShader);
		
		String blurfragShader = Gdx.files.internal("res/blurFragment.glsl").readString();
		String blurvertShader = Gdx.files.internal("res/blurVertex.glsl").readString();
		
		blurShader = new ShaderProgram(blurvertShader, blurfragShader);
	}
}

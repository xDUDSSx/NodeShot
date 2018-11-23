#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color; 
varying vec2 v_texCoords;
varying float v_shade;
uniform sampler2D u_texture;
 
uniform float shade;

void main()                                   
{                                            
	vec4 texColor = texture2D(u_texture, v_texCoords);
	if (texColor.a > 0) {
		//gl_FragColor = vec4(texColor.r * shade, texColor.g * shade, texColor.b * shade, texColor.a * v_color.a);

		gl_FragColor = vec4(texColor.r * v_shade, texColor.g * v_shade, texColor.b * v_shade, v_color.a);
	} else {
		gl_FragColor = vec4(0f, 0f, 0f, 0f);
	}
}
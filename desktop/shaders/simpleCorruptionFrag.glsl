#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;

varying vec2 v_texCoords0;
varying vec2 v_texCoords1;
varying vec2 v_texCoords2;
varying vec2 v_texCoords3;

varying vec2 v_shade;
uniform sampler2D u_texture;

void main()
{
	vec4 texColor = texture2D(u_texture, v_texCoords0);

	//Applying shade and transparency to the main texture
  vec4 texShaded = vec4(texColor.r * v_shade.x, texColor.g * v_shade.x, texColor.b * v_shade.x, v_color.r);

	if (texColor.a > 0) {
			gl_FragColor = texShaded;
	} else {
			gl_FragColor = vec4(1,1,1,0);
	}
}

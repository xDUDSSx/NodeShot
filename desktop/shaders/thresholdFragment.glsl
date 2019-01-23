#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
void main()
{
	vec4 texColor = texture2D(u_texture, v_texCoords);
	float brightness = (texColor.r * 0.2126) + (texColor.g * 0.7152) + (texColor.b * 0.0722);
	if (brightness > 0.7) {
			gl_FragColor = texColor;
	} else {
			gl_FragColor = vec4(0.0);
	}
}

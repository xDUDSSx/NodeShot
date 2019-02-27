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

	//Color of the possible secondary texture placed under the main texture
	//v_color represents the alpha value of these textures, v_color.x is alpha of tex, v_color.y is alpha of tex1
	vec4 tex1Color = texture2D(u_texture, v_texCoords1);

	//Applying shade and transparency to the main texture
  vec4 texShaded = vec4(texColor.r * v_shade.x, texColor.g * v_shade.x, texColor.b * v_shade.x, v_color.r);
	//Applying shade and transparency to the secondary (fillament) texture
  vec4 tex1Shaded = vec4(tex1Color.r * v_shade.y, tex1Color.g * v_shade.y, tex1Color.b * v_shade.y, v_color.g);

	gl_FragColor = texShaded;

	if (texColor.a == 1) {
			gl_FragColor = texShaded;
	} else if (texColor.a > 0 && texColor.a < 1) {
			//gl_FragColor = vec4(mix(texShaded.rgb, tex1Shaded.rgb, 0.8), v_color.r);
			//gl_FragColor = vec4(texShaded.rgb, v_color.r);
	} else if (texColor.a == 0) {
			gl_FragColor = tex1Shaded;
	}

	if (texColor.a == 0 && tex1Color.a == 0) {
			gl_FragColor = vec4(1,1,1,0);
	}
}

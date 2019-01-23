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
	//vec4 texColor = texture2D(u_texture, v_texCoords0);

	//Color of the possible secondary texture placed under the main texture
	//v_color represents the alpha value of these textures, v_color.x is alpha of tex, v_color.y is alpha of tex1
	//vec4 tex1Color = texture2D(u_texture, v_texCoords1);

	//Applying shade and transparency to the main texture
  //vec4 texShaded = vec4(texColor.r * v_shade.x, texColor.g * v_shade.x, texColor.b * v_shade.x, v_color.r);

	//gl_FragColor = texShaded;

  //vec4 tex1Shaded = vec4(tex1Color.r * v_shade.y, tex1Color.g * v_shade.y, tex1Color.b * v_shade.y, v_color.g);

	//if (texColor.a == 1) {
			//gl_FragColor = texShaded;
	//} else if (texColor.a > 0 && texColor.a < 1) {
			//gl_FragColor = vec4((texShaded.rgb * tex1Shaded.rgb), v_color.r);
	//} else if (texColor.a == 0) {
			//gl_FragColor = tex1Shaded;
	//}

	//if (texColor.a == 0 && tex1Color.a == 0) {
			//gl_FragColor = vec4(1,1,1,0);
	//}

	vec4 tex1 = texture2D(u_texture, v_texCoords0);
	vec4 tex2 = texture2D(u_texture, v_texCoords1);
	vec4 tex3 = texture2D(u_texture, v_texCoords2);
	vec4 tex4 = texture2D(u_texture, v_texCoords3);

	vec4 finalColor = vec4(0,0,0,0);

	if (tex4.a > 0.15) {
			if (tex4.a == 1.0) {
					finalColor = tex4;
			} else {
					//finalColor = vec4(1);
					finalColor = vec4(mix(tex4.rgb, finalColor.rgb, 0.8), 1.0);
			}
	}
	if (tex3.a > 0.15) {
			if (tex3.a == 1.0) {
					finalColor = tex3;
			} else {
					//finalColor = vec4(1);
					finalColor = vec4(mix(tex3.rgb, finalColor.rgb, 0.8), 1.0);
			}
	}
	if (tex2.a > 0.15) {
			if (tex2.a == 1.0) {
					finalColor = tex2;
			} else {
					//finalColor = vec4(1);
					finalColor = vec4(mix(tex2.rgb, finalColor.rgb, 0.8), 1.0);
			}
	}
	if (tex1.a > 0.15) {
			if (tex1.a == 1.0) {
					finalColor = tex1;
			} else {
					//finalColor = vec4(1);
					finalColor = vec4(mix(tex1.rgb, finalColor.rgb, 0.8), 1.0);
			}
	}

	gl_FragColor = finalColor;

}

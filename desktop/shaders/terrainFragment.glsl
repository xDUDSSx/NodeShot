#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_texCoords1;
uniform sampler2D u_texture;

varying float useTex1;

void main()                                 
{       
    //The main (top) texture                                    
    vec4 tex = texture2D(u_texture, v_texCoords); 
    //The lower (bottom) texture
    vec4 tex1 = texture2D(u_texture, v_texCoords1);    
     
    gl_FragColor = vec4(tex * v_color);
    
    if (useTex1 > 0.5) {
        if (tex.a == 1) {
            gl_FragColor = tex * v_color;
        }  
 
        if (tex.a > 0 && tex.a < 1) {
	    //gl_FragColor = vec4((tex.rgb * tex1.rgb), 1.0) * v_color;
            //gl_FragColor = vec4((tex.rgb + tex1.rgb), 1.0) * v_color;
            gl_FragColor = vec4(mix(tex.rgb, tex1.rgb, 0.8), 1.0);
        }

        if (tex.a == 0) {
            gl_FragColor = tex1 * v_color;
        }  
    }
}
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
    vec4 tex = texture2D(u_texture, v_texCoords); 
    vec4 tex1 = texture2D(u_texture, v_texCoords1);    
     
    gl_FragColor = vec4(tex * v_color);
    
    if (useTex1 > 0.5) {
        if (tex.a == 1) {
            gl_FragColor = tex * v_color;
        }  
 
        if (tex.a > 0 && tex.a < 1) {
	    gl_FragColor = (tex * v_color) * (tex1 * v_color);
        }

        if (tex.a == 0) {
            gl_FragColor = tex1 * v_color;
            //gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
        }  
    }
}
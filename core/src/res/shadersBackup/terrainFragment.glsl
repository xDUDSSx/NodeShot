#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_texCoords1;
uniform sampler2D u_texture;

const vec4 lumcoeff = vec4(0.299, 0.587, 0.114, 0);

void main()                                 
{                                           
    vec4 tex = texture2D(u_texture, v_texCoords); 
    /*vec4 tex1 = texture2D(u_texture, v_texCoords1);    

    if (tex.a > 0) {
        gl_FragColor = tex * v_color;
    }  
 
    if (tex1.a > 0) {
        gl_FragColor = tex1 * v_color;
    }*/  
               
    gl_FragColor = vec4(tex.rgb, 1.0);
}
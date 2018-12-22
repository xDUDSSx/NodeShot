#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;

uniform sampler2D u_texture;

varying vec2 v_texCoords0;
varying vec2 v_texCoords1;
varying vec2 v_texCoords2;
varying vec2 v_texCoords3;

void main()
{
    vec4 tex1 = texture2D(u_texture, v_texCoords0);
    vec4 tex2 = texture2D(u_texture, v_texCoords1);
    vec4 tex3 = texture2D(u_texture, v_texCoords2);
    vec4 tex4 = texture2D(u_texture, v_texCoords3);

    gl_FragColor = vec4(1,1,1,0);

    //tex1
    if (tex1.a > 0.5) {
        gl_FragColor = tex1 * v_color;
    } else
    if (tex2.a > 0.5) {
        if (tex1.a == 0) {
            gl_FragColor = tex2 * v_color;
        } else {
          	gl_FragColor = vec4(mix(tex1.rgb, tex2.rgb, 0.8), 1.0);
        }
    } else
    if (tex3.a > 0.5) {
        if (tex2.a == 0) {
            gl_FragColor = tex3 * v_color;
        } else {
            gl_FragColor = vec4(mix(tex2.rgb, tex3.rgb, 0.8), 1.0);
        }
    } else
    if (tex4.a > 0.5) {
        if (tex3.a == 0) {
            gl_FragColor = tex4 * v_color;
        } else {
            gl_FragColor = vec4(mix(tex3.rgb, tex4.rgb, 0.8), 1.0);
        }
    }
}

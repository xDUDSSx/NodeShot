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

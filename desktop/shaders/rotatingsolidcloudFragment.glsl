#ifdef GL_ES
precision mediump float;
#endif

uniform vec2 resolution;
uniform float time;
uniform vec2 pos;
uniform float shade;
uniform float zoom;

varying vec4 v_color; 
varying vec2 v_texCoords;
uniform sampler2D u_texture;

#define PI 3.14159265359

float random (in vec2 _st) {
    return fract(sin(dot(_st.xy,
                         vec2(12.9898,78.233)))*
        43758.5453123);
}

// Based on Morgan McGuire @morgan3d
// https://www.shadertoy.com/view/4dS3Wd
float noise (in vec2 _st) {
    vec2 i = floor(_st);
    vec2 f = fract(_st);

    // Four corners in 2D of a tile
    float a = random(i);
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));

    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(a, b, u.x) +
            (c - a)* u.y * (1.0 - u.x) +
            (d - b) * u.x * u.y;
}

#define NUM_OCTAVES 5

float fbm ( in vec2 _st) {
    float v = 0.0;
    float a = 0.46;
    float gain = 0.5;
    float lacunarity = 2.3;
	
    vec2 shift = vec2(100.0);
    // Rotate to reduce axial bias
    mat2 rot = mat2(cos(0.5), sin(0.5),
                    -sin(0.5), cos(0.50));
    for (int i = 0; i < NUM_OCTAVES; ++i) {
        v += a * noise(_st);
        _st = rot * _st * lacunarity + shift;
        a *= gain;
    }
    return v;
}

mat2 rotate2d(float _angle){
    return mat2(cos(_angle),-sin(_angle),
                sin(_angle),cos(_angle));
}

mat2 scale(vec2 _scale){
    return mat2(_scale.x,0.0,
                0.0,_scale.y);
}

void main() {
    float zoom = 1.;
    vec2 st = gl_FragCoord.xy/resolution.xy*zoom; 
	
    //st += st * abs(sin(time*0.1)*3.0);
    //st.x += pos.x / resolution.x;
    //st.y += pos.y / resolution.y;
	    
    st -= vec2(zoom/2.);
    // rotate the space
    st = rotate2d(time*0.03) * st;
    st = scale( vec2(sin(time*1.1)) * 0.2 - 9.) * st;
    // move it back to the original place
    st += vec2(zoom/2.);
	
    vec3 color = vec3(0.0);

    vec2 q = vec2(0.);
    q.x = fbm( st + 0.0*time);
    q.y = fbm( st + vec2(1.0));

    vec2 r = vec2(0.);
    r.x = fbm( st + 1.0*q + vec2(1.7,9.2)+ 0.136*time );
    r.y = fbm( st + 1.0*q + vec2(8.3,2.8)+ 0.126*time);

    float f = fbm(st+r);

    color = mix(vec3(0.901961,0.1608,0.466667),
                vec3(0.666667,0.72667,0.498039),
                clamp((f*f)*4.0,0.0,1.0));

    color = mix(color,
                vec3(0.05,0.1,0.0806),
                clamp(length(q),0.0,1.0));

    color = mix(color,
                vec3(0.47,0.681,1.0),
                clamp(length(r.x),0.0,1.0));

    gl_FragColor = vec4((f*f*f+.6*f*f+.5*f)*color,1.);   
    //gl_FragColor = vec4(resolution.x/255., resolution.y/1080., 0, 1);   
}
#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform sampler2D displacementMap;
uniform sampler2D bloomMap;

void main() {
    // Get the pixels off of the maps.
    vec4 displacementPixel = texture2D(displacementMap, v_texCoords);

    // Read the pixel from the displaced position.
    vec2 pos = v_texCoords;
    pos.x += (displacementPixel.r * 2.0 - 1.0) * 0.025;
    pos.y -= (displacementPixel.g * 2.0 - 1.0) * 0.025;

    // Get the displaced pixel.
    vec4 pixel = texture2D(u_texture, pos);

    vec4 bloomPixel = texture2D(bloomMap, v_texCoords);

    // Apply the final color multiplied by the gl color.
    gl_FragColor = pixel += bloomPixel;
}

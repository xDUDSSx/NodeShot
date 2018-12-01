attribute vec4 a_position;
attribute vec4 a_color;
attribute vec2 a_texCoord0;
attribute vec2 a_texCoord1;
attribute vec2 a_shade;

uniform mat4 u_projTrans;
varying vec4 v_color;
varying vec2 v_texCoords;
varying vec2 v_texCoords1;
varying float useTex1;
varying vec2 v_shade;

void main()                  
{                            
	v_color = a_color;
	v_texCoords = a_texCoord0;
	if (a_texCoord1.x == 0 && a_texCoord1.y == 0) {
   	    useTex1 = 0.0;
   	} else {
            useTex1 = 1.0;
	    v_texCoords1 = a_texCoord1; 
   	}

	v_shade = a_shade;
	gl_Position =  u_projTrans * a_position;
}
precision mediump float;       	// Set the default precision to medium. We don't need as high of a precision in the fragment shader.

uniform vec4 u_BgColor;

varying vec4 v_Color;
varying vec4 v_UV;

//#define DEBUG

void main()
{
    vec4 color = v_Color;

    // Anti-aliasing
    float k = 1.0 - smoothstep(0.0, 0.01, (1.0 - v_UV.x) * v_UV.y);
    color = mix(color, vec4(0.0), k);

    color.rgb = mix(color.rgb, u_BgColor.rgb, 0.5);

    gl_FragColor = vec4(color);

#ifdef DEBUG
    if (abs(v_UV.x) < 0.1)
    {
        gl_FragColor.r = 1.0;
    }
    if (abs(v_UV.y) < 0.1)
    {
        gl_FragColor.g = 1.0;
    }
#endif

}

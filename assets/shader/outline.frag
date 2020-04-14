varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_screenSize;
uniform vec3 u_outlineColor;

const float OUTLINE_PIXEL_WIDTH = 1.0;// in pixels

void main()
{
    vec4 pixelColor = texture2D(u_texture, v_texCoords);
    if (pixelColor.a <= 0.0){
        bool isBorderPixel = false;
        vec2 pixelSize = 1.0 / u_screenSize;
        for (float y = -OUTLINE_PIXEL_WIDTH; y <= OUTLINE_PIXEL_WIDTH; y += 1.0)
        {
            for (float x = -OUTLINE_PIXEL_WIDTH; x  <= OUTLINE_PIXEL_WIDTH; x += 1.0){
                if (x==0.0 && y==0.0) continue;

                isBorderPixel = texture2D(u_texture, v_texCoords + vec2(x*pixelSize.x, y*pixelSize.y)).a > 0.0;
                if (isBorderPixel){
                    gl_FragColor = vec4(u_outlineColor.rgb, 1.0);
                    return;
                }
            }
        }
    } else {
        gl_FragColor = v_color * pixelColor;
    }
}

const int FOG_SHAPE_SPHERICAL = 0;
const int FOG_SHAPE_CYLINDRICAL = 1;
#ifdef USE_FOG
vec4 _linearFog(vec4 inColor, float fragDistance, vec4 fogColor, float fogStart, float fogEnd) {
//    return inColor;
     if (fogStart == -512.0) { // Exponential
        float fogValue = exp(-fogEnd*fragDistance);
        return vec4(mix(fogColor.rgb, inColor.rgb, fogValue * fogColor.a), inColor.a);
    } else if (fogStart == -1024.0) { // Exponential ^2
        float fogValue = exp(-fogEnd*pow(fragDistance, 2.0));
        return vec4(mix(fogColor.rgb, inColor.rgb, fogValue * fogColor.a), inColor.a);
    } else {
        vec4 result = mix(fogColor, inColor,
            smoothstep(fogEnd, fogStart, fragDistance));
        result.a = inColor.a;

        return result;
    }
}
#else
vec4 _linearFog(vec4 fragColor, float fragDistance, vec4 fogColor, float fogStart, float fogEnd) {
    return fragColor;
}
#endif
float getFragDistance(int fogShape, vec3 position) {
    // Use the maximum of the horizontal and vertical distance to get cylindrical fog if fog shape is cylindrical
    switch (fogShape) {
        case FOG_SHAPE_SPHERICAL: return length(position);
        case FOG_SHAPE_CYLINDRICAL: return max(length(position.xz), abs(position.y));
        default: return length(position); // This shouldn't be possible to get, but return a sane value just in case
    }
}
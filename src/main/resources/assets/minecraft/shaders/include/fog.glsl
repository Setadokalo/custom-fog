#version 150

// This is a slightly modified version of the normal fog function
// For normal `fogStart` values, it will behave identically to vanilla fog
// However, to allow for Exponential and Exponential^2 fog, there are two magic constants
// `fogStart` can be that will change the behavior:
// For `fogStart = -512`, we switch to Exponential fog and use `fogEnd` as the density
// For `fogStart = -1024`, we switch to Exponential^2 fog and use `fogEnd` as the density
vec4 linear_fog(vec4 inColor, float vertexDistance, float fogStart, float fogEnd, vec4 fogColor) {
    if (fogStart == -512.0) { // Exponential
        float fogValue = exp(-fogEnd*vertexDistance);
        return vec4(mix(fogColor.rgb, inColor.rgb, fogValue * fogColor.a), inColor.a);
    } else if (fogStart == -1024.0) { // Exponential ^2
        float fogValue = exp(-fogEnd*pow(vertexDistance, 2.0));
        return vec4(mix(fogColor.rgb, inColor.rgb, fogValue * fogColor.a), inColor.a);
    } else {
        if (vertexDistance <= fogStart) {
            return inColor;
        }

        float fogValue = vertexDistance < fogEnd ? smoothstep(fogStart, fogEnd, vertexDistance) : 1.0;
        return vec4(mix(inColor.rgb, fogColor.rgb, fogValue * fogColor.a), inColor.a);
    }
}

float linear_fog_fade(float vertexDistance, float fogStart, float fogEnd) {
    if (vertexDistance <= fogStart) {
        return 1.0;
    } else if (vertexDistance >= fogEnd) {
        return 0.0;
    }

    return smoothstep(fogEnd, fogStart, vertexDistance);
}

float fog_distance(vec3 pos, int shape) {
    float shape_f = float(shape);
    float distXZ = length(vec3(pos.x, 0.0, pos.z));
    float distY = length(vec3(0.0, pos.y, 0.0));
    return mix(length(pos), max(distXZ, distY), shape_f);
}

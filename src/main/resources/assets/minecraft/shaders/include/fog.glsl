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

float cylindrical_distance(mat4 modelViewMat, vec3 pos) {
    float distXZ = length((modelViewMat * vec4(pos.x, 0.0, pos.z, 1.0)).xyz);
    float distY = length((modelViewMat * vec4(0.0, pos.y, 0.0, 1.0)).xyz);
    return max(distXZ, distY);
}

// this function is copied straight from the vanilla jar
// not including this means the game will "bootloop" because the particle shader can't find this function
// I don't know why this didn't have to be included before, though...
float fog_distance(mat4 modelViewMat, vec3 pos, int shape) {
    if (shape == 0) {
        return length((modelViewMat * vec4(pos, 1.0)).xyz);
    } else {
        float distXZ = length((modelViewMat * vec4(pos.x, 0.0, pos.z, 1.0)).xyz);
        float distY = length((modelViewMat * vec4(0.0, pos.y, 0.0, 1.0)).xyz);
        return max(distXZ, distY);
    }
}

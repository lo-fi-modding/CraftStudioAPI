package com.leviathanstudio.craftstudio.client.model;

import com.mojang.math.Vector3f;

import java.util.Map;

public record CsjsAnimationData(CsjsAnimationData parent, Map<String, CsjsAnimationData> children, Vector3f pos, Vector3f offset, Vector3f size, Vector3f rotation, Vector3f uv) {

}

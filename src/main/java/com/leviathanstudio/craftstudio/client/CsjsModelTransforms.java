package com.leviathanstudio.craftstudio.client;

import com.mojang.math.Vector3f;

import java.util.Map;

public record CsjsModelTransforms(CsjsModelTransforms parent, Map<String, CsjsModelTransforms> children, String name, Vector3f pos, Vector3f offset, Vector3f size, Vector3f rotation, Vector3f uv) {

}

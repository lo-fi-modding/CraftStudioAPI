package com.leviathanstudio.craftstudio.client.model;

import com.mojang.math.Vector3f;

import java.util.List;
import java.util.Map;

public record CsjsAnimation(String title, int duration, Map<String, Part> parts) {
  public record Part(List<Keyframe> pos, List<Keyframe> offset, List<Keyframe> size, List<Keyframe> rotation) {
    public record Keyframe(int ticks, Vector3f vec) {

    }
  }
}

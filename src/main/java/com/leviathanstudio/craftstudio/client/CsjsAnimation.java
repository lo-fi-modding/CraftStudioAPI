package com.leviathanstudio.craftstudio.client;

import com.mojang.math.Vector3f;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record CsjsAnimation(ResourceLocation loc, String title, int duration, Map<String, Part> parts) {
  public record Part(List<Keyframe> pos, List<Keyframe> offset, List<Keyframe> size, List<Keyframe> rotation) {
    public record Keyframe(int ticks, Vector3f vec) {

    }
  }
}

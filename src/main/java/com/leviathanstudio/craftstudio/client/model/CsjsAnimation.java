package com.leviathanstudio.craftstudio.client.model;

import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import java.util.Map;

public class CsjsAnimation {
  private final String title;
  private final int duration;
  private final Map<String, Part> parts;

  public CsjsAnimation(final String title, final int duration, final Map<String, Part> parts) {
    this.title = title;
    this.duration = duration;
    this.parts = parts;
  }

  public static class Part {
    private final Int2ObjectMap<Vector3f> pos;
    private final Int2ObjectMap<Vector3f> offset;
    private final Int2ObjectMap<Vector3f> size;
    private final Int2ObjectMap<Vector3f> rotation;

    public Part(final Int2ObjectMap<Vector3f> pos, final Int2ObjectMap<Vector3f> offset, final Int2ObjectMap<Vector3f> size, final Int2ObjectMap<Vector3f> rotation) {
      this.pos = pos;
      this.offset = offset;
      this.size = size;
      this.rotation = rotation;
    }
  }
}

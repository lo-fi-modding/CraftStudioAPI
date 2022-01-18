package com.leviathanstudio.craftstudio.client;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CsjsModelData {
  private final Map<String, CsjsModelTransforms> transforms;
  private final Map<String, CsjsModelTransforms> flattened = new HashMap<>();

  public CsjsModelData(final Map<String, CsjsModelTransforms> transforms) {
    this.transforms = transforms;
    flatten(flattened, transforms);
  }

  public CsjsModelData(final CsjsModelData other) {
    this(copy(null, other.transforms));
  }

  private static Map<String, CsjsModelTransforms> copy(@Nullable final CsjsModelTransforms parent, final Map<String, CsjsModelTransforms> children) {
    final Map<String, CsjsModelTransforms> newChildren = new HashMap<>();

    for(final Map.Entry<String, CsjsModelTransforms> entry : children.entrySet()) {
      final CsjsModelTransforms child = entry.getValue();

      final Map<String, CsjsModelTransforms> newChildChildren = new HashMap<>();
      final CsjsModelTransforms newChild = new CsjsModelTransforms(parent, newChildChildren, child.name(), child.pos().copy(), child.offset().copy(), child.size().copy(), child.rotation().copy(), child.uv().copy());
      newChildren.put(entry.getKey(), newChild);

      newChildChildren.putAll(copy(newChild, child.children()));
    }

    return newChildren;
  }

  private static void flatten(final Map<String, CsjsModelTransforms> flattened, final Map<String, CsjsModelTransforms> children) {
    for(final CsjsModelTransforms child : children.values()) {
      flattened.put(child.name(), child);
      flatten(flattened, child.children());
    }
  }

  public Collection<CsjsModelTransforms> roots() {
    return this.transforms.values();
  }

  @Nullable
  public CsjsModelTransforms get(final String name) {
    return this.flattened.get(name);
  }
}

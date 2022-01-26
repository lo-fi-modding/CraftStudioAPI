package com.leviathanstudio.craftstudio;

import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public interface CsjsAnimationState {
  @Nullable
  ResourceLocation getCurrentAnimation();
  void startAnimation(ResourceLocation animation);
  void stopAnimation();
}

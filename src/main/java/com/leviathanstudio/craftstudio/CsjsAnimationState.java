package com.leviathanstudio.craftstudio;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public interface CsjsAnimationState {
  @Nullable
  ResourceLocation getCurrentAnimation();
  void startAnimation(ResourceLocation animation);
  void stopAnimation();
}

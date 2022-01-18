package com.leviathanstudio.craftstudio.server;

import com.leviathanstudio.craftstudio.CsjsAnimationState;
import com.leviathanstudio.craftstudio.CsjsEntity;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class CsjsServerAnimationState implements CsjsAnimationState {
  private final CsjsEntity entity;

  public CsjsServerAnimationState(final CsjsEntity entity) {
    this.entity = entity;
  }

  @Nullable
  @Override
  public ResourceLocation getCurrentAnimation() {
    final String anim = this.entity.getEntityData().get(CsjsEntity.DATA_ANIMATION);

    if(anim.isEmpty()) {
      return null;
    }

    return new ResourceLocation(anim);
  }

  @Override
  public void startAnimation(final ResourceLocation animation) {
    this.entity.getEntityData().set(CsjsEntity.DATA_ANIMATION, animation.toString());
  }

  @Override
  public void stopAnimation() {
    this.entity.getEntityData().set(CsjsEntity.DATA_ANIMATION, "");
  }
}

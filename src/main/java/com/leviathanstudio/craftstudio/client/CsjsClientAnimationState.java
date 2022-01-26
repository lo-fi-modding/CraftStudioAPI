package com.leviathanstudio.craftstudio.client;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.CsjsAnimationState;
import com.leviathanstudio.craftstudio.CsjsEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CsjsClientAnimationState implements CsjsAnimationState {
  private final CsjsEntity entity;

  private CsjsModelData transforms;
  @Nullable
  private CsjsModelData originalTransforms;
  private String animName;
  @Nullable
  private CsjsAnimation animation;
  private int animationTicks;
  private final Map<String, KeyframeIndices> partNextKeyframes = new HashMap<>();
  private final Map<String, KeyframeIndices> partCurrentKeyframes = new HashMap<>();

  public CsjsClientAnimationState(final CsjsEntity entity, final ResourceLocation model) {
    this.entity = entity;
    this.transforms = new CsjsModelData(CraftStudioApi.getModel(model));
  }

  @Nullable
  @Override
  public ResourceLocation getCurrentAnimation() {
    this.checkForAnimChange();
    return this.animation.loc();
  }

  @Override
  public void startAnimation(final ResourceLocation animation) {
    this.originalTransforms = new CsjsModelData(this.transforms);
    this.animation = CraftStudioApi.getAnimation(animation);
    this.animName = animation.toString();
    this.animationTicks = 0;
    this.partNextKeyframes.clear();
    this.partCurrentKeyframes.clear();

    for(final String partName : this.animation.parts().keySet()) {
      this.partNextKeyframes.put(partName, new KeyframeIndices());
      this.partCurrentKeyframes.put(partName, new KeyframeIndices());
    }
  }

  @Override
  public void stopAnimation() {
    this.transforms = this.originalTransforms;
    this.originalTransforms = null;
    this.animation = null;
    this.animName = "";
  }

  private void checkForAnimChange() {
    final String anim = this.entity.getEntityData().get(CsjsEntity.DATA_ANIMATION);

    if(anim.isEmpty()) {
      if(!this.animName.isEmpty()) {
        this.stopAnimation();
      }
    } else if(!anim.equals(this.animName)) {
      this.startAnimation(new ResourceLocation(anim));
    }
  }

  public CsjsModelData getTransforms() {
    this.checkForAnimChange();
    return this.transforms;
  }

  private void resetKeyframes(final String partName) {
    this.partCurrentKeyframes.get(partName).pos = 0;
    this.partCurrentKeyframes.get(partName).offset = 0;
    this.partCurrentKeyframes.get(partName).size = 0;
    this.partCurrentKeyframes.get(partName).rotation = 0;
    this.partNextKeyframes.get(partName).pos = 1;
    this.partNextKeyframes.get(partName).offset = 1;
    this.partNextKeyframes.get(partName).size = 1;
    this.partNextKeyframes.get(partName).rotation = 1;
  }

  public void tick() {
    this.checkForAnimChange();

    if(this.animation != null) {
      for(final Map.Entry<String, KeyframeIndices> entry : this.partNextKeyframes.entrySet()) {
        final String partName = entry.getKey();
        final CsjsModelTransforms partTransforms = this.transforms.get(partName);

        if(partTransforms != null) {
          final CsjsAnimation.Part animPart = this.animation.parts().get(partName);

          if(!animPart.pos().isEmpty()) {
            final int nextPosKeyframeIndex = Math.min(entry.getValue().pos, animPart.pos().size() - 1);
            final int currentPosKeyframeIndex = Math.min(this.partCurrentKeyframes.get(partName).pos, animPart.pos().size() - 1);

            final CsjsAnimation.Part.Keyframe nextKeyframe = animPart.pos().get(nextPosKeyframeIndex);
            final CsjsAnimation.Part.Keyframe currentKeyframe = animPart.pos().get(currentPosKeyframeIndex);

            partTransforms.pos().set(currentKeyframe.vec().x(), currentKeyframe.vec().y(), currentKeyframe.vec().z());
            partTransforms.pos().lerp(nextKeyframe.vec(), Mth.clamp((float)(this.animationTicks - currentKeyframe.ticks()) / (nextKeyframe.ticks() - currentKeyframe.ticks()), 0.0f, 1.0f));
            partTransforms.pos().add(this.originalTransforms.get(partName).pos());
          }

          if(!animPart.offset().isEmpty()) {
            final int nextOffsetKeyframeIndex = Math.min(entry.getValue().offset, animPart.offset().size() - 1);
            final int currentOffsetKeyframeIndex = Math.min(this.partCurrentKeyframes.get(partName).offset, animPart.offset().size() - 1);

            final CsjsAnimation.Part.Keyframe nextKeyframe = animPart.offset().get(nextOffsetKeyframeIndex);
            final CsjsAnimation.Part.Keyframe currentKeyframe = animPart.offset().get(currentOffsetKeyframeIndex);

            partTransforms.offset().set(currentKeyframe.vec().x(), currentKeyframe.vec().y(), currentKeyframe.vec().z());
            partTransforms.offset().lerp(nextKeyframe.vec(), Mth.clamp((float)(this.animationTicks - currentKeyframe.ticks()) / (nextKeyframe.ticks() - currentKeyframe.ticks()), 0.0f, 1.0f));
            partTransforms.offset().add(this.originalTransforms.get(partName).offset());
          }

          if(!animPart.rotation().isEmpty()) {
            final int nextRotationKeyframeIndex = Math.min(entry.getValue().rotation, animPart.rotation().size() - 1);
            final int currentRotationKeyframeIndex = Math.min(this.partCurrentKeyframes.get(partName).rotation, animPart.rotation().size() - 1);

            final CsjsAnimation.Part.Keyframe nextKeyframe = animPart.rotation().get(nextRotationKeyframeIndex);
            final CsjsAnimation.Part.Keyframe currentKeyframe = animPart.rotation().get(currentRotationKeyframeIndex);

            partTransforms.rotation().set(currentKeyframe.vec().x(), currentKeyframe.vec().y(), currentKeyframe.vec().z());
            partTransforms.rotation().lerp(nextKeyframe.vec(), Mth.clamp((float)(this.animationTicks - currentKeyframe.ticks()) / (nextKeyframe.ticks() - currentKeyframe.ticks()), 0.0f, 1.0f));
            partTransforms.rotation().add(this.originalTransforms.get(partName).rotation());
          }

          if(!animPart.size().isEmpty()) {
            final int nextSizeKeyframeIndex = Math.min(entry.getValue().size, animPart.size().size() - 1);
            final int currentSizeKeyframeIndex = Math.min(this.partCurrentKeyframes.get(partName).size, animPart.size().size() - 1);

            final CsjsAnimation.Part.Keyframe nextKeyframe = animPart.size().get(nextSizeKeyframeIndex);
            final CsjsAnimation.Part.Keyframe currentKeyframe = animPart.size().get(currentSizeKeyframeIndex);

            partTransforms.size().set(currentKeyframe.vec().x(), currentKeyframe.vec().y(), currentKeyframe.vec().z());
            partTransforms.size().lerp(nextKeyframe.vec(), Mth.clamp((float)(this.animationTicks - currentKeyframe.ticks()) / (nextKeyframe.ticks() - currentKeyframe.ticks()), 0.0f, 1.0f));
            partTransforms.size().add(this.originalTransforms.get(partName).size());
          }
        }
      }

      this.animationTicks++;

      for(final Map.Entry<String, CsjsClientAnimationState.KeyframeIndices> entry : this.partNextKeyframes.entrySet()) {
        final String partName = entry.getKey();

        final CsjsAnimation.Part animPart = this.animation.parts().get(partName);

        if(!animPart.pos().isEmpty()) {
          final CsjsClientAnimationState.KeyframeIndices keyframeIndices = entry.getValue();

          final int nextKeyframeIndex = Math.min(keyframeIndices.pos, animPart.pos().size() - 1);
          if(this.animationTicks >= this.animation.parts().get(partName).pos().get(nextKeyframeIndex).ticks()) {
            this.partCurrentKeyframes.get(partName).pos = keyframeIndices.pos;
            keyframeIndices.pos++;
          }
        }

        if(!animPart.offset().isEmpty()) {
          final CsjsClientAnimationState.KeyframeIndices keyframeIndices = entry.getValue();

          final int nextKeyframeIndex = Math.min(keyframeIndices.offset, animPart.offset().size() - 1);
          if(this.animationTicks >= this.animation.parts().get(partName).offset().get(nextKeyframeIndex).ticks()) {
            this.partCurrentKeyframes.get(partName).offset = keyframeIndices.offset;
            keyframeIndices.offset++;
          }
        }

        if(!animPart.rotation().isEmpty()) {
          final CsjsClientAnimationState.KeyframeIndices keyframeIndices = entry.getValue();

          final int nextKeyframeIndex = Math.min(keyframeIndices.rotation, animPart.rotation().size() - 1);
          if(this.animationTicks >= this.animation.parts().get(partName).rotation().get(nextKeyframeIndex).ticks()) {
            this.partCurrentKeyframes.get(partName).rotation = keyframeIndices.rotation;
            keyframeIndices.rotation++;
          }
        }

        if(!animPart.size().isEmpty()) {
          final CsjsClientAnimationState.KeyframeIndices keyframeIndices = entry.getValue();

          final int nextKeyframeIndex = Math.min(keyframeIndices.size, animPart.size().size() - 1);
          if(this.animationTicks >= this.animation.parts().get(partName).size().get(nextKeyframeIndex).ticks()) {
            this.partCurrentKeyframes.get(partName).size = keyframeIndices.size;
            keyframeIndices.size++;
          }
        }
      }

      if(this.animationTicks >= this.animation.duration()) {
        this.animationTicks = 0;

        for(final String partName : this.partCurrentKeyframes.keySet()) {
          this.resetKeyframes(partName);
        }
      }
    }
  }

  public static class KeyframeIndices {
    public int pos;
    public int offset;
    public int size;
    public int rotation;
  }
}

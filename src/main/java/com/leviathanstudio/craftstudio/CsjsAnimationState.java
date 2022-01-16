package com.leviathanstudio.craftstudio;

import com.leviathanstudio.craftstudio.client.model.ClientBootstrap;
import com.leviathanstudio.craftstudio.client.model.CsjsAnimation;
import com.leviathanstudio.craftstudio.client.model.CsjsModelTransforms;
import com.leviathanstudio.craftstudio.client.model.CsjsModelTransformsMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CsjsAnimationState {
  private CsjsModelTransformsMap transforms;
  @Nullable
  private CsjsModelTransformsMap originalTransforms;
  @Nullable
  private CsjsAnimation animation;
  private int animationTicks;
  private final Map<String, KeyframeIndices> partNextKeyframes = new HashMap<>();
  private final Map<String, KeyframeIndices> partCurrentKeyframes = new HashMap<>();

  public CsjsAnimationState(final ResourceLocation model) {
    this.transforms = new CsjsModelTransformsMap(ClientBootstrap.ANIMATION_DATA.get(model));
  }

  public void startAnimation(final CsjsAnimation animation) {
    this.originalTransforms = new CsjsModelTransformsMap(this.transforms);
    this.animation = animation;
    this.animationTicks = 0;
    this.partNextKeyframes.clear();
    this.partCurrentKeyframes.clear();

    for(final String partName : animation.parts().keySet()) {
      this.partNextKeyframes.put(partName, new KeyframeIndices());
      this.partCurrentKeyframes.put(partName, new KeyframeIndices());
    }
  }

  public void stopAnimation() {
    this.transforms = this.originalTransforms;
    this.originalTransforms = null;
    this.animation = null;
  }

  public CsjsModelTransformsMap getTransforms() {
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
    if(this.animation != null) {
      for(final String partName : this.partNextKeyframes.keySet()) {
        final CsjsModelTransforms partTransforms = this.transforms.get(partName);

        if(partTransforms != null) {
          final CsjsAnimation.Part animPart = this.animation.parts().get(partName);

          if(animPart.pos().size() != 0) {
            final int nextPosKeyframeIndex = this.partNextKeyframes.get(partName).pos;
            final int currentPosKeyframeIndex = this.partCurrentKeyframes.get(partName).pos;

            final CsjsAnimation.Part.Keyframe nextKeyframe = animPart.pos().get(nextPosKeyframeIndex);
            final CsjsAnimation.Part.Keyframe currentKeyframe = animPart.pos().get(currentPosKeyframeIndex);

            partTransforms.pos().set(currentKeyframe.vec().x(), currentKeyframe.vec().y(), currentKeyframe.vec().z());
            partTransforms.pos().lerp(nextKeyframe.vec(), Mth.clamp((float)(this.animationTicks - currentKeyframe.ticks()) / (nextKeyframe.ticks() - currentKeyframe.ticks()), 0.0f, 1.0f));
            partTransforms.pos().add(this.originalTransforms.get(partName).pos());
          }

          if(animPart.offset().size() != 0) {
            final int nextOffsetKeyframeIndex = this.partNextKeyframes.get(partName).offset;
            final int currentOffsetKeyframeIndex = this.partCurrentKeyframes.get(partName).offset;

            final CsjsAnimation.Part.Keyframe nextKeyframe = animPart.offset().get(nextOffsetKeyframeIndex);
            final CsjsAnimation.Part.Keyframe currentKeyframe = animPart.offset().get(currentOffsetKeyframeIndex);

            partTransforms.offset().set(currentKeyframe.vec().x(), currentKeyframe.vec().y(), currentKeyframe.vec().z());
            partTransforms.offset().lerp(nextKeyframe.vec(), Mth.clamp((float)(this.animationTicks - currentKeyframe.ticks()) / (nextKeyframe.ticks() - currentKeyframe.ticks()), 0.0f, 1.0f));
            partTransforms.offset().add(this.originalTransforms.get(partName).offset());
          }

          if(animPart.rotation().size() != 0) {
            final int nextRotationKeyframeIndex = this.partNextKeyframes.get(partName).rotation;
            final int currentRotationKeyframeIndex = this.partCurrentKeyframes.get(partName).rotation;

            final CsjsAnimation.Part.Keyframe nextKeyframe = animPart.rotation().get(nextRotationKeyframeIndex);
            final CsjsAnimation.Part.Keyframe currentKeyframe = animPart.rotation().get(currentRotationKeyframeIndex);

            partTransforms.rotation().set(currentKeyframe.vec().x(), currentKeyframe.vec().y(), currentKeyframe.vec().z());
            partTransforms.rotation().lerp(nextKeyframe.vec(), Mth.clamp((float)(this.animationTicks - currentKeyframe.ticks()) / (nextKeyframe.ticks() - currentKeyframe.ticks()), 0.0f, 1.0f));
            partTransforms.rotation().add(this.originalTransforms.get(partName).rotation());
          }

          if(animPart.size().size() != 0) {
            final int nextSizeKeyframeIndex = this.partNextKeyframes.get(partName).size;
            final int currentSizeKeyframeIndex = this.partCurrentKeyframes.get(partName).size;

            final CsjsAnimation.Part.Keyframe nextKeyframe = animPart.size().get(nextSizeKeyframeIndex);
            final CsjsAnimation.Part.Keyframe currentKeyframe = animPart.size().get(currentSizeKeyframeIndex);

            partTransforms.size().set(currentKeyframe.vec().x(), currentKeyframe.vec().y(), currentKeyframe.vec().z());
            partTransforms.size().lerp(nextKeyframe.vec(), Mth.clamp((float)(this.animationTicks - currentKeyframe.ticks()) / (nextKeyframe.ticks() - currentKeyframe.ticks()), 0.0f, 1.0f));
            partTransforms.size().add(this.originalTransforms.get(partName).size());
          }
        }
      }

      this.animationTicks++;

      for(final Map.Entry<String, CsjsAnimationState.KeyframeIndices> entry : this.partNextKeyframes.entrySet()) {
        final String partName = entry.getKey();

        final CsjsAnimation.Part animPart = this.animation.parts().get(partName);

        if(animPart.pos().size() != 0) {
          final CsjsAnimationState.KeyframeIndices keyframeIndices = entry.getValue();

          final int nextKeyframeIndex = keyframeIndices.pos;
          if(this.animationTicks >= this.animation.parts().get(partName).pos().get(nextKeyframeIndex).ticks()) {
            this.partCurrentKeyframes.get(partName).pos = keyframeIndices.pos;
            keyframeIndices.pos++;
          }
        }

        if(animPart.offset().size() != 0) {
          final CsjsAnimationState.KeyframeIndices keyframeIndices = entry.getValue();

          final int nextKeyframeIndex = keyframeIndices.offset;
          if(this.animationTicks >= this.animation.parts().get(partName).offset().get(nextKeyframeIndex).ticks()) {
            this.partCurrentKeyframes.get(partName).offset = keyframeIndices.offset;
            keyframeIndices.offset++;
          }
        }

        if(animPart.rotation().size() != 0) {
          final CsjsAnimationState.KeyframeIndices keyframeIndices = entry.getValue();

          final int nextKeyframeIndex = keyframeIndices.rotation;
          if(this.animationTicks >= this.animation.parts().get(partName).rotation().get(nextKeyframeIndex).ticks()) {
            this.partCurrentKeyframes.get(partName).rotation = keyframeIndices.rotation;
            keyframeIndices.rotation++;
          }
        }

        if(animPart.size().size() != 0) {
          final CsjsAnimationState.KeyframeIndices keyframeIndices = entry.getValue();

          final int nextKeyframeIndex = keyframeIndices.size;
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

package com.leviathanstudio.craftstudio.client.model;

import com.leviathanstudio.craftstudio.CsjsEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.Collection;
import java.util.Map;

public class CsjsEntityModel<T extends CsjsEntity> extends EntityModel<T> {
  private final ModelPart part;
  private CsjsModelTransformsMap transforms;

  protected CsjsEntityModel(final ModelPart part) {
    this.part = part;
  }

  public void setTransforms(final CsjsModelTransformsMap transforms) {
    this.transforms = transforms;
  }

  @Override
  public void setupAnim(final T entity, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch) {
    if(entity.getAnimation() != null) {
      for(final String partName : entity.partNextKeyframes.keySet()) {
        final CsjsModelTransforms partTransforms = this.transforms.get(partName);

        if(partTransforms != null) {
          final CsjsAnimation.Part animPart = entity.getAnimation().parts().get(partName);

          if(animPart.pos().size() != 0) {
            final int nextPosKeyframeIndex = entity.partNextKeyframes.get(partName).pos;
            final int currentPosKeyframeIndex = entity.partCurrentKeyframes.get(partName).pos;

            final CsjsAnimation.Part.Keyframe nextKeyframe = animPart.pos().get(nextPosKeyframeIndex);
            final CsjsAnimation.Part.Keyframe currentKeyframe = animPart.pos().get(currentPosKeyframeIndex);

            partTransforms.pos().set(currentKeyframe.vec().x(), currentKeyframe.vec().y(), currentKeyframe.vec().z());
            partTransforms.pos().lerp(nextKeyframe.vec(), Mth.clamp((float)(entity.animationTicks - currentKeyframe.ticks()) / (nextKeyframe.ticks() - currentKeyframe.ticks()), 0.0f, 1.0f));
            partTransforms.pos().add(entity.originalTransforms.get(partName).pos());
          }

          if(animPart.offset().size() != 0) {
            final int nextOffsetKeyframeIndex = entity.partNextKeyframes.get(partName).offset;
            final int currentOffsetKeyframeIndex = entity.partCurrentKeyframes.get(partName).offset;

            final CsjsAnimation.Part.Keyframe nextKeyframe = animPart.offset().get(nextOffsetKeyframeIndex);
            final CsjsAnimation.Part.Keyframe currentKeyframe = animPart.offset().get(currentOffsetKeyframeIndex);

            partTransforms.offset().set(currentKeyframe.vec().x(), currentKeyframe.vec().y(), currentKeyframe.vec().z());
            partTransforms.offset().lerp(nextKeyframe.vec(), Mth.clamp((float)(entity.animationTicks - currentKeyframe.ticks()) / (nextKeyframe.ticks() - currentKeyframe.ticks()), 0.0f, 1.0f));
            partTransforms.offset().add(entity.originalTransforms.get(partName).offset());
          }

          if(animPart.rotation().size() != 0) {
            final int nextRotationKeyframeIndex = entity.partNextKeyframes.get(partName).rotation;
            final int currentRotationKeyframeIndex = entity.partCurrentKeyframes.get(partName).rotation;

            final CsjsAnimation.Part.Keyframe nextKeyframe = animPart.rotation().get(nextRotationKeyframeIndex);
            final CsjsAnimation.Part.Keyframe currentKeyframe = animPart.rotation().get(currentRotationKeyframeIndex);

            partTransforms.rotation().set(currentKeyframe.vec().x(), currentKeyframe.vec().y(), currentKeyframe.vec().z());
            partTransforms.rotation().lerp(nextKeyframe.vec(), Mth.clamp((float)(entity.animationTicks - currentKeyframe.ticks()) / (nextKeyframe.ticks() - currentKeyframe.ticks()), 0.0f, 1.0f));
            partTransforms.rotation().add(entity.originalTransforms.get(partName).rotation());
          }

          if(animPart.size().size() != 0) {
            final int nextSizeKeyframeIndex = entity.partNextKeyframes.get(partName).size;
            final int currentSizeKeyframeIndex = entity.partCurrentKeyframes.get(partName).size;

            final CsjsAnimation.Part.Keyframe nextKeyframe = animPart.size().get(nextSizeKeyframeIndex);
            final CsjsAnimation.Part.Keyframe currentKeyframe = animPart.size().get(currentSizeKeyframeIndex);

            partTransforms.size().set(currentKeyframe.vec().x(), currentKeyframe.vec().y(), currentKeyframe.vec().z());
            partTransforms.size().lerp(nextKeyframe.vec(), Mth.clamp((float)(entity.animationTicks - currentKeyframe.ticks()) / (nextKeyframe.ticks() - currentKeyframe.ticks()), 0.0f, 1.0f));
            partTransforms.size().add(entity.originalTransforms.get(partName).size());
          }
        }
      }

      entity.animationTicks++;

      for(final Map.Entry<String, CsjsEntity.KeyframeIndices> entry : entity.partNextKeyframes.entrySet()) {
        final String partName = entry.getKey();

        final CsjsAnimation.Part animPart = entity.getAnimation().parts().get(partName);

        if(animPart.pos().size() != 0) {
          final CsjsEntity.KeyframeIndices keyframeIndices = entry.getValue();

          final int nextKeyframeIndex = keyframeIndices.pos;
          if(entity.animationTicks >= entity.getAnimation().parts().get(partName).pos().get(nextKeyframeIndex).ticks()) {
            entity.partCurrentKeyframes.get(partName).pos = keyframeIndices.pos;
            keyframeIndices.pos++;
          }
        }

        if(animPart.offset().size() != 0) {
          final CsjsEntity.KeyframeIndices keyframeIndices = entry.getValue();

          final int nextKeyframeIndex = keyframeIndices.offset;
          if(entity.animationTicks >= entity.getAnimation().parts().get(partName).offset().get(nextKeyframeIndex).ticks()) {
            entity.partCurrentKeyframes.get(partName).offset = keyframeIndices.offset;
            keyframeIndices.offset++;
          }
        }

        if(animPart.rotation().size() != 0) {
          final CsjsEntity.KeyframeIndices keyframeIndices = entry.getValue();

          final int nextKeyframeIndex = keyframeIndices.rotation;
          if(entity.animationTicks >= entity.getAnimation().parts().get(partName).rotation().get(nextKeyframeIndex).ticks()) {
            entity.partCurrentKeyframes.get(partName).rotation = keyframeIndices.rotation;
            keyframeIndices.rotation++;
          }
        }

        if(animPart.size().size() != 0) {
          final CsjsEntity.KeyframeIndices keyframeIndices = entry.getValue();

          final int nextKeyframeIndex = keyframeIndices.size;
          if(entity.animationTicks >= entity.getAnimation().parts().get(partName).size().get(nextKeyframeIndex).ticks()) {
            entity.partCurrentKeyframes.get(partName).size = keyframeIndices.size;
            keyframeIndices.size++;
          }
        }
      }

      if(entity.animationTicks >= entity.getAnimation().duration()) {
        entity.animationTicks = 0;

        for(final String partName : entity.partCurrentKeyframes.keySet()) {
          entity.partCurrentKeyframes.get(partName).pos = 0;
          entity.partCurrentKeyframes.get(partName).offset = 0;
          entity.partCurrentKeyframes.get(partName).size = 0;
          entity.partCurrentKeyframes.get(partName).rotation = 0;
          entity.partNextKeyframes.get(partName).pos = 1;
          entity.partNextKeyframes.get(partName).offset = 1;
          entity.partNextKeyframes.get(partName).size = 1;
          entity.partNextKeyframes.get(partName).rotation = 1;
        }
      }
    }
  }

  @Override
  public void renderToBuffer(final PoseStack matrixStack, final VertexConsumer buffer, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
    matrixStack.pushPose();
    matrixStack.translate(0.0d, 24.0d / 16.0d, 0.0d);

    this.renderChildren(this.transforms.roots(), matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

    matrixStack.popPose();
  }

  private void renderChildren(final Collection<CsjsModelTransforms> children, final PoseStack matrixStack, final VertexConsumer buffer, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
    for(final CsjsModelTransforms anim : children) {
      this.renderPart(this.part.getChild(anim.name()), anim, matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
  }

  private void renderPart(final ModelPart part, final CsjsModelTransforms anim, final PoseStack matrixStack, final VertexConsumer buffer, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
    matrixStack.pushPose();
    matrixStack.translate(anim.pos().x() / 16.0f, -anim.pos().y() / 16.0f, -anim.pos().z() / 16.0f);
    matrixStack.mulPose(Quaternion.fromXYZDegrees(anim.rotation()));
    matrixStack.translate(anim.offset().x() / 16.0f, -anim.offset().y() / 16.0f, -anim.offset().z() / 16.0f);

    part.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

    this.renderChildren(anim.children().values(), matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

    matrixStack.popPose();
  }
}

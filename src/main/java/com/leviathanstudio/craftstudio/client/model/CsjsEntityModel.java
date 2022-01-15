package com.leviathanstudio.craftstudio.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;

public class CsjsEntityModel<T extends LivingEntity> extends EntityModel<T> {
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
    this.transforms.get("ArmLeft").rotation().add(0.1f, 0, 0);
    this.transforms.get("ArmLeftLower").rotation().add(0.1f, 0, 0);
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
    matrixStack.mulPose(Quaternion.fromXYZ(anim.rotation()));
    matrixStack.translate(anim.offset().x() / 16.0f, -anim.offset().y() / 16.0f, -anim.offset().z() / 16.0f);

    part.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

    this.renderChildren(anim.children().values(), matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);

    matrixStack.popPose();
  }
}

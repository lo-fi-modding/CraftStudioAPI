package com.leviathanstudio.craftstudio.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;

public class CsjsEntityModel<T extends LivingEntity> extends EntityModel<T> {
  private final ModelPart part;

  protected CsjsEntityModel(final ModelPart part) {
    this.part = part.getChild("main");
  }

  @Override
  public void setupAnim(final T entity, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch) {

  }

  @Override
  public void renderToBuffer(final PoseStack matrixStack, final VertexConsumer buffer, final int packedLight, final int packedOverlay, final float red, final float green, final float blue, final float alpha) {
    this.part.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
  }
}

package com.leviathanstudio.craftstudio.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;

public class CsjsLayer<T extends LivingEntity> extends RenderLayer<T, CsjsEntityModel<T>> {
  private final CsjsEntityModel<T> model;

  public CsjsLayer(final RenderLayerParent<T, CsjsEntityModel<T>> parent) {
    super(parent);
    this.model = parent.getModel();
  }

  @Override
  public void render(final PoseStack matrixStack, final MultiBufferSource buffer, final int packedLight, final T entity, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch) {
    final VertexConsumer vertexConsumer = buffer.getBuffer(this.getParentModel().renderType(this.getTextureLocation(entity)));
    this.model.renderToBuffer(matrixStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
  }
}

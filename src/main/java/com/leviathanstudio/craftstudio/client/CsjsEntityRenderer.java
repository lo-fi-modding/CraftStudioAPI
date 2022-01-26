package com.leviathanstudio.craftstudio.client;

import com.leviathanstudio.craftstudio.CsjsEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class CsjsEntityRenderer<T extends CsjsEntity> extends LivingEntityRenderer<T, CsjsEntityModel<T>> {
  private static final ResourceLocation TEXTURE = new ResourceLocation("craftstudioapi:textures/entity/architect_m.png");

  public CsjsEntityRenderer(final EntityRendererProvider.Context ctx, final ModelLayerLocation layerLoc) {
    super(ctx, new CsjsEntityModel<>(ctx.bakeLayer(layerLoc)), 1.0f);
  }

  @Override
  public void render(final T entity, final float yaw, final float partialTicks, final PoseStack matrixStack, final MultiBufferSource buffer, final int packedLight) {
    this.model.setTransforms(((CsjsClientAnimationState)entity.getAnimationState()).getTransforms());
    super.render(entity, yaw, partialTicks, matrixStack, buffer, packedLight);
  }

  @Override
  public ResourceLocation getTextureLocation(final T entity) {
    return TEXTURE;
  }
}

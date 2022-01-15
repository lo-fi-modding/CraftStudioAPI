package com.leviathanstudio.craftstudio.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class CsjsEntityRenderer<T extends LivingEntity> extends LivingEntityRenderer<T, CsjsEntityModel<T>> {
  private static final ResourceLocation TEXTURE = new ResourceLocation("craftstudioapi:textures/entity/architect_m.png");

  public CsjsEntityRenderer(final EntityRendererProvider.Context ctx, final ModelLayerLocation layerLocation) {
    super(ctx, new CsjsEntityModel<>(ctx.bakeLayer(layerLocation)), 1.0f);
  }

  @Override
  public void render(T pEntity, float pEntityYaw, float pPartialTicks, PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight) {
    super.render(pEntity, pEntityYaw, pPartialTicks, pMatrixStack, pBuffer, pPackedLight);
  }

  @Override
  public ResourceLocation getTextureLocation(final T entity) {
    return TEXTURE;
  }
}

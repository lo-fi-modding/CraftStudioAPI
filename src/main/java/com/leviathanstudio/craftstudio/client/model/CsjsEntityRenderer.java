package com.leviathanstudio.craftstudio.client.model;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class CsjsEntityRenderer<T extends LivingEntity> extends LivingEntityRenderer<T, CsjsEntityModel<T>> {
  private static final ResourceLocation TEXTURE = new ResourceLocation("entity_renderer_events_test:textures/entity/test_entity.png");

  public CsjsEntityRenderer(final EntityRendererProvider.Context ctx, final ModelLayerLocation layerLocation) {
    super(ctx, new CsjsEntityModel<>(ctx.bakeLayer(layerLocation)), 1.0f);
  }

  @Override
  public ResourceLocation getTextureLocation(final T entity) {
    return TEXTURE;
  }
}

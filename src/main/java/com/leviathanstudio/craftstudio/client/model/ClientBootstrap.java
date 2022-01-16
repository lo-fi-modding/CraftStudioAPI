package com.leviathanstudio.craftstudio.client.model;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = CraftStudioApi.API_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientBootstrap {
  private ClientBootstrap() { }

  public static final Map<ResourceLocation, CsjsModelTransformsMap> ANIMATION_DATA = new HashMap<>();

  public static final ModelLayerLocation LAYER = new ModelLayerLocation(CraftStudioApi.loc("test"), "test_layer");

  public static final ResourceLocation MODEL = CraftStudioApi.loc("models/block/test.csjsmodel");

  @SubscribeEvent
  public static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(LAYER, () -> {
      final CsjsModelTransformsMap transforms = CraftStudioApi.loadModel(MODEL);
      ANIMATION_DATA.put(MODEL, transforms);

      MeshDefinition meshDef = new MeshDefinition();
      PartDefinition partDef = meshDef.getRoot();

      loadParts(transforms.roots(), partDef);

      return LayerDefinition.create(meshDef, 64, 64);
    });
  }

  @SubscribeEvent
  public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(CraftStudioApi.TEST_ENTITY.get(), ctx -> new CsjsEntityRenderer<>(ctx, LAYER));
  }

  private static void loadParts(final Collection<CsjsModelTransforms> children, final PartDefinition partDef) {
    for(final CsjsModelTransforms transform : children) {
      partDef.addOrReplaceChild(transform.name(), CubeListBuilder.create().texOffs((int)transform.uv().x(), (int)transform.uv().y()).addBox(-transform.size().x() / 2, -transform.size().y() / 2, -transform.size().z() / 2, transform.size().x(), transform.size().y(), transform.size().z()), PartPose.ZERO);
      loadParts(transform.children().values(), partDef);
    }
  }
}

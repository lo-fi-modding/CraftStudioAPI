package com.leviathanstudio.craftstudio.client.model;

import com.google.common.base.Charsets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.leviathanstudio.craftstudio.CraftStudioApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@Mod.EventBusSubscriber(modid = CraftStudioApi.API_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ClientBootstrap {
  private ClientBootstrap() { }

  public static final ModelLayerLocation LAYER = new ModelLayerLocation(CraftStudioApi.loc("test"), "test_layer");

  @SubscribeEvent
  public static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(LAYER, () -> {
      final Resource res;
      try {
        res = Minecraft.getInstance().getResourceManager().getResource(CraftStudioApi.loc("models/block/test.csjsmodel"));
      } catch(final IOException e) {
        throw new RuntimeException(e);
      }

      final Reader reader = new BufferedReader(new InputStreamReader(res.getInputStream(), Charsets.UTF_8));
      final JsonElement json = new JsonParser().parse(reader).getAsJsonObject();

      MeshDefinition meshdefinition = new MeshDefinition();
      PartDefinition partdefinition = meshdefinition.getRoot();
      partdefinition.addOrReplaceChild("main", CubeListBuilder.create().addBox(-5, 10, -3, 10, 4, 6), PartPose.ZERO);
      return LayerDefinition.create(meshdefinition, 64, 32);
    });
  }

  @SubscribeEvent
  public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(CraftStudioApi.TEST_ENTITY.get(), ctx -> new CsjsEntityRenderer<>(ctx, LAYER));
  }
}

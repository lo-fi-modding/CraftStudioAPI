package com.leviathanstudio.craftstudio.client.model;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;

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
      final JsonObject json = new JsonParser().parse(reader).getAsJsonObject();

      final JsonArray tree = Objects.requireNonNull(GsonHelper.getAsJsonArray(json, "tree", null));

      MeshDefinition meshDef = new MeshDefinition();
      PartDefinition partDef = meshDef.getRoot();

      loadParts(tree, partDef, new Vector3f(0.0f, 24.0f, 0.0f));

      return LayerDefinition.create(meshDef, 64, 64);
    });
  }

  @SubscribeEvent
  public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(CraftStudioApi.TEST_ENTITY.get(), ctx -> new CsjsEntityRenderer<>(ctx, LAYER));
  }

  private static void loadParts(final JsonArray jsonArray, final PartDefinition partDef, final Vector3f parentPos) {
    for(final JsonElement element : jsonArray) {
      final JsonObject json = element.getAsJsonObject();

      final String name = GsonHelper.getAsString(json, "name");

      final Vector3f pos = parseVec3(GsonHelper.getAsJsonArray(json, "position"));
      final Vector3f offset = parseVec3(GsonHelper.getAsJsonArray(json, "offsetFromPivot"));
      final Vector3f size = parseVec3(GsonHelper.getAsJsonArray(json, "size"));
      final Vector3f rotation = parseVec3(GsonHelper.getAsJsonArray(json, "rotation"));
      final Vector3f uv = parseVec2(GsonHelper.getAsJsonArray(json, "texOffset"));

      final float x = parentPos.x() + pos.x() + offset.x();
      final float y = parentPos.y() - pos.y() - offset.y();
      final float z = parentPos.z() - pos.z() - offset.z();

      partDef.addOrReplaceChild(name, CubeListBuilder.create().texOffs((int)uv.x(), (int)uv.y()).addBox(x - size.x() / 2, y - size.y() / 2, z - size.z() / 2, size.x(), size.y(), size.z()), PartPose.ZERO);

      if(json.has("children")) {
        final JsonArray childrenJson = Objects.requireNonNull(GsonHelper.getAsJsonArray(json, "children", null));

        loadParts(childrenJson, partDef, new Vector3f(x, y, z));
      }
    }
  }

  private static Vector3f parseVec3(final JsonArray jsonArray) {
    return new Vector3f(jsonArray.get(0).getAsFloat(), jsonArray.get(1).getAsFloat(), jsonArray.get(2).getAsFloat());
  }

  private static Vector3f parseVec2(final JsonArray jsonArray) {
    return new Vector3f(jsonArray.get(0).getAsFloat(), jsonArray.get(1).getAsFloat(), 0.0f);
  }
}

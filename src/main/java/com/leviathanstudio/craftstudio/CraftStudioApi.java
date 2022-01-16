package com.leviathanstudio.craftstudio;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.leviathanstudio.craftstudio.client.model.CsjsAnimation;
import com.leviathanstudio.craftstudio.client.model.CsjsModelTransforms;
import com.leviathanstudio.craftstudio.client.model.CsjsModelTransformsMap;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Mod(CraftStudioApi.API_ID)
public class CraftStudioApi {
  public static final String API_ID = "craftstudioapi";

  public static final Logger LOGGER = LogManager.getLogger(CraftStudioApi.class);

  private static final DeferredRegister<EntityType<?>> ENTITY_TYPE_REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, API_ID);

  public static final RegistryObject<EntityType<CsjsTestEntity>> TEST_ENTITY = ENTITY_TYPE_REGISTRY.register("test", () -> EntityType.Builder.<CsjsTestEntity>of(CsjsTestEntity::new, MobCategory.AMBIENT).sized(0.6f, 1.95f).clientTrackingRange(10).build("test"));

  public CraftStudioApi() {
    final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

    bus.addListener(this::registerEntityAttributes);

    ENTITY_TYPE_REGISTRY.register(bus);
  }

  private void registerEntityAttributes(final EntityAttributeCreationEvent event) {
    event.put(TEST_ENTITY.get(), Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 1.0d).build());
  }

  public static ResourceLocation loc(final String path) {
    return new ResourceLocation(API_ID, path);
  }

  public static CsjsModelTransformsMap loadModel(final ResourceLocation file) {
    final JsonObject json = loadJson(file);

    final JsonArray tree = Objects.requireNonNull(GsonHelper.getAsJsonArray(json, "tree", null));

    return new CsjsModelTransformsMap(loadParts(tree, null));
  }

  public static CsjsAnimation loadAnimation(final ResourceLocation file) {
    final JsonObject json = loadJson(file);

    final String title = GsonHelper.getAsString(json, "title");
    final int duration = GsonHelper.getAsInt(json, "duration");

    if(GsonHelper.getAsBoolean(json, "holdLastKeyframe", false)) {
      throw new RuntimeException("Don't know what holdLastKeyframe does");
    }

    final Map<String, CsjsAnimation.Part> parts = new HashMap<>();

    final JsonObject partsJson = GsonHelper.getAsJsonObject(json, "nodeAnimations");
    for(final Map.Entry<String, JsonElement> partJsonElement : partsJson.entrySet()) {
      final JsonObject partJson = partJsonElement.getValue().getAsJsonObject();

      final List<CsjsAnimation.Part.Keyframe> pos = parseKeyframes(GsonHelper.getAsJsonObject(partJson, "position"));
      final List<CsjsAnimation.Part.Keyframe> offset = parseKeyframes(GsonHelper.getAsJsonObject(partJson, "offsetFromPivot"));
      final List<CsjsAnimation.Part.Keyframe> size = parseKeyframes(GsonHelper.getAsJsonObject(partJson, "size"));
      final List<CsjsAnimation.Part.Keyframe> rotation = parseKeyframes(GsonHelper.getAsJsonObject(partJson, "rotation"));

      parts.put(partJsonElement.getKey(), new CsjsAnimation.Part(pos, offset, size, rotation));
    }

    return new CsjsAnimation(title, duration, parts);
  }

  private static Map<String, CsjsModelTransforms> loadParts(final JsonArray jsonArray, @Nullable final CsjsModelTransforms parent) {
    final Map<String, CsjsModelTransforms> animationData = new HashMap<>();

    for(final JsonElement element : jsonArray) {
      final JsonObject json = element.getAsJsonObject();

      final String name = GsonHelper.getAsString(json, "name");

      final Vector3f pos = parseVec3(GsonHelper.getAsJsonArray(json, "position"));
      final Vector3f offset = parseVec3(GsonHelper.getAsJsonArray(json, "offsetFromPivot"));
      final Vector3f size = parseVec3(GsonHelper.getAsJsonArray(json, "size"));
      final Vector3f rotation = parseVec3(GsonHelper.getAsJsonArray(json, "rotation"));
      final Vector3f uv = parseVec2(GsonHelper.getAsJsonArray(json, "texOffset"));

      final Map<String, CsjsModelTransforms> children = new HashMap<>();
      final CsjsModelTransforms anim = new CsjsModelTransforms(parent, children, name, pos, offset, size, rotation, uv);
      animationData.put(name, anim);

      if(json.has("children")) {
        final JsonArray childrenJson = Objects.requireNonNull(GsonHelper.getAsJsonArray(json, "children", null));

        anim.children().putAll(loadParts(childrenJson, anim));
      }
    }

    return animationData;
  }

  private static JsonObject loadJson(final ResourceLocation file) {
    final Resource res;
    try {
      res = Minecraft.getInstance().getResourceManager().getResource(file);
    } catch(final IOException e) {
      throw new RuntimeException(e);
    }

    final Reader reader = new BufferedReader(new InputStreamReader(res.getInputStream(), Charsets.UTF_8));
    return JsonParser.parseReader(reader).getAsJsonObject();
  }

  private static List<CsjsAnimation.Part.Keyframe> parseKeyframes(final JsonObject json) {
    final List<CsjsAnimation.Part.Keyframe> animSet = new ArrayList<>();

    for(final Map.Entry<String, JsonElement> entry : json.entrySet()) {
      final CsjsAnimation.Part.Keyframe keyframe = new CsjsAnimation.Part.Keyframe(Integer.parseInt(entry.getKey()), parseVec3(entry.getValue().getAsJsonArray()));

      keyframe.vec().setY(-keyframe.vec().y());
      keyframe.vec().setZ(-keyframe.vec().z());

      animSet.add(keyframe);
    }

    return animSet;
  }

  private static Vector3f parseVec3(final JsonArray jsonArray) {
    return new Vector3f(jsonArray.get(0).getAsFloat(), jsonArray.get(1).getAsFloat(), jsonArray.get(2).getAsFloat());
  }

  private static Vector3f parseVec2(final JsonArray jsonArray) {
    return new Vector3f(jsonArray.get(0).getAsFloat(), jsonArray.get(1).getAsFloat(), 0.0f);
  }
}

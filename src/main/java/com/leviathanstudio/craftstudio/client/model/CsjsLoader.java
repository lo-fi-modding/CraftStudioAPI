package com.leviathanstudio.craftstudio.client.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.IModelLoader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CsjsLoader implements IModelLoader<CsjsBlockModel> {
  public static final Gson GSON = new GsonBuilder().registerTypeAdapter(CsjsBlockModel.class, new CsjsBlockModel.Deserializer()).create();

  private final Map<ResourceLocation, CsjsBlockModel> modelCache = new HashMap<>();

  private ResourceManager manager = Minecraft.getInstance().getResourceManager();

  @Override
  public void onResourceManagerReload(final ResourceManager pResourceManager) {
    this.manager = pResourceManager;
    this.modelCache.clear();
  }

  @Override
  public CsjsBlockModel read(final JsonDeserializationContext deserializationContext, final JsonObject modelContents) {
    if(!modelContents.has("model")) {
      throw new RuntimeException("CraftStudio Loader requires a 'model' key that points to a valid CraftStudio model.");
    }

    final String path = modelContents.get("model").getAsString();

    return this.loadModel(new ResourceLocation(path));
  }

  public CsjsBlockModel loadModel(final ResourceLocation path) {
    return this.modelCache.computeIfAbsent(path, modelPath -> {
      try(final Resource res = this.manager.getResource(modelPath)) {
        return Objects.requireNonNull(GsonHelper.fromJson(GSON, new BufferedReader(new InputStreamReader(res.getInputStream())), CsjsBlockModel.class));
      } catch(final FileNotFoundException e) {
        throw new RuntimeException("Could not find CraftStudio model", e);
      } catch(IOException e) {
        throw new RuntimeException("Could not read CraftStudio model", e);
      }
    });
  }
}

package com.leviathanstudio.craftstudio.client.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometryPart;
import net.minecraftforge.client.model.geometry.IMultipartModelGeometry;
import net.minecraftforge.client.model.pipeline.BakedQuadBuilder;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class CsjsModel implements IMultipartModelGeometry<CsjsModel> {
  public final String title;
  private final Map<String, Part> parts;
  private final Map<String, Part> flatParts;

  public CsjsModel(final String title, final Map<String, Part> parts) {
    this.title = title;
    this.parts = parts;
    this.flatParts = this.flattenParts(new HashMap<>(), this.parts);
  }

  private Map<String, Part> flattenParts(final Map<String, Part> output, final Map<String, Part> parts) {
    output.putAll(parts);

    for(final Part part : parts.values()) {
      this.flattenParts(output, part.children);
    }

    return output;
  }

  @Override
  public Collection<? extends IModelGeometryPart> getParts() {
    return this.flatParts.values();
  }

  @Override
  public Optional<? extends IModelGeometryPart> getPart(final String name) {
    return Optional.ofNullable(this.flatParts.get(name));
  }

  public static class Part implements IModelGeometryPart {
    public final String name;
    public final Map<String, Part> children;

    public final Vector3f parentPos;

    public final Vector3f pos;
    public final Vector3f offset;
    public final Vector3f size;
    public final Vector3f rotation;
    public final Vector3f uv; // Only uses (x, y)

    public Part(final String name, final Map<String, Part> children, final Vector3f parentPos, final Vector3f pos, final Vector3f offset, final Vector3f size, final Vector3f rotation, final Vector3f uv) {
      this.name = name;
      this.children = children;
      this.parentPos = parentPos;
      this.pos = pos;
      this.offset = offset;
      this.size = size;
      this.rotation = rotation;
      this.uv = uv;
    }

    @Override
    public String name() {
      return this.name;
    }

    @Override
    public void addQuads(final IModelConfiguration owner, final IModelBuilder<?> modelBuilder, final ModelBakery bakery, final Function<Material, TextureAtlasSprite> spriteGetter, final ModelState modelTransform, final ResourceLocation modelLocation) {
      final TextureAtlasSprite sprite = spriteGetter.apply(owner.resolveTexture("texture"));

      this.buildPart(modelBuilder, sprite, this);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration owner, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
      return Collections.singletonList(owner.resolveTexture("texture"));
    }

    private void buildPart(final IModelBuilder<?> modelBuilder, final TextureAtlasSprite sprite, final Part part) {
      //TODO centre point
      final float originX = 8.0f - part.size.x() / 2.0f;
      final float originY =      - part.size.y() / 2.0f;
      final float originZ = 8.0f - part.size.z() / 2.0f;

      final float x = this.parentPos.x() + part.pos.x() + originX + this.offset.x();
      final float y = this.parentPos.y() + part.pos.y() + originY + this.offset.y();
      final float z = this.parentPos.z() + part.pos.z() + originZ + this.offset.z();

      final float scale = 16.0f;
      final float texScale = 64.0f;

      final float x0 = x / scale;
      final float y0 = y / scale;
      final float z0 = z / scale;
      final float x1 = (x + part.size.x()) / scale;
      final float y1 = (y + part.size.y()) / scale;
      final float z1 = (z + part.size.z()) / scale;

      final float u0 = part.uv.x() / texScale;
      final float v0 = part.uv.y() / texScale;
      final float u1x = (part.uv.x() + part.size.x()) / texScale;
      final float u1z = (part.uv.x() + part.size.z()) / texScale;
      final float v1y = (part.uv.y() + part.size.y()) / texScale;
      final float v1z = (part.uv.y() + part.size.z()) / texScale;

      final float uvOffsetX = part.size.x() / texScale;
      final float uvOffsetZ = part.size.z() / texScale;

      // North
      BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
      this.putVertexData(builder, new Vector4f(x0, y0, z0, 1.0f), new Vec2(uvOffsetX + uvOffsetZ * 2.0f + u1x, uvOffsetZ + v1y), Direction.NORTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y1, z0, 1.0f), new Vec2(uvOffsetX + uvOffsetZ * 2.0f + u1x, uvOffsetZ + v0 ), Direction.NORTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y1, z0, 1.0f), new Vec2(uvOffsetX + uvOffsetZ * 2.0f + u0 , uvOffsetZ + v0 ), Direction.NORTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y0, z0, 1.0f), new Vec2(uvOffsetX + uvOffsetZ * 2.0f + u0 , uvOffsetZ + v1y), Direction.NORTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      builder.setQuadOrientation(Direction.NORTH);
      modelBuilder.addGeneralQuad(builder.build());

      // South
      builder = new BakedQuadBuilder(sprite);
      this.putVertexData(builder, new Vector4f(x0, y0, z1, 1.0f), new Vec2(uvOffsetZ + u0 , uvOffsetZ + v1y), Direction.SOUTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y0, z1, 1.0f), new Vec2(uvOffsetZ + u1x, uvOffsetZ + v1y), Direction.SOUTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y1, z1, 1.0f), new Vec2(uvOffsetZ + u1x, uvOffsetZ + v0 ), Direction.SOUTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y1, z1, 1.0f), new Vec2(uvOffsetZ + u0 , uvOffsetZ + v0 ), Direction.SOUTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      builder.setQuadOrientation(Direction.SOUTH);
      modelBuilder.addGeneralQuad(builder.build());

      // West
      builder = new BakedQuadBuilder(sprite);
      this.putVertexData(builder, new Vector4f(x0, y0, z0, 1.0f), new Vec2(u0 , uvOffsetZ + v1y), Direction.WEST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y0, z1, 1.0f), new Vec2(u1z, uvOffsetZ + v1y), Direction.WEST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y1, z1, 1.0f), new Vec2(u1z, uvOffsetZ + v0 ), Direction.WEST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y1, z0, 1.0f), new Vec2(u0 , uvOffsetZ + v0 ), Direction.WEST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      builder.setQuadOrientation(Direction.WEST);
      modelBuilder.addGeneralQuad(builder.build());

      // East
      builder = new BakedQuadBuilder(sprite);
      this.putVertexData(builder, new Vector4f(x1, y0, z0, 1.0f), new Vec2(uvOffsetX + uvOffsetX + u1z, uvOffsetZ + v1y), Direction.EAST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y1, z0, 1.0f), new Vec2(uvOffsetX + uvOffsetX + u1z, uvOffsetZ + v0 ), Direction.EAST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y1, z1, 1.0f), new Vec2(uvOffsetX + uvOffsetX + u0 , uvOffsetZ + v0 ), Direction.EAST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y0, z1, 1.0f), new Vec2(uvOffsetX + uvOffsetX + u0 , uvOffsetZ + v1y), Direction.EAST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      builder.setQuadOrientation(Direction.EAST);
      modelBuilder.addGeneralQuad(builder.build());

      // Up
      builder = new BakedQuadBuilder(sprite);
      this.putVertexData(builder, new Vector4f(x0, y1, z0, 1.0f), new Vec2(uvOffsetX + u0 , v0 ), Direction.UP.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y1, z1, 1.0f), new Vec2(uvOffsetX + u0 , v1z), Direction.UP.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y1, z1, 1.0f), new Vec2(uvOffsetX + u1x, v1z), Direction.UP.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y1, z0, 1.0f), new Vec2(uvOffsetX + u1x, v0 ), Direction.UP.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      builder.setQuadOrientation(Direction.UP);
      modelBuilder.addGeneralQuad(builder.build());

      // Down
      builder = new BakedQuadBuilder(sprite);
      this.putVertexData(builder, new Vector4f(x0, y0, z0, 1.0f), new Vec2(uvOffsetX * 2.0f + u0 , v1z), Direction.DOWN.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y0, z0, 1.0f), new Vec2(uvOffsetX * 2.0f + u1x, v1z), Direction.DOWN.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y0, z1, 1.0f), new Vec2(uvOffsetX * 2.0f + u1x, v0 ), Direction.DOWN.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y0, z1, 1.0f), new Vec2(uvOffsetX * 2.0f + u0 , v0 ), Direction.DOWN.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      builder.setQuadOrientation(Direction.DOWN);
      modelBuilder.addGeneralQuad(builder.build());
    }

    private void putVertexData(IVertexConsumer consumer, Vector4f position0, Vec2 texCoord0, Vec3i normal0, Vector4f color0, Vec2 uv2, TextureAtlasSprite texture) {
      ImmutableList<VertexFormatElement> elements = consumer.getVertexFormat().getElements();
      for(int j = 0; j < elements.size(); j++) {
        VertexFormatElement e = elements.get(j);
        switch(e.getUsage()) {
          case POSITION:
            consumer.put(j, position0.x(), position0.y(), position0.z(), position0.w());
            break;
          case COLOR:
            consumer.put(j, color0.x(), color0.y(), color0.z(), color0.w());
            break;
          case UV:
            switch(e.getIndex()) {
              case 0 -> consumer.put(j, texture.getU(texCoord0.x * 16), texture.getV(texCoord0.y * 16));
              case 2 -> consumer.put(j, uv2.x, uv2.y);
              default -> consumer.put(j);
            }
            break;
          case NORMAL:
            consumer.put(j, normal0.getX(), normal0.getY(), normal0.getZ());
            break;
          default:
            consumer.put(j);
            break;
        }
      }
    }
  }

  public static class Deserializer implements JsonDeserializer<CsjsModel> {
    @Override
    public CsjsModel deserialize(final JsonElement jsonElement, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
      final JsonObject json = jsonElement.getAsJsonObject();

      final String title = GsonHelper.getAsString(json, "title", "");

      final JsonArray tree = GsonHelper.getAsJsonArray(json, "tree", null);
      Objects.requireNonNull(tree);

      final Map<String, Part> parts = this.loadParts(tree, new HashMap<>(), new Vector3f());

      return new CsjsModel(title, parts);
    }

    private Map<String, Part> loadParts(final JsonArray jsonArray, final Map<String, Part> parts, final Vector3f parentPos) {
      for(final JsonElement element : jsonArray) {
        final JsonObject json = element.getAsJsonObject();

        final String name = GsonHelper.getAsString(json, "name");

        final Vector3f pos = this.parseVec3(GsonHelper.getAsJsonArray(json, "position"));
        final Vector3f offset = this.parseVec3(GsonHelper.getAsJsonArray(json, "offsetFromPivot"));
        final Vector3f size = this.parseVec3(GsonHelper.getAsJsonArray(json, "size"));
        final Vector3f rotation = this.parseVec3(GsonHelper.getAsJsonArray(json, "rotation"));
        final Vector3f uv = this.parseVec2(GsonHelper.getAsJsonArray(json, "texOffset"));

        final Map<String, Part> children = new HashMap<>();

        if(json.has("children")) {
          final JsonArray childrenJson = Objects.requireNonNull(GsonHelper.getAsJsonArray(json, "children", null));

          final float parentX = parentPos.x() + pos.x() + offset.x();
          final float parentY = parentPos.y() + pos.y() + offset.y();
          final float parentZ = parentPos.z() + pos.z() + offset.z();

          this.loadParts(childrenJson, children, new Vector3f(parentX, parentY, parentZ));
        }

        parts.put(name, new Part(name, children, parentPos, pos, offset, size, rotation, uv));
      }

      return parts;
    }

    private Vector3f parseVec3(final JsonArray jsonArray) {
      return new Vector3f(jsonArray.get(0).getAsFloat(), jsonArray.get(1).getAsFloat(), jsonArray.get(2).getAsFloat());
    }

    private Vector3f parseVec2(final JsonArray jsonArray) {
      return new Vector3f(jsonArray.get(0).getAsFloat(), jsonArray.get(1).getAsFloat(), 0.0f);
    }
  }
}

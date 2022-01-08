package com.leviathanstudio.craftstudio.client.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class CsjsModel implements IMultipartModelGeometry<CsjsModel> {
  public final String title;
  private final Map<String, Part> parts;
  private final List<Part> flatParts;

  public CsjsModel(final String title, final Map<String, Part> parts) {
    this.title = title;
    this.parts = parts;
    this.flatParts = this.flattenParts(new ArrayList<>(), this.parts);
  }

  private List<Part> flattenParts(final List<Part> output, final Map<String, Part> parts) {
    output.addAll(parts.values());

    for(final Part part : parts.values()) {
      this.flattenParts(output, part.children);
    }

    return output;
  }

  @Override
  public Collection<? extends IModelGeometryPart> getParts() {
    return this.flatParts;
  }

  @Override
  public Optional<? extends IModelGeometryPart> getPart(final String name) {
    return Optional.empty();
  }

  public static class Part implements IModelGeometryPart {
    public final String name;
    public final Map<String, Part> children;

    public final Vector3f pos;
    public final Vector3f offset;
    public final Vector3f size;
    public final Vector3f rotation;
    public final Vector3f uv; // Only uses (x, y)

    public Part(final String name, final Map<String, Part> children, final Vector3f pos, final Vector3f offset, final Vector3f size, final Vector3f rotation, final Vector3f uv) {
      this.name = name;
      this.children = children;
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
      //TODO this might be getting called multiple times for children since the child parts are also part of the main model (see flattenParts etc above)

      final TextureAtlasSprite sprite = spriteGetter.apply(owner.resolveTexture("texture"));

      this.buildPart(modelBuilder, sprite, this, 0.0f, 0.0f, 0.0f);
    }

    private void buildPart(final IModelBuilder<?> modelBuilder, final TextureAtlasSprite sprite, final Part part, final float offsetX, final float offsetY, final float offsetZ) {
      //TODO centre point
      final float originX = 0.0f;
      final float originY = 0.0f;
      final float originZ = 0.0f;

      final float x = offsetX + part.pos.x() + originX;
      final float y = offsetY + part.pos.y() + originY;
      final float z = offsetZ + part.pos.z() + originZ;

      final float scale = 16.0f;

      final float x0 = x / scale;
      final float x1 = (x + part.size.x()) / scale;
      final float y0 = y / scale;
      final float y1 = (y + part.size.y()) / scale;
      final float z0 = z / scale;
      final float z1 = (z + part.size.z()) / scale;

      // North
      BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
      this.putVertexData(builder, new Vector4f(x0, y0, z0, 1.0f), new Vec2(0, 0), Direction.NORTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y1, z0, 1.0f), new Vec2(0, 1), Direction.NORTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y1, z0, 1.0f), new Vec2(1, 1), Direction.NORTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y0, z0, 1.0f), new Vec2(1, 0), Direction.NORTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      builder.setQuadOrientation(Direction.NORTH);
      modelBuilder.addGeneralQuad(builder.build());

      // South
      builder = new BakedQuadBuilder(sprite);
      this.putVertexData(builder, new Vector4f(x0, y0, z1, 1.0f), new Vec2(0, 0), Direction.SOUTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y0, z1, 1.0f), new Vec2(1, 0), Direction.SOUTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y1, z1, 1.0f), new Vec2(1, 1), Direction.SOUTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y1, z1, 1.0f), new Vec2(0, 1), Direction.SOUTH.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      builder.setQuadOrientation(Direction.SOUTH);
      modelBuilder.addGeneralQuad(builder.build());

      // West
      builder = new BakedQuadBuilder(sprite);
      this.putVertexData(builder, new Vector4f(x0, y0, z0, 1.0f), new Vec2(0, 0), Direction.WEST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y0, z1, 1.0f), new Vec2(1, 0), Direction.WEST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y1, z1, 1.0f), new Vec2(1, 1), Direction.WEST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y1, z0, 1.0f), new Vec2(0, 1), Direction.WEST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      builder.setQuadOrientation(Direction.WEST);
      modelBuilder.addGeneralQuad(builder.build());

      // East
      builder = new BakedQuadBuilder(sprite);
      this.putVertexData(builder, new Vector4f(x1, y0, z0, 1.0f), new Vec2(0, 0), Direction.EAST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y1, z0, 1.0f), new Vec2(0, 1), Direction.EAST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y1, z1, 1.0f), new Vec2(1, 1), Direction.EAST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y0, z1, 1.0f), new Vec2(1, 0), Direction.EAST.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      builder.setQuadOrientation(Direction.EAST);
      modelBuilder.addGeneralQuad(builder.build());

      // Up
      builder = new BakedQuadBuilder(sprite);
      this.putVertexData(builder, new Vector4f(x0, y1, z0, 1.0f), new Vec2(0, 0), Direction.UP.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y1, z1, 1.0f), new Vec2(1, 0), Direction.UP.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y1, z1, 1.0f), new Vec2(1, 1), Direction.UP.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y1, z0, 1.0f), new Vec2(0, 1), Direction.UP.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      builder.setQuadOrientation(Direction.UP);
      modelBuilder.addGeneralQuad(builder.build());

      // Down
      builder = new BakedQuadBuilder(sprite);
      this.putVertexData(builder, new Vector4f(x0, y0, z0, 1.0f), new Vec2(0, 0), Direction.DOWN.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y0, z0, 1.0f), new Vec2(0, 1), Direction.DOWN.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x1, y0, z1, 1.0f), new Vec2(1, 1), Direction.DOWN.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      this.putVertexData(builder, new Vector4f(x0, y0, z1, 1.0f), new Vec2(1, 0), Direction.DOWN.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
      builder.setQuadOrientation(Direction.DOWN);
      modelBuilder.addGeneralQuad(builder.build());

//      for(final Direction direction : Direction.values()) {
//        final BakedQuadBuilder builder = new BakedQuadBuilder(sprite);
//
//        final float x0 = x / scale;
//        final float x1 = (x + part.size.x() * (direction.getAxis() != Direction.Axis.X ? direction.getAxisDirection().getStep() : 0)) / scale;
//        final float y0 = y / scale;
//        final float y1 = (y + part.size.y() * (direction.getAxis() != Direction.Axis.Y ? direction.getAxisDirection().getStep() : 0)) / scale;
//        final float z0 = z / scale;
//        final float z1 = (z + part.size.z() * (direction.getAxis() != Direction.Axis.Z ? direction.getAxisDirection().getStep() : 0)) / scale;
//
//        this.putVertexData(builder, new Vector4f(x0, y0, z1, 1), new Vec2(0, 0), direction.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
//        this.putVertexData(builder, new Vector4f(x1, y0, z0, 1), new Vec2(1, 0), direction.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
//        this.putVertexData(builder, new Vector4f(x1, y1, z0, 1), new Vec2(1, 1), direction.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
//        this.putVertexData(builder, new Vector4f(x0, y1, z1, 1), new Vec2(0, 1), direction.getNormal(), new Vector4f(1, 1, 1, 1), new Vec2(0, 0), sprite);
//
//        builder.setQuadOrientation(direction);
//
//        modelBuilder.addGeneralQuad(builder.build());
//      }

      for(final Part part1 : part.children.values()) {
        this.buildPart(modelBuilder, sprite, part1, x, y, z);
      }
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

      final Map<String, Part> parts = this.loadParts(tree, new HashMap<>());

      return new CsjsModel(title, parts);
    }

    private Map<String, Part> loadParts(final JsonArray jsonArray, final Map<String, Part> parts) {
      for(final JsonElement element : jsonArray) {
        final JsonObject json = element.getAsJsonObject();

        final String name = GsonHelper.getAsString(json, "name");
        final Map<String, Part> children = new HashMap<>();

        if(json.has("children")) {
          final JsonArray childrenJson = GsonHelper.getAsJsonArray(json, "children", null);
          Objects.requireNonNull(childrenJson);

          this.loadParts(childrenJson, children);
        }

        final Vector3f pos = this.parseVec3(GsonHelper.getAsJsonArray(json, "position"));
        final Vector3f offset = this.parseVec3(GsonHelper.getAsJsonArray(json, "offsetFromPivot"));
        final Vector3f size = this.parseVec3(GsonHelper.getAsJsonArray(json, "size"));
        final Vector3f rotation = this.parseVec3(GsonHelper.getAsJsonArray(json, "rotation"));
        final Vector3f uv = this.parseVec2(GsonHelper.getAsJsonArray(json, "texOffset"));

        parts.put(name, new Part(name, children, pos, offset, size, rotation, uv));
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

package com.leviathanstudio.craftstudio;

import com.google.common.base.Charsets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.leviathanstudio.craftstudio.client.model.CsjsLoader;
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
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mod(CraftStudioApi.API_ID)
public class CraftStudioApi {
    public static final String API_ID = "craftstudioapi";

    public static final Logger LOGGER = LogManager.getLogger(CraftStudioApi.class);

    private static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, API_ID);
    private static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, API_ID);
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPE_REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, API_ID);

    public static final RegistryObject<TestBlock> TEST_BLOCK = BLOCK_REGISTRY.register("test", () -> new TestBlock(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<BlockItem> TEST_ITEM = ITEM_REGISTRY.register("test", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<EntityType<CsjsEntity>> TEST_ENTITY = ENTITY_TYPE_REGISTRY.register("test", () -> EntityType.Builder.<CsjsEntity>of(CsjsEntity::new, MobCategory.AMBIENT).sized(0.6f, 1.95f).clientTrackingRange(10).build("test"));

    public CraftStudioApi() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::registerModelLoader);
        bus.addListener(this::registerEntityAttributes);

        BLOCK_REGISTRY.register(bus);
        ITEM_REGISTRY.register(bus);
        ENTITY_TYPE_REGISTRY.register(bus);
    }

    private void registerEntityAttributes(final EntityAttributeCreationEvent event) {
        event.put(TEST_ENTITY.get(), Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 1.0d).build());
    }

    private void registerModelLoader(final ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(CraftStudioApi.loc("craftstudio"), new CsjsLoader());
    }

    public static ResourceLocation loc(final String path) {
        return new ResourceLocation(API_ID, path);
    }

    public static CsjsModelTransformsMap loadModel(final ResourceLocation model) {
        final Resource res;
        try {
            res = Minecraft.getInstance().getResourceManager().getResource(model);
        } catch(final IOException e) {
            throw new RuntimeException(e);
        }

        final Reader reader = new BufferedReader(new InputStreamReader(res.getInputStream(), Charsets.UTF_8));
        final JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

        final JsonArray tree = Objects.requireNonNull(GsonHelper.getAsJsonArray(json, "tree", null));

        return new CsjsModelTransformsMap(loadParts(tree, null));
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

    private static Vector3f parseVec3(final JsonArray jsonArray) {
        return new Vector3f(jsonArray.get(0).getAsFloat(), jsonArray.get(1).getAsFloat(), jsonArray.get(2).getAsFloat());
    }

    private static Vector3f parseVec2(final JsonArray jsonArray) {
        return new Vector3f(jsonArray.get(0).getAsFloat(), jsonArray.get(1).getAsFloat(), 0.0f);
    }
}

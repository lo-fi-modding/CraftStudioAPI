package com.leviathanstudio.craftstudio;

import com.leviathanstudio.craftstudio.client.model.CsjsLoader;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(CraftStudioApi.API_ID)
public class CraftStudioApi {
    public static final String API_ID = "craftstudioapi";

    public static final Logger LOGGER = LogManager.getLogger(CraftStudioApi.class);

    private static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, API_ID);
    private static final DeferredRegister<Item> ITEM_REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, API_ID);
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPE_REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITIES, API_ID);

    public static final RegistryObject<TestBlock> TEST_BLOCK = BLOCK_REGISTRY.register("test", () -> new TestBlock(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<BlockItem> TEST_ITEM = ITEM_REGISTRY.register("test", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));
    public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY = ENTITY_TYPE_REGISTRY.register("test", () -> EntityType.Builder.<TestEntity>of(TestEntity::new, MobCategory.AMBIENT).sized(0.6f, 1.95f).clientTrackingRange(10).build("test"));

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
}

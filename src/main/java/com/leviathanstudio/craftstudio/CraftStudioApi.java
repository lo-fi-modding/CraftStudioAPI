package com.leviathanstudio.craftstudio;

import com.leviathanstudio.craftstudio.client.model.CsjsLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
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

    public static final RegistryObject<TestBlock> TEST_BLOCK = BLOCK_REGISTRY.register("test", () -> new TestBlock(BlockBehaviour.Properties.of(Material.STONE)));
    public static final RegistryObject<BlockItem> TEST_ITEM = ITEM_REGISTRY.register("test", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public CraftStudioApi() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::registerModelLoader);

        BLOCK_REGISTRY.register(bus);
        ITEM_REGISTRY.register(bus);
    }

    private void registerModelLoader(final ModelRegistryEvent event) {
        ModelLoaderRegistry.registerLoader(CraftStudioApi.loc("craftstudio"), new CsjsLoader());
    }

    public static ResourceLocation loc(final String path) {
        return new ResourceLocation(API_ID, path);
    }
}

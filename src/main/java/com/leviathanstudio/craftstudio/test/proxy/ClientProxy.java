package com.leviathanstudio.craftstudio.test.proxy;

import com.leviathanstudio.craftstudio.client.registry.CSRegistryHelper;
import com.leviathanstudio.craftstudio.client.util.EnumRenderType;
import com.leviathanstudio.craftstudio.client.util.EnumResourceType;
import com.leviathanstudio.craftstudio.common.animation.simpleImpl.CSTileEntitySpecialRenderer;
import com.leviathanstudio.craftstudio.test.client.entityRender.RenderTest;
import com.leviathanstudio.craftstudio.test.client.entityRender.RenderTest2;
import com.leviathanstudio.craftstudio.test.client.entityRender.RenderTest3;
import com.leviathanstudio.craftstudio.test.client.entityRender.RenderTest4;
import com.leviathanstudio.craftstudio.test.common.ModTest;
import com.leviathanstudio.craftstudio.test.common.RegistryHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ModTest.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy
{
    @SubscribeEvent
    public static void registerCraftStudioAssets(final FMLClientSetupEvent event) {
        CSRegistryHelper registry = new CSRegistryHelper(ModTest.MODID);
        registry.register(EnumResourceType.MODEL, EnumRenderType.BLOCK, "craftstudio_api_test2");
        registry.register(EnumResourceType.MODEL, EnumRenderType.BLOCK, "craftstudio_api_test");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "model_dead_corpse");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "dragon_brun");
        registry.register(EnumResourceType.MODEL, EnumRenderType.ENTITY, "peacock");

        registry.register(EnumResourceType.ANIM, EnumRenderType.BLOCK, "position");
        registry.register(EnumResourceType.ANIM, EnumRenderType.BLOCK, "rotation");
        registry.register(EnumResourceType.ANIM, EnumRenderType.BLOCK, "offset");
        registry.register(EnumResourceType.ANIM, EnumRenderType.BLOCK, "streching");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "fly");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "idle");
        registry.register(EnumResourceType.ANIM, EnumRenderType.ENTITY, "close_fan");
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(RegistryHandler.ENTITY_TEST_1, RenderTest.FACTORY);
        event.registerEntityRenderer(RegistryHandler.ENTITY_TEST_2, RenderTest2.FACTORY);
        event.registerEntityRenderer(RegistryHandler.ENTITY_TEST_3, RenderTest3.FACTORY);
        event.registerEntityRenderer(RegistryHandler.ENTITY_TEST_4, RenderTest4.FACTORY);

        event.registerBlockEntityRenderer(RegistryHandler.tile_test, context -> new CSTileEntitySpecialRenderer<BlockEntity>(ModTest.MODID, "craftstudio_api_test", 64, 32, new ResourceLocation(ModTest.MODID, "textures/entity/craftstudio_api_test.png")));
    }
}

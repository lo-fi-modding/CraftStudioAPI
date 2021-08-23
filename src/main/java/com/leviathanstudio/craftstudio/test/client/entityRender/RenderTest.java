package com.leviathanstudio.craftstudio.test.client.entityRender;

import com.leviathanstudio.craftstudio.client.model.ModelCraftStudio;
import com.leviathanstudio.craftstudio.test.common.ModTest;
import com.leviathanstudio.craftstudio.test.common.entity.EntityTest;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderTest<T extends EntityTest> extends LivingEntityRenderer<T, ModelCraftStudio<T>>
{
    public static final Factory<EntityTest> FACTORY = new Factory<>();

    public RenderTest(EntityRendererProvider.Context manager) {
    	super(manager, new ModelCraftStudio<T>(ModTest.MODID, "craftstudio_api_test", 64, 32), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return new ResourceLocation(ModTest.MODID, "textures/entity/craftstudio_api_test.png");
    }

    public static class Factory<T extends EntityTest> implements IRenderFactory<T>
    {
        @Override
        public EntityRenderer<? super T> createRenderFor(EntityRendererProvider.Context manager) {
            return new RenderTest<T>(manager);
        }
    }
}

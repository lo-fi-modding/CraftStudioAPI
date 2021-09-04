package com.leviathanstudio.craftstudio.test.client.entityRender;

import com.leviathanstudio.craftstudio.client.model.ModelCraftStudio;
import com.leviathanstudio.craftstudio.test.common.ModTest;
import com.leviathanstudio.craftstudio.test.common.entity.EntityTest2;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;

public class RenderTest2<T extends EntityTest2> extends LivingEntityRenderer<T, ModelCraftStudio<T>>
{
    public static final Factory<EntityTest2> FACTORY = new Factory<>();

    public RenderTest2(EntityRendererProvider.Context manager) {
        super(manager, new ModelCraftStudio<T>(ModTest.MODID, "peacock", 128, 64), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return new ResourceLocation(ModTest.MODID, "textures/entity/peacock.png");
    }

    public static class Factory<T extends EntityTest2> implements EntityRendererProvider<T>
    {
        @Override
        public EntityRenderer<T> create(EntityRendererProvider.Context manager) {
            return new RenderTest2<T>(manager);
        }
    }
}

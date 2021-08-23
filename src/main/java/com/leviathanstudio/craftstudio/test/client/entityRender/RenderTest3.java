package com.leviathanstudio.craftstudio.test.client.entityRender;

import com.leviathanstudio.craftstudio.client.model.ModelCraftStudio;
import com.leviathanstudio.craftstudio.test.common.ModTest;
import com.leviathanstudio.craftstudio.test.common.entity.EntityTest3;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderTest3<T extends EntityTest3> extends LivingEntityRenderer<T, ModelCraftStudio<T>>
{
    public static final Factory<EntityTest3> FACTORY = new Factory<>();

    public RenderTest3(EntityRendererProvider.Context manager) {
        super(manager, new ModelCraftStudio<T>(ModTest.MODID, "dragon_brun", 256), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return new ResourceLocation(ModTest.MODID, "textures/entity/dragon_brun.png");
    }

    public static class Factory<T extends EntityTest3> implements IRenderFactory<T>
    {
        @Override
        public EntityRenderer<? super T> createRenderFor(EntityRendererProvider.Context manager) {
            return new RenderTest3<T>(manager);
        }
    }
}

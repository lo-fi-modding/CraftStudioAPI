package com.leviathanstudio.craftstudio.common.animation.simpleImpl;

import com.leviathanstudio.craftstudio.client.model.ModelCraftStudio;
import com.leviathanstudio.craftstudio.client.util.MathHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Renderer of animated TileEntity. If you only need one model to be render you
 * can directly use this class. Otherwise, or if you prefer, you can use this
 * class as a model to create your renderer.
 *
 * @since 0.3.0
 *
 * @author Timmypote
 *
 * @param <T>
 */
public class CSTileEntitySpecialRenderer<T extends BlockEntity> implements BlockEntityRenderer<T>
{
    /** Efficient rotation corrector, you can use it in your renderer. */
    public static final Quaternion ROTATION_CORRECTOR = MathHelper.quatFromEuler(180, 0, 0);

    /** The model of the block. */
    protected ModelCraftStudio model;
    /** The texture of the block */
    protected ResourceLocation texture;

    /** The constructor that initialize the model and save texture. */
    public CSTileEntitySpecialRenderer(String modid, String modelNameIn, int textureWidth, int textureHeigth, ResourceLocation texture) {
        this.model = new ModelCraftStudio(modid, modelNameIn, textureWidth, textureHeigth);
        this.texture = texture;
    }

    @Override
    public void render(final T te, final float partialTicks, final PoseStack poseStack, final MultiBufferSource bufferSource, final int combinedLight, final int combinedOverlay) {
        poseStack.pushPose();
        // Correction of the position.
        poseStack.translate(0.5D, 1.5D, 0.5D);
        // Correction of the rotation.
        poseStack.mulPose(CSTileEntitySpecialRenderer.ROTATION_CORRECTOR);
        RenderSystem.setShaderTexture(0, this.texture); // Binding the texture.
        this.model.render(te); // Rendering the model.
        poseStack.popPose();
    }
}

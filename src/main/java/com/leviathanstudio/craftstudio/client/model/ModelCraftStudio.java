package com.leviathanstudio.craftstudio.client.model;

import com.leviathanstudio.craftstudio.client.animation.ClientAnimationHandler;
import com.leviathanstudio.craftstudio.client.exception.CSResourceNotRegisteredException;
import com.leviathanstudio.craftstudio.client.json.CSReadedModel;
import com.leviathanstudio.craftstudio.client.json.CSReadedModelBlock;
import com.leviathanstudio.craftstudio.client.registry.RegistryHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * Model to represent a CraftStudio model in Minecraft.
 *
 * @author Timmypote
 * @author ZeAmateis
 * @since 0.3.0
 */
@OnlyIn(Dist.CLIENT)
public class ModelCraftStudio<T extends Entity> extends EntityModel<T> {

    private List<CSModelRenderer> parentBlocks = new ArrayList<>();

    private T entity;

    /**
     * @param modid       The ID of your mod
     * @param modelNameIn The name of your craftstudio model your have registered with
     *                    {@link com.leviathanstudio.craftstudio.client.registry.CSRegistryHelper#register
     *                    CraftStudioRegistry#register}
     * @param textureSize The size of your texture if it's the same width/height
     */
    public ModelCraftStudio(String modid, String modelNameIn, int textureSize) {
        this(modid, modelNameIn, textureSize, textureSize);
    }

    /**
     * @param modid         The ID of your mod
     * @param modelNameIn   The name of your craftstudio model your have registered with
     *                      {@link com.leviathanstudio.craftstudio.client.registry.CSRegistryHelper#register
     *                      CraftStudioRegistry#register}
     * @param textureWidth  The width texture of your model
     * @param textureHeight The height texture of your model
     */
    public ModelCraftStudio(String modid, String modelNameIn, int textureWidth, int textureHeight) {
        this(new ResourceLocation(modid, modelNameIn), textureWidth, textureHeight);
    }

    private ModelCraftStudio(ResourceLocation modelIn, int textureWidth, int textureHeight) {

        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;

        CSReadedModel rModel = RegistryHandler.modelRegistry
        		.get(modelIn);

        if(rModel == null) {
            throw new CSResourceNotRegisteredException(modelIn.toString());
        }

        CSModelRenderer modelRend;

        for (CSReadedModelBlock rBlock : rModel.getParents()) {
            modelRend = this.generateCSModelRend(rBlock);
            this.parentBlocks.add(modelRend);
            this.generateChild(rBlock, modelRend);
        }
    }

    /**
     * Generate childs part of a model
     */
    private void generateChild(CSReadedModelBlock rParent, CSModelRenderer parent) {
        CSModelRenderer modelRend;
        for (CSReadedModelBlock rBlock : rParent.getChilds()) {
            modelRend = this.generateCSModelRend(rBlock);
            parent.addChild(modelRend);
            this.generateChild(rBlock, modelRend);
        }
    }

    /**
     * Generate CSModelRenderer from read model block
     */
    private CSModelRenderer generateCSModelRend(CSReadedModelBlock rBlock) {
        CSModelRenderer modelRend = new CSModelRenderer(this, rBlock.getName(), rBlock.getTexOffset()[0], rBlock.getTexOffset()[1]);
        if (rBlock.getVertex() != null) {
          ModelPart.Vertex vertices[] = new ModelPart.Vertex[8];
            for (int i = 0; i < 8; i++)
                vertices[i] = new ModelPart.Vertex(rBlock.getVertex()[i][0], rBlock.getVertex()[i][1], rBlock.getVertex()[i][2], 0.0F, 0.0F);
            modelRend.addBox(vertices, CSModelBox.getTextureUVsForRect(rBlock.getTexOffset()[0], rBlock.getTexOffset()[1], rBlock.getSize().x(),
                    rBlock.getSize().y(), rBlock.getSize().z()));
        } else
            modelRend.addBox(-rBlock.getSize().x() / 2, -rBlock.getSize().y() / 2, -rBlock.getSize().z() / 2, rBlock.getSize().x(), rBlock.getSize().y(),
                    rBlock.getSize().z());
        modelRend.setDefaultRotationPoint(rBlock.getRotationPoint().x(), rBlock.getRotationPoint().y(), rBlock.getRotationPoint().z());
        modelRend.setInitialRotationMatrix(rBlock.getRotation().x(), rBlock.getRotation().y(), rBlock.getRotation().z());
        modelRend.setDefaultOffset(rBlock.getOffset().x(), rBlock.getOffset().y(), rBlock.getOffset().z());
        modelRend.setDefaultStretch(rBlock.getStretch().x(), rBlock.getStretch().y(), rBlock.getStretch().z());
        modelRend.setTextureSize(this.textureWidth, this.textureHeight);
        return modelRend;
    }

    /**
     * Render function for an animated block<br>
     * Must be called in a
     * {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderer#render
     * renderTileEntityAt} method
     *
     * @param tileEntityIn The TileEntity who implements {@link IAnimated}
     */
    public void render(BlockEntity tileEntityIn) {
        float modelScale = 0.0625F;
        ClientAnimationHandler.performAnimationInModel(this.parentBlocks, (IAnimated) tileEntityIn);
        for (int i = 0; i < this.parentBlocks.size(); i++)
            this.parentBlocks.get(i).render(modelScale);
    }

    /**
     * Render function for a non-animated block<br>
     * Must be called in a
     * {@link net.minecraft.client.renderer.blockentity.BlockEntityRenderer#render
     * renderTileEntityAt} method
     */
    public void render() {
        float modelScale = 0.0625F;
        for (int i = 0; i < this.parentBlocks.size(); i++)
            this.parentBlocks.get(i).render(modelScale);
    }

    @Override
    public void prepareMobModel(final T entity, final float p_102615_, final float p_102616_, final float p_102617_) {
        super.prepareMobModel(entity, p_102615_, p_102616_, p_102617_);
        this.entity = entity;
    }

    @Override
    public void setupAnim(final T t, final float v, final float v1, final float v2, final float v3, final float v4) {

    }

    /**
     * Render methods for an Entity
     */
    @Override
    public void renderToBuffer(final PoseStack poseStack, final VertexConsumer buffer, final int lightmap, final int overlay, final float r, final float g, final float b, final float scale) {
        ClientAnimationHandler.performAnimationInModel(this.parentBlocks, (IAnimated) this.entity);
        for(CSModelRenderer parentBlock : this.parentBlocks) {
            parentBlock.render(scale);
        }
    }

  /**
     * Getter
     */
    public List<CSModelRenderer> getParentBlocks() {
        return this.parentBlocks;
    }
}

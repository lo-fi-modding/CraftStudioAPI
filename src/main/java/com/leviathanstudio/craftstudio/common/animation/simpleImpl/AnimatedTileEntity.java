package com.leviathanstudio.craftstudio.common.animation.simpleImpl;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * An abstract class that represent a animated TileEntity. You should extends it
 * and not create your own, or be careful to implement all the methods of this
 * class.
 *
 * @author Timmypote
 * @since 0.3.0
 */
public abstract class AnimatedTileEntity extends BlockEntity implements IAnimated, ITickableTileEntity {
    /**
     * The animation handler of this type of tile entity.
     */
    // It should be different for every entity class, unless child classes have
    // the same models.
    // You should declare a new one in your extended classes.
    protected static AnimationHandler animHandler = CraftStudioApi.getNewAnimationHandler(AnimatedTileEntity.class);

    // Here you should add all the needed animations in the animationHandler.
    static {
        //AnimatedTileEntity.animHandler.addAnim("yourModId", "yourAnimation", "yourModel", false);
    }

    public AnimatedTileEntity(BlockEntityType<?> tileEntityTypeIn, final BlockPos pos, final BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    /**
     * The constructor of the tile entity.
     */


    // You must call super.update() at the beginning of your update() method,
    // or call the animationsUpdate() method like here.
    @Override
    public void tick() {
        this.getAnimationHandler().animationsUpdate(this);
    }

    @Override
    public <T extends IAnimated> AnimationHandler<T> getAnimationHandler() {
        // Be careful to return the right animation handler.
        return AnimatedTileEntity.animHandler;
    }

    @Override
    public ResourceKey<Level> getDimension() {
        return this.level.dimension();
    }

    @Override
    public double getX() {
        return this.getBlockPos().getX();
    }

    @Override
    public double getY() {
        return this.getBlockPos().getY();
    }

    @Override
    public double getZ() {
        return this.getBlockPos().getZ();
    }

    @Override
    public boolean isWorldRemote() {
        return this.level.isClientSide();
    }

    // Here to prevent bugs on the integrated server.
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.getBlockPos().getX();
        result = prime * result + this.getBlockPos().getY();
        result = prime * result + this.getBlockPos().getZ();
        return result;
    }

    // Here to prevent bugs on the integrated server.
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;
        AnimatedTileEntity other = (AnimatedTileEntity) obj;
        if (this.getBlockPos().getX() != other.getBlockPos().getX())
            return false;
        if (this.getBlockPos().getY() != other.getBlockPos().getY())
            return false;
        if (this.getBlockPos().getZ() != other.getBlockPos().getZ())
            return false;
        return true;
    }

}

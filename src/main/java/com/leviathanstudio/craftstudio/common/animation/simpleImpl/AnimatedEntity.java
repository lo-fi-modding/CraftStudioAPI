package com.leviathanstudio.craftstudio.common.animation.simpleImpl;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;

/**
 * An abstract class that represent an animated entity. You can extends it or
 * use it as a model to create your own animated entity.
 *
 * @since 0.3.0
 *
 * @author Timmypote
 */
public abstract class AnimatedEntity extends Mob implements IAnimated
{
    /** The animation handler of this type of entity. */
    // It should be different for every entity class, unless child classes have
    // the same models.
    // You should declare a new one in your extended classes.
    protected static AnimationHandler animHandler = CraftStudioApi.getNewAnimationHandler(AnimatedEntity.class);

    // Here you should add all the needed animations in the animationHandler.
    static {
        //AnimatedEntity.animHandler.addAnim("yourModId", "yourAnimation", "yourModel", false);
    }

    /** The constructor of the entity. */
    public AnimatedEntity(final EntityType<? extends Mob> type, Level worldIn) {
        super(type, worldIn);
    }

    // You must call super.onLivingUpdate(), in your entity, or call the
    // aiStep() method like here.
    @Override
    public void aiStep() {
        super.aiStep();
        this.getAnimationHandler().animationsUpdate(this);
    }

    @Override
    public <T extends IAnimated> AnimationHandler<T> getAnimationHandler() {
        // Be careful to return the right animation handler.
        return AnimatedEntity.animHandler;
    }

    @Override
    public ResourceKey<Level> getDimension() {
        return this.getDimension();
    }

    @Override
    public boolean isWorldRemote() {
        return this.level.isClientSide;
    }
}

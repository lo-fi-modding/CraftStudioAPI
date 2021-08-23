package com.leviathanstudio.craftstudio.test.common.entity;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import com.leviathanstudio.craftstudio.test.common.ModTest;
import com.leviathanstudio.craftstudio.test.pack.animation.AnimationLootAt;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EntityTest2 extends Animal implements IAnimated
{
    protected static AnimationHandler<EntityTest2> animHandler = CraftStudioApi.getNewAnimationHandler(EntityTest2.class);
    protected boolean                 fanOpen     = true;

    static {
        EntityTest2.animHandler.addAnim(ModTest.MODID, "close_fan", "peacock", false);
        EntityTest2.animHandler.addAnim(ModTest.MODID, "open_fan", "close_fan");
        EntityTest2.animHandler.addAnim(ModTest.MODID, "lookat", new AnimationLootAt("Head"));
    }

    public EntityTest2(EntityType<? extends Animal> type, Level par1World) {
        super(type, par1World);
        this.stepHeight = 1.5F;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new LookAtGoal(this, Player.class, 10));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @SuppressWarnings("unchecked")
	@Override
    public AnimationHandler<EntityTest2> getAnimationHandler() {
        return EntityTest2.animHandler;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.getAnimationHandler().isAnimationActive(ModTest.MODID, "close_fan", this)
                && !this.getAnimationHandler().isAnimationActive(ModTest.MODID, "open_fan", this))
            if (this.fanOpen) {
                this.getAnimationHandler().networkStopStartAnimation(ModTest.MODID, "open_fan", "close_fan", this);
                this.fanOpen = false;
            }
            else {
                this.getAnimationHandler().networkStopStartAnimation(ModTest.MODID, "close_fan", "open_fan", this);
                this.fanOpen = true;
            }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        this.getAnimationHandler().animationsUpdate(this);

        if (this.isWorldRemote() && !this.getAnimationHandler().isAnimationActive(ModTest.MODID, "lookat", this))
            this.getAnimationHandler().startAnimation(ModTest.MODID, "lookat", this);
    }

    @Override
    public AgeableMob createChild(AgeableMob ageable) {
        return null;
    }

    @Override
    public ResourceKey<Level> getDimension() {
        return this.level.dimension();
    }

    @Override
    public boolean isWorldRemote() {
        return this.level.isClientSide();
    }
}

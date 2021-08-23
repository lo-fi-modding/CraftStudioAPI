package com.leviathanstudio.craftstudio.test.common.entity;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.simpleImpl.AnimatedEntity;
import com.leviathanstudio.craftstudio.test.common.ModTest;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EntityTest3 extends AnimatedEntity
{
    protected static AnimationHandler<EntityTest3> animHandler = CraftStudioApi.getNewAnimationHandler(EntityTest3.class);
    boolean                           fly         = false;

    static {
        EntityTest3.animHandler.addAnim(ModTest.MODID, "fly", "dragon_brun", true);
        EntityTest3.animHandler.addAnim(ModTest.MODID, "idle", "dragon_brun", true);
    }

    public EntityTest3(EntityType<? extends AnimatedEntity> type, Level par1World) {
        super(type, par1World);
    }

    @SuppressWarnings("unchecked")
	@Override
    public AnimationHandler<EntityTest3> getAnimationHandler() {
        return EntityTest3.animHandler;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.fly)
            this.fly = true;
        return InteractionResult.SUCCESS;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.getAnimationHandler().isAnimationActive(ModTest.MODID, "fly", this) && this.fly)
            this.getAnimationHandler().networkStartAnimation(ModTest.MODID, "fly", this);

    }
}

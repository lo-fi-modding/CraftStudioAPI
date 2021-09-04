package com.leviathanstudio.craftstudio.test.common.entity;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import com.leviathanstudio.craftstudio.test.common.ModTest;
import com.leviathanstudio.craftstudio.test.common.RegistryHandler;
import com.leviathanstudio.craftstudio.test.pack.animation.AnimationLootAt;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModTest.MODID)
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
        this.maxUpStep = 1.5F;
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 10));
    }

    @SubscribeEvent
    public static void registerAttributes(final EntityAttributeCreationEvent event) {
        event.put(RegistryHandler.ENTITY_TEST_2, Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.25D).build());
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
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageable) {
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

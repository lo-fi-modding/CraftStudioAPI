package com.leviathanstudio.craftstudio;

import com.leviathanstudio.craftstudio.client.CsjsClientAnimationState;
import com.leviathanstudio.craftstudio.server.CsjsServerAnimationState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class CsjsEntity extends PathfinderMob {
  public static final EntityDataAccessor<String> DATA_ANIMATION = SynchedEntityData.defineId(CsjsEntity.class, EntityDataSerializers.STRING);

  private final CsjsAnimationState animationState;

  public CsjsEntity(final Level level, final ResourceLocation model) {
    this(CraftStudioApi.TEST_ENTITY.get(), level, model);
  }

  public CsjsEntity(final EntityType<? extends PathfinderMob> type, final Level level, final ResourceLocation model) {
    super(type, level);

    if(level.isClientSide()) {
      this.animationState = new CsjsClientAnimationState(this, model);
    } else {
      this.animationState = new CsjsServerAnimationState(this);
    }
  }

  @Override
  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_ANIMATION, "");
  }

  public CsjsAnimationState getAnimationState() {
    return this.animationState;
  }

  @Override
  public Packet<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }

  @Override
  public void addAdditionalSaveData(final CompoundTag tag) {
    super.addAdditionalSaveData(tag);

    final ResourceLocation anim = this.getAnimationState().getCurrentAnimation();

    if(anim != null) {
      tag.putString("CraftStudioAnimation", anim.toString());
    }
  }

  @Override
  public void readAdditionalSaveData(final CompoundTag tag) {
    super.readAdditionalSaveData(tag);

    if(tag.contains("CraftStudioAnimation")) {
      this.getAnimationState().startAnimation(new ResourceLocation(tag.getString("CraftStudioAnimation")));
    } else {
      this.getAnimationState().stopAnimation();
    }
  }
}

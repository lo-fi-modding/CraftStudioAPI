package com.leviathanstudio.craftstudio;

import com.leviathanstudio.craftstudio.client.model.ClientBootstrap;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.Collections;

public class CsjsTestEntity extends LivingEntity implements CsjsAnimatedEntity {
  private final CsjsAnimationState animationState = new CsjsAnimationState(ClientBootstrap.MODEL);

  public CsjsTestEntity(final Level level) {
    this(CraftStudioApi.TEST_ENTITY.get(), level);
  }

  public CsjsTestEntity(final EntityType<? extends LivingEntity> pEntityType, final Level pLevel) {
    super(pEntityType, pLevel);
    this.animationState.startAnimation(CraftStudioApi.loadAnimation(CraftStudioApi.loc("animations/villager_hammer.csjsmodelanim")));
  }

  @Override
  public CsjsAnimationState getAnimationState() {
    return this.animationState;
  }

  @Override
  public Iterable<ItemStack> getArmorSlots() {
    return Collections.emptyList();
  }

  @Override
  public ItemStack getItemBySlot(final EquipmentSlot pSlot) {
    return ItemStack.EMPTY;
  }

  @Override
  public void setItemSlot(final EquipmentSlot pSlot, final ItemStack pStack) {

  }

  @Override
  public HumanoidArm getMainArm() {
    return HumanoidArm.RIGHT;
  }

  @Override
  public Packet<?> getAddEntityPacket() {
    return NetworkHooks.getEntitySpawningPacket(this);
  }
}

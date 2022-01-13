package com.leviathanstudio.craftstudio;

import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import java.util.Collections;

public class TestEntity extends LivingEntity {
  public TestEntity(final Level level) {
    this(CraftStudioApi.TEST_ENTITY.get(), level);
  }

  public TestEntity(final EntityType<? extends LivingEntity> pEntityType, final Level pLevel) {
    super(pEntityType, pLevel);
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

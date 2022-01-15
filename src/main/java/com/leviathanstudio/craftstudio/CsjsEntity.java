package com.leviathanstudio.craftstudio;

import com.leviathanstudio.craftstudio.client.model.ClientBootstrap;
import com.leviathanstudio.craftstudio.client.model.CsjsModelTransformsMap;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.Collections;

public class CsjsEntity extends LivingEntity {
  private CsjsModelTransformsMap transforms;

  public CsjsEntity(final Level level) {
    this(CraftStudioApi.TEST_ENTITY.get(), level);
  }

  public CsjsEntity(final EntityType<? extends LivingEntity> pEntityType, final Level pLevel) {
    super(pEntityType, pLevel);
    this.transforms = new CsjsModelTransformsMap(ClientBootstrap.ANIMATION_DATA.get(ClientBootstrap.MODEL));
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

  public CsjsModelTransformsMap getCsjsTransforms() {
    return this.transforms;
  }
}

package com.leviathanstudio.craftstudio;

import com.leviathanstudio.craftstudio.client.model.ClientBootstrap;
import com.leviathanstudio.craftstudio.client.model.CsjsAnimation;
import com.leviathanstudio.craftstudio.client.model.CsjsModelTransformsMap;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CsjsEntity extends LivingEntity {
  private CsjsModelTransformsMap transforms;

  @Nullable
  public CsjsModelTransformsMap originalTransforms;
  @Nullable
  private CsjsAnimation animation;
  public int animationTicks;
  public final Map<String, KeyframeIndices> partNextKeyframes = new HashMap<>();
  public final Map<String, KeyframeIndices> partCurrentKeyframes = new HashMap<>();

  public CsjsEntity(final Level level) {
    this(CraftStudioApi.TEST_ENTITY.get(), level);
  }

  public CsjsEntity(final EntityType<? extends LivingEntity> pEntityType, final Level pLevel) {
    super(pEntityType, pLevel);
    this.transforms = new CsjsModelTransformsMap(ClientBootstrap.ANIMATION_DATA.get(ClientBootstrap.MODEL));
    this.startAnimation(CraftStudioApi.loadAnimation(CraftStudioApi.loc("animations/villager_hammer.csjsmodelanim")));
  }

  public void startAnimation(final CsjsAnimation animation) {
    this.originalTransforms = new CsjsModelTransformsMap(this.transforms);
    this.animation = animation;
    this.animationTicks = 0;
    this.partNextKeyframes.clear();
    this.partCurrentKeyframes.clear();

    for(final String partName : animation.parts().keySet()) {
      this.partNextKeyframes.put(partName, new KeyframeIndices());
      this.partCurrentKeyframes.put(partName, new KeyframeIndices());
    }
  }

  public void stopAnimation() {
    this.transforms = this.originalTransforms;
    this.originalTransforms = null;
    this.animation = null;
  }

  @Nullable
  public CsjsAnimation getAnimation() {
    return this.animation;
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

  public static class KeyframeIndices {
    public int pos;
    public int offset;
    public int size;
    public int rotation;
  }
}

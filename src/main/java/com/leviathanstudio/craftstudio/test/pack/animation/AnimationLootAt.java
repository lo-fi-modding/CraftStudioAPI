package com.leviathanstudio.craftstudio.test.pack.animation;

import com.leviathanstudio.craftstudio.client.model.CSModelRenderer;
import com.leviathanstudio.craftstudio.client.util.MathHelper;
import com.leviathanstudio.craftstudio.common.animation.CustomChannel;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import com.mojang.math.Quaternion;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AnimationLootAt extends CustomChannel {
	private String headPart;

	public AnimationLootAt(String headPartIn) {
		super("lookat");
		this.headPart = headPartIn;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void update(CSModelRenderer parts, IAnimated animated) {
		if (animated instanceof LivingEntity)
			if (parts.boxName.equals(this.headPart)) {
				LivingEntity entityL = (LivingEntity) animated;
				float diff = entityL.getYHeadRot() - entityL.yBodyRot;
				Quaternion quat = MathHelper.quatFromEuler(entityL.getXRot(), 0.0F, diff);
				Quaternion quat2 = new Quaternion(parts.getDefaultRotationAsQuaternion());
				quat.mul(quat2);
				parts.getRotationMatrix().set(quat);
				parts.getRotationMatrix().transpose();
			}
	}

}

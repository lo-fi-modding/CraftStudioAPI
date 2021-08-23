package com.leviathanstudio.craftstudio.client.util;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Class containing useful math methods for the api.
 *
 * @author Timmypote
 * @since 0.3.0
 */
@OnlyIn(Dist.CLIENT)
public class MathHelper {
    /**
     * Create a new Quat4f representing the yaw, pitch and roll given(applied in
     * that order).
     *
     * @param rot The pitch, yaw and roll as a Vector3f(x=pitch, y=yaw, z=roll).
     * @return The new Quat4f.
     */
    public static Quaternion quatFromEuler(Vector3f rot) {
        return quatFromEuler(rot.x(), rot.y(), rot.z());
    }

    /**
     @@ -76,25 +36,27 @@ public static Quat4f quatFromEuler(Vector3f rot) {
      *            The roll.
      * @return The new Quat4f.
     */
    public static Quaternion quatFromEuler(float pitch, float yaw, float roll) {
        pitch = (float) Math.toRadians(pitch);
        yaw = (float) Math.toRadians(yaw);
        roll = (float) Math.toRadians(roll);

        final Vector3f coss = new Vector3f(
          (float) Math.cos(pitch * 0.5F),
          (float) Math.cos(yaw * 0.5F),
          (float) Math.cos(roll * 0.5F)
        );
        final Vector3f sins = new Vector3f(
          (float) Math.sin(pitch * 0.5F),
          (float) Math.sin(yaw * 0.5F),
          (float) Math.sin(roll * 0.5F)
        );

        return new Quaternion(
          sins.x() * coss.y() * coss.z() + coss.x() * sins.y() * sins.z(),
          coss.x() * sins.y() * coss.z() - sins.x() * coss.y() * sins.z(),
          coss.x() * coss.y() * sins.z() - sins.x() * sins.y() * coss.z(),
          coss.x() * coss.y() * coss.z() + sins.x() * sins.y() * sins.z()
        );
    }
}

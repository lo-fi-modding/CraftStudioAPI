package com.leviathanstudio.craftstudio.common.animation;

import com.leviathanstudio.craftstudio.client.model.CSModelRenderer;

/**
 * Class used to perform custom animations. For example, using in game data.
 *
 * @since 0.3.0
 *
 * @author Timmypote
 */
public abstract class CustomChannel extends InfoChannel
{

    public CustomChannel(String channelName) {
        super(channelName);
    }

    /**
     * Write the actual behavior of this custom animation. It will be called
     * every tick while the animation is active. You should modifies the
     * rotation, translation, offset or/and scale of the part.
     *
     * @param part
     *            The part that is animated.
     * @param animated
     *            The animated object.
     */
    public abstract void update(CSModelRenderer part, IAnimated animated);
}

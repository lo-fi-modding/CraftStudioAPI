package com.leviathanstudio.craftstudio.common.network;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.client.animation.ClientAnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import com.leviathanstudio.craftstudio.common.animation.InfoChannel;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Message to send an IAnimated event to the client.
 *
 * @since 0.3.0
 *
 * @author Timmypote
 */
public class ClientIAnimatedEventMessage extends IAnimatedEventMessage
{
    /** Constructor */
    public ClientIAnimatedEventMessage() {}

    /** Constructor */
    public ClientIAnimatedEventMessage(EnumIAnimatedEvent event, IAnimated animated, short animId) {
        super(event, animated, animId);
    }

    /** Constructor */
    public ClientIAnimatedEventMessage(EnumIAnimatedEvent event, IAnimated animated, short animId, float keyframeInfo) {
        super(event, animated, animId, keyframeInfo);
    }

    /** Constructor */
    public ClientIAnimatedEventMessage(EnumIAnimatedEvent event, IAnimated animated, short animId, float keyframeInfo, short optAnimId) {
        super(event, animated, animId, keyframeInfo, optAnimId);
    }

    /** Constructor */
    public ClientIAnimatedEventMessage(IAnimatedEventMessage eventObj) {
        super(eventObj);
    }

    public static ClientIAnimatedEventMessage fromBytes(final FriendlyByteBuf buf) {
        return IAnimatedEventMessage.fromBytes(new ClientIAnimatedEventMessage(), buf);
    }

    /**
     * Handler for IAnimated event messages send to the client.
     *
     * @since 0.3.0
     *
     * @author Timmypote
     */
    public static class ClientIAnimatedEventHandler extends IAnimatedEventHandler
    {
        @Override
        public boolean onMessage(IAnimatedEventMessage message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                if (super.onMessage(message, ctx)) {
                    boolean success = message.animated.getAnimationHandler().onClientIAnimatedEvent(message);
                    if (success && message.animated.getAnimationHandler() instanceof ClientAnimationHandler
                            && (message.event == EnumIAnimatedEvent.START_ANIM.getId() || message.event == EnumIAnimatedEvent.STOP_START_ANIM.getId())) {
                        ClientAnimationHandler hand = (ClientAnimationHandler) message.animated.getAnimationHandler();
                        String animName = hand.getAnimNameFromId(message.animId);
                        InfoChannel infoC = (InfoChannel) hand.getAnimChannels().get(animName);
                        CraftStudioApi.NETWORK.sendToServer(new ServerIAnimatedEventMessage(EnumIAnimatedEvent.ANSWER_START_ANIM, message.animated, message.animId, infoC.totalFrames));
                    }
                }
            });

            return true;
        }

        @Override
        public Entity getEntityByUUID(Supplier<NetworkEvent.Context> ctx, long most, long least) {
            UUID uuid = new UUID(most, least);
            for (Entity e : Minecraft.getInstance().level.loadedEntityList)
                if (e.getPersistentID().equals(uuid))
                    return e;
            return null;
        }

        @Override
        public BlockEntity getTileEntityByPos(Supplier<NetworkEvent.Context> ctx, int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            return Minecraft.getInstance().level.getBlockEntity(pos);
        }
    }
}

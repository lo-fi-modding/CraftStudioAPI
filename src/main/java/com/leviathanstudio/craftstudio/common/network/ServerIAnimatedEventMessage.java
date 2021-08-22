package com.leviathanstudio.craftstudio.common.network;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Message to send an IAnimated event to the server.
 *
 * @since 0.3.0
 *
 * @author Timmypote
 */
public class ServerIAnimatedEventMessage extends IAnimatedEventMessage
{
    /** Constructor */
    public ServerIAnimatedEventMessage() {}

    /** Constructor */
    public ServerIAnimatedEventMessage(EnumIAnimatedEvent event, IAnimated animated, short animId) {
        super(event, animated, animId);
    }

    /** Constructor */
    public ServerIAnimatedEventMessage(EnumIAnimatedEvent event, IAnimated animated, short animId, float keyframeInfo) {
        super(event, animated, animId, keyframeInfo);
    }

    /** Constructor */
    public ServerIAnimatedEventMessage(EnumIAnimatedEvent event, IAnimated animated, short animId, float keyframeInfo, short optAnimId) {
        super(event, animated, animId, keyframeInfo, optAnimId);
    }

    /** Constructor */
    public ServerIAnimatedEventMessage(IAnimatedEventMessage eventObj) {
        super(eventObj);
    }

  public static ServerIAnimatedEventMessage fromBytes(final FriendlyByteBuf buf) {
    return IAnimatedEventMessage.fromBytes(new ServerIAnimatedEventMessage(), buf);
  }

    /**
     * Handler for IAnimated event messages send to the server.
     *
     * @since 0.3.0
     *
     * @author Timmypote
     */
    public static class ServerIAnimatedEventHandler extends IAnimatedEventHandler
    {
        @Override
        public boolean onMessage(IAnimatedEventMessage message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                if (super.onMessage(message, ctx)) {
                    ServerPlayer player = ctx.get().getSender();
                    message.animated.getAnimationHandler();
                    boolean success = AnimationHandler.onServerIAnimatedEvent(message);
                    if (success && message.event != EnumIAnimatedEvent.ANSWER_START_ANIM.getId())
                        CraftStudioApi.NETWORK.sendTo(new ClientIAnimatedEventMessage(message), player);
                }
            });

            return true;
        }

        @Override
        public Entity getEntityByUUID(Supplier<NetworkEvent.Context> ctx, long most, long least) {
            UUID uuid = new UUID(most, least);
            for (Entity e : ctx.get().getSender().level.loadedEntityList)
                if (e.getPersistentID().equals(uuid))
                    return e;
            return null;
        }

        @Override
        public BlockEntity getTileEntityByPos(Supplier<NetworkEvent.Context> ctx, int x, int y, int z) {
            BlockPos pos = new BlockPos(x, y, z);
            return ctx.get().getSender().level.getBlockEntity(pos);
        }
    }
}

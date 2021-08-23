package com.leviathanstudio.craftstudio.common.network;

import com.google.common.base.Predicates;
import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.client.animation.ClientAnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import com.leviathanstudio.craftstudio.common.animation.InfoChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AnimatedEventMessageUtil {

    private static final CommonHandler SERVER = new ServerHandler();
    private static final CommonHandler CLIENT = new ClientHandler();

	public static final BiConsumer<AnimatedEventMessage, FriendlyByteBuf> ENCODER = (msg, buffer) -> {
    	if (msg.event < 0 || msg.event >= EnumIAnimatedEvent.ID_COUNT) {
            buffer.writeShort(-1);
            CraftStudioApi.getLogger().error("Unsuported event id " + msg.event + " for network message.");
            return;
        }
        if (msg.animated instanceof Entity) {
            Entity e = (Entity) msg.animated;
            buffer.writeShort(msg.event);
            UUID uuid = e.getUUID();
            buffer.writeLong(uuid.getMostSignificantBits());
            buffer.writeLong(uuid.getLeastSignificantBits());
        } else if (msg.animated instanceof BlockEntity) {
					BlockEntity te = (BlockEntity) msg.animated;
            buffer.writeShort(msg.event + EnumIAnimatedEvent.ID_COUNT);
            BlockPos pos = te.getBlockPos();
            buffer.writeInt(pos.getX());
            buffer.writeInt(pos.getY());
            buffer.writeInt(pos.getZ());
        } else {
            buffer.writeShort(-1);
            CraftStudioApi.getLogger().error("Unsupported class " + msg.animated.getClass().getSimpleName() + " for network message.");
            CraftStudioApi.getLogger().error("You are trying to animate an other class than Entity or TileEntity.");
            return;
        }
        buffer.writeShort(msg.animId);
        if (msg.event != EnumIAnimatedEvent.STOP_ANIM.getId())
            buffer.writeFloat(msg.keyframeInfo);
        if (msg.event == EnumIAnimatedEvent.STOP_START_ANIM.getId())
            buffer.writeShort(msg.optAnimId);
    };

    public static final Function<FriendlyByteBuf, AnimatedEventMessage> DECODER = (buffer) -> {
    	AnimatedEventMessage msg = new AnimatedEventMessage();

    	short actualEvent = buffer.readShort();
        if (actualEvent < 0 || actualEvent >= 2 * EnumIAnimatedEvent.ID_COUNT) {
        	msg.event = -1;
            CraftStudioApi.getLogger().error("Networking error : invalid packet.");
            return null;
        } else if (actualEvent < EnumIAnimatedEvent.ID_COUNT) {
        	msg.most = buffer.readLong();
        	msg.least = buffer.readLong();
        	msg.event = actualEvent;
        	msg.hasEntity = true;
        } else {
        	msg.x = buffer.readInt();
        	msg.y = buffer.readInt();
        	msg.z = buffer.readInt();
            msg.event = (short) (actualEvent - EnumIAnimatedEvent.ID_COUNT);
            msg.hasEntity = false;
        }
        msg.animId = buffer.readShort();
        if (msg.event != 2)
        	msg.keyframeInfo = buffer.readFloat();
        if (msg.event > 2)
        	msg.optAnimId = buffer.readShort();
    	return msg;
    };


    public static BiConsumer<AnimatedEventMessage, Supplier<NetworkEvent.Context>> HANDLER = (msg, ctx) -> {
    	switch(ctx.get().getDirection()) {
    	case PLAY_TO_SERVER:
    		SERVER.handle(msg, ctx.get());
    		break;
    	case PLAY_TO_CLIENT:
    		CLIENT.handle(msg, ctx.get());
    		break;
    	default:
    		// Do nothing
    		break;
    	}
        ctx.get().setPacketHandled(true);
    };



    private static class ServerHandler extends CommonHandler {

		@Override
		public Entity getEntityByUUID(NetworkEvent.Context ctx, long most, long least) {
			ServerPlayer sender = ctx.getSender(); // the client that sent this packet
	        ServerLevel world = sender.getLevel();
	        UUID uuid = new UUID(most, least);
	        for (Entity e : world.getEntities(null, Predicates.alwaysTrue()))
	        	if (e.getUUID().equals(uuid))
	        		return e;

	        return null;
		}

		@Override
		public BlockEntity getTileEntityByPos(NetworkEvent.Context ctx, int x, int y, int z) {
	        return ctx.getSender().level.getBlockEntity(new BlockPos(x, y, z));
		}

		@Override
		public void handle(AnimatedEventMessage message, NetworkEvent.Context ctx) {
			ctx.enqueueWork(() -> {
	    		if (handleCommonSide(message, ctx)) {
	                message.animated.getAnimationHandler();
	                boolean succes = AnimationHandler.onServerIAnimatedEvent(message);
	                if (succes && message.event != EnumIAnimatedEvent.ANSWER_START_ANIM.getId())
	                	CSNetworkHelper.sendPacketTo(new AnimatedEventMessage(message), ctx.getSender());
	            }
	    	});
		}

    }

    private static class ClientHandler extends CommonHandler {

    	@Override
		public Entity getEntityByUUID(NetworkEvent.Context ctx, long most, long least) {
			ServerPlayer sender = ctx.getSender(); // the client that sent this packet
			ServerLevel world = sender.getLevel();
	        UUID uuid = new UUID(most, least);

	        for (Entity e : world.getAllEntities())
	        	if (e.getUUID().equals(uuid))
	        		return e;

	        return null;
		}

		@Override
		public BlockEntity getTileEntityByPos(NetworkEvent.Context ctx, int x, int y, int z) {
	        return ctx.getSender().level.getBlockEntity(new BlockPos(x, y, z));
		}

		@Override
		public void handle(AnimatedEventMessage message, NetworkEvent.Context ctx) {
			ctx.enqueueWork(() -> {
	            if (handleCommonSide(message, ctx)) {
	                boolean succes = message.animated.getAnimationHandler().onClientIAnimatedEvent(message);
	                if (succes && message.animated.getAnimationHandler() instanceof ClientAnimationHandler
	                        && (message.event == EnumIAnimatedEvent.START_ANIM.getId() || message.event == EnumIAnimatedEvent.STOP_START_ANIM.getId())) {
	                    @SuppressWarnings("rawtypes")
						ClientAnimationHandler hand = (ClientAnimationHandler) message.animated.getAnimationHandler();
	                    String animName = hand.getAnimNameFromId(message.animId);
	                    InfoChannel infoC = (InfoChannel) hand.getAnimChannels().get(animName);
	                    CSNetworkHelper.sendPacketToServer(new AnimatedEventMessage(EnumIAnimatedEvent.ANSWER_START_ANIM, message.animated, message.animId, infoC.totalFrames));
	                }
	            }
	        });
		}

    }

    private static abstract class CommonHandler {

    	protected boolean handleCommonSide(AnimatedEventMessage message, NetworkEvent.Context ctx) {
        	if (message.hasEntity) {
                Entity e = this.getEntityByUUID(ctx, message.most, message.least);
                if (!(e instanceof IAnimated)) {
                    CraftStudioApi.getLogger().debug("Networking error : invalid entity.");
                    return false;
                }
                message.animated = (IAnimated) e;
            } else {
                BlockEntity te = this.getTileEntityByPos(ctx, message.x, message.y, message.z);
                if (!(te instanceof IAnimated)) {
                    CraftStudioApi.getLogger().debug("Networking error : invalid tile entity.");
                    return false;
                }
                message.animated = (IAnimated) te;
            }
        	return true;
        }

    	/**
         * Get an entity by its UUID.
         *
         * @param ctx   The context of the message received.
         * @param most  The most significants bits of the UUID.
         * @param least The least significants bits of the UUID.
         * @return The Entity, null if it couldn't be found.
         */
        public abstract Entity getEntityByUUID(NetworkEvent.Context ctx, long most, long least);

        /**
         * Get a TileEntity by its position.
         *
         * @param ctx The context of the message received.
         * @param x   The position on the x axis.
         * @param y   The position on the y axis.
         * @param z   The position on the z axis.
         * @return The TileEntity, null if it couldn't be found.
         */
        public abstract BlockEntity getTileEntityByPos(NetworkEvent.Context ctx, int x, int y, int z);

        public abstract void handle(AnimatedEventMessage message, NetworkEvent.Context ctx);
    }
}

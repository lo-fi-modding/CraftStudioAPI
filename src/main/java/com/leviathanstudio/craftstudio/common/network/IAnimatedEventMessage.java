package com.leviathanstudio.craftstudio.common.network;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * Base class for
 * {@link com.leviathanstudio.craftstudio.common.animation.IAnimated IAnimated}
 * event messages.
 *
 * @since 0.3.0
 *
 * @author Timmypote
 */
public class IAnimatedEventMessage
{
    /** The id of the event. See for more info {@link EnumIAnimatedEvent}. */
    public short     event;
    /** The id of the primary animation. */
    public short     animId;
    /** The id of the secondary animation, used for stopStart message. */
    public short     optAnimId    = -1;
    /** A float used to transmit keyframe related informations. */
    public float     keyframeInfo = -1;
    /** The object that is animated */
    public IAnimated animated;
    /** Variable that transmit part of the UUID of an Entity. */
    public long      most, least;
    /** Variable that transmit the position of a TileEntity. */
    public int       x, y, z;
    /** True, if on message receiving the animated object is an entity. */
    public boolean   hasEntity;

    /** Simple empty constructor for packets system. */
    public IAnimatedEventMessage() {}

    /** Constructor */
    public IAnimatedEventMessage(EnumIAnimatedEvent event, IAnimated animated, short animId) {
        if (event != null)
            this.event = event.getId();
        this.animated = animated;
        this.animId = animId;
    }

    /** Constructor */
    public IAnimatedEventMessage(EnumIAnimatedEvent event, IAnimated animated, short animId, float keyframeInfo) {
        this(event, animated, animId);
        this.keyframeInfo = keyframeInfo;
    }

    /** Constructor */
    public IAnimatedEventMessage(EnumIAnimatedEvent event, IAnimated animated, short animId, float keyframeInfo, short optAnimId) {
        this(event, animated, animId, keyframeInfo);
        this.optAnimId = optAnimId;
    }

    /** Constructor */
    public IAnimatedEventMessage(IAnimatedEventMessage eventObj) {
        this(null, eventObj.animated, eventObj.animId, eventObj.keyframeInfo, eventObj.optAnimId);
        this.event = eventObj.event;
    }

    public static <T extends IAnimatedEventMessage> T fromBytes(final T packet, FriendlyByteBuf buf) {
        short actualEvent = buf.readShort();
        if (actualEvent < 0 || actualEvent >= 2 * EnumIAnimatedEvent.ID_COUNT) {
            packet.event = -1;
            CraftStudioApi.LOGGER.error("Networking error : invalid packet.");
            return packet;
        }
        else if (actualEvent < EnumIAnimatedEvent.ID_COUNT) {
            packet.most = buf.readLong();
            packet.least = buf.readLong();
            packet.event = actualEvent;
            packet.hasEntity = true;
        }
        else {
            packet.x = buf.readInt();
            packet.y = buf.readInt();
            packet.z = buf.readInt();
            packet.event = (short) (actualEvent - EnumIAnimatedEvent.ID_COUNT);
            packet.hasEntity = false;
        }
        packet.animId = buf.readShort();
        if (packet.event != 2)
            packet.keyframeInfo = buf.readFloat();
        if (packet.event > 2)
            packet.optAnimId = buf.readShort();

        return packet;
    }

    public static void toBytes(final IAnimatedEventMessage packet, FriendlyByteBuf buf) {
        if (packet.event < 0 || packet.event >= EnumIAnimatedEvent.ID_COUNT) {
            buf.writeShort(-1);
            CraftStudioApi.LOGGER.error("Unsupported event id {} for network message.", packet.event);
            return;
        }
        if (packet.animated instanceof Entity) {
            Entity e = (Entity) packet.animated;
            buf.writeShort(packet.event);
            UUID uuid = e.getUUID();
            buf.writeLong(uuid.getMostSignificantBits());
            buf.writeLong(uuid.getLeastSignificantBits());
        }
        else if (packet.animated instanceof BlockEntity) {
            BlockEntity te = (BlockEntity) packet.animated;
            buf.writeShort(packet.event + EnumIAnimatedEvent.ID_COUNT);
            BlockPos pos = te.getBlockPos();
            buf.writeInt(pos.getX());
            buf.writeInt(pos.getY());
            buf.writeInt(pos.getZ());
        }
        else {
            buf.writeShort(-1);
            CraftStudioApi.LOGGER.error("Unsupported class {} for network message.", packet.animated.getClass().getSimpleName());
            CraftStudioApi.LOGGER.error("You are trying to animate an other class than Entity or TileEntity.");
            return;
        }
        buf.writeShort(packet.animId);
        if (packet.event != EnumIAnimatedEvent.STOP_ANIM.getId())
            buf.writeFloat(packet.keyframeInfo);
        if (packet.event == EnumIAnimatedEvent.STOP_START_ANIM.getId())
            buf.writeShort(packet.optAnimId);
    }

    /**
     * Base class for the message handler.
     *
     * @since 0.3.0
     *
     * @author Timmypote
     */
    public static abstract class IAnimatedEventHandler
    {

        /**
         * Extract the Entity or TileEntity when a message is received.
         *
         * @param message
         *            The message received.
         * @param ctx
         *            The context of the message.
         * @return True, if the Entity/TileEntity was successfully extracted.
         *         False, otherwise.
         */
        public boolean onMessage(IAnimatedEventMessage message, Supplier<NetworkEvent.Context> ctx) {
            if (message.hasEntity) {
                Entity e = this.getEntityByUUID(ctx, message.most, message.least);
                if (!(e instanceof IAnimated)) {
                    CraftStudioApi.LOGGER.debug("Networking error : invalid entity.");
                    return false;
                }
                message.animated = (IAnimated) e;
            }
            else {
                BlockEntity te = this.getTileEntityByPos(ctx, message.x, message.y, message.z);
                if (!(te instanceof IAnimated)) {
                    CraftStudioApi.LOGGER.debug("Networking error : invalid tile entity.");
                    return false;
                }
                message.animated = (IAnimated) te;
            }
            return true;
        }

        /**
         * Get an entity by its UUID.
         *
         * @param ctx
         *            The context of the message received.
         * @param most
         *            The most significant bits of the UUID.
         * @param least
         *            The least significant bits of the UUID.
         * @return The Entity, null if it couldn't be found.
         */
        public abstract Entity getEntityByUUID(Supplier<NetworkEvent.Context> ctx, long most, long least);

        /**
         * Get a TileEntity by its position.
         *
         * @param ctx
         *            The context of the message received.
         * @param x
         *            The position on the x axis.
         * @param y
         *            The position on the y axis.
         * @param z
         *            The position on the z axis.
         * @return The TileEntity, null if it couldn't be found.
         */
        public abstract BlockEntity getTileEntityByPos(Supplier<NetworkEvent.Context> ctx, int x, int y, int z);
    }
}

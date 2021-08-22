package com.leviathanstudio.craftstudio;

import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import com.leviathanstudio.craftstudio.proxy.CSClientProxy;
import com.leviathanstudio.craftstudio.proxy.CSCommonProxy;
import com.leviathanstudio.craftstudio.proxy.CSServerProxy;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class of the CraftStudioApi
 *
 * @since 0.3.0
 *
 * @author ZeAmateis
 * @author Timmypote
 */
@Mod(CraftStudioApi.API_ID)
public class CraftStudioApi
{
    public static final Logger LOGGER  = LogManager.getLogger("CraftStudio");
    public static final String API_ID  = "craftstudioapi";

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder
      .named(new ResourceLocation(API_ID, "main_channel"))
      .clientAcceptedVersions(PROTOCOL_VERSION::equals)
      .serverAcceptedVersions(PROTOCOL_VERSION::equals)
      .networkProtocolVersion(() -> PROTOCOL_VERSION)
      .simpleChannel();

    private static final CSCommonProxy proxy = DistExecutor.safeRunForDist(() -> CSClientProxy::new, () -> CSServerProxy::new);

    public CraftStudioApi() {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(this::preInit);
    }

    public void preInit(FMLCommonSetupEvent event) {
        CraftStudioApi.proxy.preInit(event);
    }

    /**
     * Helper to create an AnimationHandler to registry animation to your
     * entity/block
     *
     * @param animatedClass
     *            Class which implements IAnimated (Entity or TileEntity)
     */
    public static <T extends IAnimated> AnimationHandler<T> getNewAnimationHandler(Class<T> animatedClass) {
        return CraftStudioApi.proxy.getNewAnimationHandler(animatedClass);

    }
}

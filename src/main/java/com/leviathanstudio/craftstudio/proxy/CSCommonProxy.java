package com.leviathanstudio.craftstudio.proxy;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;
import com.leviathanstudio.craftstudio.common.network.ClientIAnimatedEventMessage;
import com.leviathanstudio.craftstudio.common.network.IAnimatedEventMessage;
import com.leviathanstudio.craftstudio.common.network.ServerIAnimatedEventMessage;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fmllegacy.network.NetworkDirection;

/**
 * Common base for the proxies of the CraftStudioApi
 *
 * @since 0.3.0
 *
 * @author Timmypote
 * @author ZeAmateis
 */
public abstract class CSCommonProxy
{
    public void preInit(FMLCommonSetupEvent e) {
        final IAnimatedEventMessage.IAnimatedEventHandler clientHandler = new ClientIAnimatedEventMessage.ClientIAnimatedEventHandler();
        CraftStudioApi.NETWORK.messageBuilder(ClientIAnimatedEventMessage.class, 0, NetworkDirection.PLAY_TO_CLIENT).encoder(IAnimatedEventMessage::toBytes).decoder(ClientIAnimatedEventMessage::fromBytes).consumer(clientHandler::onMessage).add();
        final IAnimatedEventMessage.IAnimatedEventHandler serverHandler = new ServerIAnimatedEventMessage.ServerIAnimatedEventHandler();
        CraftStudioApi.NETWORK.messageBuilder(ServerIAnimatedEventMessage.class, 1, NetworkDirection.PLAY_TO_SERVER).encoder(IAnimatedEventMessage::toBytes).decoder(ServerIAnimatedEventMessage::fromBytes).consumer(serverHandler::onMessage).add();
    }

    public abstract <T extends IAnimated> AnimationHandler<T> getNewAnimationHandler(Class<T> animatedClass);
}

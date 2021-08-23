package com.leviathanstudio.craftstudio.dev;

import com.leviathanstudio.craftstudio.dev.command.CommandCSList;
import com.leviathanstudio.craftstudio.dev.command.CommandCSUVMap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Main class of the dev mod of the CraftStudioAPI
 *
 * @since 0.3.0
 *
 * @author Timmypote
 */
@Mod(CraftStudioApiDev.API_ID)
public class CraftStudioApiDev
{

    private static final Logger LOGGER = LogManager.getLogger("CraftStudioAPIDev");
    public static final String  API_ID = "craftstudiodev";
    public static final String  NAME   = "CraftStudio API Dev";

    public CraftStudioApiDev() {
        MinecraftForge.EVENT_BUS.addListener(this::registerCommandsEvent);
    }

    private void registerCommandsEvent(RegisterCommandsEvent event) {
        CommandCSUVMap.register(event.getDispatcher());
        CommandCSList.register(event.getDispatcher());
    }

    public static Logger getLogger() {
        return CraftStudioApiDev.LOGGER;
    }
}

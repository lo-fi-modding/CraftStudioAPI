package com.leviathanstudio.craftstudio.dev;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.leviathanstudio.craftstudio.dev.command.CommandCSList;
import com.leviathanstudio.craftstudio.dev.command.CommandCSUVMap;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = CraftStudioApiDev.API_ID, name = CraftStudioApiDev.NAME, clientSideOnly = true,
    version = "0.2.1-beta",
    acceptedMinecraftVersions = "1.10.2")
public class CraftStudioApiDev
{

    private static final Logger LOGGER = LogManager.getLogger("CraftStudioAPIDev");
    public static final String  API_ID = "craftstudioapidev";
    public static final String  NAME   = "CraftStudio API Dev";

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ClientCommandHandler.instance.registerCommand(new CommandCSUVMap());
        ClientCommandHandler.instance.registerCommand(new CommandCSList());
    }

    public static Logger getLogger() {
        return CraftStudioApiDev.LOGGER;
    }
}

package com.leviathanstudio.craftstudio.test.common;

import com.leviathanstudio.craftstudio.test.proxy.ClientProxy;
import com.leviathanstudio.craftstudio.test.proxy.CommonProxy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModTest.MODID)
public class ModTest
{
    public static final String MODID = "craftstudiotest";

    public static final CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    public ModTest( ) {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

    	MinecraftForge.EVENT_BUS.register(PROXY);
    }

    public void clientSetup(FMLClientSetupEvent event) {
        ModTest.PROXY.registerCraftStudioAssets();
        ModTest.PROXY.bindTESR();
    }

}

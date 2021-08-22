package com.leviathanstudio.craftstudio.proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.client.animation.ClientAnimationHandler;
import com.leviathanstudio.craftstudio.client.registry.CSRegistryHelper;
import com.leviathanstudio.craftstudio.client.registry.RegistryHandler;
import com.leviathanstudio.craftstudio.common.animation.AnimationHandler;
import com.leviathanstudio.craftstudio.common.animation.IAnimated;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Client proxy of the CraftStudioApi
 *
 * @since 0.3.0
 *
 * @author Timmypote
 * @author ZeAmateis
 */
public class CSClientProxy extends CSCommonProxy
{
    @Override
    public void preInit(FMLCommonSetupEvent e) {
        super.preInit(e);
        this.loadCraftStudioLoaders(e);
    }

    @Override
    public <T extends IAnimated> AnimationHandler<T> getNewAnimationHandler(Class<T> animatedClass) {
        return new ClientAnimationHandler<>();
    }

    private void loadCraftStudioLoaders(FMLCommonSetupEvent e) {
        String methodName, className;
        Method method;

        RegistryHandler.init();

        ASMDataTable dataTable = e.getAsmData();
        Set<ASMData> datas = dataTable.getAll("com.leviathanstudio.craftstudio.client.registry.CraftStudioLoader");
        for (ASMData data : datas) {
            className = data.getClassName();
            methodName = data.getObjectName().substring(0, data.getObjectName().indexOf("("));
            try {
                method = Class.forName(className).getMethod(methodName);
                method.invoke(null);
            } catch (NoSuchMethodException | SecurityException | ClassNotFoundException e1) {
                e1.printStackTrace();
                CraftStudioApi.LOGGER.error("Error loading @CraftStudioLoader in class {} for method {}().", className, methodName);
                CraftStudioApi.LOGGER.error("Does that method has arguments ? Because it should have none.");
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException e1) {
                e1.printStackTrace();
                CraftStudioApi.LOGGER.error("Error loading craftstudio assets in class {} for method {}().", className, methodName);
                CraftStudioApi.LOGGER.error("Is that method 'static' ? Because it should.");
            }
        }

        CSRegistryHelper.loadModels();
        CSRegistryHelper.loadAnims();
    }

}

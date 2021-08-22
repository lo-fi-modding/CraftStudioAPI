package com.leviathanstudio.craftstudio.client.registry;

import com.leviathanstudio.craftstudio.client.json.CSReadedAnim;
import com.leviathanstudio.craftstudio.client.json.CSReadedModel;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that handle registry for the CraftStudioApi.
 *
 * @since 1.0.0
 *
 * @author Timmypote
 */
public class RegistryHandler
{
    public static Map<ResourceLocation, CSReadedModel> modelRegistry;
    public static Map<ResourceLocation, CSReadedAnim>  animationRegistry;

    /**
     * Initialize the registries.
     */
    public static void init() {
        modelRegistry = new HashMap<>();
        animationRegistry = new HashMap<>();
    }

    /**
     * Register a CSReadedModel.
     *
     * @param res
     *            The name of the model.
     * @param model
     *            The model.
     */
    public static void register(ResourceLocation res, CSReadedModel model) {
        modelRegistry.put(res, model);
    }

    /**
     * Register a CSReadedAnim.
     *
     * @param res
     *            The name of the animation.
     * @param anim
     *            The animation.
     */
    public static void register(ResourceLocation res, CSReadedAnim anim) {
        animationRegistry.put(res, anim);
    }
}

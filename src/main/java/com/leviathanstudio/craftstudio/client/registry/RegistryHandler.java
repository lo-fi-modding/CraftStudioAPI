package com.leviathanstudio.craftstudio.client.registry;

import com.leviathanstudio.craftstudio.client.json.CSReadedAnim;
import com.leviathanstudio.craftstudio.client.json.CSReadedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that handle registry for the CraftStudioApi.
 *
 * @author Timmypote
 * @since 1.0.0
 */
@OnlyIn(Dist.CLIENT)
public class RegistryHandler {
    public static final Map<ResourceLocation, CSReadedModel> modelRegistry = new HashMap<>();
    public static final Map<ResourceLocation, CSReadedAnim> animationRegistry = new HashMap<>();

    /**
     * Register a CSReadedModel.
     *
     * @param res   The name of the model.
     * @param model The model.
     */
    public static void register(ResourceLocation res, CSReadedModel model) {
        modelRegistry.put(res, model);
    }

    /**
     * Register a CSReadedAnim.
     *
     * @param res  The name of the animation.
     * @param anim The animation.
     */
    public static void register(ResourceLocation res, CSReadedAnim anim) {
        animationRegistry.put(res, anim);
    }
}

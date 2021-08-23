package com.leviathanstudio.craftstudio.client.registry;

import com.google.common.reflect.TypeToken;
import com.leviathanstudio.craftstudio.CraftStudioApi;
import com.leviathanstudio.craftstudio.client.exception.CSMalformedJsonException;
import com.leviathanstudio.craftstudio.client.exception.CSResourceNotFoundException;
import com.leviathanstudio.craftstudio.client.json.CSJsonReader;
import com.leviathanstudio.craftstudio.client.json.CSReadedAnim;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

public class AssetAnimation extends CSReadedAnim implements IForgeRegistryEntry<AssetAnimation> {
    private final TypeToken<AssetAnimation> token = new TypeToken<AssetAnimation>(getClass()) {
    };
    private ResourceLocation registryName = null;


    public AssetAnimation(ResourceLocation assetIn) {
        CSJsonReader jsonReader;
        try {
            jsonReader = new CSJsonReader(assetIn);
            if (assetIn.getNamespace() != CraftStudioApi.API_ID) {
                RegistryHandler.register(assetIn, jsonReader.readAnim());
            } else
                CraftStudioApi.getLogger().fatal("You're not allowed to use the \"craftstudioapi\" to register CraftStudio resources.");
        } catch (CSResourceNotFoundException | CSMalformedJsonException e) {
            e.printStackTrace();
        }
    }


    /**
     * A unique identifier for this entry, if this entry is registered already it will return it's official registry name.
     * Otherwise it will return the name set in setRegistryName().
     * If neither are valid null is returned.
     *
     * @return Unique identifier or null.
     */
    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return registryName != null ? registryName : null;
    }

    /**
     * Sets a unique name for this Item. This should be used for uniquely identify the instance of the Item.
     * This is the valid replacement for the atrocious 'getUnlocalizedName().substring(6)' stuff that everyone does.
     * Unlocalized names have NOTHING to do with unique identifiers. As demonstrated by vanilla blocks and items.
     * <p>
     * The supplied name will be prefixed with the currently active mod's modId.
     * If the supplied name already has a prefix that is different, it will be used and a warning will be logged.
     * <p>
     * If a name already exists, or this Item is already registered in a registry, then an IllegalStateException is thrown.
     * <p>
     * Returns 'this' to allow for chaining.
     *
     * @param name Unique registry name
     * @return This instance
     */
    @Override
    public final AssetAnimation setRegistryName(ResourceLocation name) {
        return setRegistryName(name.toString());
    }


    public final AssetAnimation setRegistryName(String name) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

        this.registryName = GameData.checkPrefix(name, true);
        return (AssetAnimation) this;
    }


    public final AssetAnimation setRegistryName(String modID, String name) {
        return setRegistryName(modID + ":" + name);
    }


    /**
     * Determines the type for this entry, used to look up the correct registry in the global registries list as there can only be one
     * registry per concrete class.
     *
     * @return Root registry type.
     */
    @Override
    public Class<AssetAnimation> getRegistryType() {
        return (Class<AssetAnimation>) token.getRawType();
    }
}

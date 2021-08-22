package com.leviathanstudio.craftstudio.client.util;

/**
 * Enumeration of the different render type.
 *
 * @since 0.3.0
 *
 * @author ZeAmateis
 * @author Phenix246
 */
public enum EnumRenderType {

    BLOCK("blocks/"), ENTITY("entity/");

    String folderName;

    private EnumRenderType(String folderNameIn) {
        this.folderName = folderNameIn;
    }

    /**
     * Get the folder associated with this render type.
     *
     * @return The folder as a string.
     */
    public String getFolderName() {
        return this.folderName;
    }
}

package com.leviathanstudio.craftstudio.client.util;

/**
 * Enumeration of the different resource type.
 *
 * @since 0.3.0
 *
 * @author ZeAmateis
 * @author Phenix246
 */
public enum EnumResourceType {

    MODEL("craftstudio/models/", ".csjsmodel"), ANIM("craftstudio/animations/", ".csjsmodelanim");

    String path, extension;

    private EnumResourceType(String pathIn, String extensionIn) {
        this.path = pathIn;
        this.extension = extensionIn;
    }

    public String getPath() {
        return this.path;
    }

    public String getExtension() {
        return this.extension;
    }
}

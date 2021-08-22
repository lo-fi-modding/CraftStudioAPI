package com.leviathanstudio.craftstudio.client.animation;

/**
 * Enumeration of the possible animation mode.
 *
 * @since 0.3.0
 *
 * @author Timmypote
 * @author Phenix246
 */
public enum EnumAnimationMode {
    /** An animation that play just once */
    LINEAR,
    /** An animation that play once and hold the last keyframe */
    HOLD,
    /** An animation that restart everytime it end */
    LOOP;
}

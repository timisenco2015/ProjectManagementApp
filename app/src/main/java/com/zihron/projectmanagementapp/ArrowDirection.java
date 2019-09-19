package com.zihron.projectmanagementapp;

/**
 * This code  and class was not written or developed by me. I got it from this link: https://github.com/MasayukiSuda/BubbleLayout/
 */


/**
 * Created by sudamasayuki on 16/06/14.
 */
public enum ArrowDirection {
    LEFT(0),
    RIGHT(1),
    TOP(2),
    BOTTOM(3),
    //CENTER
    LEFT_CENTER(4),
    RIGHT_CENTER(5),
    TOP_CENTER(6),
    BOTTOM_CENTER(7);


    private int value;

    ArrowDirection(int value) {
        this.value = value;
    }

    public static ArrowDirection fromInt(int value) {
        for (ArrowDirection arrowDirection : ArrowDirection.values()) {
            if (value == arrowDirection.getValue()) {
                return arrowDirection;
            }
        }
        return LEFT;
    }

    public int getValue() {
        return value;
    }
}

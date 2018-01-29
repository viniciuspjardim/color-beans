/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.tests;

import com.badlogic.gdx.graphics.Color;

/**
 * @author Vinícius Jardim
 * 11/08/2016
 */
public class Utils {

    public static final byte[][] map = {

            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5, 3, 3, 2, 3, 1, 1,},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 2, 1, 3, 1,},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 4, 4, 4, 2,},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 5,},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 2, 1, 1,},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 9, 9, 9, 3, 1,},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 3, 4, 4, 4, 2,},
    };

    public static void printMemory() {

        int mb = 1024*1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        System.out.println("##### Heap utilization statistics [MB] #####");

        //Print used memory
        System.out.println("Used Memory:"
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        System.out.println("Free Memory:"
                + runtime.freeMemory() / mb);

        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);

        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);
    }

    public static Color intToColor(int color) {

        switch(color) {
            case 1: return Color.RED;
            case 2: return Color.BLUE;
            case 3: return Color.GREEN;
            case 4: return Color.YELLOW;
            case 5: return Color.PURPLE;
            case 6: return Color.WHITE;
            case 7: return Color.WHITE;
            case 8: return Color.WHITE;
            case 9: return Color.GRAY;
        }

        return Color.WHITE;
    }
}

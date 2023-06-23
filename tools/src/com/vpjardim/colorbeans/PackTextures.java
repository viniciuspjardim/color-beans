/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * @author Vinícius Jardim
 *         2016/10/08
 */
public class PackTextures {
    public static void main(String[] args) {
        System.out.println("Packing textures...");
        TexturePacker.process("to_pack/", "packed/", "pack");
        System.out.println("End packing!");
    }
}

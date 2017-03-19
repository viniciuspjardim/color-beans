/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.tools;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * @author Vinícius Jardim
 */
public class PackTextures {

    public static void main(String[] args) {

        // Bug pack_s.atlas must be fixed by hand. 9 patches on low res don't
        // display properly
        // wrong values are: split: 32, 32, 28, 32 | split: 32, 32, 24, 36
        // replace with    : split: 31, 32, 27, 32 | split: 31, 32, 23, 36

        System.out.println("Packing textures...");
        TexturePacker.process("to_pack/", "packed/", "pack");
        System.out.println("End packing!");
    }
}

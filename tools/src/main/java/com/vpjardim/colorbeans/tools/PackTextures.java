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

        // Bug pack_s.atlas must be fixed by hand. Buttons on low res don't
        // display properly
        // wrong values are "split: 30, 33, 25, 38
        // replace with "split: 29, 33, 24, 38"

        System.out.println("Packing textures...");
        TexturePacker.process("to_pack/", "packed", "pack");
        System.out.println("End packing");
    }
}

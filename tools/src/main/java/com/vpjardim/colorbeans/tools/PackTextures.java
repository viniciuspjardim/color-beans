package com.vpjardim.colorbeans.tools;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class PackTextures {

    public static void main(String[] args) {
        System.out.println("Packing textures...");
        TexturePacker.process("to_pack/", "packed", "pack");
        System.out.println("End packing");
    }
}

package com.vpjardim.colorbeans.tools;


import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

/**
 * Created by viniciuspj on 13/06/2016.
 */
public class PackIcons {

    static Out[] outs = new Out[] {
            new Out(48, "android\\res\\drawable-mdpi\\ic_launcher.png"),
            new Out(72, "android\\res\\drawable-hdpi\\ic_launcher.png"),
            new Out(96, "android\\res\\drawable-xhdpi\\ic_launcher.png"),
            new Out(144, "android\\res\\drawable-xxhdpi\\ic_launcher.png"),
            new Out(192, "android\\res\\drawable-xxxhdpi\\ic_launcher.png"),
            new Out(512, "android\\ic_launcher-web.png"),
            new Out(32, "android\\assets\\icon\\desk32.png"),
            new Out(64, "android\\assets\\icon\\desk64.png"),
            new Out(128, "android\\assets\\icon\\desk128.png"),
            new Out(256, "android\\assets\\icon\\desk256.png"),
    };

    public static class Out {

        public Out(int size, String path) {
            this.size = size;
            this.path = path;
        }

        public int size;
        public String path;
    }

    public static void main(String[] args) throws Exception {

        String iconPath = "C:\\Users\\viniciuspj\\Desktop\\cbFiles\\icon2_1024.png";
        String projectPath = "D:\\Dropbox\\Projetos\\Outros\\colorbeans\\";

        File iconFile = new File(iconPath);
        BufferedImage icon = ImageIO.read(iconFile);

        System.out.println("Packing icons...");
        System.out.println("Opening base icon at: " + iconPath);
        System.out.println("Project root at: " + projectPath);

        for(Out o : outs) {

            // Icon resized
            BufferedImage iconR = Scalr.resize(
                    icon,
                    Method.ULTRA_QUALITY,
                    Mode.FIT_TO_WIDTH,
                    o.size,
                    o.size,
                    Scalr.OP_ANTIALIAS);

            ImageIO.write(iconR, "PNG", new File(projectPath + o.path));

            System.out.println("\tSize: " + o.size + "; Path: " + o.path);
        }

        System.out.println("End packing");
    }
}

package com.vpjardim.colorbeans;

import com.badlogic.gdx.backends.lwjgl3.audio.OggInputStream;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * iOS can't decode Ogg Vorbis, so the iOS build ships AAC copies of assets/audio/*.ogg in
 * ios/assets/audio/*.m4a. This regenerates them. Run it from the repo root (gradlew
 * tools:convertAudioForIOS) after changing any audio. Needs macOS for afconvert.
 */
public class ConvertAudioForIOS {
    public static void main(String[] args) throws Exception {
        File inDir = new File("assets/audio");
        File outDir = new File("ios/assets/audio");
        outDir.mkdirs();

        File[] oggs = inDir.listFiles((dir, name) -> name.endsWith(".ogg"));
        if (oggs == null || oggs.length == 0)
            throw new IllegalStateException("No .ogg files in " + inDir.getAbsolutePath());

        for (File ogg : oggs) {
            String name = ogg.getName().substring(0, ogg.getName().length() - ".ogg".length());
            File wav = File.createTempFile(name, ".wav");
            File m4a = new File(outDir, name + ".m4a");

            try {
                oggToWav(ogg, wav);
                run("afconvert", "-f", "m4af", "-d", "aac", "-b", "160000",
                        wav.getPath(), m4a.getPath());
            } finally {
                wav.delete();
            }

            System.out.println(ogg.getPath() + " -> " + m4a.getPath());
        }
    }

    private static void oggToWav(File ogg, File wav) throws IOException {
        ByteArrayOutputStream pcm = new ByteArrayOutputStream();
        int channels, sampleRate;

        try (InputStream in = new FileInputStream(ogg)) {
            OggInputStream decoder = new OggInputStream(in);
            channels = decoder.getChannels();
            sampleRate = decoder.getSampleRate();

            byte[] buffer = new byte[4096];
            int read;
            while ((read = decoder.read(buffer)) > 0) {
                pcm.write(buffer, 0, read);
            }
            decoder.close();
        }

        try (OutputStream out = new FileOutputStream(wav)) {
            writeWavHeader(out, pcm.size(), channels, sampleRate);
            pcm.writeTo(out);
        }
    }

    /** 16-bit little-endian PCM, which is what OggInputStream outputs */
    private static void writeWavHeader(OutputStream out, int dataSize, int channels, int sampleRate)
            throws IOException {
        DataOutputStream d = new DataOutputStream(out);
        int blockAlign = channels * 2;

        d.writeBytes("RIFF");
        d.writeInt(Integer.reverseBytes(36 + dataSize));
        d.writeBytes("WAVEfmt ");
        d.writeInt(Integer.reverseBytes(16));
        d.writeShort(Short.reverseBytes((short) 1)); // PCM
        d.writeShort(Short.reverseBytes((short) channels));
        d.writeInt(Integer.reverseBytes(sampleRate));
        d.writeInt(Integer.reverseBytes(sampleRate * blockAlign));
        d.writeShort(Short.reverseBytes((short) blockAlign));
        d.writeShort(Short.reverseBytes((short) 16));
        d.writeBytes("data");
        d.writeInt(Integer.reverseBytes(dataSize));
        d.flush();
    }

    private static void run(String... command) throws IOException, InterruptedException {
        Process p = new ProcessBuilder(command).inheritIO().start();
        if (p.waitFor() != 0)
            throw new IOException("Command failed: " + String.join(" ", command));
    }
}

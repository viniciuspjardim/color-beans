package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.vpjardim.colorbeans.G;

public class Audio {
    public static final String MUSIC1 = "audio/music1.ogg";

    private Music playingMusic;

    public void configMusic(String music, boolean resetVolume, boolean loop) {
        playingMusic = G.game.assets.get(music, Music.class);
        playingMusic.setLooping(loop);

        if (resetVolume) {
            playingMusic.setVolume(G.game.data.musicVolume);
        }
    }

    public void playMusic() {
        playingMusic.play();
    }

    public void pauseMusic() {
        playingMusic.pause();
    }

    public void stopMusic() {
        playingMusic.stop();
    }

    public void playEffect(String effect) {
        G.game.assets.get(effect, Sound.class).play(G.game.data.effectsVolume);
    }
}

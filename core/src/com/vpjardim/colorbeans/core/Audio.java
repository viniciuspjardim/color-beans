/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.audio.Music;
import com.vpjardim.colorbeans.G;

/**
 * @author Vinícius Jardim
 *         2018/06/06
 */
public class Audio {
    public static final String MUSIC1 = "audio/music1.ogg";

    private Music playingMusic;

    public void configMusic(String music, boolean resetVolume, boolean loop) {
        playingMusic = G.game.assets.get(music, Music.class);
        playingMusic.setLooping(loop);

        if (resetVolume) {
            playingMusic.setVolume(0.5f);
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
}

/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.core;

import com.badlogic.gdx.audio.Music;
import com.vpjardim.colorbeans.G;

/**
 * @author Vinícius Jardim
 * 2018/06/06
 */
public class Audio {

    public static final String MUSIC1 = "audio/music1.ogg";
    public static final String MUSIC2 = "audio/music2.ogg";
    public static final String MUSIC3 = "audio/music3.ogg";

    private Music playingMusic;
    private float musicVolume = 0.5f;
    private float effectsVolume = 1f;

    public static Music getMusic(String fileName) {
        return G.game.assets.get(fileName, Music.class);
    }

    public void playMusic(String music, boolean resetVolume) {
        playingMusic = G.game.assets.get(music, Music.class);
        if(resetVolume) playingMusic.setVolume(musicVolume);
        playingMusic.play();
    }

    public void playMusic() {
        playingMusic.play();
    }

    public void stopMusic() {
        playingMusic.stop();
    }

    /** Fade music volume down and stops it */
    public void fadeMusicDown() {

    }

    /** Play music and fades its volume up */
    public void fadeMusicUp() {

    }
}

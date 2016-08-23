/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.vpjardim.colorbeans.input.InputManager;

/**
 * Just a black screen waiting loading is done
 *
 * @author Vinícius Jardim
 * 10/06/2016
 */
public class LoadingScreen extends ScreenBase {

    private int frameCount = 0;

    public LoadingScreen() {
        super();
        manageInput = false;
    }

    public void loadStuff() {

        game.input = new InputManager();
        game.input.loadInputs();
        game.batch = new SpriteBatch();

        game.assets = new AssetManager();
        game.assets.load("img/pack.atlas", TextureAtlas.class);
        game.assets.load("audio/studio.ogg", Music.class);

        FileHandleResolver resolver = new InternalFileHandleResolver();
        game.assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        game.assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        FreetypeFontLoader.FreeTypeFontLoaderParameter param =
                new FreetypeFontLoader.FreeTypeFontLoaderParameter();

        param.fontFileName = "font/roboto.ttf";
        param.fontParameters.size = 24;
        game.assets.load("roboto_24.ttf", BitmapFont.class, param);

        param.fontFileName = "font/roboto_i.ttf";
        param.fontParameters.size = 24;
        game.assets.load("roboto_i_24.ttf", BitmapFont.class, param);
    }

    @Override
    public void render(float delta) {

        super.render(delta);

        // Only start loading after the first frame to avoid a white screen blink at the startup.
        // Spite using AssetManager, that load things at a nonblocking method (other thread),
        // it takes a while, sufficient to cause white screen on the game startup.
        if(frameCount == 1) loadStuff();

        // If stuff has done loading, init some vars and go to the next screen
        if(frameCount > 1 && game.assets.update()) {
            game.atlas = game.assets.get("img/pack.atlas", TextureAtlas.class);
            isFinished = true;
        }

        frameCount++;
    }
}

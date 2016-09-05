/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.ScoreTable;
import com.vpjardim.colorbeans.input.InputManager;

import java.text.NumberFormat;

/**
 * Just a black screen waiting loading is done
 *
 * @author Vinícius Jardim
 * 10/06/2016
 */
public class LoadingScreen extends ScreenBase {

    private int frameCount = 0;

    public LoadingScreen() {
        manageInput = false;
    }

    public void loadStuff() {

        G.game.assets = new AssetManager();

        FileHandleResolver resolver = new InternalFileHandleResolver();
        G.game.assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        G.game.assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        FreetypeFontLoader.FreeTypeFontLoaderParameter param;

        param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "font/roboto.ttf";
        param.fontParameters.size = 24;
        G.game.assets.load("roboto.ttf", BitmapFont.class, param);

        param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "font/roboto_i.ttf";
        param.fontParameters.size = 24;
        G.game.assets.load("roboto-i.ttf", BitmapFont.class, param);

        param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "font/dimbo.ttf";
        param.fontParameters.size = 32;
        param.fontParameters.color = Color.PURPLE;
        param.fontParameters.borderColor = Color.WHITE;
        param.fontParameters.borderWidth = 1;
        G.game.assets.load("dimbo.ttf", BitmapFont.class, param);

        G.game.batch  = new SpriteBatch();
        G.game.sr     = new ShapeRenderer();
        G.game.input  = new InputManager();
        G.game.score  = new ScoreTable();
        G.game.intFmt = NumberFormat.getInstance();

        G.game.input.loadInputs();
        G.game.assets.load("img/pack.atlas", TextureAtlas.class);
        G.game.assets.load("audio/studio.ogg", Music.class);

        // Todo load scores #debugCode
        G.game.score.addRow("vini", 55458);
        G.game.score.addRow("klabo", 25003);
        G.game.score.addRow("jão", 30058);
        G.game.score.addRow("pito", 66641);
    }

    @Override
    public void render(float delta) {

        super.render(delta);

        // Only start loading after the first frame to avoid a white screen blink at the startup.
        // Spite using AssetManager, that load things at a nonblocking method (other thread),
        // it takes a while, sufficient to cause white screen on the game startup.
        if(frameCount == 1) loadStuff();

        // If stuff has done loading, init some vars and go to the next screen
        if(frameCount > 1 && G.game.assets.update()) {

            isFinished = true;

            // After loading is done we can create atlas and skin
            G.game.atlas = G.game.assets.get("img/pack.atlas", TextureAtlas.class);

            G.game.skin  = new Skin();
            G.game.skin.addRegions(G.game.atlas);

            BitmapFont font;

            font = G.game.assets.get("roboto.ttf", BitmapFont.class);
            G.game.skin.add("roboto", font);

            font = G.game.assets.get("dimbo.ttf", BitmapFont.class);
            G.game.skin.add("dimbo", font);

            G.game.skin.load(Gdx.files.internal("img/skin.json"));
        }

        frameCount++;
    }
}

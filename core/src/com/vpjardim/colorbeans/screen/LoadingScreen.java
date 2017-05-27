/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.vpjardim.colorbeans.defaults.Db;
import com.vpjardim.colorbeans.input.InputManager;

import java.text.NumberFormat;

/**
 * Just a black screen waiting load to be done
 *
 * @author Vinícius Jardim
 * 10/06/2016
 */
public class LoadingScreen extends ScreenBase {

    private int frameCount = 0;
    private String atlasStr;

    public LoadingScreen() {
        manageInput = false;
    }

    @Override
    public void show() {
        super.show();
        G.loading = true;
    }

    public void loadStuff() {

        G.game.scale = G.game.height / 720f;
        if(G.height >= 1080) G.game.res = G.RES_MEDIUM;
        else G.game.res = G.RES_SMALL;

        G.style.setDefaults();
        G.style.scale(G.game.scale);

        G.game.assets = new AssetManager();

        FileHandleResolver resolver = new InternalFileHandleResolver();
        G.game.assets.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        G.game.assets.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        FreetypeFontLoader.FreeTypeFontLoaderParameter param;

        param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "font/roboto.ttf";
        param.fontParameters.size = G.style.fontSizeMedium;
        G.game.assets.load("roboto.ttf", BitmapFont.class, param);

        param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "font/roboto_i.ttf";
        param.fontParameters.size = G.style.fontSizeMedium;
        G.game.assets.load("roboto-i.ttf", BitmapFont.class, param);

        param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "font/dimbo.ttf";
        param.fontParameters.size = G.style.fontSizeBig;
        param.fontParameters.color = Color.PURPLE;
        param.fontParameters.borderColor = Color.WHITE;
        param.fontParameters.borderWidth = 1;
        G.game.assets.load("dimbo.ttf", BitmapFont.class, param);

        G.game.data   = Db.load();
        G.game.input  = new InputManager();
        G.game.batch  = new SpriteBatch();
        G.game.sr     = new ShapeRenderer();
        G.game.score  = ScoreTable.load();
        G.game.intFmt = NumberFormat.getInstance();

        G.game.input.loadInputs();

        if(G.res == G.RES_MEDIUM) atlasStr = "img/pack_m.atlas"; // Medium size sprites
        else atlasStr = "img/pack_s.atlas";                      // Small size sprites

        G.game.assets.load(atlasStr, TextureAtlas.class);
        G.game.assets.load("audio/studio.ogg", Music.class);
        G.game.assets.load("audio/music1.ogg", Music.class);
        G.game.assets.load("audio/chain_11.ogg", Sound.class);
        G.game.assets.load("audio/chain_12.ogg", Sound.class);
        G.game.assets.load("audio/chain_13.ogg", Sound.class);
        G.game.assets.load("audio/chain_14.ogg", Sound.class);
        G.game.assets.load("audio/chain_15.ogg", Sound.class);
        G.game.assets.load("audio/chain_16.ogg", Sound.class);
        G.game.assets.load("audio/chain_17.ogg", Sound.class);
        G.game.assets.load("audio/chain_21.ogg", Sound.class);
        G.game.assets.load("audio/chain_22.ogg", Sound.class);
        G.game.assets.load("audio/chain_23.ogg", Sound.class);
        G.game.assets.load("audio/chain_24.ogg", Sound.class);
        G.game.assets.load("audio/chain_25.ogg", Sound.class);
        G.game.assets.load("audio/chain_26.ogg", Sound.class);
        G.game.assets.load("audio/chain_27.ogg", Sound.class);
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

            action = ScreenBase.ACT_NEXT;

            // After loading is done we can create atlas and skin
            G.game.atlas = G.game.assets.get(atlasStr, TextureAtlas.class);

            G.game.skin  = new Skin();
            G.game.skin.addRegions(G.game.atlas);

            BitmapFont font;

            font = G.game.assets.get("roboto.ttf", BitmapFont.class);
            G.game.skin.add("roboto", font);

            font = G.game.assets.get("dimbo.ttf", BitmapFont.class);
            G.game.skin.add("dimbo", font);

            G.game.skin.load(Gdx.files.internal("img/skin.json"));

            G.loading = false;
        }

        frameCount++;
    }
}

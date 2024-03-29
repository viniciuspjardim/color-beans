package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.Audio;
import com.vpjardim.colorbeans.core.ScoreTable;
import com.vpjardim.colorbeans.defaults.Db;
import com.vpjardim.colorbeans.input.InputManager;

public class LoadingScreen extends ScreenBase {
    private int frameCount = 0;
    private String atlasStr;
    private Texture loadingTexture;
    private final Color BAR_COLOR = new Color(0x4048ccff);

    public LoadingScreen() {
        manageInput = false;
    }

    @Override
    public void show() {
        super.show();
        G.loading = true;
    }

    private void loadStuff() {
        G.scale = G.height / 1080f;

        G.style.setDefaults();
        G.style.scale(G.scale);

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
        param.fontFileName = "font/roboto.ttf";
        param.fontParameters.size = G.style.fontSizeBig;
        param.fontParameters.shadowColor = new Color(0x00000040);
        param.fontParameters.shadowOffsetX = 0;
        param.fontParameters.shadowOffsetY = 1;
        param.fontParameters.borderColor = new Color(0x000000ff);
        param.fontParameters.borderWidth = 1;
        G.game.assets.load("roboto_shadow.ttf", BitmapFont.class, param);

        param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "font/dimbo.ttf";
        param.fontParameters.size = G.style.fontSizeBig;
        param.fontParameters.color = Color.WHITE;
        param.fontParameters.borderColor = Color.BLACK;
        param.fontParameters.borderWidth = 1;
        G.game.assets.load("dimbo_white.ttf", BitmapFont.class, param);

        param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "font/dimbo.ttf";
        param.fontParameters.size = G.style.fontSizeXBig;
        param.fontParameters.color = new Color(0xe8e2b4ff);
        param.fontParameters.borderColor = new Color(0x7b0a41ff);
        param.fontParameters.borderWidth = 6;
        G.game.assets.load("dimbo_gtitle.ttf", BitmapFont.class, param);

        param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "font/dimbo.ttf";
        param.fontParameters.size = G.style.fontSizeBig;
        param.fontParameters.color = new Color(0xab002cff);
        param.fontParameters.shadowColor = new Color(0x660016ff);
        param.fontParameters.shadowOffsetY = -1;
        G.game.assets.load("dimbo_brown.ttf", BitmapFont.class, param);

        param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "font/dimbo.ttf";
        param.fontParameters.size = G.style.fontSizeBig;
        param.fontParameters.color = new Color(0x78001cff);
        param.fontParameters.shadowColor = new Color(0x44000fff);
        param.fontParameters.shadowOffsetY = -1;
        G.game.assets.load("dimbo_dbrown.ttf", BitmapFont.class, param);

        param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "font/dimbo.ttf";
        param.fontParameters.size = G.style.fontSizeBig;
        param.fontParameters.color = new Color(0x432d63ff);
        param.fontParameters.shadowColor = new Color(0x311732ff);
        param.fontParameters.shadowOffsetY = -1;
        G.game.assets.load("dimbo_purple.ttf", BitmapFont.class, param);

        param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "font/dimbo.ttf";
        param.fontParameters.size = G.style.fontSizeBig;
        param.fontParameters.color = new Color(0x2e5111ff);
        param.fontParameters.shadowColor = new Color(0x262909ff);
        param.fontParameters.shadowOffsetY = -1;
        G.game.assets.load("dimbo_green.ttf", BitmapFont.class, param);

        param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        param.fontFileName = "font/dimbo.ttf";
        param.fontParameters.size = G.style.fontSizeBig;
        param.fontParameters.color = new Color(0x565656ff);
        param.fontParameters.shadowColor = new Color(0x3b2c2cff);
        param.fontParameters.shadowOffsetY = -1;
        G.game.assets.load("dimbo_gray.ttf", BitmapFont.class, param);

        G.game.data = Db.load();
        G.game.input = new InputManager();
        G.game.batch = new SpriteBatch();
        G.game.score = ScoreTable.load();
        G.game.audio = new Audio();

        G.game.input.loadInputs();

        atlasStr = "img/pack.atlas";

        G.game.assets.load(atlasStr, TextureAtlas.class);
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
        G.game.assets.load("audio/lostfall.ogg", Sound.class);
        G.game.assets.load("audio/trash.ogg", Sound.class);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        // Only start loading after the first frame to avoid a white screen blink at the
        // startup. Spite using AssetManager, that load things at a nonblocking method (other
        // thread), it takes a while, sufficient to cause white screen on the game startup.
        if (frameCount == 1) {
            loadStuff();
            G.game.sr = new ShapeRenderer();
        }

        if (frameCount == 2) {
            loadingTexture = new Texture(Gdx.files.internal("img/loading.png"));
        }

        // If loading has started but not finished draw progress bar
        if (frameCount > 2 && !G.game.assets.update()) {
            final int width = (int) (Math.min(0.8f * G.height, 0.8 * G.width));
            final int height = 20;
            final int x = (int) (G.width / 2f - width / 2f);
            final int y = (int) (0.3f * G.height);
            final int progress = (int) (width * G.game.assets.getProgress());

            G.game.batch.begin();
            G.game.batch.draw(loadingTexture, x + (width / 2f) - 36, y + 24f);
            G.game.batch.end();

            G.game.sr.setColor(BAR_COLOR);
            G.game.sr.setProjectionMatrix(cam.combined);
            G.game.sr.begin(ShapeRenderer.ShapeType.Line);
            G.game.sr.rect(x, y, width, height);
            G.game.sr.end();

            G.game.sr.begin(ShapeRenderer.ShapeType.Filled);
            G.game.sr.rect(x + 5, y + 6, progress - 11, height - 11);
            G.game.sr.end();
        }

        // If stuff has done loading, init some vars and go to the next screen
        if (frameCount > 2 && G.game.assets.update()) {
            action = ScreenBase.ACT_NEXT;

            // After loading is done we can create atlas and skin
            G.game.atlas = G.game.assets.get(atlasStr, TextureAtlas.class);

            G.game.skin = new Skin();
            G.game.skin.addRegions(G.game.atlas);

            BitmapFont font;

            font = G.game.assets.get("roboto.ttf", BitmapFont.class);
            G.game.skin.add("roboto", font);

            font = G.game.assets.get("roboto_shadow.ttf", BitmapFont.class);
            G.game.skin.add("roboto_shadow", font);

            font = G.game.assets.get("dimbo_white.ttf", BitmapFont.class);
            G.game.skin.add("dimbo_white", font);

            font = G.game.assets.get("dimbo_gtitle.ttf", BitmapFont.class);
            G.game.skin.add("dimbo_gtitle", font);

            font = G.game.assets.get("dimbo_brown.ttf", BitmapFont.class);
            G.game.skin.add("dimbo_brown", font);

            font = G.game.assets.get("dimbo_dbrown.ttf", BitmapFont.class);
            G.game.skin.add("dimbo_dbrown", font);

            font = G.game.assets.get("dimbo_purple.ttf", BitmapFont.class);
            G.game.skin.add("dimbo_purple", font);

            font = G.game.assets.get("dimbo_green.ttf", BitmapFont.class);
            G.game.skin.add("dimbo_green", font);

            font = G.game.assets.get("dimbo_gray.ttf", BitmapFont.class);
            G.game.skin.add("dimbo_gray", font);

            G.game.skin.load(Gdx.files.internal("img/skin.json"));

            G.game.data.initAfterLoading();

            G.loading = false;
        }

        frameCount++;
    }
}

/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.animation.SpriteAccessor;
import com.vpjardim.colorbeans.core.MapRender;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Linear;

/**
 * @author Vinícius Jardim
 * 06/12/2015
 */
public class MenuScreen extends ScreenBase {

    // Todo finish ribbon and falling beans animation

    public static final int ACT_PLAY     = 10;
    public static final int ACT_TRAINING = 11;
    public static final int ACT_SCORE    = 12;
    public static final int ACT_CONFIG   = 13;

    private Stage stage;
    private Sprite[] beans = new Sprite[10];
    private TweenManager transition;

    public MenuScreen() {
        manageInput = false;
    }

    @Override
    public void show() {

        super.show();

        bgColor = G.game.data.bgColor();

        transition = new TweenManager();
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());

        for(int i = 0; i < beans.length; i++) {
            beans[i] = G.game.atlas.createSprite(MapRender.COLORS.get(MathUtils.random(1, 5)));
            beans[i].setPosition(MathUtils.random(G.width), MathUtils.random(G.height, G.height * 2f));

            Tween.to(beans[i], SpriteAccessor.POSITION, 10).
                    targetRelative(0, - G.game.height * 2f - 128).
                    ease(Linear.INOUT).
                    repeat(Tween.INFINITY, 1).start(transition);
        }

        stage = new Stage(viewport, G.game.batch);
        G.game.input.addProcessor(stage);

        Table outerT = new Table(G.game.skin);
        Table table = new Table(G.game.skin);

        outerT.setFillParent(true);
        table.setBackground("tbg");

        TextButton playButt, trainingButt, scoreButt, optionsButt, exitButt;

        playButt = new TextButton("Play!",
                G.game.skin.get("bttGreen", TextButton.TextButtonStyle.class));
        playButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_PLAY;
            }
        });

        trainingButt = new TextButton("Training",
                G.game.skin.get("bttBlue", TextButton.TextButtonStyle.class));
        trainingButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_TRAINING;
            }
        });

        scoreButt = new TextButton("Score Board",
                G.game.skin.get("bttYellow", TextButton.TextButtonStyle.class));
        scoreButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_SCORE;
            }
        });

        optionsButt = new TextButton("Options...",
                G.game.skin.get("bttGray", TextButton.TextButtonStyle.class));
        optionsButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_CONFIG;
            }
        });

        exitButt = new TextButton("Exit",
                G.game.skin.get("bttRed", TextButton.TextButtonStyle.class));
        exitButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        Label.LabelStyle labelStyle =
                G.game.skin.get("labelGTitle", Label.LabelStyle.class);

        Label label = new Label("Color Beans", labelStyle);
        label.setAlignment(Align.center);

        float bttW = G.style.buttWidth;
        float padS = G.style.padSmall;

        table.defaults().width(bttW).pad(padS);

        outerT.add();
        outerT.add(table);
        outerT.add();

        table.add(label).width(G.style.ribbonWidth).height(G.style.ribbonHeight);
        table.row();
        table.add(playButt);
        table.row();
        table.add(trainingButt);
        table.row();
        table.add(scoreButt);
        table.row();
        table.add(optionsButt);
        table.row();
        table.add(exitButt).width(bttW).pad(G.style.padMedium, padS, padS, padS);

        stage.addActor(outerT);
        table.setDebug(G.game.dbg.uiTable); // #debugCode
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        transition.update(delta);

        G.game.batch.begin();
        for(int i = 0; i < beans.length; i++) {
            beans[i].draw(G.game.batch, 0.15f);
        }
        G.game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        G.game.input.removeProcessor(stage);
        // Only dispose what does not come from game.assets. Do not dispose skin.
        stage.dispose();
    }
}

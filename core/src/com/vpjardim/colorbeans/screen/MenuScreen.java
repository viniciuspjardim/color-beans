/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.defaults.Db;

/**
 * @author Vinícius Jardim
 * 06/12/2015
 */
public class MenuScreen extends ScreenBase {

    public static final int ACT_PLAY = 1;
    public static final int ACT_SCORE = 2;

    private Stage stage;
    private Table table;
    private TextButton playButt, scoreButt, optionsButt, exitButt;

    public MenuScreen() {
        manageInput = false;
    }

    @Override
    public void show() {

        super.show();

        bgColor = Db.bgColor();

        stage = new Stage(viewport);
        G.game.input.addProcessor(stage);

        table = new Table(G.game.skin);
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        playButt = new TextButton("Play!",
                G.game.skin.get("bttGreen", TextButton.TextButtonStyle.class));
        playButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isFinished = true;
                action = ACT_PLAY;
            }
        });

        scoreButt = new TextButton("Score Board",
                G.game.skin.get("bttYellow", TextButton.TextButtonStyle.class));
        scoreButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isFinished = true;
                action = ACT_SCORE;
            }
        });

        optionsButt = new TextButton("Options...",
                G.game.skin.get("bttBlue", TextButton.TextButtonStyle.class));
        optionsButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {}
        });

        exitButt = new TextButton("Exit",
                G.game.skin.get("bttGray", TextButton.TextButtonStyle.class));
        exitButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(playButt).width(250).pad(20);
        table.row();
        table.add(scoreButt).width(250).pad(20);
        table.row();
        table.add(optionsButt).width(250).pad(20);
        table.row();
        table.add(exitButt).width(250).pad(60, 20, 20, 20);

        stage.addActor(table);
        // table.debug(); // #debugCode
    }

    @Override
    public void render(float delta) {
        super.render(delta);
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

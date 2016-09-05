/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.ScoreTable;
import com.vpjardim.colorbeans.defaults.Db;

/**
 * @author Vinícius Jardim
 * 03/09/2016
 */
public class ScoreScreen extends ScreenBase {

    private Stage stage;
    private Table table;
    private TextButton backButt;

    public ScoreScreen() {
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

        Label.LabelStyle labelStyle =
                G.game.skin.get("defLabel", Label.LabelStyle.class);

        int cont = 1;
        for(ScoreTable.Row row : G.game.score.getRows()) {
            table.add(new Label(Integer.toString(cont), labelStyle)).width(60);
            table.add(new Label(row.nick, labelStyle)).width(120);
            table.add(new Label(G.game.intFmt.format(row.score), labelStyle)).width(90);
            table.row().pad(10, 0, 0, 0);
            cont++;
        }

        backButt = new TextButton("Back",
                G.game.skin.get("bttYellow", TextButton.TextButtonStyle.class));
        backButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isFinished = true;
            }
        });

        table.add(backButt).width(200).pad(180, 0, 0, 0);

        stage.addActor(table);
        //table.debug(); // #debugCode
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

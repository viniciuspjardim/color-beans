/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.ScoreTable;

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

        bgColor = G.game.data.bgColor();

        stage = new Stage(viewport, G.game.batch);
        G.game.input.addProcessor(stage);

        table = new Table(G.game.skin);
        table.setFillParent(true);

        Label.LabelStyle labelStyle =
                G.game.skin.get("labelDef", Label.LabelStyle.class);

        float bttW = G.style.buttWidth;
        float padM = G.style.padMedium;

        int cont = 1;
        for(ScoreTable.Row row : G.game.score.getRows()) {
            Label label;

            label = new Label(Integer.toString(cont), labelStyle);
            label.setAlignment(Align.center);
            table.add(label).width(bttW);

            label = new Label(row.nick, labelStyle);
            label.setAlignment(Align.center);
            table.add(label).width(bttW);

            label = new Label(G.game.intFmt.format(row.score), labelStyle);
            label.setAlignment(Align.center);
            table.add(label).width(bttW);

            table.row().pad(padM, 0, 0, 0);
            cont++;
        }

        backButt = new TextButton("Back",
                G.game.skin.get("bttYellow", TextButton.TextButtonStyle.class));
        backButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ScreenBase.ACT_NEXT;
            }
        });

        table.add(backButt).width(bttW).pad(G.style.padVBig, padM, padM, padM);

        stage.addActor(table);
        table.setDebug(G.game.dbg.uiTable); // #debugCode
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

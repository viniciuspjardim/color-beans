/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.ScoreTable;
import com.vpjardim.colorbeans.events.Event;
import com.vpjardim.colorbeans.events.EventHandler;
import com.vpjardim.colorbeans.events.EventListener;

/**
 * @author Vinícius Jardim
 * 2016/09/03
 */
public class ScoreScreen extends ScreenBase {

    // Todo finish score screen

    private Stage stage;
    private Table table;
    private TextButton backButt;
    private EventListener specialKeyDown;

    @Override
    public void show() {

        super.show();

        specialKeyDown = (Event e) -> {
            int key = (Integer) e.getAttribute();
            if(key == G.game.data.escBt || key == Input.Keys.BACK)
                action = ACT_NEXT;
            else if(key == G.game.data.printScreenBt)
                printScreen();
        };

        EventHandler.getHandler().addListener("SpecialButtons.keyDown", specialKeyDown);

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
        EventHandler.getHandler().removeListener("SpecialButtons.keyDown", specialKeyDown);
        // Only dispose what does not come from game.assets. Do not dispose skin.
        stage.dispose();
    }
}

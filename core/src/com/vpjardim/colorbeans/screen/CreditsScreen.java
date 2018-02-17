/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.vpjardim.colorbeans.G;

/**
 * @author Vinícius Jardim
 * 2016/12/13
 */
public class CreditsScreen extends ScreenBase {

    private Stage stage;

    @Override
    public void show() {

        super.show();

        stage = new Stage(viewport, G.game.batch);
        Table table = new Table(G.game.skin);
        table.setFillParent(true);

        Label.LabelStyle labelStyle =
                G.game.skin.get("labelDef", Label.LabelStyle.class);

        Label label = new Label("Game by Vinícius Jardim", labelStyle);
        label.setAlignment(Align.center);
        table.add(label).width(400);

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
    public void buttonEsc(boolean isDown) { action = ScreenBase.ACT_NEXT; }

    @Override
    public void btStartDown() { action = ScreenBase.ACT_NEXT; }

    @Override
    public void bt1Down() { action = ScreenBase.ACT_NEXT; }

    @Override
    public void bt2Down() { action = ScreenBase.ACT_NEXT; }

    @Override
    public void bt3Down() { action = ScreenBase.ACT_NEXT; }

    @Override
    public void bt4Down() { action = ScreenBase.ACT_NEXT; }
}

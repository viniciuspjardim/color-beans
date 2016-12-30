/*
 * Copyright 2016 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.vpjardim.colorbeans.G;

/**
 * @author Vinícius Jardim
 * 13/12/2016
 */
public class CreditsScreen extends ScreenBase {

    private Stage stage;

    @Override
    public void show() {

        super.show();

        stage = new Stage(viewport, G.game.batch);
        Table table = new Table(G.game.skin);
        table.setBounds(0, 0, G.width, G.height);

        Label.LabelStyle labelStyle =
                G.game.skin.get("labelDef", Label.LabelStyle.class);

        Label label = new Label("Game by Vinícius Jardim", labelStyle);
        label.setAlignment(Align.center);
        table.add(label).width(400);

        stage.addActor(table);
        table.setDebug(G.game.debug); // #debugCode
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void button1(boolean isDown) { action = ScreenBase.ACT_NEXT; }

    @Override
    public void button2(boolean isDown) { action = ScreenBase.ACT_NEXT; }

    @Override
    public void button3(boolean isDown) { action = ScreenBase.ACT_NEXT; }

    @Override
    public void button4(boolean isDown) { action = ScreenBase.ACT_NEXT; }

    @Override
    public void buttonStart(boolean isDown) { action = ScreenBase.ACT_NEXT; }
}

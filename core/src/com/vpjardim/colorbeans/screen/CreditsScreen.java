/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.Audio;
import com.vpjardim.colorbeans.events.Event;
import com.vpjardim.colorbeans.events.EventHandler;
import com.vpjardim.colorbeans.events.EventListener;

/**
 * @author Vinícius Jardim
 *         2016/12/13
 */
public class CreditsScreen extends ScreenBase {

    private Stage stage;
    private EventListener specialKeyDown;

    @Override
    public void show() {

        super.show();

        specialKeyDown = (Event e) -> {
            int key = (Integer) e.getAttribute();

            if (G.isBackKey(key)) {
                action = ACT_NEXT;
            }
        };

        EventHandler.getHandler().addListener("SpecialButtons.keyDown", specialKeyDown);

        stage = new Stage(viewport, G.game.batch);
        Table table = new Table(G.game.skin);
        table.setFillParent(true);

        Label.LabelStyle labelStyle = G.game.skin.get("labelDef", Label.LabelStyle.class);

        Label label = new Label("A game by Vinícius Jardim", labelStyle);
        label.setAlignment(Align.center);
        table.add(label).width(400);

        stage.addActor(table);
        table.setDebug(G.game.dbg.uiTable); // #debugCode

        G.game.audio.playMusic(Audio.MUSIC3, true);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        G.game.audio.stopMusic();
    }

    @Override
    public void dispose() {
        super.dispose();
        EventHandler.getHandler().removeListener("SpecialButtons.keyDown", specialKeyDown);
        // Only dispose what does not come from game.assets. Do not dispose skin.
        stage.dispose();
    }

    @Override
    public void btStartDown() {
        action = ScreenBase.ACT_NEXT;
    }

    @Override
    public void bt1Down() {
        action = ScreenBase.ACT_NEXT;
    }

    @Override
    public void bt2Down() {
        action = ScreenBase.ACT_NEXT;
    }

    @Override
    public void bt3Down() {
        action = ScreenBase.ACT_NEXT;
    }

    @Override
    public void bt4Down() {
        action = ScreenBase.ACT_NEXT;
    }
}

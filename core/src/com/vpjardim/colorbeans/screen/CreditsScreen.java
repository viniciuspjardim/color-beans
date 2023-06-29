/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.vpjardim.colorbeans.G;
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
    private Pixmap tableBgPixmap;
    private TextureRegionDrawable tableBg;

    @Override
    public void show() {
        super.show();

        tableBgPixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        tableBgPixmap.setColor(0x00000080);
        tableBgPixmap.fill();
        tableBg = new TextureRegionDrawable(new TextureRegion(new Texture(tableBgPixmap)));

        specialKeyDown = (Event e) -> {
            int key = (Integer) e.getAttribute();

            if (G.isBackKey(key)) {
                action = ACT_NEXT;
            }
        };

        EventHandler.getHandler().addListener("SpecialButtons.keyDown", specialKeyDown);

        stage = new Stage(viewport, G.game.batch);

        Table containerT = new Table(G.game.skin);
        containerT.setFillParent(true);

        Table contentT = new Table(G.game.skin);

        contentT.background(tableBg);
        contentT.pad(32f, 96f, 32f, 96f);

        containerT.add(contentT);

        Label.LabelStyle keyLS = G.game.skin.get("labelWhite50", Label.LabelStyle.class);
        Label.LabelStyle valueRegLS = G.game.skin.get("labelDef", Label.LabelStyle.class);
        Label.LabelStyle valueLS = G.game.skin.get("labelCredits", Label.LabelStyle.class);

        contentT.add(new Label("Code And Art:", keyLS));
        contentT.row();
        contentT.add(new Label("Vinícius Jardim", valueLS)).padTop(8);
        contentT.row();

        contentT.add(new Label("Tests:", keyLS)).padTop(32);
        contentT.row();
        contentT.add(new Label("Anaiara Silva", valueLS)).padTop(8);
        contentT.row();
        contentT.add(new Label("Vinícius Jardim", valueLS)).padTop(8);
        contentT.row();
        contentT.add(new Label("André Jardim", valueLS)).padTop(8);
        contentT.row();

        contentT.add(new Label("Color Beans Is A Clone From:", keyLS)).padTop(32);
        contentT.row();
        contentT.add(new Label("SEGA's Puyo Puyo", valueRegLS)).padTop(8);
        contentT.row();

        contentT.add(new Label("More Info At:", keyLS)).padTop(32);
        contentT.row();
        contentT.add(new Label("github.com/viniciuspjardim/color-beans", valueRegLS)).padTop(8);
        contentT.row();

        contentT.add(new Label("****", keyLS)).padTop(32);
        contentT.row();

        contentT.add(new Label("Thank You For Playing!", valueLS)).padTop(32);
        contentT.row();
        contentT.add(new Label("Press Any Key Or Click To Go Back.", keyLS)).padTop(8);
        contentT.row();

        stage.addActor(containerT);

        // #debugCode
        containerT.setDebug(G.game.dbg.uiTable);
        contentT.setDebug(G.game.dbg.uiTable);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        G.game.beansAnim.update();

        G.game.batch.begin();
        G.game.beansAnim.render();
        G.game.batch.end();

        stage.act(delta);
        stage.draw();

        if (Gdx.input.justTouched()) {
            action = ScreenBase.ACT_NEXT;
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        EventHandler.getHandler().removeListener("SpecialButtons.keyDown", specialKeyDown);
        // Only dispose what does not come from game.assets. Do not dispose skin.
        stage.dispose();
        tableBgPixmap.dispose();
        tableBg.getRegion().getTexture().dispose();
    }

    @Override
    public void keyDown(int key) {
        action = ScreenBase.ACT_NEXT;
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

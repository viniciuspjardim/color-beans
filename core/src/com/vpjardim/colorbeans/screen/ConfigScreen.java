/*
 * Copyright 2017 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.defaults.Db;

/**
 * @author Vinícius Jardim
 * 03/01/2017
 */
public class ConfigScreen extends ScreenBase  {

    // Todo fix fps going from 60 to 30 after changing to windowed mode

    private Stage stage;

    public ConfigScreen() { manageInput = false; }

    @Override
    public void show() {
        super.show();

        bgColor = Db.bgColor();

        stage = new Stage(viewport, G.game.batch);
        G.game.input.addProcessor(stage);

        // ==== Tables ====
        Table titleT   = new Table(G.game.skin);
        Table contentT = new Table(G.game.skin);
        Table tabT     = new Table(G.game.skin);
        Table gameT    = new Table(G.game.skin);
        Table videoT   = new Table(G.game.skin);

        titleT.setFillParent(true);

        // #debugCode
        titleT.setDebug(G.game.dbg.uiTable);
        contentT.setDebug(G.game.dbg.uiTable);
        tabT.setDebug(G.game.dbg.uiTable);
        gameT.setDebug(G.game.dbg.uiTable);
        videoT.setDebug(G.game.dbg.uiTable);

        // ==== Labels ====
        Label.LabelStyle labelStyle =
                G.game.skin.get("labelDef", Label.LabelStyle.class);

        Label titleL = new Label("Options", labelStyle);
        Label gameL  = new Label(Db.bigText, labelStyle);
        Label videoL = new Label("Content 2", labelStyle);

        gameL.setWrap(true);

        // Text fields
        final TextField player1 = new TextField("bla", G.game.skin, "tField");
        final TextField player2 = new TextField("fooo", G.game.skin, "tField");
        final TextField player3 = new TextField("mnist", G.game.skin, "tField");
        final TextField player4 = new TextField("benas", G.game.skin, "tField");

        // ==== Buttons ====
        final TextButton backBtt = new TextButton("Back",
                G.game.skin.get("bttGreen", TextButton.TextButtonStyle.class));
        final TextButton gameButt = new TextButton("Game",
                G.game.skin.get("bttGreen", TextButton.TextButtonStyle.class));
        final TextButton videoButt = new TextButton("Video",
                G.game.skin.get("bttGreen", TextButton.TextButtonStyle.class));

        // ==== Groups ====
        // HorizontalGroup layoutGroup = new HorizontalGroup();
        // layoutGroup.addActor(gameButt);
        // layoutGroup.addActor(videoButt);

        // Let only one tab button be checked at a time
        ButtonGroup logicGroup = new ButtonGroup();
        logicGroup.setMinCheckCount(1);
        logicGroup.setMaxCheckCount(1);
        logicGroup.add(gameButt);
        logicGroup.add(videoButt);

        // ==== Scrolls ====
        final ScrollPane gameScroll = new ScrollPane(gameT);
        final ScrollPane videoScroll = new ScrollPane(videoT);
        gameScroll.setScrollingDisabled(true, false);
        videoScroll.setScrollingDisabled(true, false);

        // ==== Listeners ====
        backBtt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ScreenBase.ACT_NEXT;
            }
        });

        // Listen to changes in the tab button checked states
        // Set visibility of the tab content to match the checked state
        ChangeListener tabListener = new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                gameScroll.setVisible(gameButt.isChecked());
                videoScroll.setVisible(videoButt.isChecked());
            }
        };

        // Put tabs in initial state
        tabListener.changed(null, null);

        gameButt.addListener(tabListener);
        videoButt.addListener(tabListener);

        // ==== Stack ====
        Stack tabs = new Stack();
        tabs.add(videoScroll);
        tabs.add(gameScroll);

        // ==== Align, Pad / widths / heights ====
        float bttW = G.style.buttWidth;
        float padM = G.style.padMedium;

        titleL.setAlignment(Align.center);
        gameL.setAlignment(Align.topLeft);
        videoL.setAlignment(Align.topLeft);

        titleT.pad(padM);
        titleT.defaults().minWidth(bttW);
        contentT.defaults().align(Align.left);
        tabT.defaults().minWidth(bttW);

        // ==== Assembling from outer to inner components ====
        titleT.add(titleL);
        titleT.row();
        titleT.add(contentT).expand().fill();
        titleT.row();
        titleT.add(backBtt);

        contentT.add(tabT);
        contentT.row();
        contentT.add(tabs).expand().fill();

        tabT.add(gameButt);
        tabT.add(videoButt);

        gameT.add(gameL).expand().fill();
        gameT.row();
        gameT.add(player1);
        gameT.row();
        gameT.add(player2);
        gameT.row();
        gameT.add(player3);
        gameT.row();
        gameT.add(player4);
        gameT.row();
        videoT.add(videoL).expand().fill();

        stage.addActor(titleT);
        titleT.setDebug(G.game.dbg.uiTable); // #debugCode
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

/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.Cfg;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.defaults.Db;
import com.vpjardim.colorbeans.events.Event;
import com.vpjardim.colorbeans.events.EventHandler;
import com.vpjardim.colorbeans.events.EventListener;
import com.vpjardim.colorbeans.input.ControllerInput;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.KeyboardInput;
import com.vpjardim.colorbeans.input.Profile;
import com.vpjardim.colorbeans.views.ControllerActor;
import com.vpjardim.colorbeans.views.InputActor;

/**
 * @author Vinícius Jardim
 * 2017/01/03
 */
public class ConfigScreen extends ScreenBase {

    // Todo fix fps going from 60 to 30 after changing to windowed mode
    // Todo finish config screen

    public static final int ACT_MENU       = 10;
    public static final int ACT_NET_INPUT  = 11;

    private Stage stage;

    private TextField player1;
    private TextField player2;
    private TextField player3;
    private TextField player4;

    private EventListener specialKeyDown;

    public ConfigScreen() { manageInput = false; }

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

        // ==== Tables ====
        Table titleT   = new Table(G.game.skin);
        Table contentT = new Table(G.game.skin);
        Table tabT     = new Table(G.game.skin);
        Table gameT    = new Table(G.game.skin);
        Table videoT   = new Table(G.game.skin);
        Table inputT   = new Table(G.game.skin);

        titleT.setFillParent(true);
        titleT.setBackground("tbg");

        // #debugCode
        titleT.setDebug(G.game.dbg.uiTable);
        contentT.setDebug(G.game.dbg.uiTable);
        tabT.setDebug(G.game.dbg.uiTable);
        gameT.setDebug(G.game.dbg.uiTable);
        videoT.setDebug(G.game.dbg.uiTable);
        inputT.setDebug(G.game.dbg.uiTable);

        // ==== Labels ====
        Label.LabelStyle labelStyle =
                G.game.skin.get("labelDef", Label.LabelStyle.class);

        Label gameL  = new Label("Type the players name:", labelStyle);
        Label videoL = new Label("Content 2", labelStyle);
        Label inputL = new Label("Content 3", labelStyle);

        player1 = new TextField("", G.game.skin, "tField");
        player2 = new TextField("", G.game.skin, "tField");
        player3 = new TextField("", G.game.skin, "tField");
        player4 = new TextField("", G.game.skin, "tField");

        final Array<Cfg.Player> pls = G.game.data.players;

        if(pls.size >= 1)
            player1.setText(pls.get(0).name);
        if(pls.size >= 2)
            player2.setText(pls.get(1).name);
        if(pls.size >= 3)
            player3.setText(pls.get(2).name);
        if(pls.size >= 4)
            player4.setText(pls.get(3).name);

        // ==== Buttons ====
        final TextButton backBtt = new TextButton("Back",
                G.game.skin.get("bttYellow", TextButton.TextButtonStyle.class));
        final TextButton gameButt = new TextButton("Game",
                G.game.skin.get("bttRed", TextButton.TextButtonStyle.class));
        final TextButton videoButt = new TextButton("Video",
                G.game.skin.get("bttRed", TextButton.TextButtonStyle.class));
        final TextButton inputButt = new TextButton("Input",
                G.game.skin.get("bttRed", TextButton.TextButtonStyle.class));
        final TextButton netInputBtt = new TextButton("Net Input",
                G.game.skin.get("bttYellow", TextButton.TextButtonStyle.class));

        // Let only one tab button be checked at a time
        ButtonGroup logicGroup = new ButtonGroup();
        logicGroup.setMinCheckCount(1);
        logicGroup.setMaxCheckCount(1);
        logicGroup.add(gameButt);
        logicGroup.add(videoButt);
        logicGroup.add(inputButt);

        // ==== Scrolls ====
        final ScrollPane gameScroll = new ScrollPane(gameT);
        final ScrollPane videoScroll = new ScrollPane(videoT);
        final ScrollPane inputScroll = new ScrollPane(inputT);
        gameScroll.setScrollingDisabled(true, false);
        videoScroll.setScrollingDisabled(true, false);
        inputScroll.setScrollingDisabled(true, false);

        // ==== Listeners ====
        backBtt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_MENU;
            }
        });

        netInputBtt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_NET_INPUT;
            }
        });

        // ==== Stack ====
        final Stack tabs = new Stack();
        tabs.add(inputScroll);
        tabs.add(videoScroll);
        tabs.add(gameScroll);

        // Listen to changes in the tab button checked states
        // Set visibility of the tab content to match the checked state
        final ChangeListener tabListener = new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                TextButton.TextButtonStyle buttOff = G.game.skin.get(
                        "bttRed", TextButton.TextButtonStyle.class);

                TextButton.TextButtonStyle buttOn = G.game.skin.get(
                        "bttGreen", TextButton.TextButtonStyle.class);

                gameScroll.setVisible(gameButt.isChecked());
                videoScroll.setVisible(videoButt.isChecked());
                inputScroll.setVisible(inputButt.isChecked());

                if(gameButt.isChecked()) gameButt.setStyle(buttOn);
                else gameButt.setStyle(buttOff);

                if(videoButt.isChecked()) videoButt.setStyle(buttOn);
                else videoButt.setStyle(buttOff);

                if(inputButt.isChecked()) inputButt.setStyle(buttOn);
                else inputButt.setStyle(buttOff);
            }
        };

        // Put tabs in initial state
        tabListener.changed(null, null);

        gameButt.addListener(tabListener);
        videoButt.addListener(tabListener);
        inputButt.addListener(tabListener);

        // ==== Align, Pad / widths / heights ====
        float bttW = G.style.buttWidth;
        float padM = G.style.padMedium;

        gameL.setAlignment(Align.topLeft);
        videoL.setAlignment(Align.topLeft);
        inputL.setAlignment(Align.topLeft);

        titleT.pad(padM);
        titleT.defaults().minWidth(bttW);
        contentT.defaults().align(Align.left);
        tabT.defaults().minWidth(bttW);

        // ==== Assembling from outer to inner components ====
        titleT.row();
        titleT.add(contentT).expand().fill();
        titleT.row();
        titleT.add(backBtt);

        contentT.add(tabT).align(Align.center);
        contentT.row();
        contentT.add(tabs).expand().fill();

        tabT.add(gameButt);
        tabT.add(videoButt);
        tabT.add(inputButt);

        gameT.add(gameL);
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

        final ControllerActor controllerActor = new ControllerActor();
        inputT.add(controllerActor); //.expand().fill();
        inputT.row();

        inputT.add(netInputBtt);
        inputT.row();

        G.game.input.targetsClear();

        int count = 1;

        // Loop through inputs and show edit keys button for each one
        for(int i = 0; i < G.game.input.getInputs().size; i++) {

            final InputBase input = G.game.input.getInputs().get(i);
            final InputActor inputActor;

            if(input instanceof ControllerInput)
                inputActor = new InputActor(InputActor.CONTROLLER, null);
            else if(input instanceof KeyboardInput)
                inputActor = new InputActor(InputActor.KEYBOARD, input.getProfile());
            else
                continue;

            inputActor.setNumber(count);
            inputT.add(inputActor);
            G.game.input.addTarget(inputActor);

            final TextButton setBtt = new TextButton("Edit keys",
                    G.game.skin.get("bttYellow", TextButton.TextButtonStyle.class));

            setBtt.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controllerActor.setPosition(0);
                    controllerActor.addCallBack(new ControllerActor.KeysSetListener() {

                        @Override
                        public void finished(Profile profile) {
                            input.setProfile(profile);
                            input.setTarget(inputActor);

                            // #debugCode
                            Dbg.dbg(Dbg.tag(ConfigScreen.this), "Key config finished");
                        }
                    });
                    input.setTarget(controllerActor);
                }
            });

            inputT.add(setBtt);

            inputT.row();
            count++;
        }

        G.game.input.linkAll();

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
    public void hide() {

        final Array<Cfg.Player> pls = G.game.data.players;

        pls.clear();

        if(!player1.getText().equals(""))
            pls.add(new Cfg.Player(player1.getText()));
        else
            pls.add(new Cfg.Player("Player"));

        if(!player2.getText().equals(""))
            pls.add(new Cfg.Player(player2.getText()));
        if(!player3.getText().equals(""))
            pls.add(new Cfg.Player(player3.getText()));
        if(!player4.getText().equals(""))
            pls.add(new Cfg.Player(player4.getText()));

        G.game.data.kbProfs.clear();
        G.game.data.ctrlProfs.clear();

        for(int i = 0; i < G.game.input.getInputs().size; i++) {

            final InputBase input = G.game.input.getInputs().get(i);

            if(input instanceof ControllerInput)
                G.game.data.ctrlProfs.add(input.getProfile());
            else if(input instanceof KeyboardInput)
                G.game.data.kbProfs.add(input.getProfile());
        }

        Db.save(G.game.data);
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

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
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
import com.vpjardim.colorbeans.input.TouchInput;
import com.vpjardim.colorbeans.views.ControllerActor;
import com.vpjardim.colorbeans.views.InputActor;

public class ConfigScreen extends ScreenBase {
    public static final int ACT_MENU = 10;
    public static final int ACT_CREDITS = 11;
    public static final int DBG_TO_ACTIVATE = 8;
    public static final int DBG_ACTIVATED = 9;
    public static final int PAD_X = 38;
    public static final int PAD_SLIDER_X = 34;

    // ==== Tables ====
    Table outerT = new Table(G.game.skin);
    Table titleT = new Table(G.game.skin);
    Table contentT = new Table(G.game.skin);
    Table tabT = new Table(G.game.skin);
    Table gameT = new Table(G.game.skin);
    Table inputT = new Table(G.game.skin);
    Table otherT = new Table(G.game.skin);
    // private Table inputT;

    private boolean dirtInputT = false;
    private TextField player1;
    private TextField player2;
    private TextField player3;
    private TextField player4;

    protected EventListener controllerEvent;

    public ConfigScreen() {
        manageInput = false;
    }

    @Override
    public void show() {
        super.show();

        specialKeyDown = (Event e) -> {
            int key = (Integer) e.getAttribute();

            if (G.isBackKey(key)) {
                action = ACT_MENU;
            }
        };

        controllerEvent = (Event e) -> dirtInputT = true;

        EventHandler.get().addListener("SpecialButtons.keyDown", specialKeyDown);
        EventHandler.get().addListener("ControllerConnection.event", controllerEvent);


        outerT.setFillParent(true);
        titleT.setBackground("bgYellow");

        outerT.setDebug(G.game.dbg.uiTable);
        titleT.setDebug(G.game.dbg.uiTable);
        contentT.setDebug(G.game.dbg.uiTable);
        tabT.setDebug(G.game.dbg.uiTable);
        gameT.setDebug(G.game.dbg.uiTable);
        inputT.setDebug(G.game.dbg.uiTable);
        otherT.setDebug(G.game.dbg.uiTable);

        // ==== Labels ====
        Label.LabelStyle labelStyle = G.game.skin.get("robotoMenu", Label.LabelStyle.class);
        final Array<Cfg.Player> pls = G.game.data.players;

        player1 = new TextField("", G.game.skin, "tField");
        player2 = new TextField("", G.game.skin, "tField");
        player3 = new TextField("", G.game.skin, "tField");
        player4 = new TextField("", G.game.skin, "tField");

        Slider coopS = new Slider(0f, 1f, 1f, false, G.game.skin, "checkBox");
        coopS.setValue(G.game.data.coopCampaign ? 1f : 0f);
        coopS.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                G.game.data.coopCampaign = coopS.getValue() == 1f;
                if (G.game.data.coopCampaign && player1.getText().equals("")) {
                    player1.setText("Player");
                }
                if (G.game.data.coopCampaign && player2.getText().equals("")) {
                    player2.setText("Player2");
                }

                // Activate debug menu if coop slider is switched DBG_TO_ACTIVATE times.
                // Note: this function is called twice when the value of the slider change.
                if (G.game.dbg.tapsCount < DBG_TO_ACTIVATE) {
                    G.game.dbg.tapsCount++;
                }
                if (G.game.dbg.tapsCount == DBG_TO_ACTIVATE) {
                    addDebugFields(otherT, labelStyle);
                }
                Dbg.inf("debug tapsCount", G.game.dbg.tapsCount + "");
            }
        });

        player2.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (G.game.data.coopCampaign && player2.getText().equals("")) {
                    G.game.data.coopCampaign = false;
                    coopS.setValue(0f);
                }
            }
        });

        Slider difficultyS = new Slider(0f, 4f, 1f, false, G.game.skin, "slider");
        difficultyS.setValue(G.game.data.difficulty);
        Label difficultyL = new Label(G.game.data.difficultyNames[G.game.data.difficulty], labelStyle);
        difficultyS.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                G.game.data.difficulty = (int) difficultyS.getValue();
                difficultyL.setText(G.game.data.difficultyNames[G.game.data.difficulty]);
            }
        });

        Slider currentStageS = new Slider(0f, 11f, 1f, false, G.game.skin, "slider");
        currentStageS.setValue(G.game.data.campaignCurrentStage);
        Label currentStageL = new Label(G.game.data.stageNames[G.game.data.campaignCurrentStage], labelStyle);
        currentStageS.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                G.game.data.campaignCurrentStage = (int) currentStageS.getValue();
                currentStageL.setText(G.game.data.stageNames[G.game.data.campaignCurrentStage]);
            }
        });

        Slider trainingSpeedS = new Slider(0f, 11f, 1f, false, G.game.skin, "slider");
        trainingSpeedS.setValue(G.game.data.trainingSpeed);
        Label trainingSpeedL = new Label(Integer.toString(G.game.data.trainingSpeed + 1), labelStyle);
        trainingSpeedS.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                G.game.data.trainingSpeed = (int) trainingSpeedS.getValue();
                trainingSpeedL.setText(G.game.data.trainingSpeed + 1);
            }
        });

        Slider musicVolumeS = new Slider(0f, 100f, 5f, false, G.game.skin, "slider");
        musicVolumeS.setValue(G.game.data.musicVolume * 100f);
        Label musicVolumeL = new Label(Integer.toString((int) musicVolumeS.getValue()), labelStyle);
        musicVolumeS.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                G.game.data.musicVolume = musicVolumeS.getValue() / 100f;
                musicVolumeL.setText(Integer.toString((int) musicVolumeS.getValue()));
            }
        });

        Slider effectsVolumeS = new Slider(0f, 100f, 5f, false, G.game.skin, "slider");
        effectsVolumeS.setValue(G.game.data.effectsVolume * 100f);
        Label effectsVolumeL = new Label(Integer.toString((int) effectsVolumeS.getValue()), labelStyle);
        effectsVolumeS.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                G.game.data.effectsVolume = effectsVolumeS.getValue() / 100f;
                effectsVolumeL.setText(Integer.toString((int) effectsVolumeS.getValue()));
            }
        });

        Label versionL = new Label("Version 0.1.10", labelStyle);
        versionL.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_CREDITS;
            }
        });

        if (pls.size >= 1)
            player1.setText(pls.get(0).name);
        if (pls.size >= 2)
            player2.setText(pls.get(1).name);
        if (pls.size >= 3)
            player3.setText(pls.get(2).name);
        if (pls.size >= 4)
            player4.setText(pls.get(3).name);

        // ==== Buttons ====
        final TextButton backBtt = new TextButton("Back",
                G.game.skin.get("bttYellow", TextButton.TextButtonStyle.class));

        final TextButton gameButt = new TextButton("Game",
                G.game.skin.get("bttRed", TextButton.TextButtonStyle.class));

        final TextButton inputButt = new TextButton("Input",
                G.game.skin.get("bttRed", TextButton.TextButtonStyle.class));

        final TextButton otherButt = new TextButton("Other",
                G.game.skin.get("bttRed", TextButton.TextButtonStyle.class));

        // Let only one tab button be checked at a time
        ButtonGroup<TextButton> logicGroup = new ButtonGroup<>();
        logicGroup.setMinCheckCount(1);
        logicGroup.setMaxCheckCount(1);

        logicGroup.add(gameButt);
        logicGroup.add(inputButt);
        logicGroup.add(otherButt);

        // ==== Scrolls ====
        final ScrollPane gameScroll = new ScrollPane(gameT);
        final ScrollPane inputScroll = new ScrollPane(inputT);
        final ScrollPane otherScroll = new ScrollPane(otherT);

        gameScroll.setScrollingDisabled(true, false);
        inputScroll.setScrollingDisabled(true, false);
        otherScroll.setScrollingDisabled(true, false);

        // ==== Listeners ====
        backBtt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ACT_MENU;
            }
        });

        // ==== Stack ====
        final Stack tabs = new Stack();
        tabs.add(inputScroll);
        tabs.add(gameScroll);
        tabs.add(otherScroll);

        // Listen to changes in the tab button checked states
        // Set visibility of the tab content to match the checked state
        final ChangeListener tabListener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextButton.TextButtonStyle buttOff = G.game.skin.get(
                        "bttRed", TextButton.TextButtonStyle.class);

                TextButton.TextButtonStyle buttOn = G.game.skin.get(
                        "bttGreen", TextButton.TextButtonStyle.class);

                updatePlayers();
                dirtInputT = true;

                gameScroll.setVisible(gameButt.isChecked());
                inputScroll.setVisible(inputButt.isChecked());
                otherScroll.setVisible(otherButt.isChecked());

                actorsIndex.clearIndex();

                if (gameButt.isChecked()) {
                    gameButt.setStyle(buttOn);
                    actorsIndex.buildIndex(outerT, titleT, contentT, tabT, gameT);
                } else {
                    gameButt.setStyle(buttOff);
                }

                if (inputButt.isChecked()) {
                    inputButt.setStyle(buttOn);
                    actorsIndex.buildIndex(outerT, titleT, contentT, tabT, inputT);
                } else {
                    inputButt.setStyle(buttOff);
                }

                if (otherButt.isChecked()) {
                    otherButt.setStyle(buttOn);
                    actorsIndex.buildIndex(outerT, titleT, contentT, tabT, otherT);
                } else {
                    otherButt.setStyle(buttOff);
                }
            }
        };

        // Put tabs in initial state
        tabListener.changed(null, null);

        gameButt.addListener(tabListener);
        inputButt.addListener(tabListener);
        otherButt.addListener(tabListener);

        // ==== Align, Pad / widths / heights ====
        float bttW = G.style.buttWidth;
        float padM = G.style.padMedium;

        titleT.pad(padM);
        contentT.defaults().align(Align.left);

        // ==== Assembling from outer to inner components ====
        titleT.add(contentT).expand().fill().row();
        titleT.add(backBtt).width(bttW);

        contentT.add(tabT).align(Align.center).row();
        contentT.add(tabs).expand().fill();

        float bttMinWidth = G.style.buttWidth * 0.5f;

        tabT.add(gameButt).minWidth(bttMinWidth);
        tabT.add(inputButt).minWidth(bttMinWidth);
        tabT.add(otherButt).minWidth(bttMinWidth);

        gameT.add(new Label("Players:", labelStyle)).padTop(8).padLeft(PAD_X).align(Align.left).row();
        gameT.add(player1).expandX().fill().pad(0, PAD_X, 0, PAD_X).colspan(2).row();
        gameT.add(player2).expandX().fill().pad(0, PAD_X, 0, PAD_X).colspan(2).row();
        gameT.add(player3).expandX().fill().pad(0, PAD_X, 0, PAD_X).colspan(2).row();
        gameT.add(player4).expandX().fill().pad(0, PAD_X, 0, PAD_X).colspan(2).row();

        gameT.add(new Label("Co-Op Campaign (2 Players):", labelStyle)).padTop(24).padLeft(PAD_X).align(Align.left);
        gameT.add(coopS).width(92).padTop(24).padRight(PAD_X).align(Align.right).row();

        gameT.add(new Label("Difficulty:", labelStyle)).padTop(24).padLeft(PAD_X).align(Align.left);
        gameT.add(difficultyL).padTop(24).padRight(PAD_X).align(Align.right).row();
        gameT.add(difficultyS).expandX().fill().pad(4, PAD_SLIDER_X, 0, PAD_SLIDER_X).colspan(2).row();

        gameT.add(new Label("Current Stage:", labelStyle)).padTop(24).padLeft(PAD_X).align(Align.left);
        gameT.add(currentStageL).padTop(24).padRight(PAD_X).align(Align.right).row();
        gameT.add(currentStageS).expandX().fill().pad(4, PAD_SLIDER_X, 0, PAD_SLIDER_X).colspan(2).row();

        gameT.add(new Label("Training Speed:", labelStyle)).padTop(24).padLeft(PAD_X).align(Align.left);
        gameT.add(trainingSpeedL).padTop(24).padRight(PAD_X).align(Align.right).row();
        gameT.add(trainingSpeedS).expandX().fill().pad(4, PAD_SLIDER_X, 0, PAD_SLIDER_X).colspan(2).row();

        otherT.add(new Label("Music Volume:", labelStyle)).padTop(24).padLeft(PAD_X).align(Align.left);
        otherT.add(musicVolumeL).padTop(24).padRight(PAD_X).align(Align.right).row();
        otherT.add(musicVolumeS).expandX().fill().pad(4, PAD_SLIDER_X, 0, PAD_SLIDER_X).colspan(2).row();

        otherT.add(new Label("Effects Volume:", labelStyle)).padTop(24).padLeft(PAD_X).align(Align.left);
        otherT.add(effectsVolumeL).padTop(24).padRight(PAD_X).align(Align.right).row();
        otherT.add(effectsVolumeS).expandX().fill().pad(4, PAD_SLIDER_X, 0, PAD_SLIDER_X).colspan(2).row();

        otherT.add(versionL).pad(36, PAD_X, 0, PAD_X).colspan(2).row();

        if (G.game.dbg.tapsCount == DBG_ACTIVATED) {
            addDebugFields(otherT, labelStyle);
        }

        inputLoop();

        float width;
        float screenRatio = (float) G.width / (float) G.height;

        // Ultra widescreen
        if (screenRatio > 1.7778f) {
            width = G.width * 0.24f;
        // Widescreen
        } else if (screenRatio > 1.0f) {
            width = G.width * 0.34f;
        // Mobile or vertical tablet
        } else {
            width = G.width * 0.9f;
        }

        outerT.add();
        outerT.add(titleT).width(width).minHeight(G.height * 0.8f).maxHeight(G.height * 0.9f);
        outerT.add();

        stage.addActor(outerT);
        titleT.setDebug(G.game.dbg.uiTable);
        stage.act();
        actorsIndex.buildIndex(outerT, titleT, contentT, tabT, gameT);
    }

    private void inputLoop() {
        dirtInputT = false;
        inputT.clearChildren();
        final Array<Cfg.Player> pls = G.game.data.players;

        G.game.input.targetsClear();

        final ControllerActor controllerActor = new ControllerActor();
        inputT.add(controllerActor).colspan(4).align(Align.center).row();

        // Loop through inputs and show edit keys button for each one
        for (int i = 0; i < G.game.input.getInputs().size; i++) {
            final int index = i;
            final InputBase input = G.game.input.getInputs().get(i);
            final InputActor inputActor;

            final Label.LabelStyle labelStyle = G.game.skin.get("robotoMenu", Label.LabelStyle.class);
            final Label keyMapL = new Label("0", labelStyle);

            if (i < pls.size) {
                keyMapL.setText(pls.get(i).name);
            }

            if (input instanceof ControllerInput) {
                inputActor = new InputActor(InputActor.CONTROLLER, null);
            } else if (input instanceof KeyboardInput) {
                inputActor = new InputActor(InputActor.KEYBOARD, input.getProfile());
                keyMapL.setText((i < pls.size ? pls.get(i).name + " - " : "") + Profile.keyboardKeyNames(input.getProfile()));
            } else if (input instanceof TouchInput) {
                inputActor = new InputActor(InputActor.TOUCH, null);
            } else {
                inputActor = new InputActor(InputActor.CONTROLLER, null);
            }

            Dbg.inf(Dbg.tagO(this), input.getId() + "");
            inputActor.setNumber(input.getId());

            final TextButton editBtt = new TextButton("Edit",
                    G.game.skin.get("bttYellow", TextButton.TextButtonStyle.class));

            final TextButton upBtt = new TextButton("Up",
                    G.game.skin.get("bttYellow", TextButton.TextButtonStyle.class));

            final TextButton downBtt = new TextButton("Down",
                    G.game.skin.get("bttYellow", TextButton.TextButtonStyle.class));

            if (i == 0) {
                upBtt.setTouchable(Touchable.disabled);
            }
            if (i == G.game.input.getInputs().size - 1) {
                downBtt.setTouchable(Touchable.disabled);
            }

            G.game.input.addTarget(inputActor);

            inputT.add(inputActor).padTop(20);
            inputT.add(editBtt).padTop(20);
            inputT.add(upBtt).padTop(20);
            inputT.add(downBtt).padTop(20).row();

            if (!keyMapL.textEquals("0")) {
                inputT.add(keyMapL).colspan(4).align(Align.left).row();
            }

            editBtt.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controllerActor.setPosition(0);
                    controllerActor.addCallBack(profile -> {
                        input.setProfile(profile);
                        input.setTarget(inputActor);
                        dirtInputT = true;

                        Dbg.dbg(Dbg.tag(ConfigScreen.this), "Key config finished");
                        profile.log();
                    });
                    input.setTarget(controllerActor);
                }
            });

            upBtt.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    G.game.input.moveInput(index, -1);
                    dirtInputT = true;
                }
            });

            downBtt.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    G.game.input.moveInput(index, 1);
                    dirtInputT = true;
                }
            });
        }

        G.game.input.linkAll();
    }

    private static String formatDelta(float delta) {
        if (delta == 0) {
            return "Off";
        }

        final String deltaStr = Float.toString(delta);

        if (deltaStr.length() > 3) {
            return deltaStr.substring(0, 3) + "X";
        }

        return deltaStr + "X";
    }

    public void addDebugFields(Table otherT, Label.LabelStyle labelStyle) {
        // Activate debug options if coop slider is switched 4 times
        // Note: the number 8 is because the event on the coopS is firing twice each time.
        G.game.dbg.tapsCount = DBG_ACTIVATED;

        Slider fpsS = new Slider(0f, 1f, 1f, false, G.game.skin, "checkBox");
        fpsS.setValue(G.game.dbg.fps ? 1f : 0f);
        fpsS.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                G.game.dbg.fps = fpsS.getValue() == 1f;
            }
        });

        Slider debugS = new Slider(0f, 1f, 1f, false, G.game.skin, "checkBox");
        debugS.setValue(G.game.dbg.on ? 1f : 0f);
        debugS.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                G.game.dbg.on = debugS.getValue() == 1f;
            }
        });

        Slider uiS = new Slider(0f, 1f, 1f, false, G.game.skin, "checkBox");
        uiS.setValue(G.game.dbg.uiTable ? 1f : 0f);
        uiS.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                G.game.dbg.uiTable = uiS.getValue() == 1f;
            }
        });

        Slider overlayS = new Slider(0f, 1f, 1f, false, G.game.skin, "checkBox");
        overlayS.setValue(G.game.dbg.overlay ? 1f : 0f);
        overlayS.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                G.game.dbg.overlay = overlayS.getValue() == 1f;
            }
        });

        Slider logLevelS = new Slider(0f, 3f, 1f, false, G.game.skin, "slider");
        logLevelS.setValue((float)G.game.dbg.logLevel);
        Label logLevelL = new Label(Integer.toString((int) logLevelS.getValue()), labelStyle);
        logLevelS.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                G.game.dbg.setLogLevel((int) logLevelS.getValue());
                logLevelL.setText(G.game.dbg.logLevel);
            }
        });

        Slider deltaS = new Slider(0f, 8f, 0.1f, false, G.game.skin, "slider");
        deltaS.setValue(G.game.dbg.delta);
        Label deltaL = new Label(formatDelta(deltaS.getValue()), labelStyle);
        deltaS.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                G.game.dbg.delta = deltaS.getValue();
                deltaL.setText(formatDelta(deltaS.getValue()));
            }
        });

        Label commandL = new Label("Empty", labelStyle);
        TextField commandTF = new TextField("", G.game.skin, "tField");
        TextButton commandBT = new TextButton("Ok",
                G.game.skin.get("bttGreen", TextButton.TextButtonStyle.class));
        commandBT.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String result = G.game.dbg.parseCommand(commandTF.getText());
                commandL.setText(result);
            }
        });

        otherT.add(new Label("**** Debug Options ****", labelStyle)).pad(28, PAD_X, 0, PAD_X).align(Align.center).colspan(2).row();

        otherT.add(new Label("Display FPS:", labelStyle)).padTop(24).padLeft(PAD_X).align(Align.left);
        otherT.add(fpsS).width(92).padTop(24).padRight(PAD_X).align(Align.right).row();

        otherT.add(new Label("Debug Beans:", labelStyle)).padTop(8).padLeft(PAD_X).align(Align.left);
        otherT.add(debugS).width(92).padTop(8).padRight(PAD_X).align(Align.right).row();

        otherT.add(new Label("Debug UI:", labelStyle)).padTop(8).padLeft(PAD_X).align(Align.left);
        otherT.add(uiS).width(92).padTop(8).padRight(PAD_X).align(Align.right).row();

        otherT.add(new Label("Debug Overlay:", labelStyle)).padTop(8).padLeft(PAD_X).align(Align.left);
        otherT.add(overlayS).width(92).padTop(8).padRight(PAD_X).align(Align.right).row();

        otherT.add(new Label("Log Level:", labelStyle)).padTop(16).padLeft(PAD_X).align(Align.left);
        otherT.add(logLevelL).padTop(16).padRight(PAD_X).align(Align.right).row();
        otherT.add(logLevelS).expandX().fill().pad(4, PAD_SLIDER_X, 0, PAD_SLIDER_X).colspan(2).row();

        otherT.add(new Label("Frame Time:", labelStyle)).padTop(16).padLeft(PAD_X).align(Align.left);
        otherT.add(deltaL).padTop(16).padRight(PAD_X).align(Align.right).row();
        otherT.add(deltaS).expandX().fill().pad(4, PAD_SLIDER_X, 0, PAD_SLIDER_X).colspan(2).row();

        otherT.add(new Label("Command (key=v1,v2,v3,v4):", labelStyle)).padTop(16).padLeft(PAD_X).align(Align.left);
        otherT.add(commandL).padTop(16).padRight(PAD_X).align(Align.right).row();
        otherT.add(commandTF).expandX().fillX().pad(4, PAD_X, 0, 0);
        otherT.add(commandBT).pad(4, 8, 0, 0);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        G.game.batch.begin();
        G.game.bgBeans.render();
        G.game.batch.end();

        if (dirtInputT) {
            inputLoop();
        }
        stage.act(delta);
        stage.draw();

        G.game.batch.begin();
        actorsIndex.render();

        /* float padM = G.style.padMedium;

        // Tab buttons
        for (Cell row : tabT.getCells()) {
            if (!(row.getActor() instanceof TextButton)) {
                continue;
            }

            // float rowX = row.getActorX() + titleT.getX() + tabT.getX() + contentT.getX() - (2f * padM);
            // float rowY = row.getActorY() + titleT.getY() + tabT.getY() + contentT.getY() + (row.getActor().getHeight() / 2f) - (padM / 2f);
            Actor a = row.getActor();
            float rowX = 0f;
            float rowY = 0f;
            while (a != null) {
                rowX += a.getX();
                rowY += a.getY();
                a = a.getParent();
            }

            G.game.batch.draw(G.game.atlas.findRegion("game/number_bg"), rowX, rowY, padM, padM);
        } */

        G.game.batch.end();
    }

    public void updatePlayers() {
        final Array<Cfg.Player> pls = G.game.data.players;
        pls.clear();

        if (!player1.getText().equals(""))
            pls.add(new Cfg.Player(player1.getText()));
        else
            pls.add(new Cfg.Player("Player"));

        if (!player2.getText().equals(""))
            pls.add(new Cfg.Player(player2.getText()));
        if (!player3.getText().equals(""))
            pls.add(new Cfg.Player(player3.getText()));
        if (!player4.getText().equals(""))
            pls.add(new Cfg.Player(player4.getText()));

        if (stage != null) {
            stage.unfocusAll();
        }

        player1.setText("");
        player2.setText("");
        player3.setText("");
        player4.setText("");

        if (pls.size >= 1)
            player1.setText(pls.get(0).name);
        if (pls.size >= 2)
            player2.setText(pls.get(1).name);
        if (pls.size >= 3)
            player3.setText(pls.get(2).name);
        if (pls.size >= 4)
            player4.setText(pls.get(3).name);
    }

    @Override
    public void hide() {
        updatePlayers();

        int controllerCount = 0;
        int keyboardCount = 0;

        for (int i = 0; i < G.game.input.getInputs().size; i++) {
            final InputBase input = G.game.input.getInputs().get(i);

            if (input instanceof ControllerInput) {
                // If exists replace, otherwise add it
                if (G.game.data.ctrlProfs.size > controllerCount)
                    G.game.data.ctrlProfs.set(controllerCount, input.getProfile());
                else
                    G.game.data.ctrlProfs.add(input.getProfile());

                controllerCount++;
            } else if (input instanceof KeyboardInput) {
                // If exists replace, otherwise add it
                if (G.game.data.kbProfs.size > keyboardCount)
                    G.game.data.kbProfs.set(keyboardCount, input.getProfile());
                else
                    G.game.data.kbProfs.add(input.getProfile());

                keyboardCount++;
            }
        }

        Db.save(G.game.data);
    }

    @Override
    public void dispose() {
        super.dispose();

        EventHandler.get().removeListener("ControllerConnection.event", controllerEvent);
        controllerEvent = null;
    }
}

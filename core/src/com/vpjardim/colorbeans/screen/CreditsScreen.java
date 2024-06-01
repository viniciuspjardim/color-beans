package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.events.Event;
import com.vpjardim.colorbeans.events.EventHandler;
import com.vpjardim.colorbeans.events.EventListener;

public class CreditsScreen extends ScreenBase {
    private EventListener tapInput;

    @Override
    public void show() {
        super.show();

        specialKeyDown = (Event e) -> {
            int key = (Integer) e.getAttribute();

            if (G.isBackKey(key)) {
                action = ACT_NEXT;
            }
        };

        EventHandler.get().addListener("SpecialButtons.keyDown", specialKeyDown);

        Table containerT = new Table(G.game.skin);
        containerT.setFillParent(true);

        Table contentT = new Table(G.game.skin);

        contentT.background("bgBlack");
        contentT.pad(32f, 96f, 32f, 96f);

        containerT.add(contentT);

        // Tap event
        tapInput = (Event e) -> containerT.setVisible(!containerT.isVisible());
        EventHandler.get().addListener("TapInput.tap", tapInput);

        Label.LabelStyle keyLS = G.game.skin.get("labelWhite50", Label.LabelStyle.class);
        Label.LabelStyle valueRegLS = G.game.skin.get("labelDef", Label.LabelStyle.class);
        Label.LabelStyle valueLS = G.game.skin.get("labelCredits", Label.LabelStyle.class);

        Label repositoryL = new Label("github.com/viniciuspjardim/color-beans", valueRegLS);
        repositoryL.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                containerT.setVisible(true);
                Gdx.net.openURI("https://github.com/viniciuspjardim/color-beans");
            }
        });


        final TextButton backBtt = new TextButton("Back",
                G.game.skin.get("bttYellow", TextButton.TextButtonStyle.class));
        backBtt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ScreenBase.ACT_NEXT;
            }
        });

        contentT.add(new Label("Code, art and tests:", keyLS)).row();
        contentT.add(new Label("Vinícius Jardim", valueLS)).padTop(8).row();

        contentT.add(new Label("More tests:", keyLS)).padTop(32).row();
        contentT.add(new Label("Anaiara Silva", valueLS)).padTop(8).row();
        contentT.add(new Label("André Jardim", valueLS)).padTop(8).row();

        contentT.add(new Label("Color Beans is a clone from:", keyLS)).padTop(32).row();
        contentT.add(new Label("SEGA's Puyo Puyo", valueRegLS)).padTop(8).row();

        contentT.add(new Label("More info at:", keyLS)).padTop(32).row();
        contentT.add(repositoryL).padTop(8).row();

        contentT.add(new Label("****", keyLS)).padTop(32).row();

        contentT.add(new Label("Thanks for playing!", valueLS)).padTop(32).row();
        contentT.add(backBtt).width(G.style.buttWidth).padTop(48);

        stage.addActor(containerT);

        containerT.setDebug(G.game.dbg.uiTable);
        contentT.setDebug(G.game.dbg.uiTable);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        G.game.batch.begin();
        G.game.bgBeans.render();
        G.game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        EventHandler.get().removeListener("TapInput.tap", tapInput);
    }
}

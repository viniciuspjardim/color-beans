package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.events.Event;
import com.vpjardim.colorbeans.events.EventHandler;

public class CreditsScreen extends ScreenBase {
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

        Label.LabelStyle keyLS = G.game.skin.get("labelWhite50", Label.LabelStyle.class);
        Label.LabelStyle valueRegLS = G.game.skin.get("labelDef", Label.LabelStyle.class);
        Label.LabelStyle valueLS = G.game.skin.get("labelCredits", Label.LabelStyle.class);

        Label repositoryL = new Label("github.com/viniciuspjardim/color-beans", valueRegLS);
        repositoryL.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.net.openURI("https://github.com/viniciuspjardim/color-beans");
            }
        });

        Label backL = new Label("** Press any key or click to go back **", keyLS);
        backL.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action = ScreenBase.ACT_NEXT;
            }
        });

        contentT.add(new Label("Code and art:", keyLS));
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

        contentT.add(new Label("Color Beans is a clone from:", keyLS)).padTop(32);
        contentT.row();
        contentT.add(new Label("SEGA's Puyo Puyo", valueRegLS)).padTop(8);
        contentT.row();

        contentT.add(new Label("More info at:", keyLS)).padTop(32);
        contentT.row();
        contentT.add(repositoryL).padTop(8);
        contentT.row();

        contentT.add(new Label("****", keyLS)).padTop(32);
        contentT.row();

        contentT.add(new Label("Thank You For Playing!", valueLS)).padTop(32);
        contentT.row();
        contentT.add(backL).padTop(8);
        contentT.row();

        stage.addActor(containerT);

        // #debugCode
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

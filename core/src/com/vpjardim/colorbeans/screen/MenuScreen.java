package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * @author Vinícius Jardim
 * 06/12/2015
 */
public class MenuScreen extends ScreenBase {

    private Stage stage;
    private Skin skin;
    private Table table;
    private TextButton playButt, optionsButt, exitButt;
    private BitmapFont font;

    public MenuScreen() {
        super();
        manageInput = false;
    }

    @Override
    public void show() {

        super.show();

        stage = new Stage(viewport);
        game.input.addProcessor(stage);

        skin = new Skin(game.atlas);
        table = new Table(skin);
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        font = game.assets.get("roboto_24.ttf", BitmapFont.class);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = skin.getDrawable("game/b_yellow");
        buttonStyle.down = skin.getDrawable("game/b_yellow");
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.BLACK;

        playButt = new TextButton("PLAY", buttonStyle);
        playButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isFinished = true;
                // Todo bad idea, need to tell for what screen should change

                // #debugCode
                Gdx.app.log(MenuScreen.class.getSimpleName(), "Play button touch");
            }
        });

        optionsButt = new TextButton("OPTIONS", buttonStyle);
        optionsButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {}
        });

        exitButt = new TextButton("EXIT", buttonStyle);
        exitButt.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();

                // #debugCode
                Gdx.app.debug(this.getClass().getSimpleName(), "Exit button touch");
            }
        });

        table.add(playButt).width(250).pad(20);
        table.row();
        table.add(optionsButt).width(250).pad(20);
        table.row();
        table.add(exitButt).width(250).pad(60, 20, 20, 20);

        stage.addActor(table);
        table.debug(); // #debugCode
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        game.input.removeProcessor(stage);
        // Only dispose what does not come from game.assets. Do not dispose skin.
        stage.dispose();
    }
}

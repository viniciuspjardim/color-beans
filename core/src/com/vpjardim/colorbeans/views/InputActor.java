/*
 * Copyright 2017 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.Profile;
import com.vpjardim.colorbeans.input.TargetBase;

/**
 * @author Vinícius Jardim
 * 2017/05/08
 */

public class InputActor extends Actor implements TargetBase {
    public static final int CONTROLLER = 1;
    public static final int KEYBOARD   = 2;

    private Array<TextureAtlas.AtlasRegion> bodies;
    private TextureAtlas.AtlasRegion body;
    private TextureAtlas.AtlasRegion numberBg;
    private Profile profile;
    private int number;
    private float time = 0f;

    public InputActor(int type, Profile profile) {

        if(type == CONTROLLER)
            bodies = G.game.atlas.findRegions("game/controller_small");
        else if (type == KEYBOARD)
            bodies = G.game.atlas.findRegions("game/keyboard_small");
        else
            throw new IllegalArgumentException("invalid type");

        numberBg = G.game.atlas.findRegion("game/number_bg");
        this.profile = profile;

        body = bodies.first();
        setSize(body.originalWidth, body.originalHeight);
        number = 0;
        time = 0f;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public void draw(Batch batch, float alpha) {

        float x = getX();
        float y = getY();
        float width = body.originalWidth;
        float height = body.originalHeight;

        batch.draw(body, x, y, width, height);

        if(number > 0) {

            float scaleX = width / body.originalWidth;
            float scaleY = height / body.originalHeight;

            float offsetX = (body.originalWidth - numberBg.packedWidth) * scaleX;
            float offsetY = (body.originalHeight - numberBg.packedHeight) * scaleY;

            // Todo why offset in the draw method not working?
            batch.draw(numberBg, x + offsetX, y + offsetY, 0, 0, numberBg.packedWidth,
                    numberBg.packedHeight, scaleX, scaleY, 0);

            BitmapFont font = G.game.assets.get("roboto.ttf", BitmapFont.class);

            font.draw(batch, Integer.toString(number), x + offsetX + 10 * scaleX,
                    y + offsetY + 22 * scaleY);
        }

        if(time > 0f)
            time -= G.delta;
        else {
            body = bodies.first();
            time = 0f;
        }
    }

    @Override
    public void setInput(InputBase input) {}

    @Override
    public void keyPressed(int key) {

        if(profile == null || profile.hasKey(key)) {
            body = bodies.get(1);
            time = 0.25f;
        }
    }

    @Override
    public void buttonStart(boolean isDown) {}

    @Override
    public void button1(boolean isDown) {}

    @Override
    public void button2(boolean isDown) {}

    @Override
    public void button3(boolean isDown) {}

    @Override
    public void button4(boolean isDown) {}
}

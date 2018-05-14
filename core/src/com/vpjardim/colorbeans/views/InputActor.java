/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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

    // Todo fix bug where keyboard key events make the controller InputActor to animate

    public static final int CONTROLLER = 1;
    public static final int KEYBOARD   = 2;

    private Array<TextureAtlas.AtlasRegion> bodies;
    private TextureAtlas.AtlasRegion body;
    private TextureAtlas.AtlasRegion numberBg;
    private GlyphLayout gl = new GlyphLayout();
    private Profile profile;
    private int number;

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

        // Draw the input number id
        if(number > 0) {

            float scaleX = width / body.originalWidth;
            float scaleY = height / body.originalHeight;

            float offsetX = (body.originalWidth - numberBg.packedWidth) * scaleX;
            float offsetY = (body.originalHeight - numberBg.packedHeight) * scaleY;

            // Todo why offset in the draw method not working?
            batch.draw(numberBg, x + offsetX, y + offsetY, 0, 0,
                    numberBg.packedWidth, numberBg.packedHeight, scaleX, scaleY, 0);

            BitmapFont font = G.game.data.font3;
            String txt = Integer.toString(number);
            gl.setText(font, txt);

            final float fontX = x + offsetX + (numberBg.packedWidth * scaleX - gl.width) / 2f;
            final float fontY = y + offsetY + (numberBg.packedHeight * scaleY + gl.height) / 2f;

            font.draw(batch, txt, fontX, fontY);
        }
    }

    @Override
    public void setInput(InputBase input) {}

    @Override
    public void keyDown(int key) {

        if(profile == null || profile.hasKey(key))
            body = bodies.get(1);
    }

    @Override
    public void keyUp(int key) {

        if(profile == null || profile.hasKey(key))
            body = bodies.first();
    }

    @Override
    public void btStartDown() {}

    @Override
    public void bt1Down() {}

    @Override
    public void bt2Down() {}

    @Override
    public void bt3Down() {}

    @Override
    public void bt4Down() {}

    @Override
    public void btStartUp() {}

    @Override
    public void bt1Up() {}

    @Override
    public void bt2Up() {}

    @Override
    public void bt3Up() {}

    @Override
    public void bt4Up() {}
}

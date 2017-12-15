/*
 * Copyright 2017 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.Profile;
import com.vpjardim.colorbeans.input.TargetBase;

/**
 * @author Vinícius Jardim
 * 2017/05/08
 */

public class ControllerActor extends Actor implements TargetBase {

    public interface KeysSetListener {
        void finished(Profile profile);
    }

    private TextureAtlas.AtlasRegion body;
    /** Button indexes: 0 up, 1 right, 2 down, 3 left, 4 start, 5 bt1, 6 bt2, 7 bt3, 8 bt4 */
    private int position;
    private float time = 0f;
    private int[] keys;
    private KeysSetListener callback;

    public ControllerActor() {
        body = G.game.atlas.findRegion("game/controller");
        setSize(body.originalWidth, body.originalHeight);
        this.keys = new int[9];
        recycle();
    }

    public void setPosition(int position) {
        this.position = position;
        time = 0f;
    }

    public void addCallBack(KeysSetListener listener) {
        callback = listener;
    }

    @Override
    public void draw(Batch batch, float alpha) {

        float x = getX();
        float y = getY();
        float width = body.originalWidth;
        float height = body.originalHeight;
        int tile = position + 1;

        batch.draw(body, x, y, width, height);

        boolean blinkOn = time < 0.5f;
        if(time > 0.75f) time = 0f;

        if(tile >= 1 && tile <= 9 && blinkOn) {

            TextureAtlas.AtlasRegion shade = G.game.atlas.findRegion("game/c_shade", tile);

            float scaleX = width / shade.originalWidth;
            float scaleY = height / shade.originalHeight;

            // Todo why offset in the draw method not working?
            batch.draw(shade, x + (shade.offsetX * scaleX), y + (shade.offsetY * scaleY), 0, 0,
                    shade.packedWidth, shade.packedHeight, scaleX, scaleY, 0);
        }

        time += G.delta;
    }

    @Override
    public void keyDown(int key) {

        if(position < 0 || position >= keys.length) return;

        keys[position] = key;

        // The last button
        if(position == keys.length -1) {
            Profile profile = profileCreate();
            if(callback != null) callback.finished(profile);
            recycle();
        }
        else position++;
    }

    public Profile profileCreate() {
        return new Profile(keys);
    }

    public void recycle() {
        position = -1;
        callback = null;

        for(int i = 0; i < keys.length; i++) {
            keys[i] = Profile.UNDEFINED;
        }
    }

    @Override
    public void setInput(InputBase input) {}

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
    public void keyUp(int key) {}

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

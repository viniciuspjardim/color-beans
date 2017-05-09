/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.animation.SpriteAccessor;
import com.vpjardim.colorbeans.core.Dbg;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

/**
 * @author Vinícius Jardim
 * 31/10/2015
 */
public class StudioScreen extends ScreenBase {

    private Sprite studioLogo;
    private Sprite studioText;

    // Colors for blink sprite
    float bcr = 1f;
    float bcg = 1f;
    float bcb = 1f;
    float bca = 1f;

    private TweenManager transition;

    @Override
    public void show() {

        super.show();

        studioLogo = G.game.atlas.createSprite("studio/studio_logo");
        studioText = G.game.atlas.createSprite("studio/studio_text");

        G.game.assets.get("audio/studio.ogg", Music.class).play();

        Tween.setCombinedAttributesLimit(4);

        transition = new TweenManager();
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());

        // Logo fade in
        Tween.set(studioLogo, SpriteAccessor.COLORS).
                target(1.0f, 0.5f, 1.0f, 0f).start(transition);
        Tween.to(studioLogo, SpriteAccessor.COLORS, 0.5f).
                target(1f, 1f, 1f, 1f).start(transition);

        // Logo blink 1
        Tween.to(studioLogo, SpriteAccessor.COLORS, 0.1f).
                target(1f, 1f, 1f, 0f).delay(3.5f).start(transition);

        randomBlinkColor();

        Tween.to(studioLogo, SpriteAccessor.COLORS, 0.1f).
                target(bcr, bcg, bcb, bca).delay(3.6f).start(transition);
        Tween.to(studioLogo, SpriteAccessor.COLORS, 0.1f).
                target(bcr, bcg, bcb, 0f ).delay(3.9f).start(transition);
        Tween.to(studioLogo, SpriteAccessor.COLORS, 0.1f).
                target(1f, 1f, 1f, 1f).delay(4.0f).start(transition);

        // Logo blink 2
        Tween.to(studioLogo, SpriteAccessor.COLORS, 0.1f).
                target(1f, 1f, 1f, 0f).delay(6.1f).start(transition);

        randomBlinkColor();

        Tween.to(studioLogo, SpriteAccessor.COLORS, 0.1f).
                target(bcr, bcg, bcb, bca).delay(6.2f).start(transition);
        Tween.to(studioLogo, SpriteAccessor.COLORS, 0.1f).
                target(bcr, bcg, bcb, 0f).delay(6.5f).start(transition);
        Tween.to(studioLogo, SpriteAccessor.COLORS, 0.1f).
                target(1f, 1f, 1f, 1f).delay(6.6f).start(transition);

        // Logo blink 3 and fade out
        Tween.to(studioLogo, SpriteAccessor.COLORS, 0.1f).
                target(1f, 1f, 1f, 0f).delay(8.8f).start(transition);

        randomBlinkColor();

        Tween.to(studioLogo, SpriteAccessor.COLORS, 0.1f).
                target(bcr, bcg, bcb, bca).delay(8.9f).start(transition);
        Tween.to(studioLogo, SpriteAccessor.COLORS, 0.3f).
                target(bcr, bcg, bcb, 0f).delay(9.2f).start(transition);

        // Text fade out
        Tween.to(studioText, SpriteAccessor.COLORS, 0.3f).
                target(1, 1, 1, 0f).delay(10.6f).start(transition);
    }

    private void randomBlinkColor() {
        bcr = MathUtils.random(0, 4) * 0.25f;
        bcg = MathUtils.random(0, 4) * 0.25f;
        bcb = MathUtils.random(0, 4) * 0.25f;
        bca = MathUtils.random(2, 4) * 0.25f;
    }

    @Override
    public void render(float delta) {

        super.render(delta);

        transition.update(delta);

        G.game.batch.setProjectionMatrix(cam.combined);
        G.game.batch.begin();

        studioLogo.draw(G.game.batch);
        studioText.draw(G.game.batch);

        G.game.batch.end();

        if(time >= 10.9f) action = ScreenBase.ACT_NEXT;
    }

    @Override
    public void resize(int width, int height) {

        super.resize(width, height);

        float spriteSide = Math.min(width * 0.8f, height * 0.8f);
        float spriteScale = spriteSide / studioLogo.getWidth();

        studioLogo.setScale(spriteScale);
        studioText.setScale(spriteScale);

        studioLogo.setPosition(
                (width - studioLogo.getWidth()) / 2f,
                (height - studioLogo.getHeight()) / 2f
        );
        studioText.setPosition(
                (width - studioText.getWidth()) / 2f,
                (height - studioText.getHeight()) / 2f - (studioLogo.getHeight() / 2.9f * spriteScale)
        );

        // #debugCode
        Dbg.inf(Dbg.tag(this), "scale = " + spriteScale + "; textH = " +
                studioText.getHeight() + "; logoH = " + studioLogo.getHeight());
    }

    @Override
    public void dispose() {
        super.dispose();
        G.game.assets.get("audio/studio.ogg", Music.class).stop();
    }

    @Override
    public void buttonStart(boolean isDown) { action = ScreenBase.ACT_NEXT; }

    @Override
    public void button1(boolean isDown) { action = ScreenBase.ACT_NEXT; }

    @Override
    public void button2(boolean isDown) { action = ScreenBase.ACT_NEXT; }

    @Override
    public void button3(boolean isDown) { action = ScreenBase.ACT_NEXT; }

    @Override
    public void button4(boolean isDown) { action = ScreenBase.ACT_NEXT; }
}

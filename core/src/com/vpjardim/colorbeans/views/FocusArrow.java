package com.vpjardim.colorbeans.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.input.UiNav;

/** Arrow on the left of the widget the controller focused. See {@link UiNav} */
public class FocusArrow extends Actor {
    /** Seconds of a back and forth of the arrow's bob */
    private static final float BOB_PERIOD = 0.8f;

    private final UiNav nav;
    private final Vector2 tmp = new Vector2();
    /** Got on the first draw: the loading screen has a stage before the skin is loaded */
    private Drawable arrow;
    private float time;

    public FocusArrow(UiNav nav) {
        this.nav = nav;
        setTouchable(Touchable.disabled);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        time = (time + delta) % BOB_PERIOD;

        // The screens add their actors after this one
        toFront();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        Actor anchor = nav.getAnchor();

        if (anchor == null)
            return;

        // In a scroll pane, the arrow hides with its widget
        ScrollPane pane = UiNav.scrollPaneOf(anchor);

        if (pane != null) {
            anchor.localToAscendantCoordinates(pane, tmp.set(0f, anchor.getHeight() / 2f));

            if (tmp.x < 0f || tmp.x > pane.getWidth() || tmp.y < 0f || tmp.y > pane.getHeight())
                return;
        }

        if (arrow == null)
            arrow = G.game.skin.getDrawable("game/selected_arrow");

        float w = arrow.getMinWidth();
        float h = arrow.getMinHeight();
        float gap = G.style.padSmall / 2f;
        float bob = G.style.padSmall / 2f * (1f - MathUtils.cos(MathUtils.PI2 * time / BOB_PERIOD)) / 2f;

        anchor.localToStageCoordinates(tmp.set(0f, anchor.getHeight() / 2f));
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
        arrow.draw(batch, tmp.x - w - gap - bob, tmp.y - h / 2f, w, h);
    }
}

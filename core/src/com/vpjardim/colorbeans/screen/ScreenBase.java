package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.events.EventHandler;
import com.vpjardim.colorbeans.events.EventListener;
import com.vpjardim.colorbeans.input.ActorsIndex;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.TargetBase;

public class ScreenBase implements Screen, TargetBase {
    public static final int ACT_RUNNING = 1;
    public static final int ACT_NEXT = 2;

    protected int action;
    protected OrthographicCamera cam;
    protected Viewport viewport;
    protected Stage stage;
    protected EventListener specialKeyDown;
    protected ActorsIndex actorsIndex;
    protected boolean manageInput = true;
    public float time;

    public ScreenBase() {
        action = ACT_RUNNING;
        time = 0f;
    }

    public boolean isFinished() {
        return action != ACT_RUNNING;
    }

    @Override
    public void show() {
        cam = new OrthographicCamera();
        viewport = new ScreenViewport(cam);
        viewport.apply(true);

        actorsIndex = new ActorsIndex();

        if (G.game.batch != null) {
            stage = new Stage(viewport, G.game.batch);
            G.game.input.addProcessor(stage);
        }

        if (manageInput) {
            G.game.input.targetsClear();
            G.game.input.addTarget(this);
            G.game.input.linkAll();
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0.125f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        time += delta;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        G.game.bgBeans.resize();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        if (specialKeyDown != null) {
            EventHandler.get().removeListener("SpecialButtons.keyDown", specialKeyDown);
            specialKeyDown = null;
        }

        if (stage != null) {
            G.game.input.removeProcessor(stage);
            stage.dispose();
            stage = null;
        }

        G.game.input.targetsClear();
    }

    @Override
    public void setInput(InputBase input) {
    }

    @Override
    public void keyDown(int key) {
    }

    @Override
    public void btStartDown() {
        if (actorsIndex.getSelectedData() == null) {
            return;
        }

        Actor actor = actorsIndex.getSelectedData().actor;

        if (actor instanceof TextButton) {
            TextButton button = (TextButton) actor;
            InputEvent event1 = new InputEvent();
            event1.setType(InputEvent.Type.touchDown);
            button.fire(event1);
            InputEvent event2 = new InputEvent();
            event2.setType(InputEvent.Type.touchUp);
            button.fire(event2);
        } else if (actor instanceof Slider) {
            Slider slider = (Slider) actor;
            float newValue = slider.getValue() + slider.getStepSize();
            slider.setValue(newValue);
        }

    }

    @Override
    public void bt1Down() {
        actorsIndex.next();
    }

    @Override
    public void bt2Down() {
        actorsIndex.previous();
    }

    @Override
    public void bt3Down() {
        actorsIndex.next();
    }

    @Override
    public void bt4Down() {
        actorsIndex.previous();
    }

    @Override
    public void keyUp(int key) {
    }

    @Override
    public void btStartUp() {
    }

    @Override
    public void bt1Up() {
    }

    @Override
    public void bt2Up() {
    }

    @Override
    public void bt3Up() {
    }

    @Override
    public void bt4Up() {
    }
}

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.events.EventHandler;
import com.vpjardim.colorbeans.events.EventListener;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.TargetBase;

class ActorsIndex {
    static class Data {
        public Actor actor;
        public float posX;
        public float posY;

        public Data(Actor actor, float posX, float posY) {
            this.actor = actor;
            this.posX = posX;
            this.posY = posY;
        }
    }

    private final Array<Data> index = new Array<>(20);
    private int selectedIndex = 0;

    public void buildIndex(Table... tables) {
        // float padM = G.style.padMedium;

        for (Table t : tables) {
            for (Cell cell : t.getCells()) {
                Actor actor = cell.getActor();

                if (!(actor instanceof TextButton || actor instanceof TextField || actor instanceof Slider)) {
                    continue;
                }

                Actor actorRef = actor;
                float posX = 0f;
                float posY = 0f;

                while (actorRef != null) {
                    posX += actorRef.getX();
                    posY += actorRef.getY();
                    actorRef = actorRef.getParent();
                }

                index.add(new Data(actor, posX, posY));
            }
        }
    }

    public void render() {
        TextureRegion tr = G.game.atlas.findRegion("game/number_bg");
        float padM = G.style.padMedium;

        /*for (Data ad : index) {
            // Dbg.inf("kkk", ad.posX + "," + ad.posY);
            G.game.batch.draw(tr, ad.posX, ad.posY, padM, padM);
        }*/

        Data selected = getSelectedData();
        G.game.batch.draw(tr, selected.posX, selected.posY, padM, padM);
    }

    public void clearIndex() {
        index.clear();
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public Data getSelectedData() {
        if (index.size == 0) {
            return null;
        }
        return index.get(selectedIndex);
    }

    public void next() {
        selectedIndex++;
        if (selectedIndex >= index.size) {
            selectedIndex = 0;
        }
    }

    public void previous() {
        selectedIndex--;
        if (selectedIndex < 0) {
            selectedIndex = index.size -1;
        }
    }
}

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

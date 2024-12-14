package com.vpjardim.colorbeans.input;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.vpjardim.colorbeans.G;

public class ActorsIndex {
    public static class Data {
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

        // for (Data ad : index) {
        //     // Dbg.inf("kkk", ad.posX + "," + ad.posY);
        //     G.game.batch.draw(tr, ad.posX, ad.posY, padM, padM);
        // }

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

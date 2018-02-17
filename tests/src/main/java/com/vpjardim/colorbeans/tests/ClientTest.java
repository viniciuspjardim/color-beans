/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.tests;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.vpjardim.colorbeans.Block;
import com.vpjardim.colorbeans.net.GameClient;

/**
 * @author Vinícius Jardim
 * 2016/08/25
 */
public class ClientTest extends Game implements Screen {

    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setWindowedMode(1080, 860);
        config.setTitle("Net view");

        new Lwjgl3Application(new ClientTest(), config);
    }

    public static final Color COLOR = new Color(0x005500ff);

    ShapeRenderer sr;
    OrthographicCamera cam;
    GameClient client;

    @Override
    public void show() {

        sr = new ShapeRenderer();
        cam = new OrthographicCamera();
        client = new GameClient();

        client.init();

        System.out.println("Test 666");
    }

    @Override
    public void render(float delta) {

        System.out.println("Test 999");

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if(client.b == null) return;

        sr.setProjectionMatrix(cam.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);

        byte[][] b = client.b;

        float side = 30;
        int outRow = 10;
        float px = 0f; //+ Gdx.graphics.getWidth()/2f - (side * b.length) - 20f;
        float py = +300f; //+ Gdx.graphics.getHeight()/2 - 40f - side;

        // Draw map blocks
        for(int i = 0; i < b.length; i++) {

            for(int j = outRow - 2; j < b[0].length; j++) {

                if(b[i][j] == Block.EMPTY) {

                    sr.setColor(Color.DARK_GRAY);

                    if(j < outRow) sr.setColor(COLOR);

                    sr.circle(
                            px + (side * i),
                            py - (side * (j -outRow)),
                            side/8f
                    );
                    continue;
                }

                // sr.setColor(Block.intToColor(b[i][j]));
                sr.circle(
                        px + (side * i),
                        py - (side * (j -outRow)),
                        side / 2f
                );
            }
        }

        sr.end();
    }

    @Override
    public void create() {
        setScreen(this);
    }

    @Override
    public void resize(int width, int height) {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
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
        sr.dispose();
    }
}

/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.tests.treeview;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;

/**
 * @author Vinícius Jardim
 * 2016/08/03
 */
public class InputProc implements InputProcessor, GestureDetector.GestureListener
{
    MainScreen screen;
    float zoomIniDist = 0.0f;
    float zoomDistProc = 0.0f;

    boolean isKeyDown = false;
    int keyCode = 0;

    // Constroi passando o universo como parametro
    public InputProc(MainScreen screen)
    {
        this.screen = screen;
    }

    // Deslisa na tela: navega nos eixos x e y
    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY)
    {
        double dx =  deltaX / TreeView.get().cam.zoom;
        double dy =  deltaY / TreeView.get().cam.zoom;

        TreeView.get().cam.p.x -= dx;
        TreeView.get().cam.p.y += dy;

        return false;
    }

    // Zoom in e zoom out com a roda do mouse
    @Override
    public boolean scrolled(int amount) {

        // Ajusta o multiplicador (do zoom) 10% para mais ou menos por movimento
        // na roda do mouse
        double multi = (amount * -0.3) + 1.0;

        TreeView.get().cam.zoom *= multi;
        return false;
    }

    // Zoom in e zoom out com pinch to zoom no touch screen
    @Override
    public boolean zoom(float initialDistance, float distance)
    {
        // LibGdx bug?
        // A distância inicial (initialDistance) permanece igual. Se em 3 frames
        // o zoom cai 10% em cada, no primeiro frame você reduz 10% - ok. Só que
        // no segundo você reduz mais 20% e no terceiro mais 30%, totalizando 60%
        // onde deveria ser 30%. É Uma especie de zoom sobre zoom o que causa uma
        // dificuldade de operá-lo. Fazendo uma correção.
        //
        // zoom: 810.93835; 810.93835
        // zoom: 810.93835; 597.8202
        // zoom: 810.93835; 490.09592

        //Gdx.app.log("zoom", " zoom: " + initialDistance + "; " + distance);

        //double soma = (-1.0 + (distance / initialDistance)) * u.cam.zoom;
        //u.cam.zoom += (soma * 0.05);

        if(initialDistance != zoomIniDist)
        {
            zoomIniDist = initialDistance;
            zoomDistProc = 0.0f;
        }

        // Diminuindo da distancia inicial o que já foi processado em
        // outros frames
        initialDistance -= zoomDistProc;

        TreeView.get().cam.zoom *= distance / initialDistance;

        // Incrementando a distancia processada com o que já foi processado
        // nesse frame
        zoomDistProc += initialDistance - distance;

        //Gdx.app.log("zoom", " zoom: " + initialDistance + "; " + distance + "; " + zoomDistProc);

        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button)
    {
        screen.tap(x, y);
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {

        isKeyDown = true;
        this.keyCode = keycode;
        screen.keyPressed(keycode);

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        isKeyDown = false;
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) { return false; }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) { return false; }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
                         Vector2 pointer1, Vector2 pointer2) { return false; }

    @Override
    public void pinchStop() {

    }
}

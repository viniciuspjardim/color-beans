package com.vpjardim.colorbeans.tests.treeview;

/**
 * @author Vinícius Jardim
 * 03/08/2016
 */
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

/**
 * Created by vinicius on 15/02/15.
 */
public class Camera {

    public OrthographicCamera cam = new OrthographicCamera();

    // Zoom da camera
    public float zoom = 1f;

    // Foco da camera nas posições x e y
    public Vector2 p = new Vector2();

    // Seguir um corpo em particular
    // Ele passa a ser o foco da camera
    public String seguir = null;

    public void update(float width, float height)
    {
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.update();
    }

    // Converte de coordenadas espaciais p/ coordenadas na tela
    public float  coordETX(float x)
    {
        return (float) ((x - p.x) * zoom);
    }

    // Converte de coordenadas espaciais p/ coordenadas na tela
    public float coordETY(float y)
    {
        return (float) ((y - p.y) * zoom);
    }

    // Converte as coordenadas de tela p/ coordenadas espaciais
    public float coordTEX(float x)
    {
        x = tratarX(x);
        return (x / zoom) + p.x;
    }

    // Converte as coordenadas de tela p/ coordenadas espaciais
    public float coordTEY(float y)
    {
        y = tratarY(y);
        return (y / zoom) + p.y;
    }

    public float tratarX(float x) {
        return x - (cam.viewportWidth / 2.0f);
    }

    public float tratarY(float y) {
        return -(y - (cam.viewportHeight / 2.0f));
    }
}


/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.screen;

import com.badlogic.gdx.Input;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.events.Event;
import com.vpjardim.colorbeans.events.EventHandler;
import com.vpjardim.colorbeans.events.EventListener;
import com.vpjardim.colorbeans.net.ControllerClient;

/**
 * @author Vinícius Jardim
 * 2018/05/03
 */
public class NetInputScreen extends ScreenBase {

    private ControllerClient controllerClient;
    private EventListener specialKeyDown;

    public NetInputScreen() {
        manageInput = false;
    }

    @Override
    public void show() {

        super.show();

        specialKeyDown = (Event e) -> {
            int key = (Integer) e.getAttribute();
            if(key == G.game.data.escBt || key == Input.Keys.BACK)
                action = ACT_NEXT;
            else if(key == G.game.data.printScreenBt)
                printScreen();
        };

        EventHandler.getHandler().addListener("SpecialButtons.keyDown", specialKeyDown);

        controllerClient = new ControllerClient();

        G.game.input.targetsClear();
        G.game.input.addTarget(controllerClient);
        G.game.input.linkAll();

        controllerClient.init();
    }

    @Override
    public void dispose() {
        super.dispose();
        EventHandler.getHandler().removeListener("SpecialButtons.keyDown", specialKeyDown);
    }
}

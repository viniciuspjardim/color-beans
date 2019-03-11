/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.net;

import com.badlogic.gdx.utils.IntMap;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.vpjardim.colorbeans.G;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.input.InputBase;

/**
 * @author Vinícius Jardim
 * 2018/05/03
 */
public class ControllerServer extends Server {

    private IntMap<NetController> controllersMap = new IntMap<>();

    public static void main(String[] args) {

        Log.set(Log.LEVEL_NONE);

        ControllerServer s = new ControllerServer();
        s.init();
    }

    public void init() {
        try {

            Net.register(this);
            addListener(new Listener() {

                @Override
                public void received(Connection connection, Object object) {

                    NetController controller = controllersMap.get(connection.getID());
                    if(controller == null) return;

                    if(object instanceof ControllerData) {

                        ControllerData data = (ControllerData) object;
                        // Dbg.print("ControllerServer::received [" + connection.getID() + "]: key = " + data.key +
                        //         "; keyDown = " + data.keyDown);

                        if(data.key >= 0) {
                            if(data.keyDown)
                                controller.keyDown(data.key);
                            else
                                controller.keyUp(data.key);
                        }
                        else if(data.key == -1) {
                            controller.keyMap = data.keyMap;
                            controller.keyMapOld = data.keyMapOld;

                            Dbg.print(InputBase.keyMapToString(
                                    controller.keyMapOld, controller.keyMap, (short)0));
                        }
                    }
                }

                @Override
                public void connected (Connection connection) {
                    Dbg.print("ControllerServer::connected [" + connection.getID() + "]");

                    NetController controller = new NetController();
                    controllersMap.put(connection.getID(), controller);
                    G.game.input.addInput(controller);
                    G.game.input.linkAll();
                }

                @Override
                public void disconnected (Connection connection) {
                    Dbg.print("ControllerServer::disconnected [" + connection.getID() + "]");
                    NetController controller = controllersMap.remove(connection.getID());
                    G.game.input.removeInput(controller);
                    G.game.input.linkAll();
                }
            });

            bind(Net.tcpPort, Net.udpPort);
            start();

            Dbg.print("==== Server start ====");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}

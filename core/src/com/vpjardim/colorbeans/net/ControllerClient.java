/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.net;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.TargetBase;

/**
 * @author Vinícius Jardim
 * 2018/05/03
 */
public class ControllerClient implements TargetBase {

    private Client client;
    private InputBase input;
    private Connection connection;

    public static void main(String[] args) {

        Log.set(Log.LEVEL_NONE);

        ControllerClient c = new ControllerClient();
        c.init();
    }

    public void init() {

        try {
            client = new Client();

            Net.register(client);
            client.addListener(new Listener() {

                @Override
                public void received(Connection connection, Object object) { }

                @Override
                public void connected(Connection connection) {
                    Dbg.print("ControllerClient::connected [" + connection.getID() + "]");
                    ControllerClient.this.connection = connection;
                }

                @Override
                public void disconnected(Connection connection) {
                    Dbg.print("ControllerClient::disconnected [" + connection.getID() + "]");
                    ControllerClient.this.connection = null;
                }
            });

            new Thread(client, "Client").start();
            client.connect(5000, "192.168.1.110", Net.tcpPort, Net.udpPort);

            Dbg.print("==== Client start ====");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setInput(InputBase input) { this.input = input; }

    @Override
    public void keyDown(int key) {
        ControllerData data = new ControllerData();
        data.key = key;
        data.keyDown = true;

        if(connection != null)
            connection.sendUDP(data);
    }

    @Override
    public void btStartDown() {
        ControllerData data = new ControllerData();
        data.key = 10;
        data.keyDown = true;

        if(connection != null)
            connection.sendUDP(data);
    }

    @Override
    public void bt1Down() {
        ControllerData data = new ControllerData();
        data.key = 1;
        data.keyDown = true;

        if(connection != null)
            connection.sendUDP(data);
    }

    @Override
    public void bt2Down() {
        ControllerData data = new ControllerData();
        data.key = 2;
        data.keyDown = true;

        if(connection != null)
            connection.sendUDP(data);
    }

    @Override
    public void bt3Down() {
        ControllerData data = new ControllerData();
        data.key = 3;
        data.keyDown = true;

        if(connection != null)
            connection.sendUDP(data);
    }

    @Override
    public void bt4Down() {
        ControllerData data = new ControllerData();
        data.key = 4;
        data.keyDown = true;

        if(connection != null)
            connection.sendUDP(data);
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

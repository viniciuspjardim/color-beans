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
 *         2018/05/03
 */
public class ControllerClient implements TargetBase {

    private Client client;
    private InputBase input;
    private Connection connection;
    private boolean zeroSent = false;

    public static void main(String[] args) {

        Log.set(Log.LEVEL_NONE);

        ControllerClient c = new ControllerClient();
        c.init("192.168.1.110");
    }

    public void init(String ip) {

        try {
            client = new Client();

            Net.register(client);
            client.addListener(new Listener() {

                @Override
                public void received(Connection connection, Object object) {
                }

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
            client.connect(5000, ip, Net.tcpPort, Net.udpPort);

            Dbg.print("==== Client start ====");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

    @Override
    public void setInput(InputBase input) {
        this.input = input;
    }

    private boolean sendUdp(int key, boolean isDown) {

        if (connection == null)
            return false;

        ControllerData data = new ControllerData();
        data.keyMap = input.getKeyMap();
        data.keyMapOld = input.getKeyMapOld();
        data.key = (byte) key;
        data.keyDown = isDown;

        connection.sendUDP(data);

        return true;
    }

    public void update() {

        input.update();

        if (input.getKeyMap() != input.getKeyMapOld()) {
            sendUdp(-1, false);
            zeroSent = false;

            // #debugCode
            Dbg.print(InputBase.keyMapToString(
                    input.getKeyMapOld(), input.getKeyMap(), input.getEvent()));
        }
        // Send all zero keys when all keys are released
        else if (!zeroSent) {
            sendUdp(-1, false);
            zeroSent = true;

            // #debugCode
            Dbg.print(InputBase.keyMapToString(
                    input.getKeyMapOld(), input.getKeyMap(), input.getEvent()));
        }
    }

    @Override
    public void keyDown(int key) {
    }

    @Override
    public void btStartDown() {
        sendUdp(InputBase.START_KEY, true);
    }

    @Override
    public void bt1Down() {
        sendUdp(InputBase.BUTTON1_KEY, true);
    }

    @Override
    public void bt2Down() {
        sendUdp(InputBase.BUTTON2_KEY, true);
    }

    @Override
    public void bt3Down() {
        sendUdp(InputBase.BUTTON3_KEY, true);
    }

    @Override
    public void bt4Down() {
        sendUdp(InputBase.BUTTON4_KEY, true);
    }

    @Override
    public void keyUp(int key) {
    }

    @Override
    public void btStartUp() {
        sendUdp(InputBase.START_KEY, false);
    }

    @Override
    public void bt1Up() {
        sendUdp(InputBase.BUTTON1_KEY, false);
    }

    @Override
    public void bt2Up() {
        sendUdp(InputBase.BUTTON2_KEY, false);
    }

    @Override
    public void bt3Up() {
        sendUdp(InputBase.BUTTON3_KEY, false);
    }

    @Override
    public void bt4Up() {
        sendUdp(InputBase.BUTTON4_KEY, false);
    }
}

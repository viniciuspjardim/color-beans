/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.net;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.vpjardim.colorbeans.core.Dbg;

/**
 * @author Vinícius Jardim
 * 12/11/2015
 */
public class GameClient {

    private Client client;

    public byte[][] b;

    public static void main(String[] args) {

        Log.set(Log.LEVEL_DEBUG);

        GameClient c = new GameClient();
        c.init();
    }

    public void init() {
        try {

            client = new Client();

            Net.register(client);
            client.addListener(new Listener() {

                @Override
                public void received(Connection connection, Object object) {

                    if(object instanceof NetData) {

                        NetData data = (NetData) object;
                        Dbg.print("C[" + data.clientID + "] rec: " + data.num);
                        data.num++;
                        b = data.b;

                        // Wait before new request
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        connection.sendUDP(data);
                    }
                }

                @Override
                public void connected(Connection connection) {
                    Dbg.print("Connected to server " + connection.getID());
                }

                @Override
                public void disconnected(Connection connection) {
                    Dbg.print("Disconnected to server " + connection.getID());
                }

            });

            new Thread(client, "Client").start();
            client.connect(5000, "localhost", Net.tcpPort, Net.udpPort);

            NetData data = new NetData();
            data.num = 0;
            client.sendUDP(data);

            Dbg.print("==== Client start ====");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
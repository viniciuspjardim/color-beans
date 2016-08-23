/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.net;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import com.vpjardim.colorbeans.net.data.NetData;

/**
 * @author Vinícius Jardim
 * 12/11/2015
 */
public class GameClient {

    private Client client;

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
                        System.out.println("C[" + data.clientID + "] rec: " + data.num);

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        data.num++;
                        connection.sendUDP(data);
                    }
                }

                @Override
                public void connected(Connection connection) {
                    System.out.println("Connected to server " + connection.getID());
                }

                @Override
                public void disconnected(Connection connection) {
                    System.out.println("Disconnected to server " + connection.getID());
                }

            });

            new Thread(client, "Client").start();
            client.connect(5000, "192.168.1.110", Net.tcpPort, Net.udpPort);

            NetData data = new NetData();
            data.num = 0;
            client.sendUDP(data);

            System.out.println("==== Client start ====");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
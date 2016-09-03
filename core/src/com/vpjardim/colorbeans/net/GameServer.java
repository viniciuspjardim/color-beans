/*
 * Copyright 2015 Vinícius Petrocione Jardim
 */

package com.vpjardim.colorbeans.net;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.vpjardim.colorbeans.ai.AiMap;
import com.vpjardim.colorbeans.core.Campaign;

/**
 * @author Vinícius Jardim
 * 11/11/2015
 */
public class GameServer extends Server {

    public Campaign campaign;

    public static void main(String[] args) {

        Log.set(Log.LEVEL_DEBUG);

        GameServer s = new GameServer();
        s.init();
    }

    public void init() {
        try {

            Net.register(this);
            addListener(new Listener() {

                @Override
                public void received(Connection connection, Object object) {

                    if(object instanceof NetData) {

                        NetData data = (NetData) object;
                        data.clientID = connection.getID();
                        System.out.println("S rec from[" + data.clientID + "]: " + data.num);

                        data.num++;

                        data.b = AiMap.getByteBlocks(null, campaign.maps.get(1).b);

                        connection.sendUDP(data);
                    }
                }

                @Override
                public void connected (Connection connection) {
                    System.out.println("Client " + connection.getID() + " connected");
                }

                @Override
                public void disconnected (Connection connection) {
                    System.out.println("Client " + connection.getID() + " disconnected");
                }
            });

            bind(Net.tcpPort, Net.udpPort);
            start();

            System.out.println("==== Server start ====");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Connection newConnection () {

        // By providing our own connection implementation, we can store per
        // connection state without a connection ID to state look up
        return new GameConnection();
    }

    // This holds per connection state
    static class GameConnection extends Connection {
        public String name;
    }
}

/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.net;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

/**
 * @author Vinícius Jardim
 * 2015/11/21
 */
public class Net {

    public static int tcpPort = 50000;
    public static int udpPort = 50001;

    // This registers objects that are going to be sent over the network.
    public static void register(EndPoint endPoint) {

        Kryo kryo = endPoint.getKryo();

        kryo.register(byte[].class);
        kryo.register(byte[][].class);
        kryo.register(NetData.class);
        kryo.register(ControllerData.class);
    }
}

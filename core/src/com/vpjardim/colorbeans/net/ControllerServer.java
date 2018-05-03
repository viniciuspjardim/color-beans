/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.net;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import com.vpjardim.colorbeans.core.Dbg;
import com.vpjardim.colorbeans.input.InputBase;
import com.vpjardim.colorbeans.input.Profile;
import com.vpjardim.colorbeans.input.TargetBase;

/**
 * @author Vinícius Jardim
 * 2018/05/03
 */
public class ControllerServer extends Server {

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

                    if(object instanceof ControllerData) {
                        ControllerData data = (ControllerData) object;
                        Dbg.print("ControllerServer::received [" + connection.getID() + "]: key = " + data.key +
                                "; keyDown = " + data.keyDown);
                    }
                }

                @Override
                public void connected (Connection connection) {
                    Dbg.print("ControllerServer::connected [" + connection.getID() + "]");
                }

                @Override
                public void disconnected (Connection connection) {
                    Dbg.print("ControllerServer::disconnected [" + connection.getID() + "]");
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

    public static class NetController implements InputBase {

        private TargetBase target;
        public Profile p;

        @Override
        public void setTarget(TargetBase target) { this.target = target; }

        @Override
        public void setProfile(Profile profile) { p = profile; }

        @Override
        public Profile getProfile() { return p; }

        @Override
        public void update() { }

        @Override
        public int getAxisX() {
            return 0;
        }

        @Override
        public int getAxisY() {
            return 0;
        }

        @Override
        public int getAxisXOld() {
            return 0;
        }

        @Override
        public int getAxisYOld() {
            return 0;
        }

        public boolean keyDown(int keycode) {

            // #debugCode
            Dbg.dbg(Dbg.tag(this), "keyDown -> keycode = " + keycode);

            if(target == null) return false;

            return true;
        }
    }
}

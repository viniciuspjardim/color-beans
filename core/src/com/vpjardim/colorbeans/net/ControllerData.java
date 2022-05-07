/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.net;

/**
 * This class will be serialized and sent via UDP to the game host
 * 
 * @author Vinícius Jardim
 *         2018/05/03
 */
public class ControllerData {

    /**
     * The Id of the remote controller. One connection to a remote device can send
     * commands from multiple controllers. For example: one computer with 2
     * controllers can control a game hosted in a Android phone.
     */
    public byte controllerId;

    /** The event pressed/released key key code. -1 if non key was pressed */
    public byte key;
    /** True if it's down, false if it's up */
    public boolean keyDown;

    /**
     * Current binary state of all keys: 1 down, 0 up.
     * Each bit is one key. From the least significant bit to the most the key, the
     * order is up, right, down, left, start, bt1, bt2, bt3 and bt4 keys
     */
    public short keyMap;

    /**
     * Previous binary state of all keys: 1 down, 0 up.
     * Each bit is one key. From the least significant bit to the most the key, the
     * order is up, right, down, left, start, bt1, bt2, bt3 and bt4 keys
     */
    public short keyMapOld;
}

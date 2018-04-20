/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.events;

/**
 * @author Vinícius Jardim
 * 2018/04/14
 */
public interface EventHandler {

    /** Returns default EventHandler instance */
    static EventHandler getHandler() { return DefaultHandler.defaultHandler; }

    void addListener(String type, EventListener eListener);
    void removeListener(String type, EventListener eListener);
    void addEvent(String type, Event e);
}

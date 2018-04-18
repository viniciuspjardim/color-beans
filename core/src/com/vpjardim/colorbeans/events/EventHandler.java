/*
 * Copyright 2015-2018 Vinícius Petrocione Jardim. All rights reserved
 */

package com.vpjardim.colorbeans.events;

/**
 * @author Vinícius Jardim
 * 2018/04/14
 */
public interface EventHandler {
    void addEventListener(String type, EventListener eListener);
    void removeEventListener(String type, EventListener eListener);
    void addEvent(String type, Event e);
}

package com.vpjardim.colorbeans.events;

@FunctionalInterface
public interface EventListener {
    void handleEvent(Event e);
}

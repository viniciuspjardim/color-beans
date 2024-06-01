package com.vpjardim.colorbeans.events;

public interface EventHandler {
    /** Returns default EventHandler instance */
    static EventHandler get() {
        return DefaultHandler.defaultHandler;
    }

    void addListener(String type, EventListener eListener);

    void removeListener(String type, EventListener eListener);

    void emit(String type, Event e);

    int countTypes();

    int countListeners();
}

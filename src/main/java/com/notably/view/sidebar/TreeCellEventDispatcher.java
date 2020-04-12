package com.notably.view.sidebar;

import javafx.event.Event;
import javafx.event.EventDispatchChain;
import javafx.event.EventDispatcher;
import javafx.scene.input.MouseEvent;

/**
 * Custom {@code EventDispatcher} to allow for finer control over mouse click events.
 * A {@link SideBarTreeViewCell} should not respond to any mouse click events.
 *
 */
class TreeCellEventDispatcher implements EventDispatcher {
    private final EventDispatcher original;

    public TreeCellEventDispatcher(EventDispatcher original) {
        this.original = original;
    }

    @Override
    public Event dispatchEvent(Event event, EventDispatchChain tail) {

        if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
            event.consume();
        }
        return original.dispatchEvent(event, tail);
    }
}

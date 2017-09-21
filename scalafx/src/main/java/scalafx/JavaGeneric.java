package scalafx;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

public class JavaGeneric {
    protected final <T extends Event> void setEventHandler(
            final EventType<T> eventType,
            final EventHandler<? super T> eventHandler){
        System.out.println("hello");
    }
}

package Client.Events;

import Entities.Communication.Message;

public class ErrorEvent {
    private final Message message;

    public Message getMessage() {
        return message;
    }

    public ErrorEvent(Message message) {
        this.message = message;
    }
}

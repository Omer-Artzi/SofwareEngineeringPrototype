package Client.Events;

import Entities.Communication.Message;

public class MessageEvent {
    private final Message message;

    public Message getMessage() {
        return message;
    }

    public MessageEvent(Message message) {
        this.message = message;
    }
}

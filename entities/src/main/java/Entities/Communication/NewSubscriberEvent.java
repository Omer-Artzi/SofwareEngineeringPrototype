package Entities.Communication;

import Entities.Communication.Message;

public class NewSubscriberEvent {
    private final Message message;

    public Message getMessage() {
        return message;
    }

    public NewSubscriberEvent(Message message) {
        this.message = message;
    }

}

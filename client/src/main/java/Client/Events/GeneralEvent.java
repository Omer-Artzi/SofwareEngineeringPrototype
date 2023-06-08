package Client.Events;

import Entities.Communication.Message;

public class GeneralEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public GeneralEvent(Message message) {
        this.message = message;
    }
}

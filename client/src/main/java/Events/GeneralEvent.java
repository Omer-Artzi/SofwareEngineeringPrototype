package Events;

import Entities.Message;

public class GeneralEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public GeneralEvent(Message message) {
        this.message = message;
    }
}

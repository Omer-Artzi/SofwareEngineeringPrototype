package il.cshaifasweng.OCSFMediatorExample.entities;


public class NewSubscriberEvent {
    private Message message;

    public Message getMessage() {
        return message;
    }

    public NewSubscriberEvent(Message message) {
        this.message = message;
    }

}

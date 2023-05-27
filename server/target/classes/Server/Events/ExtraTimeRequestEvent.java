package Server.Events;

import Entities.ExtraTime;

public class ExtraTimeRequestEvent {
    private ExtraTime request;

    public ExtraTimeRequestEvent(ExtraTime request) {
        this.request = request;
    }

    public ExtraTime getRequest() {
        return request;
    }

    public void setRequest(ExtraTime request) {
        this.request = request;
    }
}

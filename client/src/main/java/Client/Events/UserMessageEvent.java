package Client.Events;

import Entities.Users.Person;

public class UserMessageEvent {
    private Person user;
    private String status;
    public UserMessageEvent(Person data,String stat) {
        this.user = data;
        this.status = stat;
    }

    public UserMessageEvent(String stat) {
        this.user = null;
        this.status = stat;
    }

    public Person getUser() {
        return user;
    }

    public void setUser(Person user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

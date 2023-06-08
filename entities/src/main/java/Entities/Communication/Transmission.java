package Entities.Communication;

public class Transmission {
    private int ID;
    private String request;
    private String response;
    private String timeSent;
    private String timeReceived;
    private String client;

    public Transmission() {
    }


    public Transmission(int ID, String request, String response, String timeSent, String timeReceived,String client) {
        this.ID = ID;
        this.request = request;
        this.response = response;
        this.timeSent = timeSent;
        this.timeReceived = timeReceived;
        this.client = client;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getTimeSent() {
        return timeSent;
    }

    public void setTimeSent(String timeSent) {
        this.timeSent = timeSent;
    }

    public String getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(String timeReceived) {
        this.timeReceived = timeReceived;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}

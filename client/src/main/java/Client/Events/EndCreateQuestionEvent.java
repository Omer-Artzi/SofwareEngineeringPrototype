package Client.Events;

public class EndCreateQuestionEvent {
    private String status;
    public EndCreateQuestionEvent() {
    }

    public EndCreateQuestionEvent(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

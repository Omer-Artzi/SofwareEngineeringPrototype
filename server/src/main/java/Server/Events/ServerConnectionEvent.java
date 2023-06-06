package Server.Events;

public class ServerConnectionEvent {
    private int port;

    public ServerConnectionEvent(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

package il.cshaifasweng.OCSFMediatorExample.server;

public class ClientUpdateEvent {
    private static int numOfConnectedClients;

    public ClientUpdateEvent(int connectedClients) {
        this.numOfConnectedClients = connectedClients;
    }

    public static int getNumOfConnectedClients() {
        return numOfConnectedClients;
    }

    public static void setNumOfConnectedClients(int numOfConnectedClients) {
        ClientUpdateEvent.numOfConnectedClients = numOfConnectedClients;
    }
}

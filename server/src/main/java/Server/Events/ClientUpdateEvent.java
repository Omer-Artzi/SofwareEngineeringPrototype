package Server.Events;

public class ClientUpdateEvent {
    private static int numOfConnectedClients;

    public ClientUpdateEvent(int connectedClients) {
        numOfConnectedClients = connectedClients;
    }

    public static int getNumOfConnectedClients() {
        return numOfConnectedClients;
    }

    public static void setNumOfConnectedClients(int numOfConnectedClients) {
        ClientUpdateEvent.numOfConnectedClients = numOfConnectedClients;
    }
}

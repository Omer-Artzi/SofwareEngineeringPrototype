package Server.Events;


import Entities.Transmission;

public class TransmissionEvent {
    private Transmission transmission;

    public TransmissionEvent(Transmission transmission) {
        this.transmission = transmission;
    }

    public Transmission getTransmission() {
        return transmission;
    }

    public void setTransmission(Transmission transmission) {
        this.transmission = transmission;
    }
}

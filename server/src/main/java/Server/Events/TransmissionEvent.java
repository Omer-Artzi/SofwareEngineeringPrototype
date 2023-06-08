package Server.Events;


import Entities.Communication.Transmission;

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

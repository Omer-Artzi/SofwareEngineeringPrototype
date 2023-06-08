package Server.Controllers;

import Entities.Communication.Transmission;
import Server.Events.ClientUpdateEvent;
import Server.Events.TerminationEvent;
import Server.Events.TransmissionEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class PrimaryController {

    @FXML
    private TableColumn<Transmission, Integer> IDColumn;

    @FXML
    private TableColumn<Transmission, SimpleStringProperty> RequestColumn;

    @FXML
    private TableColumn<Transmission, SimpleStringProperty> ResponseColumn;

    @FXML
    private Button TerminationButton;

    @FXML
    private TableColumn<Transmission, String> TimeSentColumn;
    @FXML
    private TableColumn<Transmission, String> TimeReceivedColumn;

    @FXML
    private TableView<Transmission> TransmissionTV;
    @FXML
    private TextField connectedClientsTF;
    @FXML
    private TableColumn<Transmission, SimpleStringProperty> clientColumn;

    @FXML
    void initialize() {
        EventBus.getDefault().register(this);
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("ID"));
        RequestColumn.setCellValueFactory(new PropertyValueFactory<>("request"));
        ResponseColumn.setCellValueFactory(new PropertyValueFactory<>("response"));
        TimeSentColumn.setCellValueFactory(new PropertyValueFactory<>("timeSent"));
        TimeReceivedColumn.setCellValueFactory(new PropertyValueFactory<>("timeReceived"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
        connectedClientsTF.setText("Number of connected clients: 0");

    }
    @Subscribe
    public void newTransmission(TransmissionEvent event)
    {
        TransmissionTV.getItems().add(event.getTransmission());
        TransmissionTV.refresh();
    }
    @FXML
    void terminate() {
    EventBus.getDefault().post(new TerminationEvent());
    Stage stage = (Stage) TerminationButton.getScene().getWindow();
    stage.close();
    }
    @Subscribe
    public void updateClientNum(ClientUpdateEvent event)
    {
        connectedClientsTF.setText("Number of connected clients: " + event.getNumOfConnectedClients());
    }

}
